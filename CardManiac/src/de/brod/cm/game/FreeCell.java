package de.brod.cm.game;

import java.util.List;

import de.brod.cm.Card;
import de.brod.cm.Card.Colors;
import de.brod.cm.Card.Values;
import de.brod.cm.CardManiacView;
import de.brod.cm.Hand;
import de.brod.gui.IAction;

public class FreeCell extends Game {

	public FreeCell(CardManiacView pCardManiacView) {
		super(pCardManiacView);
	}

	private class FinishAction implements IAction {

		private Hand h;
		private Card c;

		public FinishAction(Card c, Hand h) {
			this.c = c;
			this.h = h;
		}

		@Override
		public void action() {
			Hand oldHand = c.getHand();
			if (h != oldHand) {
				c.moveTo(h);
				oldHand.organize();
				h.organize();
			}
		}

	}

	private FinishAction getAction(Hand hand, int iMinValue) {
		Card c = hand.getLastCard();
		if (c != null) {
			if (c.getValue().getId() == iMinValue) {
				if (iMinValue == 0) {
					// search empty entry
					for (int j = 4; j < 8; j++) {
						Hand h = hands.get(j * 2);
						Card c1 = h.getLastCard();
						if (c1 == null) {
							return new FinishAction(c, h);
						}
					}
				} else {
					// search empty entry
					for (int j = 4; j < 8; j++) {
						Hand h = hands.get(j * 2);
						Card c1 = h.getLastCard();
						if (c1 != null
								&& c1.getColor().getId() == c.getColor()
										.getId()
								&& c1.getValue().getId() + 1 == iMinValue) {
							return new FinishAction(c, h);
						}
					}
				}
			}
		}
		return null;
	}

	@Override
	public IAction getNextAction() {
		// check the min value
		int iMinValue = 100;
		for (int i = 4; i < 8; i++) {
			Hand hand = hands.get(i * 2);
			Card c = hand.getLastCard();
			if (c != null) {
				iMinValue = Math.min(c.getValue().getId() + 1, iMinValue);
			} else {
				iMinValue = 0;
				break;
			}
		}
		for (int i = 0; i < 8; i++) {
			if (i < 4) {
				FinishAction a = getAction(hands.get(i * 2), iMinValue);
				if (a != null) {
					return a;
				}
			}
			FinishAction a = getAction(hands.get(i * 2 + 1), iMinValue);
			if (a != null) {
				return a;
			}
		}
		return null;
	}

	@Override
	public void initNewCards(Hand[] hands) {
		Card[] cards = hands[0].create52Cards();
		int iPos = 1;
		for (Card card : cards) {
			card.moveTo(hands[iPos]);
			iPos += 2;
			if (iPos >= 16) {
				iPos = 1;
			}
		}
	}

	@Override
	public Hand[] initHands(boolean bLandscape) {
		if (!bLandscape) {
			for (int i = 0; i < 8; i++) {
				if (i < 4) {
					hands.add(new Hand(i * 2, i - 0.1f, 0, i - 0.1f, 0, 13));
				} else {
					hands.add(new Hand(i * 2, i + 0.1f, 0, i + 0.1f, 0, 100));
				}
				hands.add(new Hand(i * 2 + 1, i, 1, i, Card.maxCardY, 15));
			}
		} else {
			for (int i = 0; i < 8; i++) {
				if (i < 4) {
					hands.add(new Hand(i * 2, -1.5f, i, -1.5f, i, 13));
				} else {
					hands.add(new Hand(i * 2, 8.5f, i - 4, 9, i - 4, 13));
				}
				hands.add(new Hand(i * 2 + 1, i, 0, i, Card.maxCardY, 10));
			}
		}
		for (int i = 4; i < 8; i++) {
			hands.get(i * 2).getStackCard().setValue(13 * 4 + 1);
		}
		return hands.toArray(new Hand[0]);
	}

	private boolean matches(Card cLastCard, Card cFirstCardOfMovingStack) {
		int i = cLastCard.getColor().getId() / 2;
		int i0 = cFirstCardOfMovingStack.getColor().getId() / 2;
		// color has to be different
		if (i == i0) {
			return false;
		}
		i = cLastCard.getValue().getId() - 1;
		i0 = cFirstCardOfMovingStack.getValue().getId();
		// value has to match
		if (i != i0) {
			return false;
		}
		return true;
	}

	@Override
	public void mouseDown(List<Card> plstMoves) {
		Card c = plstMoves.get(0);
		Hand hand = c.getHand();
		int id = hand.getId();
		int y = id % 2;
		int x = id / 2;
		if (y == 1) {
			// get amount of free cards
			int iFree = 1;
			for (int i = 0; i < 8; i += 2) {
				if (hands.get(i).getCardCount() == 0) {
					iFree++;
				}
			}
			int iFree1 = 0;
			for (int i = 1; i < 16; i += 2) {
				if (hands.get(i).getCardCount() == 0) {
					iFree1++;
				}
			}
			if (iFree1 > 1) {
				iFree += iFree1 - 1;
			}

			plstMoves.clear();
			List<Card> cards = hand.getCards();
			Card cLast = null;
			for (int i = cards.size() - 1; i >= 0; i--) {
				Card card = cards.get(i);
				if (cLast != null && !matches(card, cLast)) {
					break;
				}
				plstMoves.add(0, card);
				iFree--;
				if (card == c || iFree <= 0) {
					break;
				}
				cLast = card;
			}

			// check if card is available
			if (!plstMoves.contains(c)) {
				plstMoves.clear();
			}
		} else {
			// select only the last
		}
	}

	@Override
	public void mouseUp(List<Card> pLstMoves, Hand handTo) {
		if (handTo == null) {
			return;
		}
		int id = handTo.getId();
		int y = id % 2;
		int x = id / 2;
		Card c0 = pLstMoves.get(0);
		Card c = handTo.getLastCard();
		if (y == 0) {
			// only one card allowed
			if (pLstMoves.size() > 1) {
				return;
			}
			if (x < 4) {
				if (c != null) {
					return;
				}
			} else {
				if (c == null) {
					if (!c0.getValue().equals(Values.Ace)) {
						return;
					}
				} else if (!c.getColor().equals(c0.getColor())
						|| c.getValue().getId() != c0.getValue().getId() - 1) {
					return;
				}
			}
		} else {
			if (c != null && !matches(c, c0)) {
				return;
			}
		}

		super.mouseUp(pLstMoves, handTo);
	}

	@Override
	protected void createTitleCards(Hand hand) {
		hand.createCard(Values.Ace, Colors.Clubs);
		hand.createCard(Values.Ace, Colors.Spades);
		hand.createCard(Values.Ace, Colors.Hearts);
		hand.createCard(Values.Ace, Colors.Diamonds);
	}

	@Override
	public boolean hasHistory() {
		return true;
	}

}
