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

import android.graphics.Color;

public class GuiColors {

	public static GuiColors EMPTY = new GuiColors(0, 0, 0, 0);

	public static GuiColors BACKGROUND = new GuiColors(255, 26, 100, 50);

	public static GuiColors MENU_GREEN = new GuiColors(192, 0, 32, 0);
	public static GuiColors MENU_BACK = new GuiColors(192, 0, 0, 0);
	public static GuiColors MENU_GRAY = new GuiColors(128, 192, 192, 192);
	public static GuiColors MENUITEM_BACK = new GuiColors(64, 0, 0, 0);

	public static GuiColors TITLE_BACK = new GuiColors(128, 0, 0, 0);
	public static GuiColors TITLE_TEXT = new GuiColors(255, 255, 255, 255);

	public static GuiColors TEXT_BLACK = new GuiColors(255, 0, 0, 0);
	public static GuiColors TEXT_RED = new GuiColors(255, 255, 0, 0);
	public static GuiColors TEXT_WHITE = new GuiColors(255, 255, 255, 255);
	public static GuiColors TEXT_BLACK_TRANSPARENT = new GuiColors(64, 0, 0, 0);

	public static GuiColors ITEM_SELECTED = new GuiColors(255, 220, 220, 220);
	public static GuiColors ITEM_WHITE = new GuiColors(255, 255, 255, 255);
	public static GuiColors ITEM_RED = new GuiColors(255, 220, 128, 96);
	public static GuiColors ITEM_GREEN = new GuiColors(255, 96, 220, 128);

	public float red, green, blue, alpha;

	private int arbg;

	public static void setBackColor(int pBackColor) {
		BACKGROUND = new GuiColors(Color.alpha(pBackColor),
				Color.red(pBackColor), Color.green(pBackColor),
				Color.blue(pBackColor));
		MENU_GREEN = BACKGROUND.contrast(192, 0.5f);
		MENU_BACK = BACKGROUND.contrast(192, 0.2f);
	}

	public GuiColors contrast(int a, float f) {
		return new GuiColors(a, toInt(red, f), toInt(green, f), toInt(blue, f));
	}

	private int toInt(float col, float f) {
		if (f <= 1) {
			col = col * f;
		} else {
			col += (1 - col) * f / 100f;
		}
		int ret = (int) (col * 255);
		if (ret < 0) {
			ret = 0;
		} else if (ret > 255) {
			ret = 255;
		}
		return ret;
	}

	private GuiColors(int a, int r, int g, int b) {
		red = r / 255f;
		green = g / 255f;
		blue = b / 255f;
		alpha = a / 255f;
		arbg = Color.argb(a, r, g, b);
	}

	public int getColor() {
		return arbg;
	}

	public static void setBackColor(String psColor) {
		setBackColor(Color.argb(255,
				Integer.parseInt(psColor.substring(0, 2), 16),
				Integer.parseInt(psColor.substring(2, 4), 16),
				Integer.parseInt(psColor.substring(4, 6), 16)));
	}

}
