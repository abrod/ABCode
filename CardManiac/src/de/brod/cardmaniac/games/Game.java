package de.brod.cardmaniac.games;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import android.util.Log;
import de.brod.cardmaniac.MainActivity;
import de.brod.cardmaniac.cards.Button;
import de.brod.cardmaniac.cards.Card;
import de.brod.cardmaniac.cards.CardSet;
import de.brod.cardmaniac.cards.Hand;
import de.brod.cardmaniac.games.state.GameState;
import de.brod.cardmaniac.games.state.GameState.GameStateCard;
import de.brod.cardmaniac.games.state.GameState.GameStateHand;

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
		Hand hand = new Hand(lstHands.size(), cardSet, x1, y1, x2 - x1,
				y2 - y1, piMaxCardSize, pbSmallBorder, piCountVisible);
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

	public String getName() {
		return getClass().getSimpleName();
	}

	public boolean setState(GameState state, List<Card> cards) {

		if (state.lstHands.size() != lstHands.size()) {
			Log.d("Game",
					"dont load prev game, because handsize does not match");
			return false;
		}

		Hashtable<String, Card> ht = new Hashtable<String, Card>();
		for (Card card : cards) {
			ht.put(card.toString(), card);
		}

		for (int i = 0; i < lstHands.size(); i++) {
			Hand hand = lstHands.get(i);
			GameStateHand gsHand = state.lstHands.get(i);
			for (GameStateCard gsCard : gsHand.lstCard) {
				Card card = ht.remove(gsCard.sValue);
				if (card == null) {
					// card not found
					Log.d("Game", "card " + gsCard + " not found");
					return false;
				}
				card.setVisible(gsCard.visible);
				card.moveTo(hand);
			}
		}

		if (ht.size() > 0) {
			// not all cards consumed
			Log.d("Game", ht.size() + " remaining cards not assigned");
			return false;
		}
		for (Hand hand : lstHands) {
			hand.organize();
		}
		return true;
	}

	public GameState getState() {
		GameState state = new GameState();
		for (Hand hand : lstHands) {
			state.createHand(hand);
		}
		return state;
	}

}
