package de.brod.cm.game;

import java.util.List;

import de.brod.cm.Buttons;
import de.brod.cm.Card;
import de.brod.cm.Card.Colors;
import de.brod.cm.Card.Values;
import de.brod.cm.CardManiacView;
import de.brod.cm.Hand;
import de.brod.gui.IAction;
import de.brod.gui.shape.Button;
import de.brod.xml.XmlObject;

public class MauMau extends Game {

	private Buttons buttons;
	private Button skipButton;

	public MauMau(CardManiacView pCardManiacView) {
		super(pCardManiacView);
	}

	@Override
	public IAction getNextAction() {

		if (isFinished()) {
			return null;
		}
		final Hand h0 = get(0);
		final Hand h5 = get(5);
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
		final XmlObject settings = getSettings();
		final int iPlayer = settings.getAttributeAsInt("player");

		if (iPlayer > 0) {
			return new IAction() {

				@Override
				public void action() {
					Hand hand = get(iPlayer);
					// if card could not be played
					if (!playCard(hand)) {
						// draw a card
						Card lastCard = h0.getLastCard();
						if (lastCard != null) {
							lastCard.moveTo(hand);
						}
						h0.organize();
						// and try to play again
						playCard(hand);
					}
					hand.organize();
					// set the next player
					settings.setAttribute("player", (iPlayer + 1) % 4);
					settings.setAttribute("drawCard", true);
				}

				private boolean playCard(Hand hand) {
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
						return true;
					}
					return false;
				}
			};
		}
		// TODO Auto-generated method stub
		return null;
	}

	private boolean isFinished() {
		for (int i = 1; i <= 4; i++) {
			if (get(i).getCardCount() == 0) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void initNewCards() {
		Hand h0 = get(0);
		h0.create32Cards();
		for (int i = 1; i <= 4; i++) {
			for (int j = 0; j < 6; j++) {
				h0.getLastCard().moveTo(get(i));
			}
		}
		h0.getLastCard().moveTo(get(5));

		XmlObject settings = getSettings();
		settings.setAttribute("player", 0);
		settings.setAttribute("drawCard", true);
	}

	@Override
	public void initHands(boolean bLandscape) {

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
		add(new Hand(0, 2, y2, 3, y2, 16));
		// add the players
		add(new Hand(1, left - 1, top, left - 1, Card.maxCardY - top, maxCount));
		add(new Hand(2, left, 0, right, 0, maxCount));
		add(new Hand(3, right + 1, top, right + 1, Card.maxCardY - top,
				maxCount));
		add(new Hand(4, left, Card.maxCardY, right, Card.maxCardY, maxCount));

		// add the stacks
		add(new Hand(5, 4, y2, 5, y2, 16));

		for (int i = 0; i < size(); i++) {
			Hand hand = get(i);
			hand.setCenter(i > 0 && i < 5);
			if (i < 4) {
				hand.setAngle(180);
			} else {
				hand.setAngle(0);
			}
		}

		// add a ButtonContainer
		buttons = new Buttons(99);
		IAction action = new IAction() {

			@Override
			public void action() {
				XmlObject settings = getSettings();
				int iPlayer = settings.getAttributeAsInt("player");
				if (iPlayer == 0 && !settings.getAttributeAsBoolean("drawCard")) {
					settings.setAttribute("player", iPlayer + 1);
					settings.setAttribute("drawCard", true);
				}
			}
		};
		skipButton = Button.Type.no.createButton(0,
				Card.getY(Card.maxCardY * 3 / 4), action);
		buttons.add(skipButton);
		add(buttons);

	}

	@Override
	public boolean mouseUp(List<Card> pLstMoves, Hand handTo) {
		XmlObject settings = getSettings();
		int iPlayer = settings.getAttributeAsInt("player");
		if (iPlayer != 0) {
			// it's not your turn
			return false;
		}
		Card selectedCard = pLstMoves.get(0);
		Hand selectedHand = selectedCard.getHand();
		int hSelectedId = selectedHand.getId();
		if (selectedHand == handTo) {
			if (hSelectedId == 0 && settings.getAttributeAsBoolean("drawCard")) {
				// draw a card to hand (from stack)
				handTo = get(4);
			} else {
				return false;
			}
		}
		if (handTo == null) {
			// ignore this
			return false;
		}
		int hToId = handTo.getId();
		// draw a card or play a card
		if (hSelectedId == 0 && hToId == 4) {
			// draw a card
			if (settings.getAttributeAsBoolean("drawCard")) {
				settings.setAttribute("drawCard", false);
			} else {
				return false;
			}
		} else if (hSelectedId == 4 && hToId == 5) {
			// play a card
			if (!matchesStack(selectedCard)) {
				return false;
			}
			settings.setAttribute("player", iPlayer + 1);
			settings.setAttribute("drawCard", true);
		} else {
			// no valid move
			return false;
		}
		selectedCard.moveTo(handTo);
		return true;
	}

	@Override
	public void prepareUpdate() {
		XmlObject settings = getSettings();
		if (settings.getAttributeAsInt("player") == 0) {
			skipButton.setEnabled(!settings.getAttributeAsBoolean("drawCard"));
		} else {
			skipButton.setEnabled(false);
		}
	}

	private XmlObject getSettings() {
		return buttons.getSettings();
	}

	private boolean matchesStack(Card card) {

		Card lastCard = get(5).getLastCard();
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
		if (isFinished()) {
			plstMoves.clear();
		}
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
