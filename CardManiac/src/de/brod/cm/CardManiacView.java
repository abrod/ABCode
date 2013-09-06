package de.brod.cm;

import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import de.brod.cm.game.FreeCell;
import de.brod.cm.game.Game;
import de.brod.gui.GuiRendererView;
import de.brod.gui.IAction;
import de.brod.gui.Sprite;
import de.brod.gui.StateHandler;
import de.brod.xml.XmlObject;

public class CardManiacView extends GuiRendererView<Card> {

	Game game;

	private Hand[] hands;

	public CardManiacView(Context context) {
		super(context);
	}

	@Override
	protected String getApplicationName() {
		return game.getName();
	}

	@Override
	protected void getMenuItems(List<String> menuItems) {
		menuItems.add("New");
		menuItems.add("Close");
	}

	@Override
	protected IAction getNextAction() {
		return game.getNextAction();
	}

	@Override
	public void initGroup(Sprite root, XmlObject lastHistoryEntry) {
		// clear the hands
		for (Hand hand : hands) {
			hand.clear();
		}
		// init the hands
		if (lastHistoryEntry == null) {
			game.initCards(hands);
		} else {
			// load the last state
			for (Hand hand : hands) {
				XmlObject xmlHand = lastHistoryEntry.getObject("Hand", "id", ""
						+ hand.getId(), false);
				if (xmlHand != null) {
					hand.loadState(xmlHand);
				}
			}
		}

		for (Hand hand : hands) {
			hand.organize();
			hand.addAllCards(root);
		}

	}

	@Override
	protected void initTextures(GL10 gl, int width, int height, int piOffsetTop) {
		game = new FreeCell();
		float fOffsetTop = piOffsetTop * 2f / Math.min(width, height);
		Card.init(gl, width, height, fOffsetTop,
				game.getAmountOfCardsPerWidth());
		hands = game.initHands(width > height);
	}

	@Override
	protected boolean isInstanceOf(Sprite sprite) {
		return sprite instanceof Card;
	}

	@Override
	protected void menuPressed(String sItem, StateHandler stateHandler) {
		if (sItem.equalsIgnoreCase("New")) {
			stateHandler.clear();
		}
	}

	@Override
	protected void mouseDown(List<Card> plstMoves) {
		game.mouseDown(plstMoves);
	}

	@Override
	protected void mouseUp(List<Card> pLstMoves, Card cardTo) {
		if (pLstMoves.size() > 0) {
			Hand handFrom = pLstMoves.get(0).getHand();
			if (cardTo != null) {
				Hand handTo = cardTo.getHand();
				if (handFrom != handTo) {
					game.mouseUp(pLstMoves, handTo);
				}
			}
			// organize the hands
			for (Hand hand : hands) {
				hand.organize();
			}
		}

	}

	@Override
	protected void saveState(XmlObject historyEntry) {
		for (Hand hand : hands) {
			XmlObject xmlHand = historyEntry.getObject("Hand", "id",
					"" + hand.getId(), true);
			hand.saveState(xmlHand);
		}
	}

}
