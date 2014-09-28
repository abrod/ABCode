package de.brod.opengl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;
import android.opengl.GLUtils;
import android.util.Log;

public class Grid<E> {

	// private float mScreenWidth, mScreenHeight, wRatio, hRatio;
	private int[] _texture = new int[1]; // frame animation

	private float _xOffset, _yOffset;

	private FloatBuffer[] _texCoordPointers;

	private int _iSpriteCount;
	private int _iCountX;

	public Grid(int piCountX, int piCountY, GL10 gl, Bitmap bitmap) {

		_xOffset = 1.0f / piCountX;
		_yOffset = 1.0f / piCountY;

		_iCountX = piCountX;
		_iSpriteCount = piCountX * piCountY;
		_texCoordPointers = new FloatBuffer[_iSpriteCount];

		int index = 0;

		for (int row = 0; row < piCountY; row++) {
			for (int col = 0; col < piCountX; col++) {
				float[] texCoordPointer = createTexCoordPointer(col, row);

				ByteBuffer bb = ByteBuffer
						.allocateDirect(texCoordPointer.length * 4);
				bb.order(ByteOrder.nativeOrder());
				FloatBuffer textureBuffer = bb.asFloatBuffer();
				textureBuffer.put(texCoordPointer);
				textureBuffer.position(0);

				_texCoordPointers[index] = textureBuffer;
				index++;
			}
		}

		// load the text
		bindTexture(gl, bitmap);
	}

	private float[] createTexCoordPointer(int pX, int pY) {

		float texture[] = new float[8];

		/*
		 * V1 _____ V3 | | | | V2|_____|V4
		 */
		// StringBuffer buff = new StringBuffer();

		/** V1 */
		texture[0] = _xOffset * pX;
		texture[1] = _yOffset * (pY + 1);

		/** V2 */
		texture[2] = _xOffset * pX;
		texture[3] = _yOffset * pY;

		/** V3 */
		texture[4] = _xOffset * (pX + 1);
		texture[5] = _yOffset * (pY + 1);

		/** V4 */
		texture[6] = _xOffset * (pX + 1);
		texture[7] = _yOffset * pY;

		return texture;

	}

	private void bindTexture(GL10 gl, Bitmap bitmap) {

		gl.glGenTextures(1, _texture, 0);

		gl.glBindTexture(GL10.GL_TEXTURE_2D, _texture[0]);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,
				GL10.GL_LINEAR);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,
				GL10.GL_LINEAR);
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);

		Log.d("Grid",
				"bindTexture: " + _texture[0] + ", size: " + bitmap.getWidth()
						+ "x" + bitmap.getHeight());
	}

	public void drawTexCoordPointer(GL10 gl, int mFrame) {
		gl.glBindTexture(GL10.GL_TEXTURE_2D, _texture[0]);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, _texCoordPointers[mFrame]);
	}

	public Sprite<E> createSprite(int piPosX, int piPosY, float x, float y,
			float width, float height) {
		Sprite<E> sprite = new Sprite<E>(this, x, y, width, height);
		return sprite.setGrid(piPosX, piPosY);
	}

	public int getNextItem(int mFrame) {
		return (mFrame + 1) % _iSpriteCount;
	}

	public int getFrame(int piPosX, int piPosY) {
		return piPosX + piPosY * _iCountX;
	}

}
