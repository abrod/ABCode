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

import android.graphics.Color;
import de.brod.cm.Buttons;
import de.brod.cm.Card;
import de.brod.cm.Card.Colors;
import de.brod.cm.Card.Values;
import de.brod.cm.CardManiacView;
import de.brod.cm.Hand;
import de.brod.cm.TextAlign;
import de.brod.gui.IAction;
import de.brod.gui.StateHandler;
import de.brod.gui.shape.Button;
import de.brod.xml.XmlObject;

public class OffizierSkat extends Game {

	private final String[] cls = { Colors.Clubs.toString(),
			Colors.Spades.toString(), Colors.Hearts.toString(),
			Colors.Diamonds.toString(), "J" };
	private Buttons buttons;
	private List<Card> lstPlayable = new ArrayList<Card>();

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
			return getVal(cp0, 1) < getVal(cp1, 1);
		}
		// if trump
		if (isTrump(cp1)) {
			return true;
		}
		// color does not match
		return false;
	}

	private List<Card> checkPlayableCards(Card c, int a) {
		lstPlayable.clear();
		if (isTrump(c)) {
			// you have to play also a trump
			for (int j = 0; j < 8; j++) {
				Card co = get(j + a).getLastCard();
				if (co != null) {
					if (isTrump(co)) {
						lstPlayable.add(co);
					}
				}
			}
		} else {
			// / otherwise you have to play a color
			for (int j = 0; j < 8; j++) {
				Card co = get(j + a).getLastCard();
				if (co != null && !co.getValue().equals(Values.Jack)) {
					if (co.getColor().equals(c.getColor())) {
						lstPlayable.add(co);
					}
				}
			}
		}
		// may play any card
		if (lstPlayable.size() == 0) {
			for (int j = 0; j < 8; j++) {
				Card co = get(j + a).getLastCard();
				if (co != null) {
					lstPlayable.add(co);
				}
			}
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
		Hand handStack = get(16);
		int mx = -99;
		Card cStack = handStack.getLastCard();
		Card cbest = null;
		if (cStack == null) {
			// play first
			for (int i = 0; i < 8; i++) {
				Card c = get(i + iOffset).getLastCard();
				if (c != null) {
					int cv = getVal(c, 0);
					int iLowestValue = 20;
					for (Card co : checkPlayableCards(c, 8 - iOffset)) {
						if (cardRightIsHigher(c, co)) {
							// points are lost
							cv = -Math.abs(cv);
						} else {
							iLowestValue = Math
									.min(iLowestValue, getVal(co, 0));
						}
					}
					// don't waste jacks
					if (cv == 2 && iLowestValue == 0) {
						cv = -5;
					}
					if (cv > mx) {
						mx = cv;
						cbest = c;
					}
				}
			}
		} else {
			// react to card
			for (Card co : checkPlayableCards(cStack, iOffset)) {
				int cv = getVal(co, 1) + 5;
				if (!cardRightIsHigher(cStack, co)) {
					// points are lost
					cv = -Math.abs(cv);
				}
				if (cv > mx) {
					mx = cv;
					cbest = co;
				}
			}
		}
		return cbest;
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
									cnt += getVal(c, 1) + 20;
									if (i == 4) {
										cnt += 10;
									}
								} else if (i == 4 && cnt > 10) {
									if (c.getValue().equals(Values.Ace)) {
										cnt += getVal(c, 1) + 20;
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

	private int getVal(Card c, int iNullValue) {
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
		if (v.equals(Card.Values.C9)) {
			return -1 * iNullValue;
		}
		if (v.equals(Card.Values.C8)) {
			return -2 * iNullValue;
		}
		if (v.equals(Card.Values.C7)) {
			return -3 * iNullValue;
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
			List<Card> l = checkPlayableCards(c, 8);
			for (int i = 8; i < 16; i++) {
				Card cl = get(i).getLastCard();
				setColor(cl, l.contains(cl));
			}
		} else {
			Card cbest = getBestMove(8);
			if (cbest != null) {
				setColor(cbest, true);
			}
		}
		super.help();
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
				b.setTextColor(Color.BLACK);
			} else if (i < 4) {
				b.setTextColor(Color.RED);
			} else {
				b.setTextColor(Color.WHITE);
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
	public boolean isFinished() {
		for (int i = 0; i <= 16; i++) {
			if (get(i).getCardCount() > 0) {
				return false;
			}
		}
		return true;
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
				List<Card> l = checkPlayableCards(c, 8);
				if (!l.contains(card)) {
					help();
					plstMoves.clear();
					return;
				}
			}
		}
	}

	@Override
	public boolean mouseUp(List<Card> pLstMoves, Hand handTo) {
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
		List<Card> cards = hand.getCards();
		int cnt = 0;
		for (Card card : cards) {
			cnt += getVal(card, 0);
		}
		hand.setText(String.valueOf(cnt));
	}
}
