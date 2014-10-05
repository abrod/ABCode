package de.brod.cardmaniac.table;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.brod.opengl.Rect;

public class Hand {

	private float[]		_pX, _pY;
	private List<Card>	_lstCard	= new ArrayList<Card>();
	private boolean		_dirty;
	private int			_iCount;
	private Rect		_rect;
	private int			_iCountVisible;
	private boolean		_center		= false;

	public Hand(Deck pDeck, float x1, float y1, float x2, float y2,
			float border, int piCount, int iCountVisible) {
		_iCount = piCount;
		_iCountVisible = iCountVisible;
		_pX = new float[] { x1, x2 - x1,
				pDeck.getX(Math.min(x1, x2)) - pDeck.getCardWidth() / 2,
				pDeck.getX(Math.max(x1, x2)) + pDeck.getCardWidth() / 2 };
		_pY = new float[] { y1, y2 - y1,
				pDeck.getY(Math.min(y1, y2)) - pDeck.getCardHeight() / 2,
				pDeck.getY(Math.max(y1, y2)) + pDeck.getCardHeight() / 2 };
		float f = 1f;
		float px = (_pX[2] + _pX[3]) / 2;
		float py = (_pY[2] + _pY[3]) / 2;
		float pwidth = (_pX[3] - _pX[2] + border) * f;
		float pheight = (_pY[3] - _pY[2] + border) * f;
		_rect = new Rect(px, py, pwidth, pheight, true);
	}

	public Hand(Deck pDeck, float x1, float y1, float x2, float y2,
			int piCount, int iCountVisible) {
		this(pDeck, x1, y1, x2, y2, 0, piCount, iCountVisible);
	}

	public void addCard(Card card) {
		Hand h = card.getHand();
		if (h != null) {
			h.removeCard(card);
		}
		card.setHand(this);
		_lstCard.add(card);
		setVisible();
		_dirty = true;
	}

	public List<Card> getCards() {
		return _lstCard;
	}

	public int getCardsCount() {
		return _lstCard.size();
	}

	public int getCountVisible() {
		return _iCountVisible;
	}

	public Card getLastCard() {
		int size = _lstCard.size();
		if (size > 0) {
			return _lstCard.get(size - 1);
		}
		return null;
	}

	public Rect getRect() {
		return _rect;
	}

	public boolean isDirty() {
		return _dirty;
	}

	public void organize() {
		int count = _lstCard.size();
		if (count > 0) {
			float max = Math.max(Math.max(_iCount, count) - 1, 1);
			float iOffs = 0;
			if (_center) {
				if (count < _iCount) {
					iOffs = (_iCount - count) / 2f;
				}
			}
			for (int i = 0; i < _lstCard.size(); i++) {
				float x = _pX[0] + _pX[1] * iOffs / max;
				float y = _pY[0] + _pY[1] * iOffs / max;
				Card card = _lstCard.get(i);
				card.setPosition(x, y, i);
				iOffs++;
			}
		}
		_dirty = false;
	}

	private void removeCard(Card card) {
		_lstCard.remove(card);
		setVisible();
		_dirty = true;
	}

	public void setCenter(boolean b) {
		_center = b;
	}

	public void setCountVisible(int i) {
		_iCountVisible = i;
	}

	public void setDirty(boolean dirty) {
		_dirty = dirty;
	}

	protected void setVisible() {
		int iC = getCountVisible() - _lstCard.size();
		for (int i = 0; i < _lstCard.size(); i++) {
			_lstCard.get(i).setVisible(iC >= 0);
			iC++;
		}
	}

	public void shuffleCards() {
		Collections.shuffle(_lstCard);
	}

	public boolean touches(float eventX, float eventY) {
		return _rect.touches(eventX, eventY);
		// if (eventX < _pX[2] || eventX > _pX[3]) {
		// return false;
		// }
		// if (eventY < _pY[2] || eventY > _pY[3]) {
		// return false;
		// }
		// return true;
	}
}
