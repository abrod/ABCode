package de.brod.carddealer;

import de.brod.opengl.GLActivity;
import de.brod.opengl.Shape;
import de.brod.opengl.Shapes;

public class MainActivity extends GLActivity {

	private Shapes gridMeshes;

	@Override
	protected void actionDown(Shapes shapes) {
		// remove all except first
		while (shapes.size() > 1) {
			shapes.remove(0);
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

		CardSet cardSet = new CardSet(getResources(), 7);

		for (int i = 0; i < 13; i++) {
			for (int j = 0; j < 5; j++) {
				Card card = cardSet.createCard(i, j % 4, i * 7 / 13f, j);

				gridMeshes.add(card.getRectangle());
			}
		}
		return gridMeshes;
	}

	@Override
	protected void init(Shapes meshes, float wd, float hg) {

		meshes.addAll(addGridMeshes());

	}

}
