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

import java.util.List;

import de.brod.cm.Card;
import de.brod.cm.Card.Colors;
import de.brod.cm.Card.Values;
import de.brod.cm.CardManiacView;
import de.brod.cm.Hand;
import de.brod.gui.IAction;

public class Solitaire extends Game {

	public Solitaire(CardManiacView pCardManiacView) {
		super(pCardManiacView);
	}

	@Override
	protected void createTitleCards(Hand hand) {
		hand.createCard(Values.Ace, Colors.Hearts);
		hand.createCard(Values.Ace, Colors.Diamonds);
		hand.createCard(Values.Ace, Colors.Clubs);
		hand.createCard(Values.Ace, Colors.Spades);
	}

	@Override
	public IAction getNextAction() {
		// turn cards arround
		for (int i = 0; i < 7; i++) {
			final Card lastCard = get(i + 6).getLastCard();
			if (lastCard != null && lastCard.isCovered()) {
				return new IAction() {

					@Override
					public void action() {
						Hand hand = lastCard.getHand();
						hand.setCovered(hand.getCardCount() - 1);
						hand.organize();
					}
				};
			}
		}
		// reshuffle
		if (get(0).getCardCount() == 0) {
			if (get(1).getCardCount() > 0) {
				return new IAction() {

					@Override
					public void action() {
						Hand h0 = get(0);
						Hand h1 = get(1);
						Card[] cards = h1.getCards().toArray(new Card[0]);
						for (int i = 0; i < cards.length - 1; i++) {
							cards[i].moveTo(h0);
						}
						h0.shuffleCards();
						h0.organize();
						h1.organize();
					}
				};
			}
		}

		return null;
	}

	@Override
	public boolean hasHistory() {
		// add undo
		return true;
	}

	@Override
	public void initHands(boolean bLandscape) {
		int iCount = 0;
		float x1 = 2 * 7 / 6f;
		add(new Hand(iCount, 0, 0, x1 / 2 - 0.5f, 0, 15));
		iCount++;
		add(new Hand(iCount, x1 / 2 + 0.5f, 0, x1, 0, 15));
		iCount++;
		for (int i = 3; i < 7; i++) {
			float x = i * 7 / 6f;
			add(new Hand(iCount, x, 0, x, 0, 15));
			iCount++;
		}
		for (int i = 0; i < 7; i++) {
			float x = i * 7 / 6f;
			add(new Hand(iCount, x, 1, x, Card.maxCardY, 15));
			iCount++;
		}
		get(0).setCovered(999);
	}

	@Override
	public void initNewCards() {
		Card[] cards = get(0).create52Cards();
		get(0).setCovered(999);
		int iPos = 6;
		int iOff = 0;
		for (Card card : cards) {
			card.moveTo(get(iPos));
			iPos++;
			if (iPos >= 13) {
				iPos = 6 + iOff;
				if (iPos >= 13) {
					break;
				}
				iOff++;
			}
		}
		for (int i = 0; i < 7; i++) {
			get(i + 6).setCovered(i + 1);
		}
	}

	@Override
	public boolean isFinished() {
		// is never finished
		return false;
	}

	@Override
	public void mouseDown(List<Card> plstMoves) {
		Card card = plstMoves.get(0);
		// don't move covered cards
		if (card.isCovered()) {
			Hand hand = card.getHand();
			if (hand.getId() >= 6) {
				plstMoves.clear();
				return;
			}
		}

	}

}
