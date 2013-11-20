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

import de.brod.gui.GuiColors;
import de.brod.gui.IDialogAction;

public class MenuItem extends Rectangle {

	private IDialogAction text;
	private Text textMenu;
	private float fontHeight;

	public MenuItem(float px, float py, float width, float height,
			IDialogAction sText) {
		super(px, py, width, height);
		setColor(GuiColors.MENUITEM_BACK);
		fontHeight = height * 0.7f;
		textMenu = Text.createText(sText.getName(), px, py
				+ (height - fontHeight) / 2, fontHeight);
		add(textMenu);
		text = sText;
	}

	public IDialogAction getText() {
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
