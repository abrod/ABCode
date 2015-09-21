package de.brod.cardmaniac.game;

import java.util.List;

import de.brod.cardmaniac.Card52;
import de.brod.cardmaniac.Cards52;
import de.brod.cardmaniac.Hand;

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
						if (lstCardsToAdd.size() != 0) {
							// invalid amount
						} else if (lstCardsToAdd.size() > 1) {
							// invalid amount
						} else {
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
						} else {
							addCards(lstCardsToAdd);
						}
					}
				});
			}

			hands.add(new Hand<Card52>(i, 3, i, 0, null, 12) {
				@Override
				public void actionDown(Card52 card, List<Card52> lst) {
					Card52 bLast = null;
					for (Card52 c : getCards()) {
						if (c.equals(card)) {
							bLast = c;
							lst.add(c);
						} else if (bLast != null) {
							lst.add(c);
						}
					}
				}

				@Override
				public void actionUp(List<? extends Card52> lstCardsToAdd) {
					addCards(lstCardsToAdd);
				}
			});
		}

		List<Card52> create52Cards = Cards52.create52Cards();
		for (int i = 0; i < create52Cards.size(); i++) {
			Card52 card = create52Cards.get(i);
			int location = (i * 2 + 1) % hands.size();
			Hand<Card52> object = hands.get(location);
			object.addCard(card);
		}

		float wdButton = 1 / 2f * 2;
		float hgButton = 1 / 4f;
		float x = (wd - wdButton) / 2f;
		float y = (hg - hgButton) / 2f;
		createGuiButton(x, y, wdButton, hgButton, "Show");

	}

}
