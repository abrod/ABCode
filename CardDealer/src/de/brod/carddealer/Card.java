package de.brod.carddealer;

import de.brod.opengl.Rectangle;

public class Card {

	private Rectangle rect;

	public Card(Rectangle rect) {
		this.rect = rect;
	}

	public Rectangle getRectangle() {
		return rect;
	}

}
