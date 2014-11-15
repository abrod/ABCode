package de.brod.cardmaniac.games.state;

import java.io.Serializable;
import java.util.ArrayList;

import de.brod.cardmaniac.cards.Card;
import de.brod.cardmaniac.cards.Hand;

public class GameState implements Serializable {

	public class GameStateCard implements Serializable {

		private static final long	serialVersionUID	= 4541632723364824956L;

		public String				sValue;
		public boolean				visible;

		@Override
		public String toString() {
			return (visible ? "" : "!") + sValue;
		}
	}

	public class GameStateHand implements Serializable {

		private static final long		serialVersionUID	= -6175216387645781018L;

		public ArrayList<GameStateCard>	lstCard				= new ArrayList<GameStateCard>();
		public int						iCountVisible;

		public void addCard(Card card) {
			GameStateCard createCard = new GameStateCard();
			createCard.sValue = card.toString();
			createCard.visible = card.isVisible();
			lstCard.add(createCard);
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("[").append(iCountVisible).append("/");
			for (GameStateCard card : lstCard) {
				sb.append("/");
				sb.append(card.toString());
			}
			sb.append("]");
			return sb.toString();
		}
	}

	private static final long		serialVersionUID	= 7639400732420195691L;
	public String					className;
	public ArrayList<GameStateHand>	lstHands			= new ArrayList<GameStateHand>();
	public Serializable				specificValues;

	public GameStateHand createHand(Hand pHand) {
		GameStateHand gsHand = new GameStateHand();
		gsHand.iCountVisible = pHand.getCountVisible();

		lstHands.add(gsHand);
		for (Card card : pHand.getCards()) {
			gsHand.addCard(card);
		}
		return gsHand;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(className);
		for (GameStateHand hand : lstHands) {
			sb.append(" ").append(hand.toString());
		}
		return sb.toString();
	}
}
