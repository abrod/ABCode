package de.brod.carddealer;

import de.brod.opengl.GLActivity;
import de.brod.opengl.Mesh;
import de.brod.opengl.Meshes;
import de.brod.opengl.Square;

public class MainActivity extends GLActivity {

	@Override
	protected void init() {
		addMesh(new Square(1, 0.8f, 0, 0, 0));
		addMesh(new Square(0.8f, 1, 0.5f, 0.2f, 0));

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
						Meshes meshes = getMeshes();
						for (Mesh mesh : meshes) {
							mesh.setRotateZ(angle);
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
