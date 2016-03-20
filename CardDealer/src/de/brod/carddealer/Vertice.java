package de.brod.carddealer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Arrays;

import android.opengl.GLES20;
import android.opengl.Matrix;

public class Vertice {

	/**
	 * Store the model matrix. This matrix is used to move models from object
	 * space (where each model can be thought of being located at the center of
	 * the universe) to world space.
	 */
	private float[] mModelMatrix = new float[16];
	/**
	 * Allocate storage for the final combined matrix. This will be passed into
	 * the shader program.
	 */
	// private float[] mMVPMatrix = new float[16];

	/** How many bytes per float. */
	private final int bytesPerFloat = 4;

	/** Size of the position data in elements. */
	private final int positionDataSize = 3;

	/** Size of the color data in elements. */
	private final int colorDataSize = 4;

	private final int itemSize = colorDataSize + positionDataSize;

	/** How many elements per vertex. */
	private final int strideBytes = itemSize * bytesPerFloat;

	/** Offset of the position data. */
	private final int positionOffset = 0;

	/** Offset of the color data. */
	private final int colorOffset = positionDataSize;

	FloatBuffer triangleBuffer;

	private float[] angle = { 0.0f, 0.0f, 0.0f };

	private float x, y, z;

	private boolean dirtyFlag = true;
	private int amountOfEdges;
	private float[] triangleVerticesData;
	private float[] points;

	public Vertice() {
		points = new float[0];
	}

	public Vertice addPoints(float... newPoints) {
		int oldLength = points.length;
		points = Arrays.copyOf(points, points.length + newPoints.length);
		System.arraycopy(newPoints, 0, points, oldLength, newPoints.length);
		return this;
	}

	private void calculateModelMatrix() {
		Matrix.setIdentityM(mModelMatrix, 0);
		// set the position
		Matrix.translateM(mModelMatrix, 0, x, y, z);
		// execute transformation
		if (angle[2] != 0.0f) {
			Matrix.rotateM(mModelMatrix, 0, angle[2], 0.0f, 0.0f, 1.0f);
		}
		if (angle[0] != 0.0f) {
			Matrix.rotateM(mModelMatrix, 0, angle[0], 0.1f, 0.0f, 0.0f);
		}
		if (angle[1] != 0.0f) {
			Matrix.rotateM(mModelMatrix, 0, angle[1], 0.0f, 0.1f, 0.0f);
		}
		// This multiplies the view matrix by the model matrix, and stores the
		// result in the MVP matrix
		// (which currently contains model * view).
		Matrix.multiplyMM(mModelMatrix, 0, GLProgram.mViewMatrix, 0, mModelMatrix, 0);

		// This multiplies the modelview matrix by the projection matrix, and
		// stores the result in the MVP matrix
		// (which now contains model * view * projection).
		Matrix.multiplyMM(mModelMatrix, 0, GLProgram.mProjectionMatrix, 0, mModelMatrix, 0);

		dirtyFlag = false;
	}

	void enableVertexAttributes(int mPositionHandle, int mColorHandle) {

		// Pass in the position information
		enableVertexAttributes(mPositionHandle, positionDataSize, positionOffset);

		// Pass in the color information
		enableVertexAttributes(mColorHandle, colorDataSize, colorOffset);
	}

	private void enableVertexAttributes(int index, int size, int offset) {
		triangleBuffer.position(offset);
		GLES20.glVertexAttribPointer(index, size, GLES20.GL_FLOAT, false, strideBytes, triangleBuffer);
		GLES20.glEnableVertexAttribArray(index);
	}

	public int getAmountOfEdges() {
		return amountOfEdges;
	}

	float[] getModelMatrix() {
		if (dirtyFlag) {
			calculateModelMatrix();
		}

		return mModelMatrix;
	}

	public Vertice init() {
		triangleVerticesData = initTriangles(points);
		int capacity = triangleVerticesData.length * bytesPerFloat;
		amountOfEdges = capacity / strideBytes;
		triangleBuffer = ByteBuffer.allocateDirect(capacity).order(ByteOrder.nativeOrder()).asFloatBuffer();
		setSize(1f);
		return this;
	}

	private float[] initTriangles(float[] originalVerticesData) {
		int countPoints = originalVerticesData.length / itemSize;

		// if there are more than 3 items
		if (countPoints > 3) {
			int triangleSize = itemSize * 3;
			int trianglesAmount = (countPoints - 2) * triangleSize;
			// copy first triangle
			float[] f = Arrays.copyOf(originalVerticesData, trianglesAmount);
			int offsetNew = triangleSize;
			int offsetOrg = triangleSize;
			while (offsetNew < f.length) {
				// copy last item
				System.arraycopy(f, offsetNew - itemSize, f, offsetNew, itemSize);
				offsetNew += itemSize;
				// copy next item
				System.arraycopy(originalVerticesData, offsetOrg, f, offsetNew, itemSize);
				offsetNew += itemSize;
				offsetOrg += itemSize;
				// copy initial item
				System.arraycopy(f, 0, f, offsetNew, itemSize);
				offsetNew += itemSize;
			}
			return f;
		}
		return originalVerticesData;
	}

	private float[] resize(float[] triangleData, float f) {
		if (f == 1f) {
			return triangleData;
		} else {
			float[] copyOf = Arrays.copyOf(triangleData, triangleData.length);
			for (int i = 0; i < copyOf.length; i += 7) {
				for (int j = i; j < i + 3; j++) {
					copyOf[j] *= f;
				}
			}
			return copyOf;
		}
	}

	public Vertice setAngle(float x, float y, float z) {
		if (angle[0] != x) {
			angle[0] = x;
			dirtyFlag = true;
		}
		if (angle[1] != y) {
			angle[1] = y;
			dirtyFlag = true;
		}
		if (angle[2] != z) {
			angle[2] = z;
			dirtyFlag = true;
		}
		return this;
	}

	public void setDirtyFlag() {
		dirtyFlag = true;
	}

	public Vertice setPosition(float x, float y, float z) {
		if (this.x != x) {
			this.x = x;
			dirtyFlag = true;
		}
		if (this.y != y) {
			this.y = y;
			dirtyFlag = true;
		}
		if (this.z != z) {
			this.z = z;
			dirtyFlag = true;
		}
		return this;
	}

	public Vertice setSize(float f) {
		triangleBuffer.position(0);
		triangleBuffer.put(resize(triangleVerticesData, f)).position(0);
		return this;
	}
}
