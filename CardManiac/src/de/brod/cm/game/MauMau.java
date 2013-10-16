package de.brod.cm.game;

import java.util.List;

import de.brod.cm.Card;
import de.brod.cm.Card.Colors;
import de.brod.cm.Card.Values;
import de.brod.cm.CardManiacView;
import de.brod.cm.Hand;
import de.brod.gui.IAction;
import de.brod.xml.XmlObject;

public class MauMau extends Game {

	public MauMau(CardManiacView pCardManiacView) {
		super(pCardManiacView);
	}

	@Override
	public IAction getNextAction() {

		final Hand h0 = hands.get(0);
		final Hand h5 = hands.get(5);
		// move stack
		if (h0.getCardCount() == 0 && h5.getCardCount() > 1) {
			return new IAction() {

				@Override
				public void action() {
					// update stack
					Card[] cards = h5.getCards().toArray(new Card[0]);
					for (int i = 0; i < cards.length - 1; i++) {
						cards[i].moveTo(h0);
					}
					h0.shuffleCards();
					h0.organize();
					h5.organize();
				}
			};
		}
		final XmlObject settings = h0.getSettings();
		final int iPlayer = settings.getAttributeAsInt("player");
		if (iPlayer > 0) {
			return new IAction() {

				@Override
				public void action() {
					Hand hand = hands.get(iPlayer);
					Card cPlay = null;
					for (Card c : hand.getCards()) {
						if (matchesStack(c)) {
							cPlay = c;
							break;
						}
					}
					if (cPlay != null) {
						cPlay.moveTo(h5);
						h5.organize();
					} else {
						Card lastCard = h0.getLastCard();
						if (lastCard != null) {
							lastCard.moveTo(hand);
						}
						h0.organize();
					}
					hand.organize();
					// set the next player
					settings.setAttribute("player", (iPlayer + 1) % 4);
				}
			};
		}
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void initNewCards() {
		Hand h0 = hands.get(0);
		h0.create32Cards();
		for (int i = 1; i <= 4; i++) {
			for (int j = 0; j < 6; j++) {
				h0.getLastCard().moveTo(hands.get(i));
			}
		}
		h0.getLastCard().moveTo(hands.get(5));
	}

	@Override
	public Hand[] initHands(boolean bLandscape) {

		float y2 = Card.maxCardY / 2;

		int left = 1;
		int right = 6;
		int top = 1;
		int maxCount = 8;
		if (bLandscape) {
			left = 0;
			right = 7;
			top = 0;
			maxCount = 10;
		}
		hands.add(new Hand(0, 2, y2, 3, y2, 16));
		// add the players
		hands.add(new Hand(1, left - 1, top, left - 1, Card.maxCardY - top,
				maxCount));
		hands.add(new Hand(2, left, 0, right, 0, maxCount));
		hands.add(new Hand(3, right + 1, top, right + 1, Card.maxCardY - top,
				maxCount));
		hands.add(new Hand(4, left, Card.maxCardY, right, Card.maxCardY,
				maxCount));

		// add the stacks
		hands.add(new Hand(5, 4, y2, 5, y2, 16));

		for (int i = 0; i < hands.size(); i++) {
			Hand hand = hands.get(i);
			hand.setCenter(i > 0 && i < 5);
			if (i < 4) {
				hand.setAngle(180);
			} else {
				hand.setAngle(0);
			}
		}

		return hands.toArray(new Hand[hands.size()]);

	}

	@Override
	public void mouseUp(List<Card> pLstMoves, Hand handTo) {
		Card card = pLstMoves.get(0);
		Hand h0 = card.getHand();
		int hFrom = h0.getId();
		if (handTo == null || h0 == handTo) {
			if (hFrom == 4) {
				handTo = hands.get(5);
			} else if (hFrom == 0) {
				handTo = hands.get(4);
			} else {
				return;
			}
		}
		int hTo = handTo.getId();
		XmlObject settings = hands.get(0).getSettings();
		int iPlayer = settings.getAttributeAsInt("player");
		if (iPlayer == 0) {
			// draw a card or play a card
			if (hFrom == 0 && hTo == 4) {
				// draw a card
				settings.setAttribute("player", iPlayer + 1);
			} else if (hFrom == 4 && hTo == 5) {
				// play a card
				if (!matchesStack(card)) {
					return;
				}
				settings.setAttribute("player", iPlayer + 1);
			} else {
				return;
			}
		} else {
			return;
		}
		card.moveTo(handTo);
	}

	private boolean matchesStack(Card card) {

		Card lastCard = hands.get(5).getLastCard();
		Values lcValue = lastCard.getValue();
		Values cValue = card.getValue();

		// jack is the joker
		if (cValue.equals(Values.Jack)) {
			return true;
		}

		if (lcValue.equals(cValue)) {
			return true;
		}
		if (lastCard.getColor().equals(card.getColor())) {
			return true;
		}

		return false;
	}

	@Override
	public void mouseDown(List<Card> plstMoves) {
	}

	@Override
	protected void createTitleCards(Hand hand) {
		hand.createCard(Values.C7, Colors.Clubs);
		hand.createCard(Values.C8, Colors.Spades);
		hand.createCard(Values.C7, Colors.Diamonds);
		hand.createCard(Values.C8, Colors.Hearts);
	}

	@Override
	public boolean hasHistory() {
		return true;
	}

}
