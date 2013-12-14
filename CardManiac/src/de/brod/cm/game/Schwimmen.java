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
import java.util.Hashtable;
import java.util.List;

import de.brod.cm.Buttons;
import de.brod.cm.Card;
import de.brod.cm.Card.Colors;
import de.brod.cm.Card.Values;
import de.brod.cm.CardManiacView;
import de.brod.cm.Hand;
import de.brod.cm.ICard;
import de.brod.cm.TextAlign;
import de.brod.gui.GuiColors;
import de.brod.gui.action.IAction;
import de.brod.gui.action.NoAction;
import de.brod.gui.shape.Button;
import de.brod.tools.StateHandler;
import de.brod.xml.XmlObject;

public class Schwimmen extends Game {

	private Buttons buttons;
	private Button[] stopButton = new Button[3];

	public Schwimmen(CardManiacView pCardManiacView) {
		super(pCardManiacView);
	}

	@Override
	protected void createTitleCards(Hand hand) {
		hand.createCard(Values.Ace, Colors.Spades);
		hand.createCard(Values.King, Colors.Spades);
		hand.createCard(Values.C10, Colors.Spades);
	}

	@Override
	public IAction getNextAction() {

		final XmlObject settings = getSettings();
		if (hasStopped(settings)) {
			if (get(1).getCovered() > 0) {
				// calculate the results
				return new IAction() {

					@Override
					public void action() {
						for (int i = 1; i < 3; i++) {
							get(i).setCovered(0);
							updateText(i);
							get(i).organize();
						}
					}
				};
			}
			// finished
			return null;
		}
		if (get(4).getCardCount() == 32) {
			return new IAction() {

				@Override
				public void action() {
					// play the cards
					Hand h4 = get(4);
					ICard[] cards = h4.getCards().toArray(new ICard[0]);
					for (int i = 0; i <= 3; i++) {
						for (int j = 0; j < 3; j++) {
							cards[i * 3 + j].moveTo(get(i));
						}
					}
					for (int i = 0; i < 5; i++) {
						get(i).organize();
					}
					h4.setCovered(h4.getCardCount());
					for (int j = 0; j < 3; j++) {
						settings.setAttribute("skip" + j, false);
						settings.setAttribute("stop" + j, 0);
					}
					updateText();
				}

			};
		}
		int iSkip = getSkipCount(settings);
		// all 3 players skipped
		if (iSkip >= 3) {
			return new IAction() {

				@Override
				public void action() {
					Hand h3 = get(3);
					Hand h4 = get(4);
					// move the cards to tolon
					int covered = h4.getCovered() - 3;
					for (int i = 0; i < 3; i++) {
						h3.getCards().get(0).moveTo(h4);
						h4.getCards().get(0).moveTo(h3);
						settings.setAttribute("skip" + i, false);
					}
					h4.setCovered(Math.max(0, covered));
					h4.organize();
					h3.organize();
					updateText();
				}
			};
		}
		final int iPlayer = settings.getAttributeAsInt("player");
		if (iPlayer > 0) {
			return new IAction() {

				@Override
				public void action() {

					List<ICard> lst = get(iPlayer).getCards();
					List<ICard> lst3 = get(3).getCards();
					List<ICard> lstTo = new ArrayList<ICard>();
					double max = count(lst);
					ICard cs = null;
					ICard ct = null;
					for (ICard c1 : lst) {
						lstTo.clear();
						lstTo.addAll(lst);
						lstTo.remove(c1);
						for (ICard c2 : lst3) {
							lstTo.add(c2);
							double m = count(lstTo);
							if (m > max) {
								max = m;
								cs = c1;
								ct = c2;
							}
							lstTo.remove(c2);
						}
					}
					boolean bSkip = false;
					if (max < count(lst3)) {
						for (int i = 0; i < 3; i++) {
							lst.get(0).moveTo(get(3));
							lst3.get(0).moveTo(get(iPlayer));
						}
					} else if (cs != null) {
						cs.moveTo(get(3));
						ct.moveTo(get(iPlayer));
					} else {
						lst.add(lst.remove(0));
						// skip drawing cards
						bSkip = true;
					}
					get(iPlayer).organize();
					get(3).organize();

					nextPlayer(bSkip);
				}

			};
		}

		// no further action
		return null;
	}

	private int getSkipCount(XmlObject settings) {
		int iSkip = 0;
		for (int i = 0; i < 3; i++) {
			if (settings.getAttributeAsBoolean("skip" + i)) {
				iSkip++;
			}
		}
		return iSkip;
	}

	@Override
	public boolean hasHistory() {
		return true;
	}

	@Override
	public void initHands(boolean bLandscape) {
		float left = 2.5f;
		float right = 4.5f;
		add(new Hand(0, left, Card.maxCardY, right, Card.maxCardY, 3));
		// add the players
		float middle = Card.maxCardY / 2;
		float top = middle - 0.8f;
		float bottom = middle + 0.8f;

		if (bLandscape) {
			add(new Hand(1, -1, top, -1, bottom, 3));
			add(new Hand(2, 8, top, 8, bottom, 3));
		} else {
			add(new Hand(1, 0, top, 0, bottom, 3));
			add(new Hand(2, 7, top, 7, bottom, 3));
		}
		add(new Hand(3, left, middle, right, middle, 3));
		add(new Hand(4, 0f, 0, 7f, 0, 10));

		get(1).setCovered(999);
		get(2).setCovered(999);
		get(4).setCovered(32);

		get(1).setRotation(1);
		get(2).setRotation(-1);
		for (int i = 0; i < 5; i++) {
			get(i).setCenter(true);
		}
		get(0).initText(TextAlign.TOP);
		get(1).initText(TextAlign.RIGHT);
		get(2).initText(TextAlign.LEFT);
		get(3).initText(TextAlign.TOP);
		get(4).initText(TextAlign.BOTTOM);

		buttons = new Buttons(99);
		float w = Card.getCardWidth();
		Button skipButton = Button.Type.reload.createButton(
				bLandscape ? Card.getX(right + 1) : 0,
				Card.getY(Card.maxCardY * 3 / 4), w, new IAction() {
					@Override
					public void action() {
						nextPlayer(true);
					}
				});
		stopButton[0] = Button.Type.star_off.createButton(Card.getX(left - 1),
				Card.getY(Card.maxCardY), w, new IAction() {

					@Override
					public void action() {
						XmlObject settings = getSettings();
						int iStop = settings.getAttributeAsInt("stop0");
						if (iStop == 1) {
							int iNextStop = 0;
							for (int i = 0; i < 3; i++) {
								if (settings.getAttributeAsInt("stop" + i) >= 2) {
									// stop is press by another player
									// ... so don't change
									iNextStop = iStop;
									break;
								}
							}
							settings.setAttribute("stop0", iNextStop);
						} else if (iStop == 0) {
							// prepare to stop
							settings.setAttribute("stop0", 1);
						}
						updateText();
					}
				});
		// add stop buttons for each player
		stopButton[1] = Button.Type.star_off.createButton(get(1).getX(0),
				Card.getY(top) + w, w, new NoAction());

		stopButton[2] = Button.Type.star_off.createButton(get(2).getX(0),
				Card.getY(bottom) - w, w, new NoAction());

		buttons.add(skipButton);
		for (Button b : stopButton) {
			buttons.add(b);
		}

		add(buttons);
	}

	protected void nextPlayer(boolean pbSkip) {
		XmlObject settings = getSettings();
		int iPlayer = settings.getAttributeAsInt("player");
		settings.setAttribute("skip" + iPlayer, pbSkip);
		if (!pbSkip) {
			// remove skip flags, because player has played a card
			for (int i = 0; i < 3; i++) {
				settings.setAttribute("skip" + i, pbSkip);
			}
		}
		int iStop = settings.getAttributeAsInt("stop" + iPlayer);
		if (iStop == 1) {
			stopRound(iPlayer, settings);
		}
		int iNextPlayer = (iPlayer + 1) % 3;
		settings.setAttribute("player", iNextPlayer);

		updateText();
	}

	protected void stopRound(int piPlayer, XmlObject settings) {
		settings.setAttribute("stop" + piPlayer, 2);
		for (int i = 0; i < 3; i++) {
			settings.setAttribute("stop" + i,
					Math.max(1, settings.getAttributeAsInt("stop" + i)));
		}
	}

	private boolean hasStopped(XmlObject settings) {
		for (int i = 0; i < 3; i++) {
			if (settings.getAttributeAsInt("stop" + i) < 2) {
				return false;
			}
		}
		return true;
	}

	private XmlObject getSettings() {
		return get(0).getSettings();
	}

	@Override
	public void prepareUpdate(StateHandler stateHandler,
			Hashtable<Button.Type, Button> htTitleButtons) {
		super.prepareUpdate(stateHandler, htTitleButtons);
		updateText();
	}

	@Override
	public void initNewCards() {
		get(4).create32Cards();
		get(4).setCovered(32);
		get(4).setText(" ");

		for (int i = 1; i < 3; i++) {
			get(i).setCovered(999);
			get(i).setText(" ");
		}

		XmlObject settings = getSettings();
		settings.setAttribute("player", 0);
		for (int i = 0; i < 3; i++) {
			settings.setAttribute("skip" + i, false);
			settings.setAttribute("stop" + i, 0);
		}
	}

	@Override
	public String getFinishedText() {
		if (hasStopped(getSettings())) {
			if (get(1).getCovered() == 0) {
				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < 3; i++) {
					sb.append("Player " + (i + 1) + ": " + count(i) + "\n");
				}
				return sb.toString();
			}
		}
		return null;
	}

	@Override
	public void mouseDown(List<ICard> plstMoves) {
		if (hasStopped(getSettings())) {
			// no more moves possible
			plstMoves.clear();
			return;
		}
		int i = plstMoves.get(0).getHand().getId();
		if (i == 0) {
			// ok
		} else if (i == 3) {
			// ok
			plstMoves.clear();
			plstMoves.addAll(get(i).getCards());
		} else {
			plstMoves.clear();
		}
	}

	@Override
	public boolean mouseUp(List<ICard> pLstMoves, Hand handTo, ICard cardTo) {
		if (handTo == null || cardTo == null) {
			// dont move
			return false;
		}

		Hand handFrom = pLstMoves.get(0).getHand();
		if (handTo == handFrom) {
			return false;
		}
		int i = handTo.getId();
		if (i != 0 && i != 3) {
			return false;
		}
		if (pLstMoves.size() == 1) {
			pLstMoves.get(0).moveTo(handTo);
			cardTo.moveTo(handFrom);
		} else {
			for (ICard c : pLstMoves) {
				c.moveTo(handTo);
				handTo.getCards().get(0).moveTo(handFrom);
			}
		}
		nextPlayer(false);
		return true;

	}

	private void updateText() {
		updateText(0);
		updateText(3);
		XmlObject settings = getSettings();
		for (int i = 0; i < 3; i++) {
			int iStop = settings.getAttributeAsInt("stop" + i);
			Button b = stopButton[i];
			if (iStop > 1) {
				b.setColor(GuiColors.TEXT_RED);
				Button.Type.star_on.paintToButton(b);
			} else if (iStop == 1) {
				b.setColor(GuiColors.TEXT_RED);
				Button.Type.star_off.paintToButton(b);
			} else {
				Button.Type.star_off.paintToButton(b);
				if (settings.getAttributeAsBoolean("skip" + i)) {
					b.setColor(GuiColors.TEXT_GREEN);
				} else {
					b.setColor(null);
				}
			}
		}
	}

	private void updateText(int i) {
		get(i).setText(count(i));
	}

	private String count(int i) {
		double val = count(get(i).getCards());
		if (val == 30.5) {
			return "30 " + (char) 189;
		}
		return String.valueOf((int) val);
	}

	private double count(List<ICard> cards) {
		int max = 0;
		Card.Values v0 = null;
		for (int i = 0; i < cards.size(); i++) {
			ICard c = cards.get(i);
			int v = val(c);
			if (i == 0) {
				v0 = c.getValue();
			} else if (v0 != c.getValue()) {
				v0 = null;
			}
			for (int j = i + 1; j < cards.size(); j++) {
				ICard c2 = cards.get(j);
				if (c.getColor().equals(c2.getColor())) {
					v += val(c2);
				}
			}
			max = Math.max(max, v);
		}
		if (v0 != null) {
			if (v0.equals(Values.Ace)) {
				return 32;
			}
			return 30.5;
		}
		return max;
	}

	private int val(ICard get) {
		Card.Values v = get.getValue();
		if (v.equals(Values.Ace)) {
			return 11;
		}
		if (v.equals(Values.King)) {
			return 10;
		}
		if (v.equals(Values.Queen)) {
			return 10;
		}
		if (v.equals(Values.Jack)) {
			return 10;
		}
		if (v.equals(Values.C10)) {
			return 10;
		}
		if (v.equals(Values.C9)) {
			return 9;
		}
		if (v.equals(Values.C8)) {
			return 8;
		}
		if (v.equals(Values.C7)) {
			return 7;
		}
		return 0;
	}
}
