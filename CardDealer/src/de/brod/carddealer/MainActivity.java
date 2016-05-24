package de.brod.carddealer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import de.brod.opengl.GLActivity;
import de.brod.opengl.GLGrid;
import de.brod.opengl.Rectangle;
import de.brod.opengl.Shape;
import de.brod.opengl.Shapes;

public class MainActivity extends GLActivity {

	private void addColorRect(final Shapes meshes) {
		Rectangle rect = new Rectangle(0.8f, 0.8f, -0.2f, 0, 0);
		rect.setColors(1, 1, 0, 1, //
				0, 1, 0, 1, //
				0, 0, 1, 1, //
				1, 0, 0, 1//
		);
		meshes.add(rect);
	}

	private void addGridMeshes(final Shapes meshes) {
		GLGrid grid = new GLGrid(2, 2) {

			@Override
			protected Bitmap loadBitmap() {
				return BitmapFactory.decodeResource(getResources(), R.drawable.icon);
			}
		};
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 2; j++) {
				Rectangle rect = new Rectangle(0.5f, 0.5f, i * 0.5f - 0.5f, 0.8f - j * 0.5f, 0);
				meshes.add(rect);
				rect.setGrid(grid, i, j, i, j);
			}
		}
	}

	@Override
	protected void init(final Shapes meshes, float wd, float hg) {
		addColorRect(meshes);
		addGridMeshes(meshes);

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
						Shape shape = meshes.get(0);
						shape.setRotateZ(angle);
						float yAngle = (shape.getY() + shape.getX() * 0.5f) * 180;
						for (int i = 1; i < meshes.size(); i++) {
							Shape shape1 = meshes.get(i);
							shape1.setRotateZ(yAngle);
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
