package de.brod.gui;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;

/**
 * @author Andreas_2
 *
 */
public class GuiText implements IGuiQuad {

	GuiGrid			grid	= null;
	private GuiQuad	lstQuads;
	private float	width;
	private float	height;

	class TextGrid extends GuiGrid {

		private String	_sText;

		public TextGrid(String psText) {
			super(2, 1);
			_sText = psText;
		}

		@Override
		protected Bitmap createBitmap(int pwidth, int pheight) {
			float maxSize = Math.max(pwidth, pheight);
			int maxX = (int) (maxSize * width / 2);
			int maxY = (int) (maxSize * height / 2);
			Bitmap bitmap = Bitmap.createBitmap(maxX * 2, maxY,
					Bitmap.Config.ARGB_8888);
			Canvas c = new Canvas(bitmap);
			draw(_sText, c, 0, maxX, maxY);
			draw(_sText, c, maxX, maxX, maxY);
			return bitmap;
		}

	}

	public GuiText(float x, float y, float pfwidth, float pfheight,
			String psText) {
		this.width = pfwidth;
		this.height = pfheight;
		grid = new TextGrid(psText);
		lstQuads = new GuiQuad(grid, x, y, width, height);
		lstQuads.setGrid(0, 0);
		lstQuads.setLevel(-10);
	}

	protected void draw(String psText, Canvas c, int dx, int maxX, int maxY) {
		if (psText != null && psText.length() > 0) {
			drawText(c, dx, maxX, maxY, psText);
		}
	}

	private void drawText(Canvas c, int dx, int width, int height, String psText) {
		Paint p = new Paint();
		int d36 = Math.min(width, height) / 36;

		p.setStyle(Style.FILL);

		float textHeight = height;
		Rect rect2 = new Rect();
		float wd2 = width * 0.8f;
		float hg2 = height * 0.8f;
		do {
			p.setTextSize(textHeight);
			p.getTextBounds(psText.toCharArray(), 0, psText.length(), rect2);
			textHeight--;
		} while (textHeight > 6 && rect2.width() > wd2 && rect2.height() > hg2);
		int d = d36;
		if (dx > 0) {
			d *= 2;
		}

		p.setARGB(255, 0, 0, 0);
		c.drawText(psText, dx + d + d36 + (width - rect2.width()) / 2, d + d36
				+ rect2.height() + (height - rect2.height()) / 4, p);
		p.setARGB(255, 255, 255, 255);
		c.drawText(psText, dx + d + (width - rect2.width()) / 2,
				d + rect2.height() + (height - rect2.height()) / 4, p);

	}

	@Override
	public void close() {
		lstQuads.close();
	}

	@Override
	public void draw(GL10 pGL10) {
		lstQuads.draw(pGL10);
	}

	@Override
	public float getXY() {
		return lstQuads.getXY();
	}

	@Override
	public boolean touches(float eventX, float eventY) {
		return lstQuads.touches(eventX, eventY);
	}

	@Override
	public void moveTo(float eventX, float eventY) {
		if (isMoveable()) {
			lstQuads.moveTo(eventX, eventY);
		}
	}

	/**
	 * @return false per default
	 */
	protected boolean isMoveable() {
		return false;
	}

	public void setDown(boolean pbDown) {
		lstQuads.setGrid((pbDown ? 1 : 0), 0, true);
	}

	@Override
	public void setColor(int a, int r, int g, int b) {
		lstQuads.setColor(a, r, g, b);
	}

	@Override
	public void slideTo(float X, float Y) {
		lstQuads.slideTo(X, Y);
	}

}