package de.brod.gui;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;

public class GuiButton implements IGuiQuad {

	GuiGrid				grid	= null;
	private GuiQuad[]	lstQuads;
	private float		size;
	private float		width;
	private float		height;

	class ButtonGrid extends GuiGrid {

		public ButtonGrid() {
			super(6, 3);
		}

		@Override
		protected Bitmap createBitmap(int pwidth, int pheight) {
			int maxX = (int) (width * pwidth / 2);
			int maxY = (int) (height * pheight / 2);
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

		}
	}

	public GuiButton(float x, float y, float width, float height) {
		this.width = width;
		this.height = height;
		grid = new ButtonGrid();
		lstQuads = new GuiQuad[9];
		size = Math.min(width, height) / 3f;
		for (int i = -1; i <= 1; i++) {
			float wd = i == 0 ? width - size * 2 : width;
			float dx = x + i * (width - size) / 2;
			for (int j = -1; j <= 1; j++) {
				float hg = j == 0 ? height - size * 2 : height;
				float dy = y + j * (height - size) / 2;
				lstQuads[i + 1 + (1 - j) * 3] = new GuiQuad(grid, dx, dy, wd,
						hg).setGrid(i + 1, j + 1, true).setGrid(i + 4, j + 1,
						true);
			}
		}

	}

	@Override
	public void close() {
		for (GuiQuad guiQuad : lstQuads) {
			guiQuad.close();
		}
	}

	@Override
	public void draw(GL10 pGL10) {
		for (GuiQuad guiQuad : lstQuads) {
			guiQuad.draw(pGL10);
		}
	}

	@Override
	public float getXY() {
		return lstQuads[4].getXY();
	}

	@Override
	public boolean touches(float eventX, float eventY) {
		for (GuiQuad guiQuad : lstQuads) {
			if (guiQuad.touches(eventX, eventY)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void moveTo(float eventX, float eventY) {
		for (int i = -1; i <= 1; i++) {
			float dx = eventX + i * (width - size) / 2;
			for (int j = -1; j <= 1; j++) {
				float dy = eventY + j * (height - size) / 2;
				lstQuads[i + 1 + (j + 1) * 3].moveTo(dx, dy);
			}
		}
	}

}
