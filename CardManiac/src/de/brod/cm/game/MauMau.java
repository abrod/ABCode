package de.brod.cm.game;

import java.util.List;

import de.brod.cm.Card;
import de.brod.cm.Card.Colors;
import de.brod.cm.Card.Values;
import de.brod.cm.CardManiacView;
import de.brod.cm.Hand;
import de.brod.gui.IAction;

public class MauMau extends Game {

	public MauMau(CardManiacView pCardManiacView) {
		super(pCardManiacView);
	}

	@Override
	public IAction getNextAction() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void initNewCards(Hand[] hands) {
		hands[0].create52Cards();
		for (int i = 1; i <= 4; i++) {
			for (int j = 0; j < 6; j++) {
				hands[0].getLastCard().moveTo(hands[i]);
			}
		}
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
		if (handTo == null || h0 == handTo) {
			if (h0.getId() == 4) {
				handTo = hands.get(5);
			} else if (h0.getId() == 0) {
				handTo = hands.get(4);
			}
		}
		super.mouseUp(pLstMoves, handTo);
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
