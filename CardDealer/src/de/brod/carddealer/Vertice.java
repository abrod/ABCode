package de.brod.carddealer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.opengl.GLES20;
import android.opengl.Matrix;

public class Vertice {

	/**
	 * Store the model matrix. This matrix is used to move models from object
	 * space (where each model can be thought of being located at the center of
	 * the universe) to world space.
	 */
	private float[] mModelMatrix = new float[16];

	/** How many bytes per float. */
	private final int mBytesPerFloat = 4;

	/** Size of the position data in elements. */
	private final int mPositionDataSize = 3;

	/** Size of the color data in elements. */
	private final int mColorDataSize = 4;

	/** How many elements per vertex. */
	private final int mStrideBytes = (mPositionDataSize + mColorDataSize) * mBytesPerFloat;

	/** Offset of the position data. */
	private final int mPositionOffset = 0;

	/** Offset of the color data. */
	private final int mColorOffset = mPositionDataSize;

	FloatBuffer aTriangleBuffer;

	private float[] angle = { 0.0f, 0.0f, 0.0f };

	private float x, y, z;

	private boolean updateModel = true;

	public Vertice(float[] triangle1VerticesData) {
		aTriangleBuffer = ByteBuffer.allocateDirect(triangle1VerticesData.length * mBytesPerFloat)
				.order(ByteOrder.nativeOrder()).asFloatBuffer();
		aTriangleBuffer.put(triangle1VerticesData).position(0);
	}

	private void calculateModel() {
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
		updateModel = false;
	}

	void enableVertexAttributes(int mPositionHandle, int mColorHandle) {

		// Pass in the position information
		enableVertexAttrib(mPositionHandle, mPositionDataSize, mPositionOffset);

		// Pass in the color information
		enableVertexAttrib(mColorHandle, mColorDataSize, mColorOffset);
	}

	private void enableVertexAttrib(int index, int size, int offset) {
		aTriangleBuffer.position(offset);
		GLES20.glVertexAttribPointer(index, size, GLES20.GL_FLOAT, false, mStrideBytes, aTriangleBuffer);
		GLES20.glEnableVertexAttribArray(index);
	}

	float[] getModelMatrix() {
		if (updateModel) {
			calculateModel();
		}

		return mModelMatrix;
	}

	public void setAngle(float x, float y, float z) {
		if (angle[0] != x) {
			angle[0] = x;
			updateModel = true;
		}
		if (angle[1] != y) {
			angle[1] = y;
			updateModel = true;
		}
		if (angle[2] != z) {
			angle[2] = z;
			updateModel = true;
		}
	}

	public void setPosition(float x, float y, float z) {
		if (this.x != x) {
			this.x = x;
			updateModel = true;
		}
		if (this.y != y) {
			this.y = y;
			updateModel = true;
		}
		if (this.z != z) {
			this.z = z;
			updateModel = true;
		}
	}
}
