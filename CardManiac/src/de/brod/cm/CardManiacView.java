package de.brod.cm;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import android.app.Activity;
import de.brod.cm.game.CardManiac;
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

	private CardContainer[] hands;

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
		for (CardContainer hand : hands) {
			hand.clear();
		}
		System.out.println("Init hand");
		// init the hands
		if (lastHistoryEntry == null || !game.hasHistory()) {
			game.initNewCards();
		} else {
			// load the last state
			for (CardContainer hand : hands) {
				XmlObject xmlHand = lastHistoryEntry.getObject("Hand", "id", ""
						+ hand.getId(), false);
				if (xmlHand != null) {
					hand.loadState(xmlHand);
					System.out.println(xmlHand.toString());
					System.out.println(hand.toString());
				}
			}
		}

		System.out.println("Organize hand");
		for (CardContainer hand : hands) {
			hand.organize();
			hand.addAllCards(root);
			System.out.println(hand.toString());
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
		String sGame = settings.getAttribute("game");

		CardManiac cardManiac = new CardManiac(this);

		game = cardManiac.openGame(sGame);
	}

	@Override
	protected void initApplication() {
		settings.setAttibute("game", game.getName());

		// set the correct game
		game.initHands(_width > _height);

		hands = game.getCardContainer();
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
			if (cardTo != null) {
				Hand handTo = cardTo.getHand();
				game.mouseUp(pLstMoves, handTo);
			} else {
				game.mouseUp(pLstMoves, null);
			}
			// organize the hands
			for (CardContainer hand : hands) {
				hand.organize();
			}
		}

	}

	@Override
	protected void saveState(XmlObject historyEntry) {
		System.out.println("Save State");
		for (CardContainer hand : hands) {
			XmlObject xmlHand = historyEntry.getObject("Hand", "id",
					"" + hand.getId(), true);
			hand.saveState(xmlHand);
			System.out.println(xmlHand);
			System.out.println(hand);
		}
	}

	@Override
	protected List<Type> getButtonsBottom() {
		List<Type> lst = new ArrayList<Button.Type>();
		game.addButtonTypes(lst);
		return lst;
	}

	@Override
	protected void buttonPressed(Type type) {
		if (type.equals(Type.undo)) {
			stateHandler.undo();
			reload();
		} else if (type.equals(Type.redo)) {
			stateHandler.redo();
			reload();
		} else {
			super.buttonPressed(type);
		}
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
		Game prevGame = game.getPreviousGame(this);
		if (prevGame != null) {
			openGame(prevGame);
		}
	}

}
