package de.brod.cardmaniac.game;

import de.brod.cardmaniac.Card52;
import de.brod.cardmaniac.Cards52.CardValue;

public abstract class Patience extends Game<Card52> {

	private static final CardValue[] order = { CardValue.cA, CardValue.c2, CardValue.c3, CardValue.c4, CardValue.c5,
			CardValue.c6, CardValue.c7, CardValue.c8, CardValue.c9, CardValue.c10, CardValue.cJ, CardValue.cQ,
			CardValue.cK };

	public boolean isNextCard(Card52 cTop, Card52 cBottom, boolean sameColor) {
		if (cTop == null) {
			if (sameColor) {
				return getOrder(cBottom) == 0;
			}
			return true;
		}
		if (getOrder(cTop) + 1 != getOrder(cBottom)) {
			return false;
		}
		if (sameColor) {
			return cTop.getColor().equals(cBottom.getColor());
		}
		if (cTop.isRed() == cBottom.isRed()) {
			return false;
		}
		return true;
	}

	protected int getOrder(Card52 cTop) {
		CardValue cardValue = cTop.getCardValue();
		for (int i = 0; i < order.length; i++) {
			if (order[i].equals(cardValue)) {
				return i;
			}
		}
		return -1;
	}

}
