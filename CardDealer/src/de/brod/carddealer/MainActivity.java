package de.brod.carddealer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.brod.opengl.Button;
import de.brod.opengl.ButtonAction;
import de.brod.opengl.GLActivity;
import de.brod.opengl.Shape;
import de.brod.opengl.Shapes;

public class MainActivity extends GLActivity {

	private Shapes	gridMeshes;
	private CardSet	cardSet;

	@Override
	protected void actionDown(Shapes shapes) {
		List<Card> cards = cardSet.getCards(shapes);

		// remove all except first
		while (cards.size() > 1) {
			cards.remove(0);
		}

		for (int i = 0; i < shapes.size();) {
			boolean found = false;
			for (Card card : cards) {
				if (card.getRectangle().equals(shapes.get(i))) {
					found = true;
					break;
				}
			}
			if (found) {
				i++;
			} else {
				shapes.remove(i);
			}
		}

		if (cards.size() == 1) {
			Card card = cards.get(0);
			CardRow cardRow = card.getCardRow();
			boolean add = false;
			for (Card card2 : cardRow.getCards()) {
				if (card2 == card) {
					add = true;
				} else if (add) {
					shapes.add(card2.getRectangle());
				}
			}
		}

		for (Shape shape : shapes) {
			shape.setZ(0.5f);
		}

	}

	@Override
	protected void actionUp(Shapes selected, Shapes up) {
		List<CardRow> cardRows = cardSet.getCardRows(up);
		List<Card> cards = cardSet.getCards(selected);
		if (cardRows.size() > 0) {
			CardRow cardRow = cardRows.get(cardRows.size() - 1);
			for (Card card : cards) {
				cardRow.addCard(card);
			}
		} else {
			for (Card card : cards) {
				CardRow cardRow = card.getCardRow();
				if (cardRows.contains(cardRow)) {
					cardRows.add(cardRow);
				}
			}
			for (CardRow cardRow : cardRows) {
				cardRow.organize();
			}
		}

		for (Shape shape : selected) {
			shape.setZ(0f);
		}
	}

	private Shapes addGridMeshes() {
		gridMeshes = new Shapes();
		cardSet.clearAll();

		CardRow[] row = new CardRow[8];
		for (int i = 0; i < row.length; i++) {
			row[i] = cardSet.createCardRow(i, 1, 0, 4, 10);
			gridMeshes.add(row[i].getRectangle());
		}
		for (int i = 0; i < row.length; i++) {
			CardRow rows = cardSet.createCardRow(i, 0, 0, 0, 1);
			gridMeshes.add(rows.getRectangle());
		}
		List<Card> lst = new ArrayList<Card>();
		for (int i = 0; i < 52; i++) {
			Card card = cardSet.createCard(i % 13, i / 13, 0, 0);
			lst.add(card);
			gridMeshes.add(card.getRectangle());
		}
		Collections.shuffle(lst);
		int count = 0;
		while (lst.size() > 0) {
			row[count].addCard(lst.remove(0));
			count = (count + 1) % row.length;
		}
		return gridMeshes;
	}

	private Button createButton(float wd, float hg) {

		float abs = Math.abs(wd - hg);
		float width = abs;
		float height = abs / 2;

		return cardSet.createButton(width, height, wd - width / 2, hg - height / 2, -0.1f, new ButtonAction() {

			@Override
			public void doAction() {
				// make nothing
			}
		});
	}

	@Override
	protected void init(Shapes meshes, float wd, float hg) {

		cardSet = new CardSet(getResources(), 8);

		meshes.add(createButton(wd, hg));
		meshes.addAll(addGridMeshes());
	}

}
