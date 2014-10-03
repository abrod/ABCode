package de.brod.cardmaniac.game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.brod.cardmaniac.CardValue;
import de.brod.cardmaniac.table.Card;
import de.brod.cardmaniac.table.Deck;
import de.brod.cardmaniac.table.Hand;
import de.brod.opengl.ISprite;

public class Solitaire extends Patience {

	private List<StackHand> lstStackHand;
	private List<Hand> lstAutoMoveHand;
	private Hand handTolon;
	private Hand handStack;
	private List<Hand> lstHandTop;

	@Override
	void assignCardsToHands(List<Hand> plstHands, List<Card> lstCards) {
		Iterator<Card> iterator = lstCards.iterator();
		for (int i = 0; i < 7; i++) {
			StackHand hand = lstStackHand.get(i);
			for (int j = 0; j <= i; j++) {
				hand.addCard(iterator.next());
			}
			hand.setCountVisible(1);
		}
		// add remaining to stack
		while (iterator.hasNext()) {
			handTolon.addCard(iterator.next());
		}
	}

	@Override
	void createHands(Deck pDeck, List<Hand> plstHands) {
		lstStackHand = new ArrayList<StackHand>();
		lstAutoMoveHand = new ArrayList<Hand>();
		lstHandTop = new ArrayList<Hand>();

		for (int i = 0; i < 7; i++) {
			lstStackHand.add(new StackHand(pDeck, i / 6f * 7, 5.5f, i / 6f * 7,
					0, 12, 1));
		}
		plstHands.addAll(lstStackHand);
		handTolon = new Hand(pDeck, 6.25f, 7, 7, 7, 16, 0);
		plstHands.add(handTolon);
		handStack = new Hand(pDeck, 4.5f, 7, 5.25f, 7, 16, 52);
		plstHands.add(handStack);

		for (int i = 0; i < 4; i++) {
			Hand hand = new Hand(pDeck, i, 7, i, 7, 52, 52);
			lstHandTop.add(hand);
		}
		plstHands.addAll(lstHandTop);

		lstAutoMoveHand.addAll(lstStackHand);
		lstAutoMoveHand.add(handStack);
	}

	class StackHand extends Hand {

		private int _countInvisible;

		public StackHand(Deck pDeck, float x1, float y1, float x2, float y2,
				int piCount, int iCountVisible) {
			super(pDeck, x1, y1, x2, y2, piCount, iCountVisible);
		}

		@Override
		public void setCountVisible(int i) {
			super.setCountVisible(i);
			_countInvisible = getCardsCount() - i;
			setVisible();
		}

		@Override
		public int getCountVisible() {
			int cardsCount = getCardsCount();
			_countInvisible = Math.min(_countInvisible, cardsCount - 1);
			return Math.max(0, cardsCount - _countInvisible);
		}

	}

	@Override
	void createCards(Deck pDeck) {
		create52Cards(pDeck, true);
	}

	@Override
	public boolean actionUp(List<ISprite<Card>> plstCards, Hand handTo) {
		// don't move to stack or tolon
		if (handStack.equals(handTo) || handTolon.equals(handTo)) {
			return false;
		}
		Card card0 = plstCards.get(0).getReference();
		if (lstHandTop.contains(handTo)) {
			if (plstCards.size() > 1) {
				return false;
			}
			if (!isNext(handTo.getLastCard(), card0, false)) {
				return false;
			}
		} else {
			if (handTo.getCardsCount() == 0) {
				if (!card0.getCardValue().equals(CardValue.koenig)) {
					return false;
				}
			} else if (!isNext(handTo.getLastCard(), card0, true)) {
				return false;
			}

		}
		for (ISprite<Card> sprite : plstCards) {
			handTo.addCard(sprite.getReference());
		}
		return true;
	}

	@Override
	public boolean actionDown(Card pCard, List<ISprite<Card>> plstMoveCards) {
		Hand hand = pCard.getHand();
		if (handTolon.equals(hand)) {
			if (pCard.equals(handTolon.getLastCard())) {
				handStack.addCard(pCard);
				if (handTolon.getCardsCount() == 0) {
					Card[] cards = handStack.getCards().toArray(new Card[0]);
					for (int i = 0; i < cards.length - 1; i++) {
						handTolon.addCard(cards[i]);
					}
				}

				handStack.organize();
				handTolon.organize();
				// plstMoveCards.add(pCard.getSprite());
				pCard.setSelected(false);
				return true;
			}
			return false;
		}
		// only select visible cards
		if (!pCard.isVisible()) {
			pCard.setSelected(false);
			return false;
		}

		if (lstStackHand.contains(hand)) {
			Card lastCard = null;
			for (Card c : hand.getCards()) {
				if (c.equals(pCard)) {
					lastCard = c;
				} else if (lastCard != null) {
					if (!isNext(lastCard, c, true)) {
						for (Card c1 : hand.getCards()) {
							c1.setSelected(false);
						}
						plstMoveCards.clear();
						return false;
					}
				}
				if (lastCard != null) {
					lastCard = c;
					c.setSelected(true);
					plstMoveCards.add(c.getSprite());
				}
			}
		} else if (lstHandTop.contains(hand)) {
			if (pCard.equals(hand.getLastCard())) {
				plstMoveCards.add(pCard.getSprite());
			}
		} else if (handStack.equals(hand)) {
			if (pCard.equals(handStack.getLastCard())) {
				plstMoveCards.add(pCard.getSprite());
			}
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public INextMove getNextMove() {
		return new CleanUp(lstHandTop, lstAutoMoveHand);
	}

	@Override
	Serializable getSpecificValues() {
		return null;
	}

	@Override
	void initGame(Serializable specificValues) {

	}
}
