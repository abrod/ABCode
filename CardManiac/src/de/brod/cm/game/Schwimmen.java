package de.brod.cm.game;

import java.util.List;

import de.brod.cm.Card;
import de.brod.cm.Card.Colors;
import de.brod.cm.Card.Values;
import de.brod.cm.CardManiacView;
import de.brod.cm.Hand;
import de.brod.gui.IAction;

public class Schwimmen extends Game {

	public Schwimmen(CardManiacView pCardManiacView) {
		super(pCardManiacView);
	}

	@Override
	protected void createTitleCards(Hand hand) {
		hand.createCard(Values.Ace, Colors.Spades);
		hand.createCard(Values.King, Colors.Spades);
		hand.createCard(Values.C10, Colors.Spades);
	}

	@Override
	public IAction getNextAction() {
		if (get(4).getCardCount() == 32) {
			return new IAction() {

				@Override
				public void action() {
					Card[] cards = get(4).getCards().toArray(new Card[0]);
					for (int i = 0; i <= 3; i++) {
						for (int j = 0; j < 3; j++) {
							cards[i * 3 + j].moveTo(get(i));
						}
					}
					for (int i = 0; i < 5; i++) {
						get(i).organize();
					}
				}

			};
		}
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasHistory() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void initHands(boolean bLandscape) {
		// TODO Auto-generated method stub
		float left = 2f;
		float right = 5f;
		add(new Hand(0, left, Card.maxCardY, right, Card.maxCardY, 3));
		// add the players
		float middle = Card.maxCardY / 2;
		float top = middle - 0.6f;
		float bottom = middle + 0.6f;

		add(new Hand(1, 0, top, 0, bottom, 3));
		add(new Hand(2, 7, top, 7, bottom, 3));

		add(new Hand(3, left, middle, right, middle, 3));

		add(new Hand(4, 3.5f, 0, 3.5f, 0, 32));

		get(1).setCovered(999);
		get(2).setCovered(999);
		get(4).setCovered(999);

		get(1).setRotation(90f);
		get(2).setRotation(-90f);
		for (int i = 0; i < 4; i++) {
			get(i).setCenter(true);
		}

	}

	@Override
	public void initNewCards() {
		get(4).create32Cards();
	}

	@Override
	public String getFinishedText() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void mouseDown(List<Card> plstMoves) {
		// TODO Auto-generated method stub

	}

}
