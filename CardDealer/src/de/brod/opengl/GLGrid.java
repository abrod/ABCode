package de.brod.opengl;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;
import android.opengl.GLUtils;

public abstract class GLGrid {
	private boolean	loadTexture;
	private int		textureId;
	float			countX, countY;

	public GLGrid(int countX, int countY) {
		loadTexture = true;
		setCount(countX, countY);
	}

	public Rectangle createRectangle(float wd, float hg, float x, float y, int z, int x1, int y1, int x2, int y2) {
		Rectangle rect = new Rectangle(wd, hg, x, y, z);
		rect.setGrid(this, x1, y1, x2, y2);
		return rect;
	}

	public int getTextureId(GL10 gl) {
		if (loadTexture) {
			loadGLTexture(gl);
			loadTexture = false;
		}
		return textureId;
	}

	public float getX(int x1) {
		return x1 / countX;
	}

	public float getY(int y1) {
		return y1 / countY;
	}

	protected abstract Bitmap loadBitmap();

	/**
	 * Loads the texture.
	 *
	 * @param gl
	 */
	private void loadGLTexture(GL10 gl) {
		// Generate one texture pointer...
		int[] textures = new int[1];
		gl.glGenTextures(1, textures, 0);
		textureId = textures[0];

		// ...and bind it to our array
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textureId);

		// Create Nearest Filtered Texture
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);

		// Different possible texture parameters, e.g. GL10.GL_CLAMP_TO_EDGE
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_REPEAT);

		Bitmap bitmap = loadBitmap();
		// Use the Android GLUtils to specify a two-dimensional texture image
		// from our bitmap
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
		bitmap.recycle();
	}

	protected void setCount(int countX, int countY) {
		this.countX = Math.max(countX, 1);
		this.countY = Math.max(countY, 1);
	}

}
