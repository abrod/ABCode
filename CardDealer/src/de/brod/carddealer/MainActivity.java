package de.brod.carddealer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import de.brod.opengl.GLActivity;
import de.brod.opengl.GLGrid;
import de.brod.opengl.Rectangle;
import de.brod.opengl.Shape;
import de.brod.opengl.Shapes;

public class MainActivity extends GLActivity {

	@Override
	protected void init(final Shapes meshes, float wd, float hg) {
		Rectangle rect1 = new Rectangle(0.8f, 0.8f, -0.2f, 0, 0);
		meshes.add(rect1);
		final Rectangle rect2 = new Rectangle(0.8f, 0.8f, 0.5f, 0.2f, 0);
		meshes.add(rect2);

		GLGrid grid = new GLGrid() {

			@Override
			protected Bitmap loadBitmap() {
				return BitmapFactory.decodeResource(getResources(), R.drawable.icon);
			}
		};

		rect2.setGrid(grid, 0, 0, 0, 0);
		rect1.setColors(1, 1, 0, 1, //
				0, 1, 0, 1, //
				0, 0, 1, 1, //
				1, 0, 0, 1//
		);

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
						for (Shape mesh : meshes) {
							mesh.setRotateZ(angle);
						}
						rect2.setRotateY(angle / 2);
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
