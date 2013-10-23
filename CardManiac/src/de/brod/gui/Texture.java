package de.brod.gui;

import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;
import android.opengl.GLUtils;

public class Texture {

	public static Bitmap base2image(Bitmap bmp) {
		float fW = getBase2(bmp.getWidth());
		float fH = getBase2(bmp.getHeight());
		if (fW != 1 || fH != 1) {
			Bitmap bmp2 = Bitmap.createScaledBitmap(bmp,
					Math.round(bmp.getWidth() * fW / 4) * 4,
					Math.round(bmp.getHeight() * fH / 4) * 4, false);
			bmp.recycle();
			return bmp2;
		}
		return bmp;
	}

	private static float getBase2(int piValue) {
		float i = 256;
		float f = i / piValue;
		while (f < 0.75) {
			i *= 2;
			f = i / piValue;
		}
		while (f > 1.5f && i > 8) {
			i /= 2;
			f = i / piValue;
		}
		return f;
	}

	// Our texture id.
	int mTextureId = -1; // New variable.

	// The bitmap we want to load as a texture.
	private Bitmap mBitmap; // New variable.

	private float cX, cY, width, height;

	private Texture addTexture = null;

	/**
	 * Set the bitmap to load into a texture.
	 * 
	 * @param bitmap
	 */
	public Texture(GL10 gl, Bitmap bitmap, float countX, float countY) { // New
																			// function.
		cX = countX;
		cY = countY;
		width = bitmap.getWidth();
		height = bitmap.getHeight();
		this.mBitmap = bitmap;
		// Generate one texture pointer...
		int[] textures = new int[1];
		gl.glGenTextures(1, textures, 0);
		mTextureId = textures[0];

		// ...and bind it to our array
		gl.glBindTexture(GL10.GL_TEXTURE_2D, mTextureId);

		// Create Nearest Filtered Texture
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,
				GL10.GL_LINEAR);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,
				GL10.GL_LINEAR);

		// Different possible texture parameters, e.g. GL10.GL_CLAMP_TO_EDGE
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S,
				GL10.GL_CLAMP_TO_EDGE);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T,
				GL10.GL_REPEAT);

		// Use the Android GLUtils to specify a two-dimensional texture image
		// from our bitmap
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, mBitmap, 0);
	}

	public void add(Texture texture) {
		if (addTexture == null) {
			addTexture = texture;
		} else {
			addTexture.add(texture);
		}
	}

	public int setBuffer(FloatBuffer mTextureBuffer, float px, float py) {
		float y1 = py / cY;
		if (py >= cY && addTexture != null) {
			return addTexture.setBuffer(mTextureBuffer, px, py - cY);
		}
		float x0 = px / cX;
		float x1 = (px + 1) / cX;
		float y0 = (py + 1) / cY;
		// Mapping coordinates for the vertices
		// float textureCoordinates[] = { x0, y1, //
		// x1, y1, //
		// x0, y0, //
		// x1, y0 //
		// };
		mTextureBuffer.put(x0);
		mTextureBuffer.put(y0);
		mTextureBuffer.put(x1);
		mTextureBuffer.put(y0);
		mTextureBuffer.put(x0);
		mTextureBuffer.put(y1);
		mTextureBuffer.put(x1);
		mTextureBuffer.put(y1);
		return mTextureId;
	}

	public int setBuffer(FloatBuffer mTextureBuffer, int px1, int py1, int px2,
			int py2) {
		float x0 = px1 / width;
		float x1 = px2 / width;
		float y0 = py1 / height;
		float y1 = py2 / height;
		// Mapping coordinates for the vertices
		// float textureCoordinates[] = { x0, y1, //
		// x1, y1, //
		// x0, y0, //
		// x1, y0 //
		// };
		mTextureBuffer.put(x0);
		mTextureBuffer.put(y0);
		mTextureBuffer.put(x1);
		mTextureBuffer.put(y0);
		mTextureBuffer.put(x0);
		mTextureBuffer.put(y1);
		mTextureBuffer.put(x1);
		mTextureBuffer.put(y1);
		return mTextureId;
	}

}
