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
import de.brod.cm.TextAlign;
import de.brod.gui.GuiColors;
import de.brod.gui.action.IAction;
import de.brod.gui.shape.Button;
import de.brod.tools.StateHandler;
import de.brod.xml.XmlObject;

public class OffizierSkat extends Game {

	private final String[] cls = { Colors.Clubs.toString(),
			Colors.Spades.toString(), Colors.Hearts.toString(),
			Colors.Diamonds.toString(), "J" };
	private Buttons buttons;

	public OffizierSkat(CardManiacView pCardManiacView) {
		super(pCardManiacView);
	}

	protected boolean cardRightIsHigher(Card cp0, Card cp1) {
		if (cp0.getValue().equals(Values.Jack)) {
			if (cp1.getValue().equals(Values.Jack)) {
				return (cp1.getColor().getId() < cp0.getColor().getId());
			}
			return false;
		}
		if (cp1.getValue().equals(Values.Jack)) {
			return true;
		}

		if (cp0.getColor().equals(cp1.getColor())) {
			return getVal100(cp0) < getVal100(cp1);
		}
		// if trump
		if (isTrump(cp1)) {
			return true;
		}
		// color does not match
		return false;
	}

	private ArrayList<Card> checkPlayableCards(Card c,
			ArrayList<Card> plstPlayable) {
		if (c == null) {
			return plstPlayable;
		}
		ArrayList<Card> lstPlayable = new ArrayList<Card>();
		if (isTrump(c)) {
			// you have to play also a trump
			for (int i = 0; i < plstPlayable.size(); i++) {
				Card co = plstPlayable.get(i);
				if (isTrump(co)) {
					lstPlayable.add(co);
				}
			}
		} else {
			Colors stackColor = c.getColor();
			// / otherwise you have to play a color
			for (int i = 0; i < plstPlayable.size(); i++) {
				Card co = plstPlayable.get(i);
				if (!co.getValue().equals(Values.Jack)
						&& co.getColor().equals(stackColor)) {
					lstPlayable.add(co);
				}
			}
		}
		if (lstPlayable.size() == 0) {
			// add all
			return plstPlayable;
		}
		return lstPlayable;
	}

	private void coverCards() {
		boolean pickColor = getSettings().getAttributeAsBoolean("pickColor");
		for (int i = 0; i < 16; i++) {
			Hand hand = get(i);
			int iCovered = hand.getCardCount() - 1;
			if (pickColor && i / 4 != 2) {
				iCovered = 32;
			}
			hand.setCovered(iCovered);
		}
		setCounter(get(17));
		setCounter(get(18));
	}

	@Override
	protected void createTitleCards(Hand hand) {
		hand.createCard(Values.Jack, Colors.Hearts);
		hand.createCard(Values.Jack, Colors.Diamonds);
		hand.createCard(Values.Jack, Colors.Spades);
		hand.createCard(Values.Jack, Colors.Clubs);
	}

	protected Card getBestMove(int iOffset) {

		// get all playable cards
		ArrayList<Card> lstThis = getCards(iOffset);
		ArrayList<Card> lstOther = getCards(8 - iOffset);

		Hand handStack = get(16);
		Card cStack = handStack.getLastCard();

		Card cbest = getBestMove(lstThis, lstOther, cStack, new int[1], "", 0,
				4);
		return cbest;
	}

	public Card getSimpleBest(ArrayList<Card> lstThis,
			ArrayList<Card> lstOther, Card cStack, int[] pPoints, int faktor) {
		int mx = -9999;
		int points = 0;
		Card cBest = null;
		if (cStack == null) {
			// play first
			for (Card c : lstThis) {
				if (c != null) {
					int cv = getVal100(c);
					int iMinValue = 99999;
					int iMaxValue = 0;
					for (Card co : checkPlayableCards(c, lstOther)) {
						if (cardRightIsHigher(c, co)) {
							// points are lost
							cv = -Math.abs(cv);
						} else {
							int val0 = getVal0(co);
							iMinValue = Math.min(iMinValue, val0);
							iMaxValue = Math.min(iMaxValue, val0);
						}
					}
					int cardValue = cv / 100;
					if (cv < 0) {
						cardValue -= iMaxValue;
					} else {
						cardValue += iMinValue;
					}
					// don't waste jacks
					if (cv == 200 && iMinValue == 0) {
						cv = -500;
					}
					if (cv > mx) {
						mx = cv;
						cBest = c;
						points = cardValue;
					}
				}
			}
		} else {
			// react to card
			for (Card co : checkPlayableCards(cStack, lstThis)) {
				int cv = getVal100(co);
				if (!cardRightIsHigher(cStack, co)) {
					// points are lost
					cv = -Math.abs(cv);
				}
				if (cv > mx) {
					mx = cv;
					cBest = co;
				}
			}
			points = getVal0(cBest) + getVal0(cStack);
			if (mx < 0) {
				points = -points;
			}
		}
		pPoints[0] += points * faktor;
		return cBest;
	}

	private Card getBestMove(ArrayList<Card> lstThis, ArrayList<Card> lstOther,
			Card cStack, int[] pPoints, String sInfo, int iDeep, int piMaxDeep) {
		Card bestCard = null;
		// reset the points
		int max = 0;
		int[] points = new int[1];
		if (cStack == null) {
			// play any card
			for (int i = 0; i < lstThis.size(); i++) {
				// remove the card
				Card card = lstThis.remove(i);
				getBestMove(lstOther, lstThis, card, points, sInfo + " | "
						+ card, iDeep + 1, piMaxDeep);
				int val = -points[0];
				if (val > max || bestCard == null) {
					max = val;
					bestCard = card;
				}
				// add the card
				lstThis.add(i, card);
			}
		} else {
			int valStack = getVal100(cStack);
			// check only matching cards
			ArrayList<Card> pc = checkPlayableCards(cStack, lstThis);
			if (pc.size() <= 1) {
				// terminate
				iDeep = piMaxDeep;
			}
			for (int i = 0; i < pc.size(); i++) { // remove
				Card card = pc.get(i);
				// get the position
				int indexOf = lstThis.indexOf(card);
				// remove the card
				lstThis.remove(indexOf);
				int val = getVal100(card) + valStack;
				// compare
				if (cardRightIsHigher(cStack, card)) {
					// cards won
					if (iDeep < piMaxDeep) {
						getBestMove(lstThis, lstOther, null, points, sInfo
								+ card + " (" + val + ") ", iDeep + 1,
								piMaxDeep);
						val += points[0] * 3 / 4;
					}
				} else {
					// cards are lost
					val = -val;
					if (iDeep < piMaxDeep) {
						getBestMove(lstOther, lstThis, null, points, sInfo
								+ card + " (" + val + ") ", iDeep + 1,
								piMaxDeep);
						val -= points[0] * 3 / 4;
					}
				}
				if (val > max || bestCard == null) {
					max = val;
					bestCard = card;
				}

				// readd the card
				lstThis.add(indexOf, card);
			}
		}
		pPoints[0] = max;
		if (iDeep < 2) {
			System.out.println(sInfo + " > " + bestCard + " -> " + max);
		}
		return bestCard;
	}

	@Override
	public IAction getNextAction() {
		if (get(16).getCardCount() >= 2) {
			return new IAction() {

				@Override
				public void action() {
					XmlObject settings = getSettings();
					int player = settings.getAttributeAsInt("player");
					List<Card> cards = get(16).getCards();
					if (cards.size() >= 2) {
						Card cPlayer = cards.get(0);
						Card cOther = cards.get(1);
						Hand hPlayer = get(17 + player);
						Hand hOther = get(18 - player);
						if (cardRightIsHigher(cPlayer, cOther)) {
							cPlayer.moveTo(hOther);
							cOther.moveTo(hOther);
							settings.setAttribute("player", 1 - player);
							hOther.organize();
						} else {
							cPlayer.moveTo(hPlayer);
							cOther.moveTo(hPlayer);
							hPlayer.organize();
						}
						get(16).organize();
					}
					coverCards();
				}
			};
		}
		final XmlObject settings = getSettings();
		final int player = settings.getAttributeAsInt("player");
		if (player == 0) {
			if (settings.getAttributeAsBoolean("pickColor")) {
				return new IAction() {

					@Override
					public void action() {
						settings.setAttribute("pickColor", false);
						// try all 5 buttons
						int max = -999;
						int bt = 0;
						for (int i = 0; i < 5; i++) {
							settings.setAttribute("trumpf", i);
							int cnt = 0;
							for (int j = 4; j < 8; j++) {
								Card c = get(j).getLastCard();
								if (isTrump(c)) {
									cnt += getVal100(c) + 2000;
									if (i == 4) {
										cnt += 900;
									}
								}
							}
							if (i == 4 && cnt > 1000) {
								for (int j = 4; j < 8; j++) {
									Card c = get(j).getLastCard();
									if (c.getValue().equals(Values.Ace)) {
										cnt += getVal100(c) + 2000;
									}
								}
							}
							if (cnt > max) {
								max = cnt;
								bt = i;
							}
						}
						settings.setAttribute("trumpf", bt);
						for (int i = 0; i < 5; i++) {
							buttons.setEnabled(i, i == bt);
						}
						settings.setAttribute("pickColor", false);
						coverCards();

					}

				};
			}
			int cnt = 0;
			for (int i = 0; i < 8; i++) {
				cnt += get(i).getCardCount();
			}
			if (cnt > 0) {
				return new IAction() {

					@Override
					public void action() {
						Card cbest = getBestMove(0);
						Hand handStack = get(16);

						if (cbest != null) {
							cbest.moveTo(handStack);
							cbest.getHand().organize();
							handStack.organize();
						}
						XmlObject settings = getSettings();
						int player = settings.getAttributeAsInt("player");
						// set to next player
						settings.setAttribute("player", (player + 1) % 2);
					}
				};
			}
		}
		return null;
	}

	private XmlObject getSettings() {
		return buttons.getSettings();
	}

	private int getVal0(Card c) {
		Card.Values v = c.getValue();
		if (v.equals(Card.Values.Ace)) {
			return 11;
		}
		if (v.equals(Card.Values.C10)) {
			return 10;
		}
		if (v.equals(Card.Values.King)) {
			return 4;
		}
		if (v.equals(Card.Values.Queen)) {
			return 3;
		}
		if (v.equals(Card.Values.Jack)) {
			return 2;
		}
		return 0;
	}

	private int getVal100(Card c) {
		Card.Values v = c.getValue();
		if (v.equals(Card.Values.Ace)) {
			return 1100;
		}
		if (v.equals(Card.Values.C10)) {
			return 1000;
		}
		if (v.equals(Card.Values.King)) {
			return 400;
		}
		if (v.equals(Card.Values.Queen)) {
			return 300;
		}
		if (v.equals(Card.Values.Jack)) {
			return 200 + c.getColor().getId();
		}
		if (v.equals(Card.Values.C9)) {
			return 3;
		}
		if (v.equals(Card.Values.C8)) {
			return 2;
		}
		if (v.equals(Card.Values.C7)) {
			return 1;
		}
		return c.getValueId();
	}

	@Override
	public boolean hasHistory() {
		return true;
	}

	@Override
	protected void help() {
		Card c = get(16).getLastCard();
		if (c != null) {
			ArrayList<Card> l = checkPlayableCards(c, getCards(8));
			for (int i = 8; i < 16; i++) {
				Card cl = get(i).getLastCard();
				setColor(cl, l.contains(cl) ? CardColor.GRAY : CardColor.RED);
			}
		}
		Card cbest = getBestMove(8);
		if (cbest != null) {
			setColor(cbest, CardColor.GREEN);
		}
		super.help();
	}

	private ArrayList<Card> getCards(int a) {
		ArrayList<Card> lst = new ArrayList<Card>();
		for (int i = 0; i < 8; i++) {
			Card c = get(i + a).getLastCard();
			if (c != null) {
				lst.add(c);
			}
		}
		return lst;
	}

	@Override
	public void initHands(boolean bLandscape) {
		int iCount = 0;
		float[] dy = { 0, 1, Card.maxCardY - 1, Card.maxCardY };
		for (float element : dy) {
			for (int i = 0; i < 4; i++) {
				float x = i * 1.5f;
				add(new Hand(iCount++, x, element, x + 0.2f, element, 2));
			}
		}
		buttons = new Buttons(99);

		float px;
		float py;
		float bdy;
		float bdx;
		if (bLandscape) {
			add(new Hand(iCount++, 6, Card.maxCardY / 2, 7, Card.maxCardY / 2,
					2));
			add(new Hand(iCount++, 6, 0, 7, 0, 16));
			add(new Hand(iCount++, 6, dy[3], 7, dy[3], 16));
			px = Card.getX(-1.5f);
			py = Card.getY(Card.maxCardY / 2) - Button.height * 2f;
			bdx = 0;
			bdy = Card.getCardWidth();
			get(17).initText(TextAlign.BOTTOM);
			get(18).initText(TextAlign.TOP);
		} else {
			add(new Hand(iCount++, 3.5f, Card.maxCardY / 2 + 0.5f, 4,
					Card.maxCardY / 2 + 0.5f, 2));
			add(new Hand(iCount++, 6, 0.5f, 7, 0.5f, 16));
			add(new Hand(iCount++, 6, dy[2] + 0.5f, 7, dy[2] + 0.5f, 16));
			bdx = Card.getCardWidth();
			bdy = 0;
			px = Card.getX(3.5f) - bdx * 2f;
			py = Card.getY(Card.maxCardY / 2) + Card.getCardHeight() / 2;
			get(17).initText(TextAlign.BOTTOM);
			get(18).initText(TextAlign.BOTTOM);
		}

		for (int i = 0; i < cls.length; i++) {
			final int bt = i;
			Button b = Button.createTextButton(px + i * bdx, py + i * bdy, bdy
					+ bdx, cls[i], new IAction() {

				@Override
				public void action() {
					XmlObject settings = getSettings();
					boolean pickColor = settings
							.getAttributeAsBoolean("pickColor");
					if (!pickColor) {
						return;
					}
					for (int i = 0; i < 5; i++) {
						buttons.setEnabled(i, i == bt);
					}
					settings.setAttribute("pickColor", false);
					settings.setAttribute("trumpf", bt);
					coverCards();
				}
			});
			if (i < 2) {
				b.setTextColor(GuiColors.TEXT_BLACK);
			} else if (i < 4) {
				b.setTextColor(GuiColors.TEXT_RED);
			} else {
				b.setTextColor(GuiColors.TEXT_WHITE);
			}
			b.setEnabled(true);
			buttons.add(b);
		}
		add(buttons);
	}

	@Override
	public void initNewCards() {
		Card[] create32Cards = get(0).create32Cards();
		for (int i = 2; i < create32Cards.length; i++) {
			create32Cards[i].moveTo(get(i / 2));
		}
		for (int i = 0; i < 5; i++) {
			buttons.setEnabled(i, true);
		}
		XmlObject settings = getSettings();
		int iLastPlayer = (getSettingAsInt("lastPlayer") + 1) % 2;
		setSettings("lastPlayer", iLastPlayer);
		settings.setAttribute("player", iLastPlayer);
		settings.setAttribute("pickColor", true);
		get(17).setCovered(999);
		get(18).setCovered(999);
		coverCards();
	}

	@Override
	public String getFinishedText() {
		for (int i = 0; i <= 16; i++) {
			if (get(i).getCardCount() > 0) {
				return null;
			}
		}
		int iOther = getCounter(get(17));
		int iPlayer = getCounter(get(18));
		if (iOther > iPlayer) {
			return "You lost " + iPlayer + ":" + iOther + ".";
		} else if (iOther < iPlayer) {
			return "You won " + iPlayer + ":" + iOther + ".";
		}
		int iLastPlayer = getSettingAsInt("lastPlayer");
		if (iLastPlayer == 0) {
			return "You lost " + iPlayer + ":" + iOther + ".";
		}
		return "You won " + iPlayer + ":" + iOther + ".";
	}

	private boolean isTrump(Card cp1) {
		// jack is always trump
		if (cp1.getValue().equals(Values.Jack)) {
			return true;
		}
		int t = getSettings().getAttributeAsInt("trumpf");
		if (t == 0) {
			if (cp1.getColor().equals(Colors.Clubs)) {
				return true;
			}
		} else if (t == 1) {
			if (cp1.getColor().equals(Colors.Spades)) {
				return true;
			}
		} else if (t == 2) {
			if (cp1.getColor().equals(Colors.Hearts)) {
				return true;
			}
		} else if (t == 3) {
			if (cp1.getColor().equals(Colors.Diamonds)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void mouseDown(List<Card> plstMoves) {
		if (plstMoves.size() > 0) {

			Card card = plstMoves.get(0);
			Hand hand = card.getHand();
			// only top card selectable
			if (!hand.getLastCard().equals(card)) {
				plstMoves.clear();
				return;
			}

			int playerCard = (hand.getId() - get(0).getId()) / 8;
			int current = getSettings().getAttributeAsInt("player");
			if (playerCard != current) {
				plstMoves.clear();
				return;
			}

			Card c = get(16).getLastCard();
			if (c != null) {
				ArrayList<Card> l = checkPlayableCards(c, getCards(8));
				if (!l.contains(card)) {
					help();
					plstMoves.clear();
					return;
				}
			}
		}
	}

	@Override
	public boolean mouseUp(List<Card> pLstMoves, Hand handTo, Card pCard) {
		XmlObject settings = getSettings();
		boolean pickColor = settings.getAttributeAsBoolean("pickColor");
		if (pickColor) {
			System.out.println("pick a color first !");
			return false;
		}
		if (handTo == null) {
			System.out.println("no Card to move");
			return false;
		}
		// you may play only cards to stack
		if (handTo != get(16)) {
			System.out.println("handTo: " + handTo.getId() + " != "
					+ get(16).getId());
			return false;
		}
		Card card = pLstMoves.get(pLstMoves.size() - 1);
		Hand hand = card.getHand();

		int playerCard = (hand.getId() - get(0).getId()) / 8;
		int current = settings.getAttributeAsInt("player");
		if (playerCard != current) {
			System.out.println("player: " + hand.getId() + "-" + playerCard
					+ " != " + current);

			// only cards are playable
			return false;
		}

		card.moveTo(handTo);
		hand.organize();
		handTo.organize();
		// coverCards();
		// next players turn
		settings.setAttribute("player", (playerCard + 1) % 2);
		return true;
	}

	@Override
	public void prepareUpdate(StateHandler stateHandler,
			Hashtable<Button.Type, Button> htTitleButtons) {
		super.prepareUpdate(stateHandler, htTitleButtons);

		XmlObject settings = getSettings();
		boolean pickColor = settings.getAttributeAsBoolean("pickColor");
		if (pickColor) {
			for (int i = 0; i < 5; i++) {
				buttons.setEnabled(i, true);
			}
		} else {
			int trumpf = settings.getAttributeAsInt("trumpf");
			for (int i = 0; i < 5; i++) {
				buttons.setEnabled(i, i == trumpf);
			}
		}
		setCounter(get(17));
		setCounter(get(18));
		// coverCards();
	}

	private void setCounter(Hand hand) {

		int cnt = getCounter(hand);
		hand.setText(String.valueOf(cnt));
	}

	private int getCounter(Hand hand) {
		List<Card> cards = hand.getCards();
		int cnt = 0;
		for (Card card : cards) {
			cnt += getVal0(card);
		}
		return cnt;
	}
}
