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

import java.util.Hashtable;
import java.util.List;

import de.brod.cm.Card;
import de.brod.cm.CardManiacView;
import de.brod.cm.Hand;
import de.brod.gui.IAction;
import de.brod.gui.StateHandler;
import de.brod.gui.shape.Button;
import de.brod.gui.shape.Button.Type;

public class CardManiac extends Game {

	private static Class<?>[] classes = { FreeCell.class, Solitaire.class,
			MauMau.class, OffizierSkat.class };

	private Game[] games;

	public CardManiac(CardManiacView pCardManiacView) {
		super(pCardManiacView);
	}

	@Override
	public Game getPreviousGame(CardManiacView cardManiacView2) {
		return null;
	}

	@Override
	public void addButtonTypes(List<Type> lst) {
		lst.add(Type.next);
		lst.add(Type.info);
		lst.add(Type.previous);
	}

	@Override
	protected void createTitleCards(Hand hand) {
		// make nothing
	}

	@Override
	public void getMenuItems(List<String> menuItems) {
		// make nothing
	}

	@Override
	public IAction getNextAction() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasHistory() {
		return false;
	}

	@Override
	public void initHands(boolean bLandscape) {

		games = new Game[classes.length];

		for (int i = 0; i < classes.length; i++) {
			try {
				games[i] = (Game) classes[i].getConstructor(
						CardManiacView.class).newInstance(cardManiacView);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			Hand h = newHand(i);
			String name = classes[i].getName();
			h.setText(name.substring(name.lastIndexOf(".") + 1));
			add(h);
		}
	}

	@Override
	public void initNewCards() {
		for (int i = 0; i < size(); i++) {
			Hand hand = get(i);
			games[i].createTitleCards(hand);
		}
	}

	@Override
	public String getFinishedText() {
		// never finishes
		return null;
	}

	@Override
	public void mouseDown(List<Card> plstMoves) {
		int id = plstMoves.get(0).getHand().getId();
		if (id >= 0 && id < games.length) {
			super.openGame(games[id]);
		}
	}

	private Hand newHand(int i) {
		int x = (i % 4) * 2;
		float y = (i / 4) * 1.4f;
		return new Hand(i, x, y, x + 1, y, 2);
	}

	public Game openGame(String sName) {

		for (Class<?> cls : classes) {
			if (cls.getName().endsWith("." + sName)) {
				try {
					Game g = (Game) cls.getConstructor(CardManiacView.class)
							.newInstance(cardManiacView);
					return g;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return this;
	}

	@Override
	public void prepareUpdate(StateHandler stateHandler,
			Hashtable<Button.Type, Button> htTitleButtons) {
		try {
			htTitleButtons.get(Type.next).setEnabled(false);
			htTitleButtons.get(Type.previous).setEnabled(false);
		} catch (Exception ex) {
			// button not found ... which should not happen
		}
		super.prepareUpdate(stateHandler, htTitleButtons);
	}

}
