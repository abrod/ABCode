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

		for (int i = 0; i < h.length; i++) {
			h[i] = newHand(i);
		}
		return h;
	}

	private Hand newHand(int i) {
		int x = (i % 4) * 2;
		float y = (i / 4) * 1.4f;
		return new Hand(0, x, y, x + 1, y, 4);
	}

	@Override
	public void mouseDown(List<Card> plstMoves) {
		int id = plstMoves.get(0).getHand().getId();
		if (id == 0) {
			super.openGame(new FreeCell(cardManiacView));
		}
	}

}
