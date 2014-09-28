package de.brod.opengl;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;

public class Rect {

	private static Grid<Rect> _grid;
	private List<Sprite<Rect>> _sprite = new ArrayList<Sprite<Rect>>();
	private Sprite<Rect> centerSprite;

	public Rect(float x, float y, float width, float height,
			boolean pbRoundBorder) {
		centerSprite = _grid.createSprite(1, 1, x, y, width, height);
		if (pbRoundBorder) {
			// addSprite(centerSprite);
			float min = Math.min(width, height) / 3;
			float x1 = (width - min) / 2;
			float y1 = (height - min) / 2;
			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < 3; j++) {
					Sprite<Rect> spriteItem = _grid.createSprite(i, j, x + x1
							* (i - 1), y - y1 * (j - 1),
							i == 1 ? (width - min * 2) : min,
							j == 1 ? (height - min * 2) : min);
					addSprite(spriteItem);
				}
			}
		} else {
			addSprite(centerSprite);
		}
	}

	private void addSprite(Sprite<Rect> sprite) {
		sprite.setReference(this);
		_sprite.add(sprite);
	}

	public void draw(GL10 gl) {

		for (Sprite<Rect> sprite : _sprite) {
			sprite.draw(gl);
		}

	}

	public void setColor(int pr, int pg, int pb, int pa) {
		for (Sprite<Rect> sprite : _sprite) {
			sprite.setColor(pr, pg, pb, pa);
		}
	}

	public static void onSurfaceChanged(OpenGLActivity pActivity, GL10 gl,
			int width, int height) {

		Bitmap bmp = Bitmap.createBitmap(256, 256, Config.ARGB_8888);
		Canvas c = new Canvas(bmp);

		Paint paint = new Paint();
		int bd = 10;
		float r = bd * 2;
		paint.setStrokeWidth(bd * 1.5f);

		paint.setColor(Color.WHITE);
		paint.setStyle(Style.FILL_AND_STROKE);
		RectF rect = new RectF(bd, bd, 255 - bd, 255 - bd);
		c.drawRoundRect(rect, r, r, paint);

		paint.setColor(Color.BLACK);
		paint.setStyle(Style.STROKE);
		c.drawRoundRect(rect, r, r, paint);

		_grid = new Grid<Rect>(3, 3, gl, bmp);
		bmp.recycle();
	}

	public void setColor(int color) {
		int r = Color.red(color);
		int g = Color.green(color);
		int b = Color.blue(color);
		int a = Color.alpha(color);
		setColor(r, g, b, a);
	}

	public boolean touches(float eventX, float eventY) {
		return centerSprite.touches(eventX, eventY);
	}

}