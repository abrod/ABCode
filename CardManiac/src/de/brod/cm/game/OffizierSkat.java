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

import android.graphics.Color;
import de.brod.cm.Buttons;
import de.brod.cm.Card;
import de.brod.cm.Card.Colors;
import de.brod.cm.Card.Values;
import de.brod.cm.CardManiacView;
import de.brod.cm.Hand;
import de.brod.gui.IAction;
import de.brod.gui.StateHandler;
import de.brod.gui.shape.Button;
import de.brod.xml.XmlObject;

public class OffizierSkat extends Game {

	private final String[] cls = { Colors.Clubs.toString(),
			Colors.Spades.toString(), Colors.Hearts.toString(),
			Colors.Diamonds.toString(), "J" };
	private Buttons buttons;

	public OffizierSkat(CardManiacView pCardManiacView) {
		super(pCardManiacView);
	}

	@Override
	protected void createTitleCards(Hand hand) {
		hand.createCard(Values.Jack, Colors.Hearts);
		hand.createCard(Values.Jack, Colors.Diamonds);
		hand.createCard(Values.Jack, Colors.Spades);
		hand.createCard(Values.Jack, Colors.Clubs);
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
		// TODO Auto-generated method stub
		return null;
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
			return getVal(cp0) < getVal(cp1);
		}
		// if trump
		int t=getSettings().getAttributeAsInt("trumpf");
		if (t==0){
			if (cp1.getColor().equals(Colors.Clubs)){
				return true;
			}
		} else if (t==1){
			if (cp1.getColor().equals(Colors.Spades)){
				return true;
			}
		} else if (t==2){
			if (cp1.getColor().equals(Colors.Hearts)){
				return true;
			}
		} else if (t==3){
			if (cp1.getColor().equals(Colors.Diamonds)){
				return true;
			}
		}
		return false;
	}
	
	private int getVal(Card c){
		Card.Values v=c.getValue();
		if (v.equals(Card.Values.Ace))
		{
			return 11;
		}
		if (v.equals(Card.Values.C10))
		{
			return 10;
		}
		if (v.equals(Card.Values.King))
		{
			return 4;
		}
		if (v.equals(Card.Values.Queen))
		{
			return 3;
		}
		if (v.equals(Card.Values.Jack))
		{
			return 2;
		}
		if (v.equals(Card.Values.C9))
		{
			return -1;
		}
		if (v.equals(Card.Values.C8))
		{
			return -2;
		}
		if (v.equals(Card.Values.C7))
		{
			return -3;
		}
		return c.getValueId();
	}

	@Override
	public boolean hasHistory() {
		return true;
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
			bdy = Button.height;
		} else {
			add(new Hand(iCount++, 3.5f, Card.maxCardY / 2 + 0.5f, 4,
					Card.maxCardY / 2 + 0.5f, 2));
			add(new Hand(iCount++, 6, 0.5f, 7, 0.5f, 16));
			add(new Hand(iCount++, 6, dy[2] + 0.5f, 7, dy[2] + 0.5f, 16));
			px = Card.getX(3.5f) - Button.width * 2f;
			py = Card.getY(Card.maxCardY / 2) + Card.getCardHeight() / 2;
			bdx = Button.width;
			bdy = 0;
		}
		get(17).setCovered(99);
		get(18).setCovered(99);

		for (int i = 0; i < cls.length; i++) {
			final int bt = i;
			Button b = Button.createTextButton(px + i * bdx, py + i * bdy,
					cls[i], new IAction() {

						@Override
						public void action() {
							XmlObject settings = getSettings();
							boolean pickColor = settings.getAttributeAsBoolean("pickColor");
							if (!pickColor)
								return;
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

		// coverCards();
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
		settings.setAttribute("player", 1);
		settings.setAttribute("pickColor", true);
		coverCards();
	}

	private XmlObject getSettings() {
		return buttons.getSettings();
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
	}

	@Override
	public void mouseDown(List<Card> plstMoves) {
		if (plstMoves.size() > 0) {
			Hand hand = plstMoves.get(0).getHand();

			int playerCard = (hand.getId() - get(0).getId()) / 8;
			int current = getSettings().getAttributeAsInt("player");
			if (playerCard != current) {
				plstMoves.clear();
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
}
