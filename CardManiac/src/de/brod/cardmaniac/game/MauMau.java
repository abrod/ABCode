package de.brod.cardmaniac.game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import de.brod.cardmaniac.CardColor;
import de.brod.cardmaniac.CardValue;
import de.brod.cardmaniac.table.Button;
import de.brod.cardmaniac.table.Card;
import de.brod.cardmaniac.table.Deck;
import de.brod.cardmaniac.table.Hand;
import de.brod.opengl.IAction;
import de.brod.opengl.ISprite;
import de.brod.opengl.OpenGLButton;
import de.brod.opengl.Rect;

public class MauMau extends Game {

	public static class MauMauState implements Serializable {

		private static final long	serialVersionUID	= -696396603457811655L;

		int							currentPlayer		= 0;
		int							forceColor			= -1;
		boolean						mayDraw				= true;
		boolean						nextPlayer			= false;
		int							forceValue			= -1;
		int							additionalDraw		= 0;

		public void nextPlayer(boolean pbSet) {
			if (pbSet) {
				currentPlayer = (currentPlayer + 1) % 4;
				nextPlayer = false;
			} else {
				nextPlayer = true;
			}
		}

	}

	private OpenGLButton	buttonDraw;
	private OpenGLButton	buttonSkip;
	private Hand[]			players;
	private Hand			stack;
	private MauMauState		state	= new MauMauState();
	private Hand			tolon;
	private OpenGLButton[]	buttonIcon;
	private int				lastAddDraw;

	@Override
	public boolean actionDown(Card pCard, List<ISprite<Card>> plstMoveCards) {

		Hand hand = pCard.getHand();
		if (hand.equals(players[state.currentPlayer])) {
			plstMoveCards.add(pCard.getSprite());
			return false;
		}
		if (hand.equals(tolon) && state.mayDraw) {
			plstMoveCards.add(pCard.getSprite());
			return false;
		}

		pCard.setSelected(false);
		return false;
	}

	@Override
	public boolean actionUp(List<ISprite<Card>> plstMoveCards, Hand handTo) {
		Card lastCard = plstMoveCards.get(plstMoveCards.size() - 1)
				.getReference();
		if (handTo.equals(stack)) {
			if (!matches(lastCard, stack.getLastCard())) {
				return false;
			}
			playCard(lastCard, false);
			checkStack(true);
			return true;
		}
		if (handTo.equals(players[state.currentPlayer]) && state.mayDraw) {
			drawCards(players[state.currentPlayer]);
			checkStack(true);
			return true;
		}
		return false;
	}

	private void drawCards(Hand handTo) {
		state.mayDraw = false;

		for (int i = 0; i < Math.max(1, state.additionalDraw); i++) {
			Card lastCard = tolon.getLastCard();
			if (lastCard != null) {
				handTo.addCard(lastCard);
			}
			checkTolon(true);
		}
		state.additionalDraw = 0;
	}

	@Override
	void assignCardsToHands(List<Hand> plstHands, List<Card> lstCards) {
		state = new MauMauState();
		int iCount = 0;
		for (int i = 0; i < 6; i++) {
			for (Hand player : players) {
				player.addCard(lstCards.get(iCount++));
			}
		}
		stack.addCard(lstCards.get(iCount++));
		for (int i = iCount; i < lstCards.size(); i++) {
			tolon.addCard(lstCards.get(i));
		}
	}

	private void checkButtons() {
		if (state != null && buttonDraw != null) {
			boolean bDraw = state.mayDraw && tolon.getCardsCount() > 0;
			buttonDraw.setEnabled(bDraw);
			if (lastAddDraw != state.additionalDraw) {
				lastAddDraw = state.additionalDraw;
				if (lastAddDraw > 0) {
					buttonDraw.setText("+" + lastAddDraw);
				} else {
					buttonDraw.setText("Draw");
				}
			}
			tolon.getRect().setEnabled(bDraw);
			buttonSkip.setEnabled(!bDraw);

			for (int i = 0; i < players.length; i++) {
				Hand player = players[i];
				Rect rect = player.getRect();
				rect.setEnabled(state.currentPlayer == i);
			}

			if (state.forceColor <= 0) {
				for (OpenGLButton element : buttonIcon) {
					element.setEnabled(state.forceColor == 0);
				}
			} else {
				for (int i = 0; i < buttonIcon.length; i++) {
					buttonIcon[i].setEnabled(i + 1 == state.forceColor);
				}
			}
		}
	}

	private void checkStack(boolean bOrganize) {
		checkTolon(bOrganize);
		checkButtons();
	}

	private void checkTolon(boolean bOrganize) {
		if (tolon.getCardsCount() == 0) {
			Card[] arrCards = stack.getCards().toArray(new Card[0]);
			for (int i = 0; i < arrCards.length - 1; i++) {
				tolon.addCard(arrCards[i]);
			}
			tolon.shuffleCards();
			if (bOrganize) {
				tolon.organize();
				stack.organize();
			}
		}
	}

	@Override
	void createCards(Deck pDeck) {
		create32Cards(pDeck, true);
	}

	@Override
	void createHands(Deck pDeck, List<Hand> plstHands) {
		float border = 0.05f;
		int v = 0;
		players = new Hand[] {
				new Hand(pDeck, 0.5f, 0, 6.5f, 0, border, 12, 52),
				new Hand(pDeck, 0, 2, 0, 5, border, 12, v),
				new Hand(pDeck, 0.5f, 7, 6.5f, 7, border, 12, v),
				new Hand(pDeck, 7, 2, 7, 5, border, 12, v) };
		tolon = new Hand(pDeck, 1.75f, 3.5f, 2.75f, 3.5f, border, 12, 0);
		stack = new Hand(pDeck, 4.25f, 3.5f, 5.25f, 3.5f, border, 12, 52);
		for (Hand hand : players) {
			hand.setCenter(true);
			plstHands.add(hand);
		}
		plstHands.add(stack);
		plstHands.add(tolon);

	}

	@Override
	public INextMove getNextMove() {

		if ((state.currentPlayer != 0 || state.nextPlayer)
				&& state.forceColor != 0) {
			return new INextMove() {

				@Override
				public boolean hasNext() {
					if (state.nextPlayer) {
						state.nextPlayer(true);
					}
					if (players[state.currentPlayer].getCardsCount() > 0
							&& state.currentPlayer != 0) {

						List<Card> cards = players[state.currentPlayer]
								.getCards();
						List<Card> lstPossible = new ArrayList<Card>();
						Card lastCard = stack.getLastCard();
						for (Card card : cards) {
							if (matches(card, lastCard)) {
								lstPossible.add(card);
							}
						}
						if (lstPossible.size() > 0) {
							Card card = lstPossible
									.get((int) (Math.random() * lstPossible
											.size()));
							// stack.organize();
							// players[state.currentPlayer].organize();
							playCard(card, true);
						} else {
							if (state.mayDraw) {
								state.mayDraw = false;
								drawCards(players[state.currentPlayer]);
							} else {
								// skip
								state.nextPlayer(false);
								state.mayDraw = true;
							}
							state.forceValue = -1;
							state.additionalDraw = 0;
						}
						checkStack(false);
						return true;
					}
					checkStack(false);
					return false;
				}
			};
		}
		return null;
	}

	protected void playCard(Card card, boolean pbRandomValue) {
		state.nextPlayer(false);
		stack.addCard(card);
		if (card.getCardValue().equals(CardValue.bube)) {
			if (pbRandomValue) {
				state.forceColor = (int) (Math.random() * 4 + 1);
			} else {
				state.forceColor = 0;
			}
		} else {
			state.forceColor = -1;
		}
		if (card.getCardValue().equals(CardValue.c8)) {
			state.mayDraw = false;
			state.forceValue = CardValue.c8.ordinal();
			state.additionalDraw = 0;
		} else if (card.getCardValue().equals(CardValue.c7)) {
			state.mayDraw = true;
			state.forceValue = CardValue.c7.ordinal();
			state.additionalDraw += 2;
		} else {
			state.mayDraw = true;
			state.forceValue = -1;
			state.additionalDraw = 0;
		}
	}

	@Override
	Serializable getSpecificValues() {
		return state;
	}

	@Override
	public void initButtons(Deck pDeck, List<OpenGLButton> lstButtons) {
		float y = 2f;
		IAction drawAction = new IAction() {

			@Override
			public void doAction() {
				state.mayDraw = false;
				players[state.currentPlayer].addCard(tolon.getLastCard());

				checkStack(true);
			}

			@Override
			public String getTitle() {
				return "Draw";
			}
		};
		IAction skipAction = new IAction() {

			@Override
			public void doAction() {
				state.nextPlayer(true);
				state.mayDraw = true;
				state.forceValue = -1;
				checkButtons();
			}

			@Override
			public String getTitle() {
				return "Skip";
			}
		};

		buttonDraw = Button.createButton(pDeck, 1.5f, y, 2.9f, y, "Draw",
				drawAction);
		lstButtons.add(buttonDraw);
		buttonSkip = Button.createButton(pDeck, 4.1f, y, 5.5f, y, "Skip",
				skipAction);
		lstButtons.add(buttonSkip);

		y = 5f;
		final CardColor[] colors = CardColor.values();
		buttonIcon = new OpenGLButton[colors.length];
		for (int i = 0; i < colors.length; i++) {
			float x = 1.5f + i * 1.33f;
			final CardColor cardColor = colors[i];
			buttonIcon[i] = Button.createButton(pDeck, x, y, x, y,
					cardColor.getChar(), new IAction() {

				@Override
				public String getTitle() {
					return "";
				}

				@Override
				public void doAction() {
					state.forceColor = cardColor.ordinal() + 1;
				}
			});
			buttonIcon[i].setTextColor(cardColor.getColor());
			buttonIcon[i].setEnabled(false);
			lstButtons.add(buttonIcon[i]);

		}

		checkButtons();
	}

	@Override
	void initGame(Serializable specificValues) {
		state = (MauMauState) specificValues;
		checkButtons();
	}

	private boolean matches(Card card, Card lastCard) {

		if (lastCard == null) {
			return true;
		}
		CardValue cardValue = card.getCardValue();
		CardColor cardColor = card.getCardColor();
		if (state.forceValue > 0) {
			return cardValue.ordinal() == state.forceValue;
		}
		if (state.forceColor > 0) {
			if (cardColor.ordinal() + 1 == state.forceColor) {
				return true;
			}
		} else {
			if (lastCard.getCardColor().equals(cardColor)) {
				return true;
			}
			if (lastCard.getCardValue().equals(cardValue)) {
				return true;
			}
		}
		if (cardValue.equals(CardValue.bube)) {
			return true;
		}

		return false;
	}
}
