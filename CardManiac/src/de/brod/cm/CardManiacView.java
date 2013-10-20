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

	private CardContainer[] _cardContainers;

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
		// clear the cardContainers
		for (CardContainer cc : _cardContainers) {
			cc.clear();
		}
		System.out.println("Init Containers");
		// init the Containers
		if (lastHistoryEntry == null || !game.hasHistory()) {
			game.initNewCards();
		} else {
			// load the last state
			for (CardContainer cc : _cardContainers) {
				XmlObject xmlObject = lastHistoryEntry.getObject(cc.getName(),
						"id", "" + cc.getId(), false);
				if (xmlObject != null) {
					cc.loadState(xmlObject);
				}
			}
		}

		System.out.println("Organize CardContainers");
		for (CardContainer cc : _cardContainers) {
			cc.organize();
			cc.addAllSpritesTo(root);
			System.out.println(cc.toString());
		}

		game.prepareUpdate(applicationStateHandler, htTitleButtons);
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
		String sGame = globalStateHandler.getAttribute("game");

		CardManiac cardManiac = new CardManiac(this);

		game = cardManiac.openGame(sGame);
	}

	@Override
	protected void initApplication() {
		globalStateHandler.setAttibute("game", game.getName());

		// set the correct game
		game.initHands(_width > _height);

		_cardContainers = game.getCardContainer();
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
	public void requestRender() {
		game.prepareUpdate(applicationStateHandler, htTitleButtons);
		super.requestRender();
	}

	@Override
	protected boolean mouseUp(List<Card> pLstMoves, Card cardTo) {
		boolean bChanged = false;
		if (pLstMoves.size() > 0) {
			if (cardTo != null) {
				Hand handTo = cardTo.getHand();
				bChanged = game.mouseUp(pLstMoves, handTo);
			} else {
				bChanged = game.mouseUp(pLstMoves, null);
			}
			// organize the hands
			for (CardContainer cc : _cardContainers) {
				cc.organize();
			}
		}
		return bChanged;
	}

	@Override
	protected void saveState(XmlObject historyEntry) {
		System.out.println("Save State");
		for (CardContainer cc : _cardContainers) {
			XmlObject xmlObject = historyEntry.getObject(cc.getName(), "id", ""
					+ cc.getId(), true);
			cc.saveState(xmlObject);
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
		if (game.buttonPressed(type, applicationStateHandler)) {
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
