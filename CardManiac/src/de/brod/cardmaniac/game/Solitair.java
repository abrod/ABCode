package de.brod.cardmaniac.game;

import java.util.List;

import de.brod.cardmaniac.Card52;
import de.brod.cardmaniac.Cards52;
import de.brod.cardmaniac.Hand;
import de.brod.cardmaniac.R;
import de.brod.gui.IAction;

public class Solitair extends Patience {

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
						if (lstCardsToAdd.size() != 1
								|| lstCardsToAdd.size() > 1) {
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
						} else if (isNextCard(getLastCard(),
								lstCardsToAdd.get(0), true)) {
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
				}

				@Override
				public void actionUp(List<? extends Card52> lstCardsToAdd) {
					Card52 lastCard = getLastCard();
					if (lastCard == null
							|| isNextCard(lstCardsToAdd.get(0), lastCard, false)) {
						addCards(lstCardsToAdd);
					}
				}
			});
		}

		List<Card52> create52Cards = new Cards52().create52Cards();
		for (int i = 0; i < create52Cards.size(); i++) {
			Card52 card = create52Cards.get(i);
			int location = (i * 2 + 1) % hands.size();
			Hand<Card52> object = hands.get(location);
			object.addCard(card);
		}

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

}
