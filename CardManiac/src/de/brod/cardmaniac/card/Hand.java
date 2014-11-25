package de.brod.cardmaniac.card;

import android.graphics.Color;
import de.brod.cardmaniac.card.set.CardType;
import de.brod.opengl.Rect;

public class Hand<CARDTYPE extends CardType> {

	private Rect	_rect;
	private int		_iCount;
	private int		_iCountVisible;
	private float[]	_pX, _pY;

	public Hand(CardType pCardType, float x1, float y1, float x2, float y2,
			float border, int piCount, int iCountVisible) {
		_iCount = piCount;
		_iCountVisible = iCountVisible;
		_pX = new float[] {
				x1,
				x2 - x1,
				pCardType.getX(Math.min(x1, x2)) - pCardType.getCardWidth() / 2,
				pCardType.getX(Math.max(x1, x2)) + pCardType.getCardWidth() / 2 };
		_pY = new float[] {
				y1,
				y2 - y1,
				pCardType.getY(Math.min(y1, y2)) - pCardType.getCardHeight()
				/ 2,
				pCardType.getY(Math.max(y1, y2)) + pCardType.getCardHeight()
				/ 2 };
		float f = 1f;
		float px = (_pX[2] + _pX[3]) / 2;
		float py = (_pY[2] + _pY[3]) / 2;
		float pwidth = (_pX[3] - _pX[2] + border) * f;
		float pheight = (_pY[3] - _pY[2] + border) * f;
		_rect = new Rect(px, py, pwidth, pheight, true);
		int i = 32;
		_rect.setColor(Color.argb(128, i, i, i));
	}

	public Rect getRect() {
		return _rect;
	}

	public void clearCards() {
		// TODO Auto-generated method stub

	}

}
