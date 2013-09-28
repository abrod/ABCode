package de.brod.cm.game;

import java.util.List;

import de.brod.cm.Card;
import de.brod.cm.CardManiacView;
import de.brod.cm.Hand;
import de.brod.gui.IAction;

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

	public abstract void initCards(Hand[] hands);

	public abstract Hand[] initHands(boolean bLandscape);

	public abstract void mouseDown(List<Card> plstMoves);

	public void mouseUp(List<Card> pLstMoves, Hand handTo) {
		for (Card card : pLstMoves) {
			card.moveTo(handTo);
		}
	}

	public void openGame(Game pGame) {
		cardManiacView.openGame(pGame);
	}

}
