package de.brod.carddealer.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.res.Resources;
import de.brod.carddealer.Card;
import de.brod.carddealer.CardRow;

public class FreeCell extends GameImpl {

	private CardRow[]	rowDown	= new CardRow[8];
	private CardRow[]	rowUp	= new CardRow[8];

	public FreeCell(Resources resources, float wd, float hg) {
		super(resources, 8, wd, hg);

	}

	@Override
	public List<Card> actionDown(Card card) {
		List<Card> lst = new ArrayList<Card>();
		CardRow cardRow = card.getCardRow();
		boolean add = false;
		boolean downRow = isDownRow(cardRow);
		for (Card card2 : cardRow.getCards()) {
			if (card2 == card) {
				add = true;
				lst.add(card2);
			} else if (add) {
				if (downRow && isNext(card2, lst.get(lst.size() - 1), false)) {
					lst.add(card2);
				} else {
					lst.clear();
					return lst;
				}
			}
		}
		return lst;
	}

	@Override
	public void actionUp(List<Card> cards, CardRow cardRow) {
		boolean downRow = isDownRow(cardRow);
		Card firstCard = cards.get(0);
		if (downRow) {
			if (isNext(firstCard, cardRow.getLastCard(), false)) {
				for (Card card : cards) {
					cardRow.addCard(card);
				}
			}
		} else if (cards.size() == 1) {
			if (cardRow.getIndex() < 4) {
				if (cardRow.getCards().isEmpty()) {
					cardRow.addCard(firstCard);
				}
			} else {
				if (isNext(cardRow.getLastCard(), firstCard, true)) {
					cardRow.addCard(firstCard);
				}
			}
		}
	}

	private boolean isDownRow(CardRow cardRow) {
		return cardRow.getIndex() > 7;
	}

	private boolean isNext(Card card1, Card card2, boolean sameColor) {
		if (card2 == null) {
			return true;
		}
		if (card1 == null) {
			return card2.getValue() == 0;
		}
		if (card1.getValue() != card2.getValue() - 1) {
			return false;
		}
		if (sameColor) {
			return card1.getColor() == card2.getColor();
		}
		return card1.getColor() / 2 != card2.getColor() / 2;
	}

	@Override
	public void newGame() {
		// init
		super.newGame();

		createButton(wd, hg);

		for (int i = 0; i < rowDown.length; i++) {
			rowDown[i] = createCardRow(i + 8, i, 1, 0, 4, 10);
		}
		for (int i = 0; i < rowUp.length; i++) {
			rowUp[i] = createCardRow(i, i, 0, 0, 0, 1);
		}
		List<Card> lst = new ArrayList<Card>();
		for (int i = 0; i < 52; i++) {
			lst.add(createCard(i % 13, i / 13, 0, 0));
		}
		Collections.shuffle(lst);
		int count = 0;
		while (lst.size() > 0) {
			rowDown[count].addCard(lst.remove(0));
			count = (count + 1) % rowDown.length;
		}
	}

}
