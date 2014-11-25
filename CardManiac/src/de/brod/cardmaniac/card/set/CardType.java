package de.brod.cardmaniac.card.set;

import android.graphics.Bitmap;

public abstract class CardType {

	private int		_iCardsCount;
	private float	_cardWidth;
	private float	_cardHeight;

	public CardType(int piCardsCount) {
		_iCardsCount = piCardsCount;
		_cardWidth = 2f / _iCardsCount;
		_cardHeight = _cardWidth * 4f / 3f;
	}

	public abstract Bitmap createBitmap();

	abstract int countX();

	abstract int countY();

	public float getX(float pfX) {
		float x = pfX / (_iCardsCount - 1) * (2 - _cardWidth)
				- (1 - _cardWidth / 2);
		return x;
	}

	public float getY(float pfY) {
		float y = pfY / (_iCardsCount - 1) * (2 - _cardHeight)
				- (1 - _cardHeight / 2);
		return y;
	}

	public float getCardWidth() {
		return _cardWidth;
	}

	public float getCardHeight() {
		return _cardHeight;
	}

}
