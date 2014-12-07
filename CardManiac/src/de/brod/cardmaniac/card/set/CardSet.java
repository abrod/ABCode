package de.brod.cardmaniac.card.set;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;
import de.brod.opengl.Grid;
import de.brod.opengl.Sprite;

public class CardSet<CARDTYPE extends CardType> {

	private Grid		_grid;
	private CardType	_cardType;

	public CardSet(CardType cardType, GL10 gl) {
		_cardType = cardType;
		Bitmap bitmap = cardType.createBitmap();
		_grid = new Grid(cardType.countX(), cardType.countY(), gl, bitmap);
		bitmap.recycle();
	}

	public Sprite createSprite(int piPosX, int piPosY) {
		Sprite sprite = _grid.createSprite(piPosX, piPosY, 0, 0,
				_cardType.getCardWidth(), _cardType.getCardHeight());
		return sprite;
	}
}
