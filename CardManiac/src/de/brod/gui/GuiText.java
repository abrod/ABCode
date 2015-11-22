package de.brod.gui;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Paint.Style;

/**
 * @author Andreas_2
 *
 */
public class GuiText extends GuiQuad {

	private String text;

	public GuiText(float x, float y, float pfwidth, float pfheight, String psText) {
		super(new GuiTwoItemGrid(pfwidth, pfheight), x, y, pfwidth, pfheight);
		this.text = psText;
		getGrid().assignQuad(this);
		setLevel(-10);
	}

	@Override
	public void moveTo(float eventX, float eventY) {
		if (isMoveable()) {
			super.moveTo(eventX, eventY);
		}
	}

	/**
	 * @return false per default
	 */
	protected boolean isMoveable() {
		return false;
	}

	public void setDown(boolean pbDown) {
		setGrid((pbDown ? 1 : 0), 0, true);
	}

	@Override
	protected void draw(Canvas c, int dx, int width, int height, boolean up) {
		if (text == null)
			return;
		Paint p = new Paint();
		p.setAntiAlias(true);
		int d36 = Math.min(width, height) / 36;

		p.setStyle(Style.FILL);

		float textHeight = height;

		Rect rect2 = new Rect();
		float wd2 = width * 0.8f;
		float hg2 = height * 0.8f;
		p.setTextSize(textHeight);
		p.getTextBounds(text.toCharArray(), 0, text.length(), rect2);
		float dx2 = rect2.width() / wd2;
		float dy2 = rect2.height() / hg2;
		float dmax = Math.max(dx2, dy2);
		if (dmax > 1.01) {
			textHeight = textHeight / dmax;
			p.setTextSize(textHeight);
			p.getTextBounds(text.toCharArray(), 0, text.length(), rect2);
		}
		int d = d36;
		if (dx > 0) {
			d *= 2;
		}

		int x1 = d + dx + (width - rect2.width()) / 3;
		int y1 = d + rect2.height() + (height - rect2.height()) / 3;

		if (up) {
			p.setARGB(255, 0, 0, 0);
			c.drawText(text, x1 + d36, y1 + d36, p);

			p.setARGB(255, 255, 255, 255);
			c.drawText(text, x1, y1, p);
		} else {
			p.setARGB(255, 0, 0, 0);
			c.drawText(text, x1, y1, p);

			p.setARGB(255, 255, 255, 255);
			c.drawText(text, x1 + d36, y1 + d36, p);
		}

	}

}