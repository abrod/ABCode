package de.brod.cardmaniac.cards;

import javax.microedition.khronos.opengles.GL10;

import de.brod.opengl.Grid;
import de.brod.opengl.Sprite;

public class CardSet extends Grid<Card> {

	private float	_cardWidth;
	private float	_cardHeight;
	private float[]	_border	= new float[4];

	public CardSet(GL10 gl) {
		super(13, 5, gl, DeckBmp.createBitmap());
		_cardWidth = 2f / 8;
		_cardHeight = _cardWidth * 4 / 3;
		_border[0] = -1 + _cardWidth / 2;//left
		_border[1] = -1 + _cardHeight / 2;// bottom
		_border[2] = (2 - _cardWidth) / 7;// card width
		_border[3] = (2 - _cardHeight) / 7 * 4 / 3;// height
	}

	Sprite<Card> createSprite(Card card, int x, int y, float posX, float posY) {
		Sprite<Card> createSprite = createSprite(x, y, posX, posY, _cardWidth,
				_cardHeight);
		createSprite.setReference(card);
		return createSprite;
	}

	public float getCardWidth() {
		return _cardWidth;
	}

	public float getCardHeight() {
		return _cardHeight;
	}

	float transformX(float x) {
		return _border[0] + _border[2] * x;
	}

	float transformY(float y) {
		return _border[1] + _border[3] * y;
	}

	public float getMaxY() {
		return 7f * 3 / 4;
	}

}
