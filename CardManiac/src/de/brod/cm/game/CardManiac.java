package de.brod.cm.game;

import java.util.List;

import de.brod.cm.Card;
import de.brod.cm.Card.Colors;
import de.brod.cm.Card.Values;
import de.brod.cm.CardManiacView;
import de.brod.cm.Hand;
import de.brod.gui.IAction;

public class CardManiac extends Game {

	public CardManiac(CardManiacView pCardManiacView) {
		super(pCardManiacView);
	}

	@Override
	public IAction getNextAction() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void initCards(Hand[] hands) {
		hands[0].createCard(Values.Jack, Colors.Spades);
		hands[0].createCard(Values.Jack, Colors.Clubs);
		hands[0].createCard(Values.Jack, Colors.Hearts);
		hands[0].createCard(Values.Jack, Colors.Diamonds);
	}

	@Override
	public Hand[] initHands(boolean bLandscape) {
		Hand[] h = new Hand[1];
		h[0] = new Hand(0, 0, 0, 1, 0, 4);
		return h;
	}

	@Override
	public void mouseDown(List<Card> plstMoves) {
		int id = plstMoves.get(0).getHand().getId();
		if (id == 0) {
			super.openGame(new FreeCell(cardManiacView));
		}
	}

}
