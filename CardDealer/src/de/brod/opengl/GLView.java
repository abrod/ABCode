package de.brod.opengl;

import android.content.Context;
import android.opengl.GLSurfaceView;

public class GLView extends GLSurfaceView {

	private GLRenderer renderer;

	final Square square = new Square(1, 1);

	public GLView(Context context) {
		super(context);
		renderer = new GLRenderer();

		setRenderer(renderer);
		setRenderMode(RENDERMODE_WHEN_DIRTY);

		renderer.addMesh(square);

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
						square.setRotateZ(angle);
						requestRender();
						sleep(25);
					}
				} catch (InterruptedException e) {
					// stopped
				}
			}
		}.start();
	}

	public void actionDown(float x, float y) {
		float x2 = renderer.getX(x);
		float y2 = renderer.getY(y);
		square.setPosition(x2, y2, 0);
		requestRender();
	}

	public void actionMove(float x, float y) {
		float x2 = renderer.getX(x);
		float y2 = renderer.getY(y);
		square.setPosition(x2, y2, 0);
		requestRender();
	}

	public void actionUp(float x, float y) {
		// TODO Auto-generated method stub

	}

}
