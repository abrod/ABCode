package de.brod.gui;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class GuiTwoItemGrid extends GuiGrid {

	private float width;
	protected float height;

	public GuiTwoItemGrid(float width, float height) {
		super(2, 1);
		this.width = width;
		this.height = height;
	}

	@Override
	protected Bitmap createBitmap(int pwidth, int pheight) {
		float maxSize = Math.max(pwidth, pheight);
		int maxX = (int) (maxSize * width / 2);
		int maxY = (int) (maxSize * height / 2);
		Bitmap bitmap = Bitmap.createBitmap(maxX * 2, maxY, Bitmap.Config.ARGB_8888);
		Canvas c = new Canvas(bitmap);
		GuiQuad guiQuad = getGuiQuad();
		guiQuad.draw(c, 0, maxX, maxY, true);
		guiQuad.draw(c, maxX, maxX, maxY, true);
		return bitmap;
	}

}
