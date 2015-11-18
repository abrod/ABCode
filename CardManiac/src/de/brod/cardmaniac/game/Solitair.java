package de.brod.cardmaniac.game;

import java.util.Hashtable;
import java.util.List;
import java.util.Map.Entry;

import de.brod.cardmaniac.Card52;
import de.brod.cardmaniac.Cards52;
import de.brod.cardmaniac.Hand;
import de.brod.cardmaniac.R;
import de.brod.gui.IAction;

public class Solitair extends Patience {

	static int[] handsToCheck = { 0, 2, 4, 6, 1, 3, 5, 7, 9, 11, 13, 15 };
	static int[] handsToLayDown = { 14, 12, 10, 8 };

	@Override
	public void init(float wd, float hg, int width, int height) {

		hands.clear();
		for (int i = 0; i < 8; i++) {
			if (i < 4) {
				hands.add(new Hand<Card52>(i, 4, i, 4, null, 52) {

					@Override
					public void actionDown(Card52 card, List<Card52> lst) {
						lst.add(card);
					}

					@Override
					public void actionUp(List<? extends Card52> lstCardsToAdd) {
						if (lstCardsToAdd.size() != 1 || lstCardsToAdd.size() > 1) {
							// invalid amount
						} else if (getCards().size() == 0) {
							addCards(lstCardsToAdd);
						}
					}

				});
			} else {
				hands.add(new Hand<Card52>(i, 4, i, 4, "A", 52) {
					@Override
					public void actionDown(Card52 card, List<Card52> lst) {
						lst.add(card);
					}

					@Override
					public void actionUp(List<? extends Card52> lstCardsToAdd) {
						if (lstCardsToAdd.size() != 1) {
							// invalid amount
						} else if (isNextCard(getLastCard(), lstCardsToAdd.get(0), true)) {
							addCards(lstCardsToAdd);
						}
					}
				});
			}

			hands.add(new Hand<Card52>(i, 3, i, 0, null, 12) {
				@Override
				public void actionDown(Card52 card, List<Card52> lst) {
					Card52 lastCard = null;
					for (Card52 c : getCards()) {
						if (c.equals(card)) {
							lastCard = c;
							lst.add(c);
						} else if (lastCard != null) {
							if (isNextCard(c, lastCard, false)) {
								lst.add(c);
							} else {
								lst.clear();
								return;
							}
							lastCard = c;
						}
					}
					if (lst.size() > getAmountOfFreeHands()) {
						lst.clear();
					}
				}

				@Override
				public void actionUp(List<? extends Card52> lstCardsToAdd) {
					Card52 lastCard = getLastCard();
					if (lastCard == null || isNextCard(lstCardsToAdd.get(0), lastCard, false)) {
						addCards(lstCardsToAdd);
					}
				}
			});
		}

		List<Card52> create52Cards = new Cards52().create52Cards(false);
		shareCards(create52Cards);

		float wdButton = 1 / 2f;
		float hgButton = 1 / 4f;
		float x = (wd - wdButton) / 2f;
		float y = (hg - hgButton) / 2f;
		createGuiButton(x, y, wdButton, hgButton, new IAction() {

			@Override
			public String getTitle() {
				return getString(R.string.new_);
			}

			@Override
			public void doAction() {
				askForNewGame();
			}

		});

	}

	protected int getAmountOfFreeHands() {

		int iCount = 1;
		int iCountLower = 0;
		for (int i = 0; i < 8; i += 2) {
			if (hands.get(i).getCards().size() == 0) {
				iCount++;
			}
		}
		for (int i = 1; i < 16; i += 2) {
			if (hands.get(i).getCards().size() == 0) {
				iCountLower++;
			}
		}

		return iCount + Math.max(iCountLower - 1, 0);
	}

	private void shareCards(List<Card52> create52Cards) {
		for (int i = 0; i < create52Cards.size(); i++) {
			Card52 card = create52Cards.get(i);
			int location = (i * 2 + 1) % hands.size();
			Hand<Card52> hand = hands.get(location);
			hand.addCard(card);
		}
	}

	class NextSolitairMove implements NextMove {

		Hashtable<Card52, Hand> map = new Hashtable<Card52, Hand>();

		@Override
		public void calculateNextMove() {
			// get the min value
			int minCard = 99;
			for (int i : handsToLayDown) {
				Hand<Card52> hand = hands.get(i);
				Card52 lastCard = hand.getLastCard();
				int order = getOrder(lastCard);
				if (order < minCard) {
					minCard = order;
				}
			}
			// check for aces
			for (int j : handsToLayDown) {
				Hand<Card52> handTo = hands.get(j);
				Card52 lastCardTo = handTo.getLastCard();
				int orderTo = getOrder(lastCardTo);
				if (orderTo == minCard)
					for (int i : handsToCheck) {
						Hand<Card52> hand = hands.get(i);
						Card52 lastCardHand = hand.getLastCard();
						if (lastCardHand != null && !map.keySet().contains(lastCardHand)) {
							int orderHand = getOrder(lastCardHand);
							if (orderHand == minCard + 1) {
								if (lastCardTo == null || lastCardTo.getColor().equals(lastCardHand.getColor())) {
									map.put(lastCardHand, handTo);
									break;
								}
							}
						}
					}
			}
		}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		@Override
		public boolean makeNextMove() {
			if (map.size() > 0) {
				for (Entry<Card52, Hand> entry : map.entrySet()) {
					entry.getValue().addCard(entry.getKey());
				}
				return true;
			}
			return false;

		}

	}

	@Override
	public NextMove getNextMoveThread() {
		return new NextSolitairMove();
	}

	@Override
	public void resetGame() {
		List<Card52> lstAllCards = getAllCards();
		shareCards(lstAllCards);
	}

}
