package de.brod.cm.game;

import java.util.Hashtable;
import java.util.List;

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

public class MauMau extends Game
{

	private Buttons buttons;
	private Button skipButton;

	public MauMau(CardManiacView pCardManiacView)
	{
		super(pCardManiacView);
	}
	
	public void getMenuItems(List<String> menuItems)
	{
		super.getMenuItems(menuItems);
		menuItems.add("Reset score");
	}

	public void menuPressed(String sItem, StateHandler stateHandler)
	{
		if(sItem.equals("Reset score")){
			for (int i = 1; i <= 4; i++)
			{
				Hand hand = get(i);
				hand.setCenter(true);
				hand.setText("0");
				setSettings("points"+i,0);
			}
		} else{
			super.menuPressed(sItem,stateHandler);
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

	@Override
	public IAction getNextAction()
	{

		if (isFinished())
		{
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
		final XmlObject settings = getSettings();
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
					Card cPlay = null;
					XmlObject xmlSettings = getSettings();
					for (Card c : hand.getCards())
					{
						if (matchesStack(c, xmlSettings))
						{
							cPlay = c;
							break;
						}
					}
					if (cPlay != null)
					{
						cPlay.moveTo(h5);
						h5.organize();
						check4finshed();
					}
					return cPlay;
				}
			};
		}
		// TODO Auto-generated method stub
		return null;
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
		add(new Hand(0, 2, y2, 3, y2, 16));
		// add the players
		add(new Hand(1, left - 1, top, left - 1, Card.maxCardY - top, maxCount));
		add(new Hand(2, left, 0, right, 0, maxCount));
		add(new Hand(3, right + 1, top, right + 1, Card.maxCardY - top,
					 maxCount));
		add(new Hand(4, left, Card.maxCardY, right, Card.maxCardY, maxCount));

		// add the stacks
		add(new Hand(5, 4, y2, 5, y2, 16));

		for (int i = 0; i < size(); i++)
		{
			Hand hand = get(i);
			if (i < 4)
			{
				hand.setAngle(180);
			}
			else
			{
				hand.setAngle(0);
			}
		}

		get(1).initText(TextAlign.RIGHT);
		get(2).initText(TextAlign.BOTTOM);
		get(3).initText(TextAlign.LEFT);
		get(4).initText(TextAlign.TOP);
		for (int i = 1; i <= 4; i++)
		{
			Hand hand = get(i);
			hand.setCenter(true);
			hand.setText(""+getSettingAsInt("points"+i));
		}
		// set order
		get(4).setCardComperator(getColorOrder());
		// add a ButtonContainer
		buttons = new Buttons(99);
		IAction skipAction = new IAction() {

			@Override
			public void action()
			{
				XmlObject settings = getSettings();
				int iPlayer = settings.getAttributeAsInt("player");
				if (iPlayer == 0 && !settings.getAttributeAsBoolean("drawCard"))
				{
					setNextPlayer(null, settings);
				}
			}
		};
		skipButton = Button.Type.no.createButton(0,
												 Card.getY(Card.maxCardY * 3 / 4), skipAction);
		buttons.add(skipButton);
		add(buttons);
		// init the text
		get(0).setText("");

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

		XmlObject settings = getSettings();
		settings.setAttribute("player", 0);
		settings.setAttribute("drawCard", true);
		settings.setAttribute("drawCardCount", 0);
		settings.setAttribute("matchValue", false);
		h0.setText("");
	}

	private boolean isFinished()
	{
		for (int i = 1; i <= 4; i++)
		{
			if (get(i).getCardCount() == 0)
			{
				return true;
			}
		}
		return false;
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

		// jack is the joker
		if (cValue.equals(Values.Jack))
		{
			return true;
		}

		Card lastCard = get(5).getLastCard();
		Values lcValue = lastCard.getValue();

		// jack is the joker
		if (cValue.equals(Values.Jack))
		{
			return true;
		}

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
	public void mouseDown(List<Card> plstMoves)
	{
		if (isFinished())
		{
			plstMoves.clear();
		}
	}

	@Override
	public boolean mouseUp(List<Card> pLstMoves, Hand handTo)
	{
		XmlObject settings = getSettings();
		int iPlayer = settings.getAttributeAsInt("player");
		if (iPlayer != 0)
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
		check4finshed();
		return true;
	}

	private void check4finshed()
	{
		for (int i = 1; i <= 4; i++)
		{
			Hand hand=get(i);
			if (hand.getCardCount() == 0)
			{
				int p=getSettingAsInt("points" + i);
				p++;
				setSettings("points" + i,p);
				hand.setText(""+p);
				return;
			}
		}
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

}
