/*
 * ******************************************************************************
 * Copyright (c) 2013 Andreas Brod
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * *****************************************************************************
 */
package de.brod.gui;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import de.brod.tools.IOTools;
import de.brod.xml.XmlObject;

public class StateHandler {

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

	public synchronized void addHistory(XmlObject historyEntry,
			boolean pbSetUndoPoint) {
		historyEntry.setAttribute("undoPoint", "" + pbSetUndoPoint);
		if (lastHistoryEntry == null
				|| !lastHistoryEntry.toString().equals(historyEntry.toString())) {
			int iCounter = xmlState.getAttributeAsInt("counter") + 1;
			xmlState.setAttribute("counter", iCounter);
			if (pbSetUndoPoint) {
				xmlState.setAttribute("maxcounter", iCounter);
			}
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

	public String getAttribute(String psAttributeName) {
		return xmlState.getAttribute(psAttributeName);
	}

	public int getEntriesCount() {
		return xmlState.getAttributeAsInt("maxcounter");
	}

	private File getHistoryFile() {
		int iCounter = xmlState.getAttributeAsInt("counter");
		if (iCounter > 0) {
			String name = file.getName();
			File f = new File(file.getParent(), name.substring(0,
					name.lastIndexOf(".") + 1)
					+ "h" + iCounter + ".xml");
			return f;
		}
		return null;
	}

	public XmlObject getLastHistoryEntry() {
		return lastHistoryEntry;
	}

	public XmlObject getObject(String psElement, String psName) {
		return xmlState.getObject(psElement, "name", psName, true);
	}

	public boolean isEOF() {
		int iCounter = xmlState.getAttributeAsInt("counter");
		int iMaxCounter = xmlState.getAttributeAsInt("maxcounter");
		boolean EOF = iCounter >= iMaxCounter;
		return EOF;
	}

	private XmlObject loadXml(File pFile) throws IOException {
		System.out.println("load File " + pFile.getAbsolutePath());
		InputStream pxStream = new ByteArrayInputStream(IOTools.read(pFile));
		XmlObject ret = new XmlObject(pxStream);
		pxStream.close();
		return ret;
	}

	public void redo() {
		int iCounter = xmlState.getAttributeAsInt("counter");
		int iMaxCounter = xmlState.getAttributeAsInt("maxcounter");
		while (iCounter < iMaxCounter) {
			iCounter++;
			xmlState.setAttribute("counter", iCounter);
			setValues();
			if (lastHistoryEntry == null
					|| !lastHistoryEntry.getAttribute("undoPoint").equals(
							"false")) {
				break;
			}
		}
		saveState();
	}

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
		IOTools.write(pFile, pXml.toString().getBytes());
	}

	public void setAttibute(String psAttributeName, String psAttributeValue) {
		if (xmlState.setAttribute(psAttributeName, psAttributeValue)) {
			saveState();
		}
	}

	public void setObject(String p0, String name, String psName, String psValue) {
		if (getObject(p0, name).setAttribute(psName, psValue)) {
			saveState();
		}
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

	public void undo() {
		int iCounter = xmlState.getAttributeAsInt("counter");
		// go to next undo point
		while (lastHistoryEntry != null
				&& lastHistoryEntry.getAttribute("undoPoint").equals("false")) {
			iCounter--;
			if (iCounter > 0) {
				xmlState.setAttribute("counter", iCounter);
				setValues();
				saveState();
			} else {
				xmlState.setAttribute("counter", 0);
				clear();
				return;
			}
		}

		iCounter--;
		if (iCounter > 0) {
			xmlState.setAttribute("counter", iCounter);
			setValues();
			saveState();
		} else {
			xmlState.setAttribute("counter", 0);
			xmlState.setAttribute("maxcounter", 0);
			clear();
		}
	}
}
