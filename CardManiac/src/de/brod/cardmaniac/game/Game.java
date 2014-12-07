package de.brod.cardmaniac.game;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import de.brod.cardmaniac.card.Card;
import de.brod.cardmaniac.card.Hand;
import de.brod.cardmaniac.card.set.CardSet;
import de.brod.cardmaniac.card.set.CardType;

public abstract class Game<CARDTYPE extends CardType> {

	private CardSet<CARDTYPE>	_cardSet;
	private List<Hand<?>>		_lstHands	= new ArrayList<Hand<?>>();

	public void initCardSet(GL10 gl) {
		// init the CardSet
		_cardSet = (new CardSet<CARDTYPE>(getCardType(), gl));
	}

	protected abstract CARDTYPE getCardType();

	public void initHands() {
		_lstHands.clear();
		createHands();
	}

	protected abstract void createHands();

	public Hand<CARDTYPE> createHand(float x1, float y1, float x2, float y2,
			float border, int piCount, int iCountVisible) {
		Hand<CARDTYPE> hand = new Hand<CARDTYPE>(getCardType(), x1, y1, x2, y2,
				border, piCount, iCountVisible);
		_lstHands.add(hand);
		return hand;
	}

	public List<Hand<?>> getHands() {
		return _lstHands;
	}

	public void clearHands() {
		for (Hand<?> hand : _lstHands) {
			hand.clearCards();
		}

	}

	public abstract void newGame();

	public CardSet<CARDTYPE> getCardSet() {
		return _cardSet;
	}

	@SuppressWarnings("unchecked")
	public Hand<CARDTYPE>[] newHands(int i) {
		return new Hand[i];
	}

	public List<Card<?>> actionDown(Card<?> card) {
		List<Card<?>> lst = new ArrayList<Card<?>>();
		lst.add(card);
		return lst;
	}

	public void actionUp(List<Card<CARDTYPE>> listMovingCards,
			Card<CARDTYPE> card, Hand<CARDTYPE> hand) {
		if (hand != null) {
			for (Card<CARDTYPE> c : listMovingCards) {
				c.moveTo(hand);
			}
		}
	}

	public void organize() {
		for (Hand<?> hand : _lstHands) {
			hand.organize();
		}
	}

}
