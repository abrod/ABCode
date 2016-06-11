package de.brod.carddealer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import de.brod.opengl.Button;
import de.brod.opengl.ButtonAction;
import de.brod.opengl.GLGrid;
import de.brod.opengl.Grid3d;
import de.brod.opengl.Rectangle;
import de.brod.opengl.Shape;
import de.brod.opengl.Shapes;

public class CardSet {
	private float				wd;
	private float				hg;
	private GLGrid				grid;
	private float				left;
	private float				top;
	private Grid3d				buttonGrid;
	private Map<Shape, Card>	cards		= new HashMap<Shape, Card>();
	private Map<Shape, CardRow>	cardRows	= new HashMap<Shape, CardRow>();

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

	public void clearAll() {
		cards.clear();
		cardRows.clear();
	}

	public Button createButton(float width, float height, float x, float y, float z, ButtonAction action) {
		return buttonGrid.createButton(width, height, x, y, z, action);
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
		Card card = new Card(rect);
		cards.put(rect, card);
		return card;
	}

	public CardRow createCardRow(float px, float py, float width, float height, int count) {

		float x1 = getX(px);
		float y1 = getY(py);
		float x2 = getX(px + width);
		float y2 = getY(py + height);
		float widthButton = wd + Math.abs(x1 - x2);
		float heighButton = hg + Math.abs(y1 - y2);
		float x = (x1 + x2) / 2;
		float y = (y1 + y2) / 2;
		float z = -0.1f;
		CardRow cardRow = new CardRow();
		Button button = createButton(widthButton, heighButton, x, y, z, cardRow);
		button.setRotateZ(0);
		cardRow.init(button, x1, x2, y1, y2, count);
		cardRows.put(button, cardRow);
		return cardRow;
	}

	public List<CardRow> getCardRows(Shapes shapes) {
		List<CardRow> lst = new ArrayList<CardRow>();
		for (Shape shape : shapes) {
			CardRow cardRow = cardRows.get(shape);
			if (cardRow != null) {
				lst.add(cardRow);
			}
		}
		return lst;
	}

	public List<Card> getCards(Shapes shapes) {
		List<Card> lst = new ArrayList<Card>();
		for (Shape shape : shapes) {
			Card card = cards.get(shape);
			if (card != null) {
				lst.add(card);
			}
		}
		return lst;
	}

	public float getX(float px) {
		return left + wd * px;
	}

	public float getY(float py) {
		return top - hg * py;
	}
}
