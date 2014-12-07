package de.brod.cardmaniac.card;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Color;
import de.brod.cardmaniac.card.set.CardType;
import de.brod.opengl.Rect;

public class Hand<CARDTYPE extends CardType> {

	private Rect					_rect;
	private int						_iCount;
	private int						_iCountVisible;
	private float[]					_pX, _pY;
	private List<Card<CARDTYPE>>	_lstCards	= new ArrayList<Card<CARDTYPE>>();
	private boolean					_center;
	private boolean					_dirty		= true;

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
		_lstCards.clear();
		_dirty = true;
	}

	public void removeCard(Card<CARDTYPE> pCard) {
		_lstCards.remove(pCard);
		_dirty = true;
	}

	public void addCard(Card<CARDTYPE> pCard) {
		_lstCards.add(pCard);
		_dirty = true;
	}

	public void organize() {
		if (_dirty) {
			int count = _lstCards.size();
			if (count > 0) {
				float max = Math.max(Math.max(_iCount, count) - 1, 1);
				float iOffs = 0;
				if (_center) {
					if (count < _iCount) {
						iOffs = (_iCount - count) / 2f;
					}
				}
				int iVisible = _lstCards.size() - _iCountVisible;
				for (int i = 0; i < _lstCards.size(); i++) {
					float x = _pX[0] + _pX[1] * iOffs / max;
					float y = _pY[0] + _pY[1] * iOffs / max;
					Card<CARDTYPE> card = _lstCards.get(i);
					card.setVisible(i >= iVisible);
					card.cardMoveTo(x, y);
					iOffs++;
				}
			}
		}
		_dirty = false;
	}

	public List<Card<CARDTYPE>> getCards() {
		return _lstCards;
	}

	public boolean isDirty() {
		return _dirty;
	}

	public void setDirty(boolean dirty) {
		_dirty = dirty;
	}
}
