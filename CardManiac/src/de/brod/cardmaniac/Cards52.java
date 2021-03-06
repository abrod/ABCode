package de.brod.cardmaniac;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import de.brod.gui.GuiGrid;

public class Cards52 extends CardSet {

	public enum CardValue {

		c2("2"), c3("3"), c4("4"), c5("5"), c6("6"), c7("7"), c8("8"), c9("9"), c10("10"), cJ("J"), cQ("Q"), cK(
				"K"), cA("A");
		private String sText;

		CardValue(String psText) {
			sText = psText;
		}

		public String getText() {
			return sText;
		}

		public static int length() {
			return 13;
		}

		@Override
		public String toString() {
			return sText;
		}
	}

	public enum CardColor {
		clubs(9827, false, Color.BLACK), spades(9824, false, Color.BLACK), hearts(9829, true, Color.RED), diamonds(9830,
				true, Color.RED), special(32, false, Color.BLUE);

		private String sText;
		private boolean red;
		private int realColor;

		CardColor(int piValue, boolean red, int realColor) {
			sText = String.valueOf((char) piValue);
			this.red = red;
			this.realColor = realColor;
		}

		public String getText() {
			return sText;
		}

		public static int length() {
			return 5;
		}

		@Override
		public String toString() {
			return sText;
		}

		public boolean isRed() {
			return red;
		}

		public int getRealColor() {
			// TODO Auto-generated method stub
			return realColor;
		}
	}

	private final Hashtable<CardColor, Card52Grid> cardsGridMap = new Hashtable<Cards52.CardColor, Cards52.Card52Grid>();

	public static int background = 14;
	private static int countX = 5;
	private static int countY = 3;

	private class Card52Grid extends GuiGrid {

		public Card52Grid(CardColor pColor) {
			super(countX, countY);
			color = pColor;
		}

		private CardColor color;

		@Override
		protected Bitmap createBitmap(int piScreenWidth, int piScreenHeight) {
			int avgSize = (piScreenWidth + piScreenHeight) / 2;

			int maxX = avgSize;
			int maxY = avgSize * countY * 3 / countX / 2;

			Bitmap bitmap = Bitmap.createBitmap(maxX, maxY, Bitmap.Config.ARGB_8888);
			Canvas canvas = new Canvas(bitmap);
			Paint paint = new Paint();

			paint.setColor(Color.WHITE);
			paint.setStyle(Paint.Style.FILL_AND_STROKE);
			paint.setAntiAlias(true);
			float dx = maxX * 1f / countX;
			float dy = maxY * 1f / countY;
			int textSize = (int) (dy / 6);

			for (int i = 0; i < countX; i++) {
				for (int j = 0; j < countY; j++) {
					RectF rectF = new RectF(i * dx + 1, j * dy + 1, (i + 1) * dx - 1, (j + 1) * dy - 1);
					drawCard(i + j * countX, canvas, rectF, paint, dx, textSize);
				}
			}
			return bitmap;
		}

		private void drawCard(int iCardNumber, Canvas c, RectF pr, Paint p, float dx, int pTextSize) {
			CardValue[] values = CardValue.values();
			p.setColor(Color.argb(64, 0, 0, 0));
			float dx10 = dx / 10;
			float dx20 = dx / 20;
			c.drawRoundRect(pr, dx10, dx10, p);
			float dx40 = dx20 / 2;
			float dx80 = dx40 / 2;
			RectF r = new RectF(pr.left + dx80, pr.top + dx40, pr.right - dx40, pr.bottom - dx80);
			p.setColor(Color.WHITE);
			c.drawRoundRect(r, dx10, dx10, p);

			if (iCardNumber > values.length) {
				if (iCardNumber == background) {
					r = new RectF(r.left + dx40, r.top + dx40, r.right - dx40, r.bottom - dx40);
					p.setColor(Color.BLUE);
					c.drawRoundRect(r, dx10, dx10, p);
				}
				return;
			}

			p.setColor(color.getRealColor());

			p.setTextSize(pTextSize * 1.3f);

			Rect bounds = new Rect();
			CardValue cardVal = values[iCardNumber % values.length];
			String sValue = cardVal.getText();
			float border = Math.max(1, dx20);

			p.getTextBounds(sValue, 0, sValue.length(), bounds);

			c.drawText(sValue, r.right - bounds.width() - border * 2, r.bottom - border, p);
			c.drawText(sValue, r.left + border, r.top + p.getTextSize(), p);

			String sCol = color.getText();
			p.getTextBounds(sCol, 0, sCol.length(), bounds);
			c.drawText(sCol, r.right - bounds.width() - border * 2, r.top + p.getTextSize(), p);
			c.drawText(sCol, r.left + border, r.bottom - border, p);

			p.setTextSize(pTextSize * 2);
			p.getTextBounds(sCol, 0, sCol.length(), bounds);
			c.drawText(sCol, r.left + (r.width() - border) / 2 - bounds.centerX(), r.centerY() - bounds.centerY(), p);

		}

		public Card52 createCard(CardValue cardValue, float x, float y, boolean redBack) {
			Card52 card = new Card52(this, x, y, CARD_WIDTH, CARD_HEIGHT, color, cardValue, redBack);
			int ordinal = cardValue.ordinal();
			card.setGrid(ordinal % countX, ordinal / countX, true);
			card.setGrid(background % countX, background / countX, false);
			return card;
		}
	}

	private Card52 createCard(CardColor pCardColor, CardValue pCardValue, float x, float y, boolean redBack) {
		Card52Grid cardSetColor = cardsGridMap.get(pCardColor);
		if (cardSetColor == null) {
			cardSetColor = new Card52Grid(pCardColor);
			cardsGridMap.put(pCardColor, cardSetColor);
		}
		return cardSetColor.createCard(pCardValue, x, y, redBack);
	}

	public List<Card52> create52Cards(boolean redBack) {
		List<Card52> lstQuads = new ArrayList<Card52>();
		for (int j = 0; j < 4; j++) {
			for (int i = 0; i < 13; i++) {
				CardColor cardColor = CardColor.values()[j];
				CardValue cardValue = CardValue.values()[i];
				Card52 createdCard = createCard(cardColor, cardValue, 0, 0, redBack);
				lstQuads.add(createdCard);
			}
		}
		Collections.shuffle(lstQuads);
		return lstQuads;
	}

}
