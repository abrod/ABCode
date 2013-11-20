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

import java.util.ArrayList;
import java.util.List;

import de.brod.gui.GuiColors;
import de.brod.gui.IDialogAction;

public class Menu extends Sprite {

	private Rectangle back;
	private Frame frame;
	private List<MenuItem> lstMenuItems = new ArrayList<MenuItem>();

	public void addItem(IDialogAction psText) {
		if (size() == 0) {
			back = new Rectangle(-Button.maxWidth, -Button.maxHeight,
					Button.maxWidth * 2, Button.maxHeight * 2);
			back.setColor(GuiColors.MENU_BACK);
			add(back);
			frame = new Frame(-Button.maxWidth, -Button.maxHeight,
					Button.maxWidth * 2, Button.maxHeight * 2);
			frame.setColor(GuiColors.BACKGROUND);
			add(frame);
		}

		float fTitleHeight = Button.height;
		MenuItem menuItem = new MenuItem(0, Button.maxHeight - fTitleHeight
				* (1 + size()), Button.maxWidth, fTitleHeight, psText);
		add(menuItem);
		lstMenuItems.add(menuItem);
	}

	@Override
	public void clear() {
		super.clear();
		lstMenuItems.clear();
	}

	public void finish(List<MenuItem> plstMenuItems) {
		float maxWidth = 0;
		for (MenuItem menuItem : lstMenuItems) {
			maxWidth = Math.max(maxWidth, menuItem.getTextWidth());
		}
		float fTitleHeight = Button.height;
		float border = fTitleHeight / 30;
		float x = Button.maxWidth - maxWidth - border - Frame.border;
		float y = Button.maxHeight - (border + fTitleHeight);
		for (int i = 0; i < lstMenuItems.size(); i++) {
			MenuItem menuItem = lstMenuItems.get(i);
			y -= fTitleHeight;
			menuItem.setDimension(x, y, maxWidth, fTitleHeight);
			y -= border;
		}
		float fTotalTitleHeight = lstMenuItems.size() * (border + fTitleHeight);
		float fBorder = Frame.border / 2;
		frame.setDimension(x - fBorder, y - fBorder, maxWidth + fBorder * 2,
				fTotalTitleHeight + fBorder * 2);
		plstMenuItems.clear();
		plstMenuItems.addAll(lstMenuItems);
	}

	public void activateBack() {
		if (size() == 0) {
			float h = Button.maxHeight;
			float w = Button.maxWidth;
			float titleHeight = Button.height;

			back = new Rectangle(-w, h - titleHeight, w * 2, titleHeight);
			back.setColor(GuiColors.MENU_GREEN);
			add(back);

			if (h > w) {
				back = new Rectangle(-w, -h, w * 2, titleHeight);
				back.setColor(GuiColors.MENU_GREEN);
				add(back);
			}
		}

	}
}
