package de.brod.carddealer;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import de.brod.opengl.GLGrid;
import de.brod.opengl.Rectangle;

public class CardSet {
	private float	wd;
	private float	hg;
	private GLGrid	grid;
	private float	left;
	private float	top;

	public CardSet(final Resources resources, int countX) {

		wd = 2f / countX;
		hg = wd * 4 / 3;
		left = -1 + wd / 2;
		top = 1 - hg / 2;

		grid = new GLGrid(14, 4) {

			@Override
			protected Bitmap loadBitmap() {

				return BitmapFactory.decodeResource(resources, R.drawable.cards);
			}
		};
	}

	public Card createCard(int i, int j, float px, float py) {
		float x = left + wd * px;
		float y = top - hg * py;
		Rectangle rect = grid.createRectangle(wd, hg, x, y, 0, i, j, 13, j);
		if (Math.random() < 0.3f) {
			rect.setRotateX(180);
		}
		rect.setRotateZ(0);
		return new Card(rect);
	}
}
