package de.brod.gui;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;
import android.opengl.GLUtils;
import android.util.Log;

public class GuiTexture {

	public static int bind(GL10 gl, Bitmap pBitmap) {

		Bitmap bitmap = resizeBitmap(pBitmap);
		// loading texture

		int[] textures = new int[1];
		// generate one texture pointer
		gl.glGenTextures(1, textures, 0);
		// ...and bind it to our array
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);

		// create nearest filtered texture
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,
				GL10.GL_NEAREST);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,
				GL10.GL_LINEAR);

		// Use Android GLUtils to specify a two-dimensional texture image from our bitmap
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);

		// Clean up
		bitmap.recycle();
		Log.d("Texture", "Create " + textures[0]);
		return textures[0];
	}

	private static Bitmap resizeBitmap(Bitmap bitmap) {
		int wd = bitmap.getWidth();
		int hg = bitmap.getHeight();

		int wd2 = get2exp(wd);
		int hg2 = get2exp(hg);
		if (wd != wd2 || hg != hg2) {
			// create a new bitmap
			Bitmap newBmp = Bitmap.createScaledBitmap(bitmap, wd2, hg2, true);
			bitmap.recycle();
			return newBmp;
		}
		return bitmap;
	}

	private static int get2exp(int wOrig) {
		if (wOrig < 4) {
			return 4;
		}
		int wNew = 256;
		if (wNew > wOrig) {
			while (wNew > wOrig) {
				wNew /= 2;
			}
			return wNew * 2;
		} else {
			while (wNew < wOrig) {
				wNew *= 2;
			}
		}
		return wNew;
	}

}
