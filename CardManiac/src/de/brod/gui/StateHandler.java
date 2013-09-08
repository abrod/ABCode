package de.brod.gui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import de.brod.xml.XmlObject;

public class StateHandler {

	private void saveState() {
		try {
			System.out.println("Save " + file.getAbsolutePath());
			FileOutputStream out = new FileOutputStream(file);
			String sXml = xmlState.toString();
			out.write(sXml.getBytes());
			out.close();
			System.out.println(sXml);
		} catch (IOException e) {
			// invalid file
			e.printStackTrace();
		}
	}

	private XmlObject xmlState;
	private XmlObject lastHistoryEntry;
	private XmlObject histories;
	private File file;

	private int iCounter;

	public StateHandler(File pFile) {
		xmlState = null;
		file = pFile;
		try {
			System.out.println("Load " + file.getAbsolutePath());
			FileInputStream pxStream = new FileInputStream(file);
			xmlState = new XmlObject(pxStream);
			pxStream.close();
		} catch (IOException e) {
			// invalid file ... so clear
			e.printStackTrace();
		}
		if (xmlState == null || !xmlState.getName().equals("State")) {
			xmlState = new XmlObject("<State />");
		}
		histories = xmlState.getObject("Histories", "root", "true", true);
		setValues();
	}

	public void addHistory(XmlObject historyEntry) {
		if (lastHistoryEntry == null
				|| !lastHistoryEntry.toString().equals(historyEntry.toString())) {
			while (histories.getObjectCount() > iCounter) {
				histories.deleteObject(iCounter);
			}
			histories.addObject(historyEntry);
			iCounter = histories.getObjectCount();

			lastHistoryEntry = historyEntry;
			saveState();
		}
	}

	public void clear() {
		histories.deleteAll();
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
		XmlObject[] history = histories.getObjects();
		iCounter = history.length;
		if (iCounter > 0) {
			lastHistoryEntry = history[iCounter - 1];
		} else {
			lastHistoryEntry = null;
		}
	}
}
