package de.brod.gui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import de.brod.xml.XmlObject;

public class StateHandler {
	class SaveThread extends Thread {
		@Override
		public void run() {
			try {
				System.out.println("Save " + file.getAbsolutePath());
				FileOutputStream out = new FileOutputStream(file);
				out.write(xmlState.toString().getBytes());
				out.close();
			} catch (IOException e) {
				// invalid file
				e.printStackTrace();
			}
		}
	}

	private XmlObject xmlState;
	private XmlObject lastHistoryEntry;
	private XmlObject histories;
	private File file;

	private SaveThread saveThread;
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
		if (xmlState == null) {
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

	public void saveState() {
		if (saveThread != null) {
			try {
				saveThread.join();
			} catch (InterruptedException e) {
				// Interrupted
				e.printStackTrace();
			}
		}
		saveThread = new SaveThread();
		saveThread.start();
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
