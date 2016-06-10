package de.brod.carddealer;

import de.brod.opengl.Rectangle;
import de.brod.opengl.Shape;

public class Card {

	private Rectangle rect;

	public Card(Rectangle rect) {
		this.rect = rect;
	}

	public Shape getRectangle() {
		return rect;
	}

}
