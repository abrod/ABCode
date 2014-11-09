package de.brod.cardmaniac.games;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import de.brod.cardmaniac.MainActivity;
import de.brod.cardmaniac.cards.Button;
import de.brod.cardmaniac.cards.Card;
import de.brod.cardmaniac.cards.CardSet;
import de.brod.cardmaniac.cards.Hand;
import de.brod.cardmaniac.cards.PlayingCard;
import de.brod.cardmaniac.cards.PlayingCard.CardColor;
import de.brod.cardmaniac.cards.PlayingCard.CardValue;

public abstract class Game {

	protected CardSet	cardSet;
	private List<Card>	lstCards;
	private List<Hand>	lstHands	= new ArrayList<Hand>();

	public void init(GL10 gl, MainActivity mainActivity) {
		cardSet = new CardSet(gl);
	}

	public List<Card> initCards() {
		lstCards = new ArrayList<Card>();

		fillCards(lstCards);
		return lstCards;
	}

	public float getMaxY() {
		return cardSet.getMaxY();
	}

	abstract void fillCards(List<Card> plstCards);

	public abstract List<Hand> initHands();

	public void fill32Cards(List<Card> plstCards) {
		fillCards(
				plstCards,
				PlayingCard.CardColor.values(),
				new PlayingCard.CardValue[] { PlayingCard.CardValue.ass,
					PlayingCard.CardValue.c7, PlayingCard.CardValue.c8,
					PlayingCard.CardValue.c9, PlayingCard.CardValue.c10,
					PlayingCard.CardValue.jack,
					PlayingCard.CardValue.queen, PlayingCard.CardValue.king });

	}

	public void fill52Cards(List<Card> plstCards) {
		fillCards(
				plstCards,
				PlayingCard.CardColor.values(),
				new PlayingCard.CardValue[] { PlayingCard.CardValue.ass,
					PlayingCard.CardValue.c2, PlayingCard.CardValue.c3,
					PlayingCard.CardValue.c4, PlayingCard.CardValue.c5,
					PlayingCard.CardValue.c6, PlayingCard.CardValue.c7,
					PlayingCard.CardValue.c8, PlayingCard.CardValue.c9,
					PlayingCard.CardValue.c10, PlayingCard.CardValue.jack,
					PlayingCard.CardValue.queen, PlayingCard.CardValue.king });
	}

	private void fillCards(List<Card> plstCards, CardColor[] cardColor,
			CardValue[] cardValues) {
		Hand tempHand = new Hand(cardSet, 0, 0, 0, 0, 0, false, 999);
		for (CardColor color : cardColor) {
			for (CardValue value : cardValues) {
				PlayingCard createCard = tempHand.createCard(value, color);
				createCard.moveTo(null);
				plstCards.add(createCard);
			}
		}
		Collections.shuffle(plstCards);

	}

	protected Hand addHand(float x1, float y1, float x2, float y2,
			int piMaxCardSize, boolean pbSmallBorder, int piCountVisible) {
		Hand hand = new Hand(cardSet, x1, y1, x2 - x1, y2 - y1, piMaxCardSize,
				pbSmallBorder, piCountVisible);
		lstHands.add(hand);
		return hand;
	}

	public abstract void newGame(List<Card> cards);

	public void organize() {
		for (Hand hand : lstHands) {
			if (hand.isDirty()) {
				hand.organize();
			}
		}
	}

	public List<Hand> getHands() {
		return lstHands;
	}

	public abstract void mouseClick(Card card, List<Card> plstSelected);

	public abstract boolean playCard(List<Card> plstSelected, Card cardTo,
			Hand handTo);

	public List<Button> initButtons() {
		return new ArrayList<Button>();
	}

	public abstract ITurn getNextTurn();

}
