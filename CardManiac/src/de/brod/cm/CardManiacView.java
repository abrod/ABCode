/*
 * ******************************************************************************
 * Copyright (c) 2013 Andreas Brod
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * *****************************************************************************
 */
package de.brod.cm;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import de.brod.cm.Buttons.UpdateType;
import de.brod.cm.game.CardManiac;
import de.brod.cm.game.Game;
import de.brod.gui.GuiRendererView;
import de.brod.gui.action.DefaultDialogAction;
import de.brod.gui.action.IAction;
import de.brod.gui.action.IDialogAction;
import de.brod.gui.shape.Button;
import de.brod.gui.shape.Button.Type;
import de.brod.gui.shape.Container;
import de.brod.gui.shape.Sprite;
import de.brod.tools.StateHandler;
import de.brod.xml.XmlObject;

public class CardManiacView extends GuiRendererView {

	Game game;

	private CardContainer[] _cardContainers;

	private int _width;

	private int _height;

	private int _piOffsetTop;

	private GL10 _gl;
	Boolean finished = null;

	public CardManiacView(CardManiacActivity context) {
		super(context);
	}

	@Override
	protected boolean backButtonPressed() {
		Game prevGame = game.getPreviousGame(this);
		if (prevGame != null) {
			openGame(prevGame);
			return true;
		}
		return false;
	}

	@Override
	protected void buttonPressed(Type type) {
		UpdateType buttonPressed = game.buttonPressed(type,
				applicationStateHandler);
		if (buttonPressed != null) {
			if (buttonPressed.equals(UpdateType.RELOAD)) {
				reload();
			} else if (buttonPressed.equals(UpdateType.REFRESH)) {
				requestRender();
			}
		} else {
			super.buttonPressed(type);
		}
	}

	@Override
	protected String getApplicationName() {
		return game.getName();
	}

	@Override
	protected List<Type> getButtonsBottom() {
		List<Type> lst = new ArrayList<Button.Type>();
		game.addButtonTypes(lst);
		return lst;
	}

	public String getGlobalSettings(String psName) {
		return globalStateHandler.getObject("Settings", game.getName())
				.getAttribute(psName);
	}

	@Override
	protected void getMenuItems(List<IDialogAction> menuItems,
			StateHandler stateHandler) {
		game.getMenuItems(menuItems, stateHandler);
		menuItems.add(new DefaultDialogAction("Close") {

			@Override
			public void action() {
				getActivity().finish();
			}
		});
	}

	@Override
	protected IAction getNextAction() {
		String finishedText = game.getFinishedText();
		if (finishedText != null) {
			if (finished == null) {
				IDialogAction[] sButtons = { new IDialogAction() {

					@Override
					public void action() {
						applicationStateHandler.clear();
						reload();
					}

					@Override
					public String getName() {
						return "Yes";
					}
				}, new IDialogAction() {

					@Override
					public void action() {

					}

					@Override
					public String getName() {
						return "No";
					}
				} };
				showMessage("Finished", finishedText
						+ "\nDo you want to start a new game ?", sButtons);
				finished = Boolean.TRUE;
			}
			return null;
		}
		return game.getNextAction();
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
		}
		finished = null;
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
	protected boolean isInstanceOf(Container sprite) {
		if (sprite instanceof ICard) {
			return true;
		}
		if (sprite instanceof CardFrame) {
			return true;
		}
		return false;
	}

	@Override
	protected void mouseDown(List<Container> plstMoves) {
		if (game.getFinishedText() != null) {
			plstMoves.clear();
			return;
		}
		game.clearHelp();
		List<ICard> cards = getCards(plstMoves);
		game.mouseDown(cards);
		removeCards(plstMoves, cards);
	}

	private void removeCards(List<Container> plstMoves, List<ICard> cards) {
		for (int i = 0; i < plstMoves.size();) {
			if (cards.contains(plstMoves.get(i))) {
				i++;
			} else {
				plstMoves.remove(i);
			}
		}
		for (ICard iCard : cards) {
			if (!plstMoves.contains(iCard)) {
				plstMoves.add((Container) iCard);
			}
		}

	}

	private List<ICard> getCards(List<Container> plstMoves) {
		List<ICard> lst = new ArrayList<ICard>();
		for (Container sprite : plstMoves) {
			if (sprite instanceof ICard) {
				lst.add((ICard) sprite);
			}
		}
		return lst;
	}

	@Override
	protected boolean mouseUp(List<Container> pLstMoves, Container pCardTo) {
		if (game.getFinishedText() != null) {
			return false;
		}
		boolean bChanged = false;
		if (pLstMoves.size() > 0) {
			List<ICard> cards = getCards(pLstMoves);
			if (pCardTo != null && pCardTo instanceof ICard) {
				ICard cardTo = (ICard) pCardTo;
				Hand handTo = cardTo.getHand();
				bChanged = game.mouseUp(cards, handTo, cardTo);
			} else {
				bChanged = game.mouseUp(cards, null, null);
			}
			removeCards(pLstMoves, cards);

			// organize the hands
			for (CardContainer cc : _cardContainers) {
				cc.organize();
			}
		}
		return bChanged;
	}

	public void openGame(Game pGame) {
		game = pGame;
		initApplication();
	}

	@Override
	public void requestRender() {
		game.prepareUpdate(applicationStateHandler, htTitleButtons);
		super.requestRender();
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

	public void setGlobalSettings(String psName, String psValue) {
		globalStateHandler.setObject("Settings", game.getName(), psName,
				psValue);
	}

	@Override
	protected boolean showBackButton() {
		return game == null || !(game instanceof CardManiac);
	}

}
