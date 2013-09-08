package de.brod.gui.shape;

import android.graphics.Color;

public class MenuItem extends Rectangle {

	private String text;
	private Text textMenu;
	private float fontHeight;

	public MenuItem(float px, float py, float width, float height, String sText) {
		super(px, py, width, height);
		setColor(Color.argb(194, 0, 0, 0));
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
