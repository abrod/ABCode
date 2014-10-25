package de.brod.cardmaniac.game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;
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

	static class MMState implements Serializable {

		private static final long	serialVersionUID	= 9112483104576267722L;

		protected boolean			mayDraw				= true;
		protected boolean			nextPlayer			= false;
		protected int				currentPlayer		= 0;
		protected int				forceValue			= -1;
		protected int				forceColor			= -1;
		public int					additionalDraw		= 0;

		protected boolean checkNextPlayer() {
			if (nextPlayer) {
				currentPlayer = (currentPlayer + 1) % 4;
				nextPlayer = false;
				return true;
			}
			return false;
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("[Player");
			sb.append(currentPlayer);
			if (mayDraw) {
				sb.append(" mayDraw");
			}
			if (nextPlayer) {
				sb.append(" nextPlayer");
			}
			if (forceColor >= 0) {
				sb.append(" forceColor=").append(forceColor);
			}
			if (forceValue >= 0) {
				sb.append(" forceValue=").append(forceValue);
			}
			if (additionalDraw > 0) {
				sb.append(" additionalDraw=").append(additionalDraw);
			}
			sb.append("]");

			return sb.toString();
		}
	}

	public void drawCard() {
		checkTolon();
		if (tolon.getCardsCount() > 0) {
			for (int i = 0; i < Math.max(1, state.additionalDraw); i++) {
				players[state.currentPlayer].addCard(tolon.getLastCard());
				checkTolon();
			}
		}
		state.mayDraw = false;
		state.additionalDraw = 0;
		state.forceValue = -1;
		checkButtons();
	}

	public boolean mayDraw() {
		return state.mayDraw && tolon.getCardsCount() > 0;
	}

	public void playCard(Card card, boolean pbRandomValue) {
		stack.addCard(card);
		// set forceColor
		if (card.getCardValue().equals(CardValue.bube)) {
			if (pbRandomValue) {
				state.forceColor = (int) (Math.random() * 4 + 1);
			} else {
				state.forceColor = 0;
			}
		} else {
			state.forceColor = -1;
		}
		// set other values
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
		// next player
		state.nextPlayer = true;
	}

	public void skip() {
		state.nextPlayer = true;
		state.mayDraw = true;
		state.forceValue = -1;
		state.additionalDraw = 0;
		checkButtons();
	}

	private Hand[]			players;
	private Hand			tolon;
	private Hand			stack;
	private MMState			state;
	private OpenGLButton	buttonDraw;
	private OpenGLButton	buttonSkip;
	private OpenGLButton[]	buttonIcon;

	private int				lastAddDraw	= 0;

	@Override
	public boolean actionDown(Card pCard, List<ISprite<Card>> plstMoveCards) {

		if (state.forceColor == 0) {
			// user has to select the color via button
			return false;
		}
		state.checkNextPlayer();
		if (state.currentPlayer != 0) {
			return false;
		}

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
			checkButtons();
			checkTolon();
			Log.d("Player", state.toString() + " play card " + lastCard);
			return true;
		}
		if (handTo.equals(players[state.currentPlayer]) && mayDraw()) {
			drawCard();
			checkButtons();
			checkTolon();
			Log.d("Player", state.toString() + " draw cards");
			return true;
		}
		return false;
	}

	@Override
	void assignCardsToHands(List<Hand> plstHands, List<Card> lstCards) {
		state = new MMState();
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
			boolean bDraw = mayDraw();
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

	private void checkTolon() {
		if (tolon.getCardsCount() == 0) {
			Card[] arrCards = stack.getCards().toArray(new Card[0]);
			for (int i = 0; i < arrCards.length - 1; i++) {
				tolon.addCard(arrCards[i]);
			}
			tolon.shuffleCards();
			tolon.organize();
			stack.organize();
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
		if (state.forceColor == 0) {
			// user has to select the color
			return null;
		}
		return new INextMove() {

			@Override
			public boolean hasNext() {

				StringBuilder sb = new StringBuilder();
				state.checkNextPlayer();
				Log.d("Player", sb.toString());
				if (state.currentPlayer != 0) {
					// get next move for player
					sb.append("Player ").append(state.currentPlayer);
					if (players[state.currentPlayer].getCardsCount() == 0) {
						state.nextPlayer = true;
						sb.append(" -> no cards");
					} else {
						List<Card> cards = players[state.currentPlayer]
								.getCards();
						List<Card> lstPossible = new ArrayList<Card>();
						Card lastCard = stack.getLastCard();
						for (Card card : cards) {
							if (matches(card, lastCard)) {
								lstPossible.add(card);
								sb.append(" (").append(card.toString())
								.append(")");
							} else {
								sb.append(" ").append(card.toString());
							}
						}
						if (lstPossible.size() > 0) {
							Card card = lstPossible
									.get((int) (Math.random() * lstPossible
											.size()));
							// stack.organize();
							// players[state.currentPlayer].organize();
							playCard(card, true);
							sb.append(" -> play ").append(card.toString());
						} else {
							if (state.mayDraw) {
								drawCard();
								sb.append(" -> draw ");
							} else {
								skip();
								sb.append(" -> skip ");
							}
						}
					}
					checkButtons();
					checkTolon();
					sb.append(" ").append(state.toString());
					Log.d("Player", sb.toString());
					return true;
				}
				sb.append(" -> has no next");
				Log.d("Player", sb.toString());
				return false;
			}
		};
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
				drawCard();
			}

			@Override
			public String getTitle() {
				return "Draw";
			}
		};
		IAction skipAction = new IAction() {

			@Override
			public void doAction() {
				skip();
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
				public void doAction() {
					state.forceColor = cardColor.ordinal() + 1;
				}

				@Override
				public String getTitle() {
					return "";
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
		state = (MMState) specificValues;
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
