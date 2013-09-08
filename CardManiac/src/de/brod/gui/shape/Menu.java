package de.brod.gui.shape;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Color;

public class Menu extends Sprite {

	private Rectangle back;
	private List<MenuItem> lstMenuItems = new ArrayList<MenuItem>();

	public void addItem(String psText) {
		if (size() == 0) {
			back = new Rectangle(-Button.maxWidth, -Button.maxHeight,
					Button.maxWidth * 2, Button.maxHeight * 2);
			back.setColor(Color.argb(128, 0, 0, 0));
			add(back);
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
		float border = fTitleHeight / 15;
		for (int i = 0; i < lstMenuItems.size(); i++) {
			MenuItem menuItem = lstMenuItems.get(i);
			menuItem.setDimension(Button.maxWidth - maxWidth - border,
					Button.maxHeight - (border + fTitleHeight) * (2 + i),
					maxWidth, fTitleHeight);
		}
		plstMenuItems.clear();
		plstMenuItems.addAll(lstMenuItems);
	}
}
