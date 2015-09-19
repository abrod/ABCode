package de.brod.cardmaniac.game;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.brod.cardmaniac.Card;
import de.brod.cardmaniac.Card52;
import de.brod.cardmaniac.Hand;

public abstract class Patience extends Game {

	protected abstract class PatienceHand extends Hand<Card52> {

		public PatienceHand(float x1, float y1, float x2, float y2,
				String psText, int pMax) {
			super(x1, y1, x2, y2, psText, pMax);
		}

		public abstract void actionDown(Card card, List<Card> lst);
	}

	public boolean isNextCard(Card52 cTop, Card52 cBottom, boolean sameColor) {
		// check the values
		return false;
	}

	protected List<PatienceHand>	hands	= new ArrayList<PatienceHand>();

	@Override
	public Collection<? extends Hand> getHands() {
		return hands;
	}

	@Override
	public List<Card> actionDown(Card card) {
		List<Card> lst = new ArrayList<Card>();
		Hand cardHand = card.getHand();
		if (cardHand instanceof PatienceHand) {
			PatienceHand hand = (PatienceHand) cardHand;
			hand.actionDown(card, lst);
		}
		return lst;
	}

	@Override
	public void actionUp(List<Card> lstActionCards, Hand handTo) {
		for (Card card : lstActionCards) {
			handTo.addCard(card);
		}
	}

}
