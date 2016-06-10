package de.brod.carddealer;

import de.brod.opengl.ButtonAction;
import de.brod.opengl.GLActivity;
import de.brod.opengl.Grid3d;
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

		CardSet cardSet = new CardSet(getResources(), 8);

		for (int i = 0; i < 8; i++) {
			CardRow cardRow = cardSet.createCardRow(i, 0, 0, 5, 10);
			gridMeshes.add(cardRow.getRectangle());
		}

		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 6; j++) {
				Card card = cardSet.createCard(((int) (Math.random() * 14)), ((int) (Math.random() * 8)), i, j);
				gridMeshes.add(card.getRectangle());
			}
		}
		return gridMeshes;
	}

	private Shape createButton(float wd, float hg) {
		Grid3d buttonGrid = new Grid3d();

		float abs = Math.abs(wd - hg);
		float width = abs;
		float height = abs / 2;

		return buttonGrid.createButton(width, height, wd - width / 2, hg - height / 2, -0.1f, new ButtonAction() {

			@Override
			public void doAction() {
				// make nothing
			}
		});
	}

	@Override
	protected void init(Shapes meshes, float wd, float hg) {
		meshes.add(createButton(wd, hg));
		meshes.addAll(addGridMeshes());
	}

}
