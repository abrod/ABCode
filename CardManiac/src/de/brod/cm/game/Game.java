package de.brod.cm.game;

import java.util.List;

import de.brod.cm.Card;
import de.brod.cm.Hand;
import de.brod.gui.IAction;

public abstract class Game {

	public int getAmountOfCardsPerWidth() {
		return 8;
	}

	public String getName() {
		return getClass().getName();
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

}
