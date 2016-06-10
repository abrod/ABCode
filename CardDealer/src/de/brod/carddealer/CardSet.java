package de.brod.carddealer;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import de.brod.opengl.Button;
import de.brod.opengl.ButtonAction;
import de.brod.opengl.GLGrid;
import de.brod.opengl.Grid3d;
import de.brod.opengl.Rectangle;

public class CardSet {
	private float	wd;
	private float	hg;
	private GLGrid	grid;
	private float	left;
	private float	top;
	private Grid3d	buttonGrid;

	public CardSet(final Resources resources, int countX) {

		wd = 2f / countX;
		hg = wd * 4 / 3;
		left = -1 + wd / 2;
		top = 1 - hg / 2;

		grid = new GLGrid(9, 7) {

			@Override
			protected Bitmap loadBitmap() {

				return BitmapFactory.decodeResource(resources, R.drawable.card2);
			}
		};
		buttonGrid = new Grid3d();
	}

	public Card createCard(int i, int j, float px, float py) {
		int a;
		if (i >= 13) {
			// joker = position 53,54
			a = 52 + j % 2;
		} else {
			a = (j % 4) * 12 + i;
		}
		// back = position 55,56,57,58
		int b = 54 + (j / 4);

		float x = getX(px);
		float y = getY(py);
		Rectangle rect = grid.createRectangle(wd, hg, x, y, 0, a, b);
		if (Math.random() < 0.3f) {
			rect.setRotateX(180);
		}
		rect.setRotateZ(0);
		return new Card(rect);
	}

	public CardRow createCardRow(float px, float py, float width, float height, int count) {

		float x1 = getX(px);
		float y1 = getY(py);
		float x2 = getX(px + width);
		float y2 = getY(py + height);
		Button button = buttonGrid.createButton(wd + Math.abs(x1 - x2), hg + Math.abs(y1 - y2), (x1 + x2) / 2,
				(y1 + y2) / 2, -0.1f, new ButtonAction() {

					@Override
					public void doAction() {
						// make nothing
					}
				});
		button.setRotateZ(0);
		button.setColor(0.5f, 0.5f, 1f, 0.5f);
		return new CardRow(button, x1, x2, y1, y2, count);
	}

	public float getX(float px) {
		return left + wd * px;
	}

	public float getY(float py) {
		return top - hg * py;
	}
}
