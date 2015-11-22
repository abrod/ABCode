package de.brod.gui;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;

public class GuiRectangle extends GuiText {

	public GuiRectangle(float x, float y, float pfwidth, float pfheight, String psText) {
		super(x, y, pfwidth, pfheight, psText);
	}

	@Override
	protected void draw(Canvas c, int dx, int maxX, int maxY, boolean up) {
		if (dx == 0) {
			drawBorder(c, 0, maxX, maxY, Color.WHITE, Color.BLACK);
		} else {
			drawBorder(c, dx, maxX, maxY, Color.BLACK, 0);
		}
		// draw the text
		super.draw(c, dx, maxX, maxY, up);
	}

	private void drawBorder(Canvas c, int dx, int width, int height, int white, int black) {
		Paint p = new Paint();
		p.setAntiAlias(true);
		int corner = Math.min(width, height) / 6;
		int wd = corner / 3;
		int d = wd / 2;

		RectF rect = new RectF(dx + d, d, dx + width - d - wd, height - d - wd);
		p.setStyle(Style.STROKE);
		p.setStrokeWidth(wd + 1);
		if (white != 0) {
			p.setColor(white);
			c.drawRoundRect(rect, corner, corner, p);
		}

		rect = new RectF(dx + d + wd, d + wd, dx + width - d, height - d);
		int d2 = d;
		int dOff = 0;
		int cornerGardient = corner / 6;
		if (black != 0) {
			p.setColor(black);
			c.drawRoundRect(rect, corner, corner, p);
		} else {
			d2 = d * 3 / 5;
			dOff = d;
		}

		rect = new RectF(dx + d, d, dx + width - d2, height - d2);
		p.setARGB(255, 192, 192, 192);
		p.setStyle(Style.FILL);
		c.drawRoundRect(rect, corner, corner, p);

		rect = new RectF(dx + d + cornerGardient + dOff, d * 2 + cornerGardient + dOff,
				dx + width - d - cornerGardient + dOff, height - d * 2 - cornerGardient + dOff);
		createLinearGradient(c, p, corner, rect, true, Color.WHITE);
	}

	private void createLinearGradient(Canvas c, Paint p, int corner, RectF rect, boolean landscape, int white) {
		float left = rect.left;
		float right = rect.right;
		float top = rect.top;
		float bottom = rect.bottom;
		float height = bottom - top;

		int alpha = Color.alpha(white);
		int red = Color.red(white);
		int green = Color.green(white);
		int blue = Color.blue(white);
		float l = left;
		float r = right;
		int iMax = (int) (height - corner);
		float[] circleYToX = new float[corner + 1];
		for (int i = 0; i <= corner; i++) {
			circleYToX[i] = corner - circleYToX(i, corner);
		}
		for (int i = 0; i <= height; i++) {
			if (i <= corner) {
				float cX = circleYToX[corner - i];
				l = left + cX;
				r = right - cX;
			} else if (i > iMax) {
				float cX = circleYToX[(i - iMax)];
				l = left + cX;
				r = right - cX;
			}
			float line = top + i;
			float fakt = 1 - i / height / 2;
			p.setColor(Color.argb(alpha, (int) (red * fakt), (int) (green * fakt), (int) (blue * fakt)));
			c.drawLine(l, line, r, line, p);
		}
	}

	private float circleYToX(float y, float radius) {
		// radius = sqrt(x*x + y*y)
		// radius*radius = x*x + y*y
		// radius*radius - y*y = x*x
		// sqrt(radius*radius - y*y) = x
		return (float) Math.sqrt(radius * radius - y * y);
	}

}
