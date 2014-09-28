package de.brod.cardmaniac.game;

import java.io.Serializable;
import java.util.List;

import de.brod.cardmaniac.table.Button;
import de.brod.cardmaniac.table.Card;
import de.brod.cardmaniac.table.Deck;
import de.brod.cardmaniac.table.Hand;
import de.brod.opengl.OpenGLButton;
import de.brod.opengl.Sprite;

public class MauMau extends Game {

	public static class MauMauState implements Serializable {

		private static final long serialVersionUID = -696396603457811655L;

		boolean mayDraw = true;
		int currentPlayer = 0;
	}

	private Hand[] players;
	private Hand stack;
	private Hand tolon;
	private MauMauState state = new MauMauState();

	@Override
	public boolean actionDown(Card pCard, List<Sprite<Card>> plstMoveCards) {

		Hand hand = pCard.getHand();
		if (hand.equals(players[state.currentPlayer])) {
			plstMoveCards.add(pCard.getSprite());
			return false;
		}
		if (hand.equals(tolon) && state.mayDraw) {
			// if (pCard.equals(tolon.getLastCard())) {
			plstMoveCards.add(pCard.getSprite());
			return false;
			// }
		}

		pCard.setSelected(false);
		return false;
	}

	@Override
	public boolean actionUp(List<Sprite<Card>> plstMoveCards, Hand handTo) {
		if (handTo.equals(stack)) {
			state.currentPlayer = (state.currentPlayer + 1) % 4;
			state.mayDraw = true;
			for (Sprite<Card> sprite : plstMoveCards) {
				handTo.addCard(sprite.getReference());
			}
			checkStack();
			return true;
		}
		if (handTo.equals(players[state.currentPlayer])) {
			state.mayDraw = false;
			for (Sprite<Card> sprite : plstMoveCards) {
				handTo.addCard(sprite.getReference());
			}
			checkStack();
			return true;
		}
		return false;
	}

	private void checkStack() {
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

	@Override
	void createCards(Deck pDeck) {
		create32Cards(pDeck, false);
	}

	@Override
	void createHands(Deck pDeck, List<Hand> plstHands) {
		players = new Hand[] { new Hand(pDeck, 0, 0, 7, 0, 12, 52),
				new Hand(pDeck, 0, 2, 0, 5, 12, 0),
				new Hand(pDeck, 1, 7, 6, 7, 12, 0),
				new Hand(pDeck, 7, 2, 7, 5, 12, 0) };
		tolon = new Hand(pDeck, 2, 3.5f, 3, 3.5f, 12, 0);
		stack = new Hand(pDeck, 4, 3.5f, 5, 3.5f, 12, 52);
		for (Hand hand : players) {
			hand.setCenter(true);
			plstHands.add(hand);
		}
		plstHands.add(stack);
		plstHands.add(tolon);

	}

	@Override
	public void initButtons(Deck pDeck, List<OpenGLButton> lstButtons) {
		float y = 2f;
		lstButtons.add(new Button(pDeck, 1.5f, y, 2.9f, y, "Draw", null).getButton());
		lstButtons.add(new Button(pDeck, 4.1f, y, 5.5f, y, "Skip", null).getButton());
	}

	@Override
	public INextMove getNextMove() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	Serializable getSpecificValues() {
		return state;
	}

	@Override
	void initGame(Serializable specificValues) {
		state = (MauMauState) specificValues;
	}

}
