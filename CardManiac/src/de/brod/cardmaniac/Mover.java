package de.brod.cardmaniac;

import java.util.ArrayList;
import java.util.List;

import de.brod.cardmaniac.card.Card;

public class Mover {

	private List<Card<?>>	_lstMovingCards		= new ArrayList<Card<?>>();
	private List<Card<?>>	_lstSelectedCards	= new ArrayList<Card<?>>();
	private boolean			_MovingCards;

	public void clear() {
		_lstMovingCards.clear();
		setMovingCards(false);
	}

	public void setMovingCards(List<Card<?>> lstMovingCards, float eventX,
			float eventY) {
		for (Card<?> card : _lstMovingCards) {
			card.mouseDown(eventX, eventY);
		}
		_lstMovingCards.addAll(lstMovingCards);
		setMovingCards(_lstMovingCards.size() > 0);
	}

	public boolean moveCards(float eventX, float eventY) {
		if (hasMovingCards()) {
			for (Card<?> card : _lstMovingCards) {
				card.getSprite().moveTo(eventX, eventY);
			}
		}
		clearSelected();
		return hasMovingCards();
	}

	public boolean finish() {
		// TODO Auto-generated method stub
		if (hasMovingCards()) {
			for (Card<?> card : _lstMovingCards) {
				card.getSprite().mouseUp();
			}
			clear();
			return true;
		}
		return false;
	}

	public boolean hasMovingCards() {
		return _MovingCards;
	}

	public void setMovingCards(boolean mMovingCards) {
		this._MovingCards = mMovingCards;
	}

	public boolean containsAsMovingCard(Card<?> card) {
		return _lstMovingCards.contains(card);
	}

	public List<Card<?>> getMovingCards() {
		return _lstMovingCards;
	}

	public void selectMovingCards() {
		clearSelected();
		_lstSelectedCards.addAll(_lstMovingCards);
		for (Card<?> card : _lstSelectedCards) {
			card.setSelected(true);
		}
	}

	public void clearSelected() {
		if (_lstSelectedCards.size() > 0) {
			for (Card<?> card : _lstSelectedCards) {
				card.setSelected(false);
			}
			_lstSelectedCards.clear();
		}
	}

	public boolean hasSelectedCards() {
		return _lstSelectedCards.size() > 0;
	}

	public List<Card<?>> getSelectedCards() {
		return _lstSelectedCards;
	}
}
