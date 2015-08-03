package de.brod.gui;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;

public class GuiButton extends GuiText {

	public GuiButton(float x, float y, float pfwidth, float pfheight,
			String psText) {
		super(x, y, pfwidth, pfheight, psText);
	}

	@Override
	protected void draw(String psText, Canvas c, int dx, int maxX, int maxY) {
		if (dx == 0) {
			drawBorder(c, 0, maxX, maxY, Color.WHITE, Color.BLACK, psText);
		} else {
			drawBorder(c, dx, maxX, maxY, Color.BLACK, 0, psText);
		}
		// draw the text
		super.draw(psText, c, dx, maxX, maxY);
	}

	private void drawBorder(Canvas c, int dx, int width, int height, int white,
			int black, String _sText) {
		Paint p = new Paint();
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
		if (black != 0) {
			p.setColor(black);
			c.drawRoundRect(rect, corner, corner, p);
		} else {
			d2 = d * 3 / 5;
		}

		rect = new RectF(dx + d, d, dx + width - d2, height - d2);
		p.setARGB(255, 192, 192, 192);
		p.setStyle(Style.FILL);
		c.drawRoundRect(rect, corner, corner, p);

	}

	public void doAction() {
		// TODO Auto-generated method stub

	}

}
