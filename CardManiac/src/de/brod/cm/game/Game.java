package de.brod.cm.game;

import java.util.ArrayList;
import java.util.List;

import de.brod.cm.Card;
import de.brod.cm.CardManiacView;
import de.brod.cm.Hand;
import de.brod.gui.IAction;
import de.brod.gui.shape.Button.Type;

public abstract class Game {

	protected CardManiacView cardManiacView;

	public Game(CardManiacView pCardManiacView) {
		cardManiacView = pCardManiacView;
	}

	public String getName() {
		String sName = getClass().getName();
		sName = sName.substring(sName.lastIndexOf(".") + 1);
		return sName;
	}

	public abstract IAction getNextAction();

	public abstract void initNewCards(Hand[] hands);

	public abstract Hand[] initHands(boolean bLandscape);

	public abstract void mouseDown(List<Card> plstMoves);

	public void mouseUp(List<Card> pLstMoves, Hand handTo) {
		if (handTo == null) {
			return;
		}
		Hand handFrom = pLstMoves.get(0).getHand();
		if (handFrom != handTo) {
			for (Card card : pLstMoves) {
				card.moveTo(handTo);
			}
		}
	}

	public void openGame(Game pGame) {
		cardManiacView.openGame(pGame);
	}

	public void addButtonTypes(List<Type> lst) {
		lst.add(Type.question);
		lst.add(Type.redo);
		lst.add(Type.undo);
	}

	protected ArrayList<Hand> hands = new ArrayList<Hand>();

	protected abstract void createTitleCards(Hand hand);

	public Game getPreviousGame(CardManiacView cardManiacView2) {
		return new CardManiac(cardManiacView2);
	}

	public abstract boolean hasHistory();

}
