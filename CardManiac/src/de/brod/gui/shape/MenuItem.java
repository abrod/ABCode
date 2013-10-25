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
package de.brod.gui.shape;

import android.graphics.Color;

public class MenuItem extends Rectangle {

	private String text;
	private Text textMenu;
	private float fontHeight;

	public MenuItem(float px, float py, float width, float height, String sText) {
		super(px, py, width, height);
		setColor(Color.argb(64, 0, 0, 0));
		fontHeight = height * 0.7f;
		textMenu = Text.createText(sText, px, py + (height - fontHeight) / 2,
				fontHeight);
		add(textMenu);
		text = sText;
	}

	public String getText() {
		return text;
	}

	public float getTextWidth() {
		return textMenu.getTextWdith() + Button.height * 1.4f;
	}

	@Override
	public void setDimension(float px, float py, float width, float height) {
		super.setDimension(px, py, width, height);
		textMenu.setPosition(px + Button.height / 2, py + (height - fontHeight)
				/ 2);
	}

}
