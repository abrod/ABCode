package de.brod.cardmaniac.games;

import java.util.ArrayList;
import java.util.List;

import de.brod.cardmaniac.MainActivity;
import de.brod.cardmaniac.cards.Button;
import de.brod.cardmaniac.cards.Card;
import de.brod.cardmaniac.cards.CardSet;
import de.brod.cardmaniac.cards.Hand;

public abstract class Game {

	protected CardSet	cardSet;
	private List<Card>	lstCards;
	private List<Hand>	lstHands	= new ArrayList<Hand>();

	public void init(CardSet pCardSet, MainActivity mainActivity) {
		cardSet = pCardSet;
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

	public static List<Class<? extends Game>> getGameClasses() {
		List<Class<? extends Game>> lst = new ArrayList<Class<? extends Game>>();
		lst.add(FreeCell.class);
		lst.add(MauMau.class);
		return lst;
	}

}
