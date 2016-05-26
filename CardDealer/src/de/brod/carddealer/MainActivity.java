package de.brod.carddealer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import de.brod.opengl.GLActivity;
import de.brod.opengl.GLGrid;
import de.brod.opengl.Rectangle;
import de.brod.opengl.Shape;
import de.brod.opengl.Shapes;

public class MainActivity extends GLActivity {

	private Rectangle	rect;
	private Shapes		gridMeshes;

	private void addColorRect(final Shapes meshes) {
		rect = new Rectangle(0.8f, 0.8f, -0.2f, 0, -1);
		rect.setColors(1, 1, 0, 1, //
				0, 1, 0, 1, //
				0, 0, 1, 1, //
				1, 0, 0, 1//
		);
		meshes.add(rect);
	}

	private Shapes addGridMeshes() {
		gridMeshes = new Shapes();
		GLGrid grid = new GLGrid(14, 4) {

			@Override
			protected Bitmap loadBitmap() {
				return BitmapFactory.decodeResource(getResources(), R.drawable.cards);
			}
		};
		float wd = 2f / 8;
		float hg = wd * 4 / 3;

		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 4; j++) {
				float x = -1 + wd / 2 + wd * i;
				float y = 1 - hg / 2 - hg * j;
				Rectangle rect = new Rectangle(wd, hg, x, y, 0);
				gridMeshes.add(rect);
				rect.setGrid(grid, i, j, i, j);
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
						float yAngle = (rect.getY() + rect.getX() * 0.5f) * 180;
						for (Shape shape : gridMeshes) {
							shape.setRotateZ(yAngle);
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
