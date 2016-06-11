package de.brod.opengl;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;

public class Grid3d extends GLGrid {
	private final int	size;
	final int			wd;

	public Grid3d() {
		super(9, 9);
		size = 256;
		wd = size / 25;
	}

	public Button createButton(float width, float height, float x, float y, float z, ButtonAction action) {
		return new Button(width, height, x, y, z, this, action);
	}

	private void draw(Canvas c, int dx, int width, int white, int black) {
		Paint p = new Paint();
		int d = wd / 2;
		int corner = wd * 3;

		RectF rect = new RectF(dx + d, d, dx + width - d - wd, width - d - wd);
		p.setStyle(Style.STROKE);
		p.setStrokeWidth(wd);
		if (white != 0) {
			p.setColor(white);
			c.drawRoundRect(rect, corner, corner, p);
		}

		rect = new RectF(dx + d + wd, d + wd, dx + width - d, width - d);
		d = wd * 7 / 10;
		int d2 = d;
		if (black != 0) {
			p.setColor(black);
			c.drawRoundRect(rect, corner, corner, p);
		} else {
			d2 = wd / 2;
		}

		rect = new RectF(dx + d, d, dx + width - d2, width - d2);
		p.setARGB(255, 192, 192, 192);
		p.setStyle(Style.FILL);
		c.drawRoundRect(rect, corner, corner, p);

	}

	@Override
	protected Bitmap loadBitmap() {
		Bitmap bitmap = Bitmap.createBitmap(size * 2, size, Config.ARGB_8888);
		Canvas c = new Canvas(bitmap);
		draw(c, 0, size - 1, Color.WHITE, Color.BLACK);
		draw(c, size, size - 1, Color.BLACK, 0);
		return bitmap;
	}

}
