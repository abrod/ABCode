package de.brod.carddealer;

import de.brod.opengl.Rectangle;
import de.brod.opengl.Shape;

public class Card {

	private Rectangle	rect;
	private CardRow		cardRow;
	private final int	color;
	private final int	value;

	public Card(Rectangle rect, int value, int color) {
		this.rect = rect;
		this.color = color;
		this.value = value;
	}

	public CardRow getCardRow() {
		return cardRow;
	}

	public int getColor() {
		return color;
	}

	public Shape getRectangle() {
		return rect;
	}

	public int getValue() {
		return value;
	}

	public void moveTo(float x, float y) {
		rect.setPosition(x, y, 0);
	}

	public void setCardRow(CardRow cardRownew) {
		if (this.cardRow != null) {
			this.cardRow.removeCard(this);
		}
		this.cardRow = cardRownew;

	}

}
