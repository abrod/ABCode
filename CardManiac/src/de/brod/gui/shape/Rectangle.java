package de.brod.gui.shape;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import de.brod.gui.Texture;

public class Rectangle extends Sprite {

	private static Texture texture;

	public static void init(GL10 gl, float fTitleHeight) {
		// create an empty bitmap
		Bitmap bmp = Bitmap.createBitmap(128, 128, Config.ARGB_8888);
		new Canvas(bmp).drawColor(Color.WHITE);
		texture = new Texture(gl, bmp, 3, 3);
		bmp.recycle();
	}

	public Rectangle(float px, float py, float width, float height) {
		super(texture, width, height);
		setCenter(false);
		setPosition(px, py);
		setCell(1, 1, 1, 1);
	}
}
