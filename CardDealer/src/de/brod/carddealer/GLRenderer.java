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

		final float[] triangle4VerticesData = {
				// X, Y, Z,
				// R, G, B, A
				-p0, -p1, 0.0f, 1.0f, 1.0f, 0.5f, 1.0f, //
				p0, -p1, 0.0f, 0.5f, 0.5f, 0.2f, 1.0f, //
				0.0f, p2, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f };

		// Initialize the buffers.
		for (int i = 0; i < 10; i++) {
			float f = 0.51f - i / 20f;
			createVertice(triangle1VerticesData).setPosition(f, -f, 0.0f);
			createVertice(triangle2VerticesData).setPosition(f, f, 0.0f);
			createVertice(triangle3VerticesData).setPosition(-f, f, 0.0f);
			createVertice(triangle4VerticesData).setPosition(-f, -f, 0.0f);
		}
	}

	public Vertice createVertice(float[] triangleVerticesData) {
		Vertice vertice = new Vertice(triangleVerticesData);
		listOfVertices.add(vertice);
		return vertice;
	}

	@Override
	public void onDrawFrame(GL10 glUnused) {
		GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

		// Do a complete rotation every 10 seconds.
		long time = SystemClock.uptimeMillis() % 10000L;
		float angleInDegrees = (360.0f / 10000.0f) * ((int) time);
		for (Vertice vertice : listOfVertices) {
			vertice.setAngle(0, 0, angleInDegrees);
			angleInDegrees += 19;
		}

		// Draw the triangle facing straight on.
		for (Vertice vertice : listOfVertices) {
			glProgram.drawTriangle(vertice);
		}
	}

	@Override
	public void onSurfaceChanged(GL10 glUnused, int width, int height) {
		// Set the OpenGL viewport to the same size as the surface.
		GLES20.glViewport(0, 0, width, height);

		// Create a new perspective projection matrix. The height will stay the
		// same
		// while the width will vary as per aspect ratio.
		final float ratio = (float) width / height;
		final float left = -ratio;
		final float right = ratio;
		final float bottom = -1.0f;
		final float top = 1.0f;
		final float near = 1.0f;
		final float far = 10.0f;

		glProgram.setProjectionMatrix(left, right, bottom, top, near, far);
	}

	@Override
	public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {

		// Set the background clear color to gray.
		GLES20.glClearColor(0.5f, 0.5f, 0.5f, 0.5f);

		// Position the eye behind the origin.
		final float eyeX = 0.0f;
		final float eyeY = 0.0f;
		final float eyeZ = 1.5f;

		// We are looking toward the distance
		final float lookX = 0.0f;
		final float lookY = 0.0f;
		final float lookZ = -5.0f;

		// Set our up vector. This is where our head would be pointing were we
		// holding the camera.
		final float upX = 0.0f;
		final float upY = 1.0f;
		final float upZ = 0.0f;

		glProgram = new GLProgram(eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);
	}

}
