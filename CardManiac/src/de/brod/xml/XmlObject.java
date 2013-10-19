package de.brod.xml;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class XmlObject {

	class XmlReader extends InputStream {

		private byte[] bytes;
		private int pos;

		XmlReader(InputStream parentReader) {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			byte[] buffer = new byte[4096];
			int count;
			try {
				BufferedInputStream in = new BufferedInputStream(parentReader);
				while ((count = in.read(buffer)) > 0) {
					out.write(buffer, 0, count);
				}
			} catch (IOException e) {
				// ignore this
			}
			pos = 0;
			bytes = out.toByteArray();
		}

		@Override
		public void close() throws IOException {
			// make nothing
		}

		@Override
		public int read() throws IOException {
			if (bytes == null) {
				return -1;
			}
			if (pos >= bytes.length) {
				bytes = null;
				return -1;
			}
			int read = bytes[pos];
			pos++;
			if (read < 0) {
				read += 256;
			}
			if (read >= 192) {
				int count;
				if (read >= 248) {
					count = 4;
					read = read % 8;
				} else if (read >= 240) {
					count = 3;
					read = read % 16;
				} else if (read >= 224) {
					count = 2;
					read = read % 16;
				} else {
					count = 1;
					read = read % 32;
				}
				for (int i = 0; (i < count) && (pos < bytes.length); i++) {
					int r = bytes[pos];
					if (r < 0) {
						r += 256;
					}
					pos++;
					read = (read * 64) + (r % 64);
				}
			}
			return read;
		}

	}

	private static final String WHITESPACECHARS;
	private static String ENDCHARS;
	private static String END;
	private static String ENDATTR;
	static {
		char[] c = { (char) 10, (char) 13, (char) 7, (char) 32 };
		WHITESPACECHARS = new String(c);
		ENDCHARS = WHITESPACECHARS + "/>";
		END = "?/>";
		ENDATTR = WHITESPACECHARS + END + "=";

	}
	private XmlObject _parent = null;
	private XmlReader _stream;
	List<XmlObject> preObjects = null;
	private String sName;
	List<XmlAttribute> subAtr = new ArrayList<XmlAttribute>();

	List<XmlObject> subObjects = new ArrayList<XmlObject>();

	public XmlObject(InputStream pxStream) throws IOException {
		_stream = new XmlReader(pxStream);
		consumeStream();
	}

	public XmlObject(String psName) {
		if (psName == null) {
			sName = "";
		} else {
			sName = psName;
		}
		if (sName.startsWith("<")) {
			try {
				_stream = new XmlReader(new ByteArrayInputStream(
						psName.getBytes()));
				consumeStream();
			} catch (IOException e) {
				// could not create
				sName = "";
			}
		}
	}

	private XmlObject(XmlReader pxStream) throws IOException {
		_stream = pxStream;
		consumeStream();
	}

	public void addObject(XmlObject xmlObject) {
		xmlObject._parent = this;
		if (xmlObject.getName().startsWith("?")) {
			if (preObjects == null) {
				preObjects = new ArrayList<XmlObject>();
			}
			preObjects.add(xmlObject);
		} else {
			subObjects.add(xmlObject);
		}
	}

	private void consumeStream() throws IOException {
		sName = "";
		// read until startTag
		if (readUntil('<') > 0) {
			StringBuilder sbName = new StringBuilder();
			char c = read(ENDCHARS, sbName);
			sName = sbName.toString();
			if (sName.length() > 0) {
				// read until endTag
				while (END.indexOf(c) < 0) {
					// read the whiteSpaces
					while (WHITESPACECHARS.indexOf(c) >= 0) {
						c = (char) read();
					}
					StringBuilder sbAttr = new StringBuilder(String.valueOf(c));
					// read the name
					c = read(ENDATTR, sbAttr);
					if (sbAttr.length() > 0) {
						if (c == '=') {
							c = (char) read();
							if ((c == '\"') || (c == '\'')) {
								StringBuilder sValue = new StringBuilder();
								read(c, sValue);
								setAttribute(sbAttr.toString(),
										sValue.toString());
								// read the next character
								c = (char) read();
							}
						}
					} // read the whiteSpaces
					while (WHITESPACECHARS.indexOf(c) >= 0) {
						c = (char) read();
					}

				}
			}
			if (c == '?') {
				readUntil('>');
				XmlObject pre = new XmlObject(sName);
				pre.subAtr = subAtr;
				subAtr = new ArrayList<XmlAttribute>();
				addObject(pre);
				consumeStream();
			} else if (c == '/') {
				readUntil('>');
			} else if (sName.length() > 0) {
				XmlObject xmlObject = new XmlObject(_stream);
				while (xmlObject.getName().length() > 0) {
					addObject(xmlObject);
					xmlObject = new XmlObject(_stream);
				}
			}
		}
		_stream = null;
	}

	public XmlObject createObject(String psName) {
		XmlObject xmlObject = new XmlObject(psName);
		addObject(xmlObject);
		return xmlObject;
	}

	public void deleteAll() {
		subObjects.clear();
	}

	public void deleteObject(int iCount) {
		subObjects.remove(iCount);
	}

	public boolean deleteObject(XmlObject subObject) {
		return subObjects.remove(subObject);
	}

	public XmlObject[] deleteObjects(String psName) {
		List<XmlObject> xmlFound = new ArrayList<XmlObject>();
		for (int i = 0; i < subObjects.size();) {
			XmlObject sub = subObjects.get(i);
			if (sub.sName.equals(psName)) {
				subObjects.remove(i);
				xmlFound.add(sub);
			} else {
				i++;
			}
		}

		return xmlFound.toArray(new XmlObject[xmlFound.size()]);
	}

	public String getAttribute(String psAttributeName) {
		for (XmlAttribute atr : subAtr) {
			if (atr.equalsName(psAttributeName)) {
				return atr.getValue();
			}
		}
		return "";
	}

	public int getAttributeAsInt(String psAttributeName) {
		try {
			return Integer.parseInt(getAttribute(psAttributeName));
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	public String getName() {
		return sName;
	}

	public XmlObject getObject(String psName, String psAttribute,
			String psAttributeValue, boolean pbCreateIfNotFound) {
		for (XmlObject sub : subObjects) {
			if (sub.sName.equals(psName)) {
				if (sub.getAttribute(psAttribute).equals(psAttributeValue)) {
					return sub;
				}
			}
		}
		if (pbCreateIfNotFound) {
			XmlObject createObject = createObject(psName);
			createObject.setAttribute(psAttribute, psAttributeValue);
			return createObject;
		}
		return null;
	}

	public int getObjectCount() {
		return subObjects.size();
	}

	public XmlObject[] getObjects() {
		return subObjects.toArray(new XmlObject[subObjects.size()]);
	}

	public XmlObject[] getObjects(String psName) {
		List<XmlObject> xmlFound = new ArrayList<XmlObject>();
		for (XmlObject sub : subObjects) {
			if (sub.sName.equals(psName)) {
				xmlFound.add(sub);
			}
		}
		return xmlFound.toArray(new XmlObject[xmlFound.size()]);
	}

	public XmlObject getParent() {
		return _parent;
	}

	private int read() throws IOException {
		if (_stream == null) {
			return -1;
		}
		int read = _stream.read();
		if (read < 0) {
			_stream = null;
		}
		return read;
	}

	private char read(char psCharacters, StringBuilder sbName)
			throws IOException {
		char read;
		while ((read = (char) read()) > 0) {
			if (psCharacters == read) {
				return read;
			}
			sbName.append(read);
		}
		return read;
	}

	private char read(String psCharacters, StringBuilder sbName)
			throws IOException {
		char read;
		while ((read = (char) read()) > 0) {
			if (psCharacters.indexOf(read) >= 0) {
				return read;
			}
			sbName.append(read);
		}
		return read;
	}

	private int readUntil(char pcCharacters) throws IOException {
		int read;
		int iCharacters = pcCharacters;
		while ((read = read()) > 0) {
			if (iCharacters == read) {
				return read;
			}
		}
		return -1;

	}

	public void setAttribute(String psAttributeName, int i) {
		setAttribute(psAttributeName, String.valueOf(i));
	}

	public void setAttribute(String psAttributeName, boolean b) {
		setAttribute(psAttributeName, String.valueOf(b));
	}

	public boolean setAttribute(String psAttributeName, String psAttributeValue) {
		for (XmlAttribute atr : subAtr) {
			if (atr.equalsName(psAttributeName)) {
				if (psAttributeValue == null) {
					subAtr.remove(atr);
					return true;
				}
				return atr.setValue(psAttributeValue);

			}
		}
		if (psAttributeValue != null) {
			subAtr.add(new XmlAttribute(psAttributeName, psAttributeValue));
		}
		return true;
	}

	public void setName(String psName) {
		sName = psName;
	}

	@Override
	public String toString() {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			write(out);
		} catch (IOException e) {
			// should never happen on ByteArrayOutputStream
		}
		return out.toString();
	}

	public void write(OutputStream out) throws IOException {
		write(out, 0);
	}

	private void write(OutputStream out, int iLevel) throws IOException {
		if (preObjects != null) {
			for (XmlObject sub : preObjects) {
				sub.write(out, iLevel);
			}
		}
		for (int i = 0; i < (iLevel * 2); i++) {
			out.write(' ');
		}
		out.write('<');
		out.write(sName.getBytes());
		// add attributes
		for (XmlAttribute atr : subAtr) {
			out.write(' ');
			out.write(atr.toString().getBytes("UTF-8"));
		}
		if (subObjects.size() > 0) {
			out.write('>');
			out.write('\n');
			for (XmlObject sub : subObjects) {
				sub.write(out, iLevel + 1);
			}
			for (int i = 0; i < (iLevel * 2); i++) {
				out.write(' ');
			}
			out.write('<');
			out.write('/');
			out.write(sName.getBytes());
		} else {
			if (sName.startsWith("&") || sName.startsWith("?")) {
				out.write(sName.charAt(0));
			} else {
				out.write('/');
			}
		}
		out.write('>');
		out.write('\n');
	}

	public boolean getAttributeAsBoolean(String psAttributeName) {
		String sAttribute = getAttribute(psAttributeName);
		if (sAttribute.equalsIgnoreCase("true") || sAttribute.equals("1")) {
			return true;
		}
		return false;
	}
}
