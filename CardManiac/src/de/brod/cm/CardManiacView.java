package de.brod.cm;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import android.app.Activity;
import de.brod.cm.game.CardManiac;
import de.brod.cm.game.FreeCell;
import de.brod.cm.game.Game;
import de.brod.gui.GuiRendererView;
import de.brod.gui.IAction;
import de.brod.gui.StateHandler;
import de.brod.gui.shape.Button;
import de.brod.gui.shape.Button.Type;
import de.brod.gui.shape.Container;
import de.brod.gui.shape.Sprite;
import de.brod.xml.XmlObject;

public class CardManiacView extends GuiRendererView<Card> {

	Game game;

	private Hand[] hands;

	private int _width;

	private int _height;

	private int _piOffsetTop;

	private GL10 _gl;

	public CardManiacView(Activity context) {
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
		_width = width;
		_height = height;
		_piOffsetTop = piOffsetTop;
		_gl = gl;

		float fOffsetTop = _piOffsetTop * 2f / Math.min(_width, _height);
		Card.init(_gl, _width, _height, fOffsetTop);
		// create the game
		game = new FreeCell(this);
	}

	protected void initApplication() {
		// set the correct game
		hands = game.initHands(_width > _height);
		// init the application (menu, etc.)
		super.initApplication();
	}

	@Override
	protected boolean isInstanceOf(Container sprite) {
		return sprite instanceof Card;
	}

	@Override
	protected void menuPressed(String sItem, StateHandler stateHandler) {
		if (sItem.equalsIgnoreCase("New")) {
			stateHandler.clear();
		} else {
			getActivity().finish();
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

	@Override
	protected List<Type> getButtonsBottom() {
		List<Type> lst = new ArrayList<Button.Type>();
		game.addButtonTypes(lst);
		return lst;
	}

	public void openGame(Game pGame) {
		game = pGame;
		initApplication();
	}

	@Override
	protected boolean showBackButton() {
		return game == null || !(game instanceof CardManiac);
	}

	@Override
	protected void backButtonPressed() {
		openGame(new CardManiac(this));
	}

}
