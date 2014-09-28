package de.brod.cardmaniac.game;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import android.util.Log;
import de.brod.cardmaniac.CardValue;
import de.brod.cardmaniac.table.Card;
import de.brod.cardmaniac.table.Hand;

public abstract class Patience extends Game {

	class CleanUp implements INextMove {

		private Hashtable<Card, Hand> cardMove = new Hashtable<Card, Hand>();
		private List<? extends Hand> lstUpperRight;
		private List<Hand> lstLowerHands;

		CleanUp(List<? extends Hand>... plstLower) {
			lstUpperRight = plstLower[0];
			lstLowerHands = new ArrayList<Hand>();
			for (int i = 1; i < plstLower.length; i++) {
				for (Hand h : plstLower[i]) {
					lstLowerHands.add(h);
				}
			}
		}

		@Override
		public void startMove() {
			for (Entry<Card, Hand> set : cardMove.entrySet()) {
				set.getValue().addCard(set.getKey());
			}
			cardMove.clear();
		}

		@Override
		public boolean hasNext() {
			int iMin = 100;
			HashSet<Hand> hsHands = new HashSet<Hand>();
			for (int i = 0; i < lstUpperRight.size(); i++) {
				Hand hand = lstUpperRight.get(i);
				Card lastCard = hand.getLastCard();
				int value;
				if (lastCard == null) {
					value = -1;
				} else {
					value = getValue(lastCard.getCardValue());
				}
				if (i == 0 || value < iMin) {
					hsHands.clear();
					iMin = value;
					hsHands.add(hand);
				} else if (iMin == value) {
					hsHands.add(hand);
				}
			}

			Iterator<Hand> iterator = hsHands.iterator();
			cardMove.clear();
			for (Hand handLower : lstLowerHands) {
				checkHand(handLower, iMin, hsHands, iterator);
			}
			return cardMove.size() > 0;
		}

		private void checkHand(Hand handLower, int iMin, HashSet<Hand> hsHands,
				Iterator<Hand> iterator) {
			Card lastCard = handLower.getLastCard();
			if (lastCard != null) {
				if (iMin < 0) {
					if (lastCard.getCardValue().equals(CardValue.ass)
							&& iterator.hasNext()) {
						cardMove.put(lastCard, iterator.next());
					}
				} else {
					for (Hand handTop : hsHands) {
						Card card = handTop.getLastCard();
						if (isNext(card, lastCard, false)) {
							cardMove.put(lastCard, handTop);
						}
					}
				}
			}
		}

	}

	boolean isNext(Card card, Card nextCard, boolean pbColorChange) {
		if (nextCard == null) {
			return false;
		}
		if (card == null) {
			if (!pbColorChange) {
				boolean equals = nextCard.getCardValue().equals(CardValue.ass);
				Log.d("IsNext-ass", nextCard + "->" + equals);
				return equals;
			}
		} else {
			int val1 = getValue(card.getCardValue());
			int val2 = getValue(nextCard.getCardValue());
			int o1 = card.getCardColor().ordinal();
			int o2 = nextCard.getCardColor().ordinal();
			if (pbColorChange) {
				if (val1 - 1 != val2) {
					Log.d("IsNext-value", card + " (" + val1 + " - 1) != "
							+ nextCard + " (" + val2 + ")");
					return false;
				}
				int col1 = o1 / 2;
				int col2 = o2 / 2;
				boolean bComp = col1 == col2;
				Log.d("IsNext-colorChange", card + "(" + o1 + "/2=" + col1
						+ ")" + (bComp ? " = " : " != ") + nextCard + "(" + o2
						+ "/2=" + col2 + ")");
				// color should not be the same (colorchange)
				return !bComp;
			} else {
				if (val1 + 1 != val2) {
					Log.d("IsNext-value", card + " (" + val1 + " - 1) != "
							+ nextCard + " (" + val2 + ")");
					return false;
				}
				boolean bComp = o1 == o2;
				Log.d("IsNext-colorSame", card + (bComp ? " = " : " != ")
						+ nextCard);
				return bComp;
			}
		}
		return true;
	}

	private CardValue[] cvs = { CardValue.ass, CardValue.c2, CardValue.c3,
			CardValue.c4, CardValue.c5, CardValue.c6, CardValue.c7,
			CardValue.c8, CardValue.c9, CardValue.c10, CardValue.bube,
			CardValue.dame, CardValue.koenig };

	int getValue(CardValue cardValue) {
		for (int i = 0; i < cvs.length; i++) {
			if (cvs[i].ordinal() == cardValue.ordinal()) {
				return i;
			}
		}
		return -1;
	}
}
