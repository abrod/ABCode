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
package de.brod.cm.game;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;

import android.graphics.Color;
import de.brod.cm.Card;
import de.brod.cm.CardContainer;
import de.brod.cm.CardManiacView;
import de.brod.cm.Hand;
import de.brod.gui.IAction;
import de.brod.gui.StateHandler;
import de.brod.gui.shape.Button;
import de.brod.gui.shape.Button.Type;

public abstract class Game {

	protected CardManiacView cardManiacView;

	protected ArrayList<Hand> hands = new ArrayList<Hand>();

	private ArrayList<CardContainer> cardContainer = new ArrayList<CardContainer>();

	private boolean showHelp;

	public Game(CardManiacView pCardManiacView) {
		cardManiacView = pCardManiacView;
	}

	public void getMenuItems(List<String> menuItems) {
		menuItems.add("New");
	}

	public void menuPressed(String sItem, StateHandler stateHandler) {
		if (sItem.equals("New")) {
			stateHandler.clear();
		}
	}

	protected void add(CardContainer cc) {
		cardContainer.add(cc);
		if (cc instanceof Hand) {
			hands.add((Hand) cc);
		}
	}

	public void addButtonTypes(List<Type> lst) {
		lst.add(Type.question);
		lst.add(Type.redo);
		lst.add(Type.undo);
	}

	public boolean buttonPressed(Type type, StateHandler stateHandler) {
		if (type.equals(Type.undo)) {
			stateHandler.undo();
		} else if (type.equals(Type.redo)) {
			stateHandler.redo();
		} else if (type.equals(Type.question)) {
			help();
		} else {
			return false;
		}
		return true;
	}

	protected void help() {
		// make nothing
		showHelp = true;
	}

	protected abstract void createTitleCards(Hand hand);

	public Hand get(int i) {
		return hands.get(i);
	}

	public CardContainer[] getCardContainer() {
		return cardContainer.toArray(new CardContainer[cardContainer.size()]);
	}

	public String getName() {
		String sName = getClass().getName();
		sName = sName.substring(sName.lastIndexOf(".") + 1);
		return sName;
	}

	public abstract IAction getNextAction();

	public Game getPreviousGame(CardManiacView cardManiacView2) {
		return new CardManiac(cardManiacView2);
	}

	public abstract boolean hasHistory();

	public abstract void initHands(boolean bLandscape);

	public abstract void initNewCards();

	public abstract void mouseDown(List<Card> plstMoves);

	public boolean mouseUp(List<Card> pLstMoves, Hand handTo) {
		if (handTo == null) {
			return false;
		}
		boolean bChanged = false;
		Hand handFrom = pLstMoves.get(0).getHand();
		if (handFrom != handTo) {
			for (Card card : pLstMoves) {
				card.moveTo(handTo);
				bChanged = true;
			}
		}
		return bChanged;
	}

	public void openGame(Game pGame) {
		cardManiacView.openGame(pGame);
	}

	public void prepareUpdate(StateHandler stateHandler,
			Hashtable<Button.Type, Button> htTitleButtons) {
		try {
			htTitleButtons.get(Type.undo).setEnabled(
					stateHandler.getEntriesCount() > 0);
			htTitleButtons.get(Type.redo).setEnabled(!stateHandler.isEOF());
		} catch (Exception ex) {
			// button not found ... which should not happen
		}
	}

	public int size() {
		return hands.size();
	}

	public Comparator<? super Card> getColorOrder() {
		return new Comparator<Card>() {

			@Override
			public int compare(Card lhs, Card rhs) {
				int diff = lhs.getColor().getId() - rhs.getColor().getId();
				if (diff != 0) {
					return diff;
				}

				return getValue(lhs) - getValue(rhs);
			}

			private int getValue(Card rhs) {
				int id = rhs.getValue().getId();
				if (id == 0) {
					// shift ace
					return 13;
				}
				return id;
			}
		};
	}

	public String getSetting(String psName) {
		return cardManiacView.getGlobalSettings(psName);
	}

	public int getSettingAsInt(String psName) {
		try {
			return Integer.parseInt(cardManiacView.getGlobalSettings(psName));
		} catch (Exception ex) {
			// ignore
			return 0;
		}
	}

	public void setSettings(String psName, String psValue) {
		cardManiacView.setGlobalSettings(psName, psValue);
	}

	public void setSettings(String psName, int piValue) {
		cardManiacView.setGlobalSettings(psName, "" + piValue);
	}

	protected void setColor(Card cl, boolean pOK) {
		if (cl == null) {
			return;
		}
		int a = 220;
		int b = 128;
		int c = 96;
		if (!pOK) {
			cl.setColor(Color.argb(255, a, b, c));
		} else {
			cl.setColor(Color.argb(255, c, a, b));
		}

	}

	public abstract boolean isFinished();

	public void clearHelp() {
		if (showHelp) {
			resetColors();
			showHelp = false;
		}
	}

	protected void resetColors() {
		for (Hand h : hands) {
			for (Card c : h.getCards()) {
				c.setColor(Color.WHITE);
			}
		}
	}

}
