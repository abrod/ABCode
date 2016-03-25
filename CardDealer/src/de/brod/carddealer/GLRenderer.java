package de.brod.carddealer;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.SystemClock;

public class GLRenderer implements GLSurfaceView.Renderer {

	/** Store our model data in a float buffer. */
	private final List<Vertice> listOfVertices = new ArrayList<Vertice>();
	private GLProgram glProgram;

	/**
	 * Initialize the model data.
	 */
	public GLRenderer() {
		// Define points for equilateral triangles.

		// This triangle is red, green, and blue.
		float p0 = 0.5f;
		float p1 = 0.25f;
		float p2 = 0.559016994f;
		float pS = 0.5f * 0.707f;
		final float[] triangle1VerticesData = {
				// X, Y, Z,
				// R, G, B, A
				-p0, -p1, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, //
				p0, -p1, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, //
				0.0f, p2, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f };

		// This triangle is yellow, cyan, and magenta.
		final float[] triangle2VerticesData = {
				// X, Y, Z,
				// R, G, B, A
				-p0, -p1, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, //
				p0, -p1, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, //
				0.0f, p2, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f };

		// This triangle is white, gray, and black.
		final float[] triangle3VerticesData = {
				// X, Y, Z,
				// R, G, B, A
				-p0, -p1, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, //
				p0, -p1, 0.0f, 0.5f, 0.5f, 0.5f, 1.0f, //
				0.0f, p2, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f };

		final float[] squareVerticesData = {
				// X, Y, Z,
				// R, G, B, A
				-pS, -pS, 0.0f, 1.0f, 1.0f, 0.5f, 1.0f, //
				pS, -pS, 0.0f, 0.5f, 0.5f, 0.2f, 1.0f, //
				pS, pS, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, //
				-pS, pS, 0.0f, 0.7f, 0.7f, 0.4f, 1.0f };

		// Initialize the buffers.
		for (int i = 0; i < 10; i++) {
			float f = 0.5f - i / 34f;
			float size = 1 - i / 20f;
			createVertice(triangle1VerticesData).setPosition(f, -f, 0.0f).setSize(size);
			createVertice(triangle2VerticesData).setPosition(f, f, 0.0f).setSize(size);
			createVertice(triangle3VerticesData).setPosition(-f, f, 0.0f).setSize(size);
			createVertice(squareVerticesData).setPosition(-f, -f, 0.0f).setSize(size);
		}
	}

	public Vertice createVertice(float[] verticesData) {
		Vertice vertice = new Vertice();
		vertice.addPoints(verticesData);
		listOfVertices.add(vertice);
		vertice.init();
		return vertice;
	}

	@Override
	public void onDrawFrame(GL10 glUnused) {
		GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

		rotate();

		// Draw the triangle facing straight on.
		for (Vertice vertice : listOfVertices) {
			glProgram.drawVertice(vertice);
		}
	}

	@Override
	public void onSurfaceChanged(GL10 glUnused, int width, int height) {
		// Set the OpenGL viewport to the same size as the surface.
		GLES20.glViewport(0, 0, width, height);

		// Create a new perspective projection matrix. The height will stay the
		// same
		// while the width will vary as per aspect ratio.
		float ratio = (float) width / height;
		float right;
		float top;
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
