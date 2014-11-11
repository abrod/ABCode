package de.brod.cardmaniac.games;

import java.util.List;

import de.brod.cardmaniac.cards.Card;
import de.brod.cardmaniac.cards.Hand;

public class FreeCell extends Game {

	private Hand[][]	hands;

	@Override
	void fillCards(List<Card> plstCards) {
		fill52Cards(plstCards);
	}

	@Override
	public List<Hand> initHands() {
		hands = new Hand[8][2];
		float maxY = getMaxY();
		float d = 0.1f;
		for (int i = 0; i < 8; i++) {
			if (i < 4) {
				hands[i][0] = addHand(i - d, maxY, i - d, maxY, 1, false, 999);
			} else {
				hands[i][0] = addHand(i + d, maxY, i + d, maxY, 1, false, 999);
			}
			hands[i][1] = addHand(i, maxY - 1 - d * 2, i, 0, 10, false, 999);
		}
		return getHands();
	}

	@Override
	public void newGame(List<Card> cards) {
		int i = 0;
		for (int j2 = 0; j2 < cards.size(); j2++) {
			Card card = cards.get(j2);
			card.moveTo(hands[i][1]);
			i = (i + 1) % 8;
		}
	}

	@Override
	public void mouseClick(Card card, List<Card> plstSelected) {
		boolean bAdd = false;
		for (Card c : card.getHand().getCards()) {
			if (c.equals(card)) {
				bAdd = true;
			}
			if (bAdd) {
				plstSelected.add(c);
			}
		}
	}

	@Override
	public boolean playCard(List<Card> plstSelected, Card cardTo, Hand handTo) {
		for (Card card : plstSelected) {
			card.moveTo(handTo);
		}
		return true;
	}

	@Override
	public ITurn getNextTurn() {
		// TODO Auto-generated method stub
		return new ITurn() {

			@Override
			public void calculateNextMove() {
				// check the stack (get min value)
				System.out.println("calculateNextMove");
				for (int i = 4; i < 8; i++) {
					Card lastCard = hands[i][0].getLastCard();
					if (lastCard == null) {

					}
				}
			}

			@Override
			public boolean hasMoreMoves() {
				// TODO Auto-generated method stub
				System.out.println("MoreMoves=" + false);
				return false;
			}

		};
	}
}
