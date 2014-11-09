package de.brod.cardmaniac.cards;

import java.util.ArrayList;
import java.util.List;

import de.brod.cardmaniac.cards.PlayingCard.CardColor;
import de.brod.cardmaniac.cards.PlayingCard.CardValue;
import de.brod.opengl.Rect;

public class Hand {

	private List<Card>	lst	= new ArrayList<Card>();
	private CardSet		_cardSet;
	private float		_x;
	private float		_y;
	private float		_width;
	private float		_height;
	private boolean		_dirty;
	private int			_iMaxCardSize;
	private Rect		rect;
	private int			_iCountVisible;

	public Hand(CardSet cardSet, float x, float y, float width, float height,
			int piMaxCardSize, boolean pbSmallBorder, int piCountVisible) {
		this._cardSet = cardSet;
		_iCountVisible = piCountVisible;
		float w = cardSet.getCardWidth() / 2;
		float h = cardSet.getCardHeight() / 2;
		float[] coord = { _cardSet.transformX(x) - w,
				_cardSet.transformY(y + height) - h,
				_cardSet.transformX(x + width) + w, _cardSet.transformY(y) + h };
		float fSmallBorder = pbSmallBorder ? 0.05f : 0;
		rect = new Rect((coord[0] + coord[2]) / 2, (coord[1] + coord[3]) / 2,
				coord[2] - coord[0] + fSmallBorder, coord[3] - coord[1]
						+ fSmallBorder, true);
		rect.setColor(128, 128, 128, 64);
		rect.setDown(pbSmallBorder);

		System.out.print("Hand " + x + " " + y + " -> ");
		for (float f : coord) {
			System.out.print(" " + (Math.round(f * 1000) / 1000f));
		}
		System.out.println("");
		_iMaxCardSize = Math.max(1, piMaxCardSize - 1);
		_x = x;
		_y = y;
		_width = width;
		_height = height;
		_dirty = true;
	}

	public PlayingCard createCard(CardValue value, CardColor color) {
		PlayingCard playingCard = new PlayingCard(_cardSet, value, color);
		playingCard.moveTo(this);
		return playingCard;
	}

	public void remove(Card card) {
		lst.remove(card);
		_dirty = true;
	}

	public void add(Card card) {
		lst.add(card);
		_dirty = true;
	}

	public boolean isDirty() {
		return _dirty;
	}

	public void setDirty(boolean dirty) {
		this._dirty = dirty;
	}

	public void organize() {
		for (int i = 0; i < lst.size(); i++) {
			Card card = lst.get(i);
			float d = i / Math.max(1, Math.max(_iMaxCardSize, lst.size() - 1f));
			card.setVisible(i < _iCountVisible);
			card.setPosition(_x + _width * d, _y + _height * d);
		}
		_dirty = false;
	}

	public Rect getRectangle() {
		return rect;
	}

	public int getCardCount() {
		return lst.size();
	}

	public Card getLastCard() {
		int lastPos = lst.size() - 1;
		if (lastPos >= 0) {
			return lst.get(lastPos);
		}
		return null;
	}

	public List<Card> getCards() {
		return lst;
	}
}
