/*
 * ******************************************************************************
 * Copyright (c) 2013 Andreas Brod
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * *****************************************************************************
 */
package de.brod.cm.game;

import java.util.ArrayList;
import java.util.List;

import de.brod.cm.Card;
import de.brod.cm.Card.Colors;
import de.brod.cm.Card.Values;
import de.brod.cm.CardManiacView;
import de.brod.cm.Hand;
import de.brod.cm.ICard;
import de.brod.gui.action.IAction;

public class FreeCell extends Game {

	private class FinishAction implements IAction {

		private Hand h;
		private ICard c;
		List<FinishAction> lst;

		public FinishAction() {
			lst = new ArrayList<FinishAction>();
		}

		public FinishAction(ICard c, Hand h) {
			lst = null;
			this.c = c;
			this.h = h;
		}

		@Override
		public void action() {
			if (lst != null) {
				for (FinishAction f : lst) {
					f.action();
				}
				return;
			}
			Hand oldHand = c.getHand();
			if (h != oldHand) {
				c.moveTo(h);
				oldHand.organize();
				h.organize();
			}
		}

		public void add(FreeCell.FinishAction a) {
			lst.add(a);
		}

	}

	public FreeCell(CardManiacView pCardManiacView) {
		super(pCardManiacView);
	}

	@Override
	protected void createTitleCards(Hand hand) {
		hand.createCard(Values.Ace, Colors.Clubs);
		hand.createCard(Values.Ace, Colors.Spades);
		hand.createCard(Values.Ace, Colors.Hearts);
		hand.createCard(Values.Ace, Colors.Diamonds);
	}

	private FinishAction getAction(Hand hand, int iMinValue) {
		ICard c = hand.getLastCard();
		if (c != null) {
			if (c.getValue().getId() == iMinValue) {
				if (iMinValue == 0) {
					// search empty entry
					for (int j = 4; j < 8; j++) {
						Hand h = get(j * 2);
						ICard c1 = h.getLastCard();
						if (c1 == null) {
							return new FinishAction(c, h);
						}
					}
				} else {
					// search empty entry
					for (int j = 4; j < 8; j++) {
						Hand h = get(j * 2);
						ICard c1 = h.getLastCard();
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
		int iMinValue = 10000;
		for (int i = 4; i < 8; i++) {
			Hand hand = get(i * 2);
			ICard c = hand.getLastCard();
			if (c != null) {
				iMinValue = Math.min(c.getValue().getId() + 1, iMinValue);
			} else {
				iMinValue = 0;
				break;
			}
		}
		FinishAction f = new FinishAction();
		for (int i = 0; i < 8; i++) {
			if (i < 4) {
				FinishAction a = getAction(get(i * 2), iMinValue);
				if (a != null) {
					if (iMinValue == 0) {
						return a;
					}
					f.add(a);
				}
			}
			FinishAction a = getAction(get(i * 2 + 1), iMinValue);
			if (a != null) {
				if (iMinValue == 0) {
					return a;
				}
				f.add(a);
			}
		}
		if (f.lst.size() == 0) {
			return null;
		}
		return f;
	}

	@Override
	public boolean hasHistory() {
		return true;
	}

	@Override
	public void initHands(boolean bLandscape) {
		if (!bLandscape) {
			for (int i = 0; i < 8; i++) {
				if (i < 4) {
					add(new Hand(i * 2, i - 0.1f, 0, i - 0.1f, 0, 13));
				} else {
					add(new Hand(i * 2, i + 0.1f, 0, i + 0.1f, 0, 100));
				}
				add(new Hand(i * 2 + 1, i, 1, i, Card.maxCardY, 15));
			}
		} else {
			for (int i = 0; i < 8; i++) {
				if (i < 4) {
					add(new Hand(i * 2, -1.5f, i, -1.5f, i, 13));
				} else {
					add(new Hand(i * 2, 8.5f, i - 4, 9, i - 4, 13));
				}
				add(new Hand(i * 2 + 1, i, 0, i, Card.maxCardY, 10));
			}
		}
		for (int i = 4; i < 8; i++) {
			get(i * 2).getStackCard().setValue(13 * 4 + 1);
		}
	}

	@Override
	public void initNewCards() {
		ICard[] cards = get(0).create52Cards();
		int iPos = 1;
		for (ICard card : cards) {
			card.moveTo(get(iPos));
			iPos += 2;
			if (iPos >= 16) {
				iPos = 1;
			}
		}
	}

	@Override
	public String getFinishedText() {
		for (int i = 0; i < 16; i++) {
			if (i % 2 == 0 && i >= 8) {
				// ignore
			} else {
				if (get(i).getCardCount() > 0) {
					return null;
				}
			}
		}
		return "All cards played.";
	}

	private boolean matches(ICard cLastCard, ICard cFirstCardOfMovingStack) {
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
	public void mouseDown(List<ICard> plstMoves) {
		ICard c = plstMoves.get(0);
		Hand hand = c.getHand();
		int id = hand.getId();
		int y = id % 2;
		if (y == 1) {
			// get amount of free cards
			int iFree = 1;
			for (int i = 0; i < 8; i += 2) {
				if (get(i).getCardCount() == 0) {
					iFree++;
				}
			}
			int iFree1 = 0;
			for (int i = 1; i < 16; i += 2) {
				if (get(i).getCardCount() == 0) {
					iFree1++;
				}
			}
			if (iFree1 > 1) {
				iFree += iFree1 - 1;
			}

			plstMoves.clear();
			List<ICard> cards = hand.getCards();
			ICard cLast = null;
			for (int i = cards.size() - 1; i >= 0; i--) {
				ICard card = cards.get(i);
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
	public boolean mouseUp(List<ICard> pLstMoves, Hand handTo, ICard cardTo) {
		if (handTo == null) {
			return false;
		}
		int id = handTo.getId();
		int y = id % 2;
		int x = id / 2;
		ICard c0 = pLstMoves.get(0);
		ICard c = handTo.getLastCard();
		if (y == 0) {
			// only one card allowed
			if (pLstMoves.size() > 1) {
				return false;
			}
			if (x < 4) {
				if (c != null) {
					return false;
				}
			} else {
				if (c == null) {
					if (!c0.getValue().equals(Values.Ace)) {
						return false;
					}
				} else if (!c.getColor().equals(c0.getColor())
						|| c.getValue().getId() != c0.getValue().getId() - 1) {
					return false;
				}
			}
		} else {
			if (c != null && !matches(c, c0)) {
				return false;
			}
		}

		return super.mouseUp(pLstMoves, handTo, cardTo);
	}

}
