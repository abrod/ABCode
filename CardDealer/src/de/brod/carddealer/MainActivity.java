package de.brod.carddealer;

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
		for (Shape shape : shapes) {
			shape.setZ(0.5f);
		}

	}

	@Override
	protected void actionUp(Shapes selected, Shapes up) {
		for (Shape shape : selected) {
			shape.setZ(0f);
		}
	}

	private Shapes addGridMeshes() {
		gridMeshes = new Shapes();
		cardSet.clearAll();

		CardRow[] row = new CardRow[8];
		for (int i = 0; i < row.length; i++) {
			row[i] = cardSet.createCardRow(i, 0, 0, 5, 10);
			gridMeshes.add(row[i].getRectangle());
		}

		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 6; j++) {
				Card card = cardSet.createCard(((int) (Math.random() * 14)), ((int) (Math.random() * 8)), i, j);
				gridMeshes.add(card.getRectangle());
				row[i].addCard(card);
			}
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
