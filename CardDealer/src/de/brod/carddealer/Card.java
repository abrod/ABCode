package de.brod.carddealer;

import de.brod.opengl.Rectangle;
import de.brod.opengl.Shape;

public class Card {

	private Rectangle	rect;
	private CardRow		cardRow;

	public Card(Rectangle rect) {
		this.rect = rect;
	}

	public CardRow getCardRow() {
		return cardRow;
	}

	public Shape getRectangle() {
		return rect;
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
