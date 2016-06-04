package de.brod.carddealer;

import de.brod.opengl.GLActivity;
import de.brod.opengl.Rectangle;
import de.brod.opengl.Shape;
import de.brod.opengl.Shapes;

public class MainActivity extends GLActivity {

	private Rectangle	rect;
	private Shapes		gridMeshes;

	@Override
	protected void actionDown(Shapes shapes) {
		// remove all except first
		while (shapes.size() > 1) {
			shapes.remove(0);
		}
		for (Shape shape : shapes) {
			if (!shape.equals(rect)) {
				shape.setZ(1f);
			}
		}
	}

	@Override
	protected void actionUp(Shapes selected, Shapes up) {
		for (Shape shape : selected) {
			if (!shape.equals(rect)) {
				shape.setZ(0f);
			}
		}
	}

	private void addColorRect(final Shapes meshes) {
		rect = new Rectangle(0.8f, 0.8f, -0.2f, 0, 0.8f);
		rect.setColors(1, 1, 0, 1, //
				0, 1, 0, 1, //
				0, 0, 1, 1, //
				1, 0, 0, 1//
		);
		meshes.add(rect);
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
		addColorRect(meshes);
		meshes.addAll(addGridMeshes());

		new Thread() {
			@Override
			public void run() {
				try {
					while (true) {
						long l = System.currentTimeMillis() % 20000;
						if (l > 10000) {
							l = 20000 - l;
						}
						float angle = l / 10000f * 360f;
						rect.setRotateZ(angle);
						angle = Math.abs((rect.getY() + rect.getX() * 0.5f) * 180);
						for (Shape shape : gridMeshes) {
							shape.setRotateZ(angle);
						}
						requestRender();
						sleep(25);
					}
				} catch (InterruptedException e) {
					// stopped
				}
			}
		}.start();
	}

}
