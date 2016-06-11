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
		rect.moveTo(x, y);
	}

	public void setCardRow(CardRow cardRow) {
		if (cardRow != null) {
			cardRow.removeCard(this);
		}
		this.cardRow = cardRow;

	}

}
