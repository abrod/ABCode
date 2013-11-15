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
import de.brod.cm.TextAlign;
import de.brod.gui.IAction;
import de.brod.gui.IDialogAction;
import de.brod.gui.StateHandler;
import de.brod.gui.shape.Button;
import de.brod.xml.XmlObject;

public class MauMau extends Game
{

	private Buttons buttons;
	private Button skipButton;

	private final Colors[] cls = { Colors.Clubs, Colors.Spades, Colors.Hearts,
		Colors.Diamonds };

	public MauMau(CardManiacView pCardManiacView)
	{
		super(pCardManiacView);
	}

	private void check4finshed()
	{
		for (int i = 1; i <= 4; i++)
		{
			Hand hand = get(i);
			if (hand.getCardCount() == 0)
			{
				int p = getSettingAsInt("points" + i);
				p++;
				setSettings("points" + i, p);
				hand.setText(String.valueOf(p));
				return;
			}
		}
	}

	@Override
	protected void createTitleCards(Hand hand)
	{
		hand.createCard(Values.C7, Colors.Clubs);
		hand.createCard(Values.C8, Colors.Spades);
		hand.createCard(Values.C7, Colors.Diamonds);
		hand.createCard(Values.C8, Colors.Hearts);
	}

	private void drawStack(Hand handTo, XmlObject settings)
	{
		Hand h0 = get(0);
		int ic = Math.max(1, settings.getAttributeAsInt("drawCardCount"));
		for (int i = 0; i < ic; i++)
		{
			if (h0.getCardCount() == 0)
			{
				updateStack();
			}
			Card c = h0.getLastCard();
			if (c != null)
			{
				c.moveTo(handTo);
			}
		}
		h0.setText("");
		settings.setAttribute("drawCardCount", 0);
		settings.setAttribute("drawCard", false);
		settings.setAttribute("matchValue", false);
		handTo.organize();
		h0.organize();
	}

	@Override
	public void getMenuItems(List<String> menuItems)
	{
		super.getMenuItems(menuItems);
		menuItems.add("Reset Score");
	}

	@Override
	public IAction getNextAction()
	{

		final XmlObject settings = getSettings();
		if (settings.getAttributeAsBoolean("jack"))
		{
			// player has to select color
			return null;
		}

		final Hand h0 = get(0);
		final Hand h5 = get(5);
		// move stack
		if (h0.getCardCount() == 0 && h5.getCardCount() > 1)
		{
			return new IAction() {

				@Override
				public void action()
				{
					updateStack();
				}
			};
		}

		final int iPlayer = settings.getAttributeAsInt("player");

		if (iPlayer > 0)
		{
			return new IAction() {

				@Override
				public void action()
				{
					Hand hand = get(iPlayer);
					Card playedCard = null;
					// if card could not be played
					if ((playedCard = playCard(hand)) == null)
					{
						if (settings.getAttributeAsBoolean("drawCard"))
						{
							// draw a card
							drawStack(hand, settings);
							hand.organize();
							settings.setAttribute("drawCard", false);
							return;
							// and try to play again
							// playedCard = playCard(hand);
						}
					}
					hand.organize();
					// set the next player
					setNextPlayer(playedCard, settings);
				}

				private Card playCard(Hand hand)
				{
					Card cPlay = getNextCard(hand);
					XmlObject xmlSettings = getSettings();
					if (cPlay != null)
					{
						cPlay.moveTo(h5);
						h5.organize();
						check4finshed();
						if (cPlay.getValue().equals(Card.Values.Jack))
						{
							int f = (int) (Math.random() * 4);
							settings.setAttribute("force", f + 1);

						}
						else
						{
							settings.setAttribute("force", 0);
						}
						showColorButtons(settings);
					}
					return cPlay;
				}

			};
		}
		// TODO Auto-generated method stub
		return null;
	}

	protected Card getNextCard(Hand hand)
	{
		XmlObject xmlSettings = getSettings();
		Card cPlay = null;
		double imax=-1;
		List<Card> lst=hand.getCards();
		for (Card c : lst)
		{
			if (matchesStack(c, xmlSettings))
 			{
				double i=Math.random();
				if (!c.getValue().equals(Values.Jack))
				{
					for (Card c1 : lst)
					{
						if (c1.getValue().equals(c.getValue()))
						{
							i++;
						}
						else if (c1.getColor().equals(c.getColor()))
						{
							i++;
						}
					}
				}
				if (i > imax)
				{
					cPlay = c;
					imax = i;
				}
				// if this is a jack, try another card ... else try
				// again
			}
		}
		return cPlay;
	}

	private XmlObject getSettings()
	{
		return buttons.getSettings();
	}

	@Override
	public boolean hasHistory()
	{
		return true;
	}

	@Override
	protected void help()
	{
		Hand player = get(4);
		Card nextCard = getNextCard(player);
		XmlObject settings = getSettings();
		if (nextCard == null)
		{
			for (Card c : player.getCards())
			{
				setColor(c, CardColor.RED);
			}
		}
		else
		{
			for (Card c : player.getCards())
			{
				if (c == nextCard)
				{
					setColor(c, CardColor.GREEN);
				}
				else if (matchesStack(c, settings))
				{
					setColor(c, CardColor.GRAY);
				}
				else
				{
					setColor(c, CardColor.RED);
				}
			}
		}
		CardColor col = nextCard == null ? CardColor.GREEN : CardColor.GRAY;
		if (!settings.getAttributeAsBoolean("drawCard"))
		{
			col = CardColor.RED;
		}
		for (Card c : get(0).getCards())
		{
			setColor(c, col);
		}
		super.help();
	}

	@Override
	public void initHands(boolean bLandscape)
	{

		float y2 = Card.maxCardY / 2;

		int left = 1;
		int right = 6;
		int top = 1;
		int maxCount = 8;
		if (bLandscape)
		{
			left = 0;
			right = 7;
			top = 0;
			maxCount = 10;
		}
		int maxCount2 = maxCount * 3 / 2;
		add(new Hand(0, 2, y2, 3, y2, 16));
		// add the players
		add(new Hand(1, left - 1, top, left - 1, Card.maxCardY - top, maxCount2));
		add(new Hand(2, left, 0, right, 0, maxCount));
		add(new Hand(3, right + 1, top, right + 1, Card.maxCardY - top,
					 maxCount2));
		add(new Hand(4, left, Card.maxCardY, right, Card.maxCardY, maxCount));

		// add the stacks
		add(new Hand(5, 4, y2, 5, y2, 16));
		get(1).setRotation(90f);
		get(3).setRotation(-90f);

		get(1).initText(TextAlign.RIGHT);
		get(2).initText(TextAlign.BOTTOM);
		get(3).initText(TextAlign.LEFT);
		get(4).initText(TextAlign.TOP);
		for (int i = 1; i <= 4; i++)
		{
			get(i).setCenter(true);
		}
		// set order
		get(4).setCardComperator(getColorOrder());
		// add a ButtonContainer
		buttons = new Buttons(99);
		skipButton = Button.Type.no.createButton(0,
			Card.getY(Card.maxCardY * 3 / 4), Card.getCardWidth(),
			new IAction() {

				@Override
				public void action()
				{
					XmlObject settings = getSettings();
					int iPlayer = settings.getAttributeAsInt("player");
					if (iPlayer == 0
						&& !settings.getAttributeAsBoolean("drawCard"))
					{
						setNextPlayer(null, settings);
						resetColors();
					}
				}

			});
		float py = Card.getY(Card.maxCardY * 1 / 4);
		float bwd = Card.getCardWidth();
		float px = Card.getX(3.5f) - bwd * 1.5f;
		for (int i = 0; i < cls.length; i++)
		{
			final int bt = i;
			Button b = Button.createTextButton(px + i * bwd, py, bwd,
				cls[i].toString(), new IAction() {

					@Override
					public void action()
					{
						XmlObject s = getSettings();
						s.setAttribute("jack", false);
						s.setAttribute("force", bt + 1);
						showColorButtons(s);
					}

				});
			if (i < 2)
			{
				b.setTextColor(Color.BLACK);
			}
			else
			{
				b.setTextColor(Color.RED);
			}
			b.setEnabled(false);
			buttons.add(b);
		}

		buttons.add(skipButton);

		add(buttons);
		// init the text
		get(0).setText(" ");

	}

	@Override
	public void initNewCards()
	{
		Hand h0 = get(0);
		h0.create32Cards();
		for (int i = 1; i <= 4; i++)
		{
			for (int j = 0; j < 6; j++)
			{
				h0.getLastCard().moveTo(get(i));
			}
		}
		h0.getLastCard().moveTo(get(5));

		for (int i = 0; i < size(); i++)
		{
			Hand hand = get(i);
			if (i < 4)
			{
				hand.setCovered(999);
			}
			else
			{
				hand.setCovered(0);
			}
		}

		XmlObject settings = getSettings();
		settings.setAttribute("player", 0);
		settings.setAttribute("drawCard", true);
		settings.setAttribute("drawCardCount", 0);
		settings.setAttribute("matchValue", false);
		settings.setAttribute("jack", false);
		settings.setAttribute("force", 0);
		showColorButtons(settings);
		h0.setText(" ");
	}

	@Override
	public String getFinishedText()
	{
		for (int i = 1; i <= 4; i++)
		{
			if (get(i).getCardCount() == 0)
			{
				if (i == 4)
				{
					return "You have won.";
				}
				else
				{
					return "Player " + i + " won.";
				}
			}
		}
		return null;
	}

	private boolean matchesStack(Card card, XmlObject settings)
	{
		Values cValue = card.getValue();

		if (settings.getAttributeAsInt("drawCardCount") > 0)
		{
			if (!cValue.equals(Values.C7))
			{
				return false;
			}
		}
		if (settings.getAttributeAsBoolean("matchValue"))
		{
			if (!cValue.equals(get(5).getLastCard().getValue()))
			{
				return false;
			}
		}
		// jack is the joker
		if (cValue.equals(Values.Jack))
		{
			return true;
		}

		Card lastCard = get(5).getLastCard();

		int f = settings.getAttributeAsInt("force");
		if (f > 0)
		{
			if (!card.getColor().equals(cls[f - 1]))
			{
				return false;
			}
			return true;
		}
		Values lcValue = lastCard.getValue();

		if (lcValue.equals(cValue))
		{
			return true;
		}
		if (lastCard.getColor().equals(card.getColor()))
		{
			return true;
		}

		return false;
	}

	@Override
	public void menuPressed(String sItem, StateHandler stateHandler)
	{
		if (sItem.equals("Reset Score"))
		{
			showMessage("Question", "Do you really want to reset the scores",
				new IDialogAction() {

					@Override
					public void action()
					{
						for (int i = 1; i <= 4; i++)
						{
							Hand hand = get(i);
							hand.setCenter(true);
							hand.setText("0");
							setSettings("points" + i, 0);
						}
					}

					@Override
					public String getName()
					{
						return "Yes";
					}
				}, new IDialogAction() {

					@Override
					public void action()
					{
						// make nothing
					}

					@Override
					public String getName()
					{
						return "No";
					}
				});
		}
		else
		{
			super.menuPressed(sItem, stateHandler);
		}
	}

	@Override
	public void mouseDown(List<Card> plstMoves)
	{
		resetColors();
		XmlObject settings = getSettings();
		int iPlayer = settings.getAttributeAsInt("player");
		if (iPlayer != 0 || settings.getAttributeAsBoolean("jack"))
		{
			// it's not your turn
			plstMoves.clear();
			return;
		}

		Card cl = plstMoves.get(0);
		int hid = cl.getHand().getId();
		if (hid == 0)
		{
			// ok
			if (!settings.getAttributeAsBoolean("drawCard"))
			{
				help();
				plstMoves.clear();
				return;
			}
		}
		else if (hid == 4)
		{
			if (!matchesStack(cl, settings))
			{
				help();
				plstMoves.clear();
				return;
			}
		}
		else
		{
			plstMoves.clear();
			return;
		}
	}

	@Override
	public boolean mouseUp(List<Card> pLstMoves, Hand handTo)
	{
		XmlObject settings = getSettings();
		int iPlayer = settings.getAttributeAsInt("player");
		if (iPlayer != 0 || settings.getAttributeAsBoolean("jack"))
		{
			// it's not your turn
			return false;
		}
		Card selectedCard = pLstMoves.get(0);
		Hand selectedHand = selectedCard.getHand();
		int hSelectedId = selectedHand.getId();
		if (selectedHand == handTo)
		{
			if (hSelectedId == 0 && settings.getAttributeAsBoolean("drawCard"))
			{
				// draw a card to hand (from stack)
				handTo = get(4);
			}
			else
			{
				return false;
			}
		}
		if (handTo == null)
		{
			// ignore this
			return false;
		}
		int hToId = handTo.getId();
		// draw a card or play a card
		if (hSelectedId == 0 && hToId == 4)
		{
			// draw a card
			if (settings.getAttributeAsBoolean("drawCard"))
			{
				drawStack(handTo, settings);
				return true;
			}
			else
			{
				return false;
			}
		}
		else if (hSelectedId == 4 && hToId == 5)
		{
			// play a card
			if (!matchesStack(selectedCard, settings))
			{
				return false;
			}
			setNextPlayer(selectedCard, settings);
		}
		else
		{
			// no valid move
			return false;
		}
		selectedCard.moveTo(handTo);
		// reset colors
		resetColors();
		check4finshed();
		if (selectedCard.getValue().equals(Card.Values.Jack))
		{
			settings.setAttribute("jack", true);
		}
		else
		{
			settings.setAttribute("jack", false);
		}
		settings.setAttribute("force", 0);
		showColorButtons(settings);
		return true;
	}

	@Override
	public void prepareUpdate(StateHandler stateHandler,
							  Hashtable<Button.Type, Button> htTitleButtons)
	{
		XmlObject settings = getSettings();
		if (settings.getAttributeAsInt("player") == 0)
		{
			skipButton.setEnabled(!settings.getAttributeAsBoolean("drawCard"));
		}
		else
		{
			skipButton.setEnabled(false);
		}
		super.prepareUpdate(stateHandler, htTitleButtons);
		showColorButtons(settings);
		for (int i = 1; i <= 4; i++)
		{
			get(i).setText(String.valueOf(getSettingAsInt("points" + i)));
		}
	}

	private void setNextPlayer(Card playedCard, XmlObject settings)
	{
		int iPlayer = settings.getAttributeAsInt("player");
		int iDrawCardCount = settings.getAttributeAsInt("drawCardCount");
		boolean bNextPlayerMayDrawCard = true;
		settings.setAttribute("matchValue", false);
		if (playedCard != null)
		{
			Values value = playedCard.getValue();
			if (value.equals(Values.C7))
			{
				iDrawCardCount += 2;
			}
			else if (value.equals(Values.C8))
			{
				bNextPlayerMayDrawCard = false;
				settings.setAttribute("matchValue", true);
			}
		}
		else
		{
			iDrawCardCount = 0;
		}
		settings.setAttribute("drawCardCount", iDrawCardCount);
		if (iDrawCardCount > 0)
		{
			get(0).setText("+" + iDrawCardCount);
		}
		else
		{
			get(0).setText("");
		}
		settings.setAttribute("player", (iPlayer + 1) % 4);
		settings.setAttribute("drawCard", bNextPlayerMayDrawCard);

	}

	private void showColorButtons(XmlObject settings)
	{
		int rt = settings.getAttributeAsInt("force") - 1;
		if (settings.getAttributeAsBoolean("jack"))
		{
			for (int i = 0; i < 4; i++)
			{
				buttons.setEnabled(i, true);
			}
		}
		else
		{
			for (int i = 0; i < 4; i++)
			{
				buttons.setEnabled(i, rt == i);
			}
		}
	}

	private void updateStack()
	{
		Hand h0 = get(0);
		Hand h5 = get(5);
		Card[] cards = h5.getCards().toArray(new Card[0]);
		for (int i = 0; i < cards.length - 1; i++)
		{
			cards[i].moveTo(h0);
		}
		h0.shuffleCards();
		h0.organize();
		h5.organize();
	}

}
