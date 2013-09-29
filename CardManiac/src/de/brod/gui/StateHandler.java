package de.brod.gui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import de.brod.xml.XmlObject;

public class StateHandler {

	private void saveState() {
		try {
			saveXml(file, xmlState);
			if (lastHistoryEntry != null) {
				File f = getHistoryFile();
				if (f != null) {
					saveXml(f, lastHistoryEntry);
				}
			}

		} catch (IOException e) {
			// invalid file
			e.printStackTrace();
		}
	}

	private void saveXml(File pFile, XmlObject pXml) throws IOException {
		System.out.println("Save File " + pFile.getAbsolutePath());
		FileOutputStream out = new FileOutputStream(pFile);
		String sXml = pXml.toString();
		out.write(sXml.getBytes());
		out.close();
	}

	private XmlObject xmlState;
	private XmlObject lastHistoryEntry;
	private File file;

	public StateHandler(File pFile) {
		xmlState = null;
		file = pFile;
		try {
			xmlState = loadXml(file);
		} catch (IOException e) {
			// invalid file ... so clear
			e.printStackTrace();
		}
		if (xmlState == null || !xmlState.getName().equals("State")) {
			xmlState = new XmlObject("<State />");
		}
		setValues();
	}

	private XmlObject loadXml(File pFile) throws IOException {
		System.out.println("load File " + pFile.getAbsolutePath());
		FileInputStream pxStream = new FileInputStream(pFile);
		XmlObject ret = new XmlObject(pxStream);
		pxStream.close();
		return ret;
	}

	public void addHistory(XmlObject historyEntry) {
		if (lastHistoryEntry == null
				|| !lastHistoryEntry.toString().equals(historyEntry.toString())) {
			int iCounter = xmlState.getAttributeAsInt("counter") + 1;
			xmlState.setAttribute("counter", iCounter);
			lastHistoryEntry = historyEntry;
			saveState();
		}
	}

	public void clear() {
		xmlState = new XmlObject("<State />");
		setValues();
	}

	public XmlObject createEmptyHistoryEntry() {
		XmlObject history = new XmlObject("History");
		return history;
	}

	public XmlObject getLastHistoryEntry() {
		return lastHistoryEntry;
	}

	private void setValues() {
		File f = getHistoryFile();
		lastHistoryEntry = null;
		if (f != null && f.exists()) {
			try {
				lastHistoryEntry = loadXml(f);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private File getHistoryFile() {
		int iCounter = xmlState.getAttributeAsInt("counter");
		if (iCounter > 0) {
			File f = new File(file.getParent(), file.getName() + ".h"
					+ iCounter);
			return f;
		}
		return null;
	}

	public void undo() {
		int iCounter = xmlState.getAttributeAsInt("counter") - 1;
		if (iCounter > 0) {
			xmlState.setAttribute("counter", iCounter);
			setValues();
			saveState();
		} else {
			clear();
		}
	}
}
