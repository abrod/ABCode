package de.brod.carddealer;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.SystemClock;
import android.util.Log;

public class GLRenderer implements GLSurfaceView.Renderer {

	/** Store our model data in a float buffer. */
	private final List<Vertice> listOfVertices = new ArrayList<Vertice>();
	private GLProgram glProgram;

	float right, top, wd2, hg2;

	/**
	 * Initialize the model data.
	 */
	public GLRenderer() {

		float p0 = 0.5f;
		float p1 = 0.25f;
		float p2 = 0.559016994f;
		float pS = 0.5f * 0.707f;

		final float[] trianglePosition = {
				// X, Y, Z,
				-p0, -p1, 0.0f, //
				p0, -p1, 0.0f, //
				0.0f, p2, 0.0f };

		// This triangle is red, green, and blue.
		final float[] triangle1Color = {
				// R, G, B, A
				1.0f, 0.0f, 0.0f, 1.0f, //
				0.0f, 0.0f, 1.0f, 1.0f, //
				0.0f, 1.0f, 0.0f, 1.0f };

		// This triangle is yellow, cyan, and magenta.
		final float[] triangle2Color = {
				// R, G, B, A
				1.0f, 1.0f, 0.0f, 1.0f, //
				0.0f, 1.0f, 1.0f, 1.0f, //
				1.0f, 0.0f, 1.0f, 1.0f };

		// This triangle is white, gray, and black.
		final float[] triangle3Color = {
				// R, G, B, A
				1.0f, 1.0f, 1.0f, 1.0f, //
				0.5f, 0.5f, 0.5f, 1.0f, //
				0.0f, 0.0f, 0.0f, 1.0f };

		final float[] squarePosition = {
				// X, Y, Z,
				-pS, -pS, 0.0f, //
				-pS, pS, 0.0f, //
				pS, -pS, 0.0f, //
				pS, pS, 0.0f };
		final float[] squareColor = {
				// R, G, B, A
				1.0f, 1.0f, 0.5f, 1.0f, //
				0.5f, 0.5f, 0.2f, 1.0f, //
				0.7f, 0.7f, 0.4f, 1.0f, //
				0.0f, 0.0f, 0.0f, 1.0f };

		// Initialize the vertices
		for (int i = 0; i < 1; i++) {
			float f = 0.5f - i / 34f;
			float size = 1 - i / 20f;
			createVertice(trianglePosition, triangle1Color).setPosition(f, -f, 0.0f).setSize(size);
			createVertice(trianglePosition, triangle2Color).setPosition(f, f, 0.0f).setSize(size);
			createVertice(trianglePosition, triangle3Color).setPosition(-f, f, 0.0f).setSize(size);
			createVertice(squarePosition, squareColor).setPosition(-f, -f, 0.0f).setSize(size);
		}
	}

	public void actionDown(float x, float y) {
		float x1 = (x / wd2 - 1) * top;
		float y1 = (y / hg2 - 1) * right;
		Log.d("Pos", x1 + " x " + y1);
		for (Vertice vertice : listOfVertices) {
			if (vertice.touches(x1, y1)) {
				vertice.setColors(new float[] { 1, 1, 1, 1 });
			} else {
				vertice.setColors(new float[] { 0, 0, 0, 0 });
			}
		}
	}

	public Vertice createVertice(float[] positionData, float[] colorData) {
		Vertice vertice = new Vertice(positionData);

		vertice.setColors(colorData);
		listOfVertices.add(vertice);
		return vertice;
	}

	@Override
	public void onDrawFrame(GL10 glUnused) {
		GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

		// rotate();

		// Draw the triangle facing straight on.
		for (Vertice vertice : listOfVertices) {
			vertice.draw();
		}
	}

	@Override
	public void onSurfaceChanged(GL10 glUnused, int width, int height) {
		wd2 = width / 2f;
		hg2 = height / 2f;

		// Set the OpenGL viewport to the same size as the surface.
		GLES20.glViewport(0, 0, width, height);

		float ratio = (float) width / height;
		if (ratio < 1) { // height > width
			right = 1f;
			top = 1f / ratio;
		} else { // width > height
			right = ratio;
			top = 1f;
		}
		float left = -right;
		float bottom = -top;
		final float near = 1.0f;
		final float far = 10.0f;

		// set the projection matrix
		glProgram.setProjectionMatrix(left, right, bottom, top, near, far);
		// set dirty flags
		setDirtyFlagsWithinVertices();
	}

	@Override
	public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {

		// Set the background clear color to gray.
		GLES20.glClearColor(0.5f, 0.5f, 0.5f, 0.5f);

		// Position the eye behind the origin.
		final float eyeX = 0.0f;
		final float eyeY = 0.0f;
		final float eyeZ = 1.1f;

		// We are looking toward the distance
		final float lookX = 0.0f;
		final float lookY = 0.0f;
		final float lookZ = -5.0f;

		// Set our up vector. This is where our head would be pointing were we
		// holding the camera.
		final float upX = 0.0f;
		final float upY = 1.0f;
		final float upZ = 0.0f;

		// init the program
		glProgram = new GLProgram(eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);

	}

	private void rotate() {
		// Do a complete rotation every 10 seconds.
		long time = SystemClock.uptimeMillis() % 10000L;
		float angleInDegrees = (360.0f / 10000.0f) * ((int) time);
		for (Vertice vertice : listOfVertices) {
			vertice.setAngle(0, 0, angleInDegrees);
			angleInDegrees += 3;
		}
	}

	private void setDirtyFlagsWithinVertices() {
		for (Vertice vertice : listOfVertices) {
			vertice.setDirtyFlag();
		}
	}

}
