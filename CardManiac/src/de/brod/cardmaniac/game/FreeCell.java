package de.brod.cardmaniac.game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import de.brod.cardmaniac.table.Card;
import de.brod.cardmaniac.table.Deck;
import de.brod.cardmaniac.table.Hand;
import de.brod.opengl.Sprite;

public class FreeCell extends Patience {

	private List<Hand> lstUpperLeft, lstUpperRight, lstLower;

	@Override
	void createCards(Deck pDeck) {
		create52Cards(pDeck, true);
	}

	@Override
	void createHands(Deck pDeck, List<Hand> plstHands) {
		lstUpperLeft = new ArrayList<Hand>();
		lstUpperRight = new ArrayList<Hand>();
		lstLower = new ArrayList<Hand>();
		for (int i = 0; i < 8; i++) {
			Hand upper;
			if (i < 4) {
				upper = new Hand(pDeck, i - 0.1f, 7, i - 0.1f, 7, 52, 52);
				lstUpperLeft.add(upper);
			} else {
				upper = new Hand(pDeck, i + 0.1f, 7, i + 0.1f, 7, 52, 52);
				lstUpperRight.add(upper);
			}
			Hand lower = new Hand(pDeck, i, 5.5f, i, 0, 12, 52);
			plstHands.add(upper);
			plstHands.add(lower);
			lstLower.add(lower);
		}
	}

	@Override
	void assignCardsToHands(List<Hand> plstHands, List<Card> lstCards) {
		for (int i = 0; i < lstCards.size(); i++) {
			plstHands.get((i % 8) * 2 + 1).addCard(lstCards.get(i));
		}
	}

	@Override
	public boolean actionUp(List<Sprite<Card>> plstCards, Hand handTo) {
		if (lstUpperLeft.contains(handTo)) {
			// add only one card
			if (plstCards.size() != 1 || handTo.getCardsCount() > 0) {
				return false;
			}
		} else {
			Card handCard = handTo.getLastCard();
			Card stackCard = plstCards.get(0).getReference();
			if (lstUpperRight.contains(handTo)) {
				// add only one card
				if (plstCards.size() != 1) {
					return false;
				}
				if (!isNext(handCard, stackCard, false)) {
					return false;
				}
			} else {
				// lower cards
				if (handCard != null && !isNext(handCard, stackCard, true)) {
					return false;
				}
			}
		}

		for (Sprite<Card> sprite : plstCards) {
			handTo.addCard(sprite.getReference());
		}
		return true;
	}

	@Override
	public boolean actionDown(Card pCard, List<Sprite<Card>> plstSelectedCards) {

		// get amount of cards
		int iFree = 1;
		for (Hand h : lstUpperLeft) {
			if (h.getCardsCount() == 0) {
				iFree++;
			}
		}
		boolean bLower = true;
		for (Hand h : lstLower) {
			if (h.getCardsCount() == 0) {
				if (bLower) {
					bLower = false;
				} else {
					iFree++;
				}
			}
		}

		List<Card> cards = pCard.getHand().getCards();
		boolean bSelected = false;
		Card lastCard = null;
		for (Card card : cards) {
			if (card.equals(pCard)) {
				bSelected = pCard.isSelected();
			} else {
				card.setSelected(bSelected);
				if (lastCard != null && plstSelectedCards.size() > 0) {
					if (!isNext(lastCard, card, true)) {
						for (Card c : cards) {
							c.setSelected(false);
						}
						plstSelectedCards.clear();
						return false;
					}
				}
			}
			if (bSelected) {
				plstSelectedCards.add(card.getSprite());
				if (plstSelectedCards.size() > iFree) {
					for (Card c : cards) {
						c.setSelected(false);
					}
					plstSelectedCards.clear();
					return false;
				}
			}
			lastCard = card;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public INextMove getNextMove() {
		return new CleanUp(lstUpperRight, lstLower, lstUpperLeft);
	}

	@Override
	Serializable getSpecificValues() {
		return null;
	}

	@Override
	void initGame(Serializable specificValues) {

	}
}
