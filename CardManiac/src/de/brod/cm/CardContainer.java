package de.brod.cm;

import android.graphics.Color;
import de.brod.gui.shape.Sprite;
import de.brod.gui.shape.Text;
import de.brod.xml.XmlObject;

public abstract class CardContainer {
	private XmlObject settings = null;
	private int id;
	protected float[] pos;
	protected boolean bLandScape;
	private Text text;

	public CardContainer(int piId, float px1, float py1, float px2, float py2) {
		id = piId;
		float x1 = Card.getX(px1);
		float y1 = Card.getY(py1);
		float x2 = Card.getX(px2);
		float y2 = Card.getY(py2);
		pos = new float[] { x1, y1, x2 - x1, y2 - y1 };
		bLandScape = Math.abs(pos[2]) > Math.abs(pos[3]);
	}

	public int getId() {
		return id;
	}

	public void loadState(XmlObject xmlHand) {
		XmlObject[] arrSettings = xmlHand.getObjects("Settings");
		if (arrSettings.length > 0) {
			settings = arrSettings[0];
		} else {
			settings = null;
		}
	}

	public void addAllCards(Sprite sprite) {
		if (text != null) {
			sprite.add(text);
		}
	}

	public void saveState(XmlObject xmlHand) {
		XmlObject[] objects = xmlHand.getObjects("Settings");
		if (objects.length == 0 && settings == null) {
			// no settings
		} else {
			// remove settings
			xmlHand.deleteObjects("Settings");
			if (settings != null) {
				xmlHand.addObject(settings);
			}
		}
	}

	public void setText(String psText) {
		if (psText.length() == 0) {
			text = null;
		} else {
			float fTitleHeight = Card.getCardHeight() / 4;
			text = Text.createText(psText, pos[0],
					pos[1] - pos[3] - Card.getCardHeight() / 2 - fTitleHeight,
					fTitleHeight);
			text.setMoveable(false);
			text.setColor(Color.WHITE);
		}
	}

	public XmlObject getSettings() {
		if (settings == null) {
			settings = new XmlObject("Settings");
		}
		return settings;
	}

	public abstract void clear();

	public abstract void organize();

}
