package de.brod.cardmaniac.game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

import de.brod.cardmaniac.CardColor;
import de.brod.cardmaniac.CardValue;
import de.brod.cardmaniac.game.GameState.GameStateCard;
import de.brod.cardmaniac.game.GameState.GameStateHand;
import de.brod.cardmaniac.table.Card;
import de.brod.cardmaniac.table.Deck;
import de.brod.cardmaniac.table.Hand;
import de.brod.opengl.OpenGLButton;
import de.brod.opengl.Rect;
import de.brod.opengl.ISprite;

public abstract class Game {

	static final List<Class<? extends Game>> arrClasses = new ArrayList<Class<? extends Game>>();

	static {
		arrClasses.add(FreeCell.class);
		arrClasses.add(Solitaire.class);
		arrClasses.add(MauMau.class);
	}

	public static List<Class<? extends Game>> getGameClasses() {
		return arrClasses;
	}

	private List<ISprite<Card>> _lstAllSpriteCards;
	private List<Hand> _lstHands;

	private ArrayList<Card> _lstCards;

	public abstract boolean actionDown(Card pCard,
			List<ISprite<Card>> plstMoveCards);

	public abstract boolean actionUp(List<ISprite<Card>> _lstMoveCards,
			Hand handTo);

	abstract void assignCardsToHands(List<Hand> plstHands, List<Card> lstCards);

	void create52Cards(Deck pDeck, boolean pbBackFlag) {
		CardValue[] values = { CardValue.ass, CardValue.koenig, CardValue.dame,
				CardValue.bube, CardValue.c10, CardValue.c9, CardValue.c8,
				CardValue.c7, CardValue.c6, CardValue.c5, CardValue.c4,
				CardValue.c3, CardValue.c2 };
		CardColor[] colors = { CardColor.herz, CardColor.karo, CardColor.kreuz,
				CardColor.pik };
		for (CardValue value : values) {
			for (CardColor color : colors) {
				pDeck.createCard(value, color, pbBackFlag);
			}
		}
	}

	void create32Cards(Deck pDeck, boolean pbBackFlag) {
		CardValue[] values = { CardValue.ass, CardValue.koenig, CardValue.dame,
				CardValue.bube, CardValue.c10, CardValue.c9, CardValue.c8,
				CardValue.c7 };
		CardColor[] colors = { CardColor.herz, CardColor.karo, CardColor.kreuz,
				CardColor.pik };
		for (CardValue value : values) {
			for (CardColor color : colors) {
				pDeck.createCard(value, color, pbBackFlag);
			}
		}
	}

	void create55Cards(Deck pDeck, boolean pbBackFlag) {
		create52Cards(pDeck, pbBackFlag);
		CardValue[] values = { CardValue.joker };
		CardColor[] colors = { CardColor.herz, CardColor.kreuz, CardColor.pik };
		for (CardValue value : values) {
			for (CardColor color : colors) {
				pDeck.createCard(value, color, pbBackFlag);
			}
		}
	}

	abstract void createCards(Deck pDeck);

	abstract void createHands(Deck pDeck, List<Hand> plstHands);

	private void createHandsAndCards(Deck pDeck) {
		_lstHands = new ArrayList<Hand>();
		createHands(pDeck, _lstHands);

		_lstCards = new ArrayList<Card>();
		for (ISprite<Card> card : _lstAllSpriteCards) {
			_lstCards.add(card.getReference());
		}
		Collections.shuffle(_lstCards);
	}

	public Hand getHandAt(float eventX, float eventY) {
		for (Hand hand : _lstHands) {
			if (hand.touches(eventX, eventY)) {
				return hand;
			}
		}
		return null;
	}

	public List<Hand> getHands() {
		return _lstHands;
	}

	public String getName() {
		return getClass().getName();
	}

	public abstract INextMove getNextMove();

	public List<Rect> getRectangles() {
		List<Rect> lst = new ArrayList<Rect>();
		for (Hand hand : _lstHands) {
			lst.add(hand.getRect());
		}
		return lst;
	}

	public List<ISprite<Card>> getSprites() {
		return _lstAllSpriteCards;
	}

	public GameState getState() {
		GameState gameState = new GameState();
		gameState.className = this.getClass().getName();
		gameState.specificValues = getSpecificValues();
		for (int i = 0; i < _lstHands.size(); i++) {
			Hand hand = _lstHands.get(i);
			GameStateHand createHand = gameState.createHand(hand);
			List<Card> cards = hand.getCards();
			for (int j = 0; j < cards.size(); j++) {
				createHand.addCard(cards.get(j));
			}
		}
		return gameState;
	}

	abstract Serializable getSpecificValues();

	public void initGame(Deck pDeck, GameState state) {

		createCards(pDeck);
		_lstAllSpriteCards = pDeck.getAllSprites();

		createHandsAndCards(pDeck);

		if (!loadState(pDeck, state)) {
			assignCardsToHands(_lstHands, _lstCards);
		} else {
			if (state.specificValues != null) {
				initGame(state.specificValues);
			}
		}

		for (Hand hand : _lstHands) {
			hand.organize();
		}
	}

	abstract void initGame(Serializable specificValues);

	private boolean loadState(Deck pDeck, GameState state) {
		if (state != null && state.lstHands != null) {
			Hashtable<String, Card> ht = new Hashtable<String, Card>();
			for (ISprite<Card> sprite : _lstAllSpriteCards) {
				Card card = sprite.getReference();
				ht.put(card.toString(), card);
			}
			for (int i = 0; i < state.lstHands.size() && i < _lstHands.size(); i++) {
				GameStateHand stateHand = state.lstHands.get(i);
				Hand hand = _lstHands.get(i);
				for (GameStateCard stateCard : stateHand.lstCard) {
					Card card = ht.remove(stateCard.sValue);
					if (card != null) {
						hand.addCard(card);
					}
				}
				hand.setCountVisible(stateHand.iCountVisible);
			}
			// if there are remaining (not assigned cards)
			if (ht.size() > 0) {
				// recreate the hands
				createHandsAndCards(pDeck);
				return false;
			}
			return true;
		}
		return false;
	}

	public void initButtons(Deck pDeck, List<OpenGLButton> lstButtons) {
		// no buttons
	}

}
