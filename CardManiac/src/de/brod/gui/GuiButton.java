package de.brod.gui;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;

public class GuiButton implements IGuiQuad {

	GuiGrid			grid	= null;
	private GuiQuad	lstQuads;
	private float	size;
	private float	width;
	private float	height;

	class ButtonGrid extends GuiGrid {

		private String	_sText;

		public ButtonGrid(String psText) {
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
			draw(c, 0, maxX, maxY, Color.WHITE, Color.BLACK);
			draw(c, maxX, maxX, maxY, Color.BLACK, 0);
			return bitmap;
		}

		private void draw(Canvas c, int dx, int width, int height, int white,
				int black) {
			Paint p = new Paint();
			int corner = Math.min(width, height) / 3;
			int wd = corner / 3;
			int d = wd / 2;

			RectF rect = new RectF(dx + d, d, dx + width - d - wd, height - d
					- wd);
			p.setStyle(Style.STROKE);
			p.setStrokeWidth(wd);
			if (white != 0) {
				p.setColor(white);
				c.drawRoundRect(rect, corner, corner, p);
			}

			rect = new RectF(dx + d + wd, d + wd, dx + width - d, height - d);
			d = 7;
			int d2 = 7;
			if (black != 0) {
				p.setColor(black);
				c.drawRoundRect(rect, corner, corner, p);
			} else {
				d2 = 5;
			}

			rect = new RectF(dx + d, d, dx + width - d2, height - d2);
			p.setARGB(255, 192, 192, 192);
			p.setStyle(Style.FILL);
			c.drawRoundRect(rect, corner, corner, p);
			p.setARGB(255, 255, 255, 255);
			Rect rect2 = new Rect();
			p.getTextBounds(_sText.toCharArray(), 0, _sText.length(), rect2);
			c.drawText(_sText, dx + d, d + rect2.height(), p);

		}
	}

	public GuiButton(float x, float y, float pfwidth, float pfheight,
			String psText) {
		this.width = pfwidth;
		this.height = pfheight;
		grid = new ButtonGrid(psText);
		size = Math.min(width, height) / 3f;
		lstQuads = new GuiQuad(grid, x, y, width, height);
		lstQuads.setGrid(0, 0);

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
		lstQuads.moveTo(eventX, eventY);
	}

	public void setDown(boolean pbDown) {
		lstQuads.setGrid((pbDown ? 1 : 0), 0, true);
	}

	public void doAction() {
		// TODO Auto-generated method stub

	}

}
