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
package de.brod.cm;

import android.graphics.Color;
import de.brod.gui.shape.Container;
import de.brod.gui.shape.Sprite;
import de.brod.gui.shape.Text;
import de.brod.xml.XmlObject;

public abstract class CardContainer {
	private XmlObject settings = null;
	private int id;
	protected float[] pos;
	protected boolean bLandScape;
	private Container textContainer;
	private TextAlign _textAlign = TextAlign.BOTTOM;

	public CardContainer(int piId, float px1, float py1, float px2, float py2) {
		id = piId;
		float x1 = Card.getX(px1);
		float y1 = Card.getY(py1);
		float x2 = Card.getX(px2);
		float y2 = Card.getY(py2);
		pos = new float[] { x1, y1, x2 - x1, y2 - y1 };
		bLandScape = Math.abs(pos[2]) > Math.abs(pos[3]);
	}

	public void addAllSpritesTo(Sprite sprite) {
		if (textContainer != null) {
			sprite.add(textContainer);
		}
	}

	public abstract void clear();

	public int getId() {
		return id;
	}

	public abstract String getName();

	public XmlObject getSettings() {
		if (settings == null) {
			settings = new XmlObject("Settings");
		}
		return settings;
	}

	public void loadState(XmlObject xmlHand) {
		XmlObject[] arrSettings = xmlHand.getObjects("Settings");
		if (arrSettings.length > 0) {
			settings = arrSettings[0];
		} else {
			settings = null;
		}
	}

	public abstract void organize();

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

	public void initText(TextAlign pAlign) {
		if (textContainer == null) {
			textContainer = new Container();
		} else {
			textContainer.clear();
		}
		_textAlign = pAlign;
	}

	public Container setText(String psText) {

		if (psText == null) {
			if (textContainer != null) {
				textContainer.clear();
			}
		} else {
			if (textContainer == null) {
				initText(_textAlign);
			} else {
				textContainer.clear();
			}
			if (psText.length() > 0) {
				float fTitleHeight = Card.getCardHeight() / 4;
				Text text;
				int ia=0;
				if (_textAlign.equals(TextAlign.TOP)) {
					text = Text.createText(psText, pos[0]+pos[2]/2, pos[1] + pos[3]
							+ Card.getCardHeight() / 2,
							fTitleHeight);
					ia=2;
				} else if (_textAlign.equals(TextAlign.LEFT)) {
					text = Text.createText(psText, pos[0]-Card.getCardWidth()/2, pos[1] + pos[3]/2
										   - fTitleHeight/2,
										   fTitleHeight);
					ia=1;
				} else if (_textAlign.equals(TextAlign.RIGHT)) {
					text = Text.createText(psText, pos[0]+pos[2]+Card.getCardWidth()/2, pos[1] + pos[3]/2
							- fTitleHeight/2,
							fTitleHeight);
				} else {
					// bottom
					text = Text.createText(psText, pos[0]+pos[2]/2, pos[1] + pos[3]
							- Card.getCardHeight() / 2 - fTitleHeight,
							fTitleHeight);
					ia=2;
				}

				if (ia==1){
					text.setPosition(text.getX()-text.getTextWdith(),
					  text.getY());
				}
				if (ia==2){
					text.setPosition(text.getX()-text.getTextWdith()/2,
									 text.getY());
				}
				text.setMoveable(false);
				text.setColor(Color.WHITE);
				textContainer.add(text);
			}
		}
		return textContainer;
	}

}
