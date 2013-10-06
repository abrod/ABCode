package de.brod.cm.game;

import java.util.List;

import de.brod.cm.Card;
import de.brod.cm.Card.Colors;
import de.brod.cm.Card.Values;
import de.brod.cm.CardManiacView;
import de.brod.cm.Hand;
import de.brod.gui.IAction;
import de.brod.gui.shape.Button.Type;

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
		for (Hand hand : hands) {
			hand.createCard(Values.Ace, Colors.Clubs);
			hand.createCard(Values.Ace, Colors.Spades);
			hand.createCard(Values.Ace, Colors.Hearts);
			hand.createCard(Values.Ace, Colors.Diamonds);
			hand.setText("FreeCell");
		}
	}

	@Override
	public void addButtonTypes(List<Type> lst) {
		lst.add(Type.next);
		lst.add(Type.info);
		lst.add(Type.previous);
	}

	@Override
	public Hand[] initHands(boolean bLandscape) {
		Hand[] h = new Hand[5];

		h[0] = new Hand(0, 0, 0, 1, 0, 4);
		h[1] = new Hand(0, 2, 0, 3, 0, 4);
		h[2] = new Hand(0, 4, 0, 5, 0, 4);
		h[3] = new Hand(0, 6, 0, 7, 0, 4);

		float dy = 1.4f;
		h[4] = new Hand(0, 0, dy, 1, dy, 4);
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
