package de.brod.cardmaniac.game;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.brod.cardmaniac.Card;
import de.brod.cardmaniac.Card52;
import de.brod.cardmaniac.Cards52;
import de.brod.cardmaniac.Hand;

public class Solitair extends Game {

	List<Hand>	hands	= new ArrayList<Hand>();

	@Override
	public void init(float wd, float hg, int width, int height) {

		Cards52 cards52 = new Cards52();
		List<Card52> create52Cards = cards52.create52Cards();
		for (int i = 0; i < 8; i++) {
			if (i < 4) {
				hands.add(new Hand(i, 4, i, 4, null, 52));
			} else {
				hands.add(new Hand(i, 4, i, 4, "A", 52));
			}
			hands.add(new Hand(i, 3, i, 0, null, 12));
		}

		for (int i = 0; i < create52Cards.size(); i++) {
			Card52 card = create52Cards.get(i);
			hands.get((i * 2 + 1) % hands.size()).addCard(card);
		}

		float wdButton = 1 / 2f * 2;
		float hgButton = 1 / 4f;
		float x = (wd - wdButton) / 2f;
		float y = (hg - hgButton) / 2f;
		createGuiButton(x, y, wdButton, hgButton, "Show");

	}

	@Override
	public Collection<? extends Hand> getHands() {
		return hands;
	}

	@Override
	public List<Card> actionDown(Card card) {
		Hand hand = card.getHand();
		List<Card> lst = new ArrayList<Card>();
		boolean bAdd = false;
		for (Card card2 : hand.getCards()) {
			if (card2.equals(card)) {
				bAdd = true;
			}
			if (bAdd) {
				lst.add(card2);
			}
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
