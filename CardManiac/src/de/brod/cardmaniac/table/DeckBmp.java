package de.brod.cardmaniac.table;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import de.brod.cardmaniac.CardColor;

public class DeckBmp {

	private static int	c13;
	private static int	cHeight;
	private static int	cWidth;
	private static int	x, y, wd, hg;

	public static Bitmap createBitmap() {
		cWidth = 1024;
		cHeight = 512;
		c13 = 13;

		Bitmap bmp = Bitmap.createBitmap(cWidth, cHeight, Config.ARGB_8888);
		Canvas c = new Canvas(bmp);
		x = 0;
		y = 0;
		wd = cWidth / c13;
		hg = cHeight / 5;
		String[] values = { "A", "K", "D", "B", "10", "9", "8", "7", "6", "5",
				"4", "3", "2" };
		CardColor[] colors = CardColor.values();
		for (int i = 0; i < c13; i++) {
			for (int j = 0; j < 4; j++) {
				drawCard(c);
				drawValue(c, values[i], colors[j].getChar(), j);
				next();
			}
		}
		// tree jokers
		int[] jokerColors = { Color.BLACK, Color.RED, Color.BLUE };
		for (int iColor : jokerColors) {
			drawCard(c);
			drawJoker(c, iColor);
			next();
		}
		// backSides
		int[] backBolors = { Color.BLUE, Color.RED };
		for (int iColor : backBolors) {
			drawCard(c);
			drawBack(c, iColor);
			next();
		}

		// fill up with empty cards
		while (y < 5) {
			drawCard(c);
			next();
		}
		return bmp;
	}

	private static void drawBack(Canvas c, int piColor) {

		int px = x * cWidth / c13;
		int py = y * cHeight / 5;

		Paint paint = new Paint();
		paint.setColor(piColor);
		float strokeWidth = wd / 20;

		paint.setStrokeWidth(strokeWidth);
		paint.setStyle(Style.STROKE);
		float w1 = wd * 0.1f;
		float w2 = w1 + strokeWidth * 2;
		c.drawRect(px + w1, py + w1, px + wd - w1, py + hg - w1, paint);
		c.drawRect(px + w2, py + w2, px + wd - w2, py + hg - w2, paint);

		for (float i = px + w2 + strokeWidth / 2; i < px + wd - w2; i += strokeWidth * 2) {
			c.drawLine(i, py + w2, i, py + hg - w2, paint);
		}
		for (float i = py + w2 + strokeWidth / 2; i < py + hg - w2; i += strokeWidth * 2) {
			c.drawLine(px + w2, i, px + wd - w2, i, paint);
		}

	}

	private static void drawCard(Canvas c) {
		int px = x * cWidth / c13;
		int py = y * cHeight / 5;

		RectF rect = new RectF(px + 2, py + 2, px + wd - 2, py + hg - 2);
		Paint paint = new Paint();
		paint.setColor(Color.argb(128, 0, 0, 0));
		paint.setStyle(Style.STROKE);
		paint.setStrokeWidth(2);
		c.drawRoundRect(rect, wd / 10f, wd / 10f, paint);
		paint.setColor(Color.WHITE);
		paint.setStyle(Style.FILL);
		c.drawRoundRect(rect, wd / 10f, wd / 10f, paint);

	}

	private static void drawJoker(Canvas c, int piColor) {
		Paint paint = new Paint();
		paint.setColor(piColor);

		paint.setTextSize(hg / 8f);

		String s = ((char) 9734) + "Joker";
		Rect[] bounds = new Rect[s.length()];
		int max = 1;
		for (int i = 0; i < s.length(); i++) {
			bounds[i] = new Rect();
			paint.getTextBounds(s, i, i + 1, bounds[i]);
			max = Math.max(i, bounds[i].right);
		}
		int px = x * cWidth / c13;
		int py = y * cHeight / 5;

		int x1 = px + wd / 10;
		float y1 = py + wd / 10;
		for (int i = 0; i < s.length(); i++) {
			String si = s.substring(i, i + 1);
			c.drawText(si, x1 + (max - bounds[i].right) / 2,
					y1 + bounds[i].height(), paint);
			// if (i == 0) {
			// c.drawText(si, px + wd * 0.9f - bounds[i].right,
			// y1 + bounds[i].height(), paint);
			// }
			y1 += bounds[i].height() * 1.4f;
		}

		drawText(c, s.substring(0, 1), px, py, paint, false, false);

	}

	private static void drawText(Canvas c, String psText, int px, int py,
			Paint paint, boolean bTop, boolean bRight) {
		paint.setTextSize(hg / 3.5f);
		Rect bounds = new Rect();
		paint.getTextBounds(psText, 0, psText.length(), bounds);
		int x2 = px + (bRight ? wd / 20 : wd - wd / 20 - bounds.right);
		int y2 = py + (bTop ? wd / 10 + bounds.height() : hg - wd / 10);
		c.drawText(psText, x2, y2, paint);

		// c.drawText(psColors, x2 - bounds2.right, y1 + bounds1.height(),
		// paint);

	}

	private static void drawValue(Canvas c, String psText, String psColors,
			int piColor) {
		Paint paint = new Paint();
		if (piColor < 2) {
			paint.setColor(Color.BLACK);
		} else {
			paint.setColor(Color.RED);
		}
		// paint.setTextSize(hg / 5.5f);
		// Rect bounds1 = new Rect();
		// Rect bounds2 = new Rect();
		//
		// paint.getTextBounds(psText, 0, psText.length(), bounds1);
		// paint.getTextBounds(psColors, 0, psColors.length(), bounds2);
		//
		int px = x * cWidth / c13;
		int py = y * cHeight / 5;
		//
		// int x1 = px + wd / 20;
		// int y1 = py + wd / 10;
		// int x2 = px + wd - wd / 20;
		//
		// int dx = Math.max(0, bounds2.right - bounds1.right) / 2;
		// c.drawText(psText, x1 + dx, y1 + bounds1.height(), paint);
		//
		// int thg = (int) (bounds1.height() * 1.2);
		// // c.drawText(psColors, x1, y1 + bounds2.height() + thg, paint);
		// // c.drawText(psColors, x2 - bounds2.right, y2 - thg, paint);
		// c.drawText(psColors, x1, y1 + bounds2.height() + thg, paint);

		drawText(c, psColors, px, py, paint, false, true);
		drawText(c, psText, px, py, paint, true, true);

		drawText(c, psColors, px, py, paint, true, false);
		drawText(c, psText, px, py, paint, false, false);
	}

	private static void next() {
		x++;
		if (x >= c13) {
			x = 0;
			y++;
		}
	}

}
