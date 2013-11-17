/*
 * ******************************************************************************
 * Copyright (c) 2013 Andreas Brod
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * *****************************************************************************
 */
package de.brod.cm;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import de.brod.cm.Card.Values;
import de.brod.gui.GuiColors;

public class CardImage {

	private static void cloneRectangle(Bitmap bitmap, Canvas c, int wd, int hg,
			int px, int py, float dx, float dy, Rect src, Rect dst, Paint paint) {
		int w = (int) (dx * wd);
		int h = (int) (dy * hg);
		src.set(0, 0, w, h);
		dst.set((int) (px * dx), (int) (py * dy), (int) (px * dx + w),
				(int) (py * dy + h));
		c.drawBitmap(bitmap, src, dst, paint);
	}

	public static Bitmap createBitmap(int piMin, int piOffset,
			int amountOfCardsPerWidth) {

		int min = piMin * 6 / amountOfCardsPerWidth;
		int wd = 512;
		while (wd < min) {
			wd = wd * 2;
		}
		float dx = wd / 6f;
		float dy = dx * 3 / 2;

		System.out.println("Create CardBitmap " + wd + "x" + wd + " bitmap");
		Bitmap bitmap = Bitmap.createBitmap(wd, wd, Config.ARGB_8888);
		// create canvas to draw to
		Canvas c = new Canvas(bitmap);
		Paint paint = new Paint();
		c.drawColor(Color.argb(0, 0, 0, 0));
		int iOffset = piOffset;
		RectF rect = new RectF();

		float w = dx / 4;
		paint.setTextSize(w);
		Rect bounds = new Rect();
		paint.getTextBounds("ABC0123Q", 0, 8, bounds);
		paint.setTextSize(w * w / bounds.height());

		float wid = dx - 2;
		float heig = dy - 2;

		Rect src = new Rect();
		Rect dst = new Rect();

		drawEmptyCard(c, 1, 1, wid, heig, paint, rect, Color.WHITE);
		cloneRectangle(bitmap, c, 1, 1, 0, 1, dx, dy, src, dst, paint);
		cloneRectangle(bitmap, c, 1, 2, 0, 2, dx, dy, src, dst, paint);
		cloneRectangle(bitmap, c, 1, 4, 1, 0, dx, dy, src, dst, paint);
		cloneRectangle(bitmap, c, 2, 4, 2, 0, dx, dy, src, dst, paint);
		cloneRectangle(bitmap, c, 2, 4, 4, 0, dx, dy, src, dst, paint);

		for (int j = 0; j < 4; j++) {
			for (int i = 0; i < 6; i++) {
				float x = i * dx + 1;
				float y = j * dy + 1;
				drawCard(c, x, y, wid, heig, paint, iOffset / 13, iOffset % 13,
						rect);
				iOffset++;
			}
		}
		return bitmap;
	}

	private static void drawCard(Canvas c, float left, float top, float wd,
			float hg, Paint paint, int piColor, int piValue, RectF rect) {
		float border = 1 + wd / 8;

		float l = left + border;
		float t = top + border;
		float r = left + wd - border;
		float bt = top + hg - border;
		rect.set(l, t, r, bt);
		Rect bounds = new Rect();

		if (piColor < 4) {

			String sValue = Card.Values.getString(piValue);

			if (piColor < 2) {
				paint.setColor(Color.BLACK);
			} else {
				paint.setColor(Color.RED);
			}

			drawText(c, rect, bounds, sValue, paint, true, true);
			drawText(c, rect, bounds, sValue, paint, false, false);

			String sColor = Card.Colors.getString(piColor);
			drawText(c, rect, bounds, sColor, paint, true, false);
			drawText(c, rect, bounds, sColor, paint, false, true);
		} else if (piColor == 4) {
			if (piValue == 0) {
				int backColor = GuiColors.BACKGROUND.getColor();
				drawEmptyCard(c, left, top, wd, hg, paint, rect, backColor);
			} else if (piValue == 1) {
				int backColor = GuiColors.BACKGROUND.contrast(255, 0.5f)
						.getColor();
				drawEmptyCard(c, left, top, wd, hg, paint, rect, backColor);
				rect.set(l, t, r, bt);
				String sValue = Values.Ace.toString();
				paint.setColor(Color.BLACK);
				drawText(c, rect, bounds, sValue, paint, true, true);
				drawText(c, rect, bounds, sValue, paint, false, false);
			} else if (piValue == 2) {
				int backColor0 = GuiColors.BACKGROUND.getColor();
				int backColor = GuiColors.BACKGROUND.contrast(255, 30)
						.getColor();
				paint.setColor(backColor);
				c.drawRect(rect, paint);

				rect.set(l + border / 2, t + border / 2, r - border / 2, bt
						- border / 2);
				paint.setColor(backColor0);
				drawText(c, rect, bounds, Card.Colors.Clubs.toString(), paint,
						true, true);
				drawText(c, rect, bounds, Card.Colors.Spades.toString(), paint,
						true, false);
				drawText(c, rect, bounds, Card.Colors.Diamonds.toString(),
						paint, false, true);
				drawText(c, rect, bounds, Card.Colors.Hearts.toString(), paint,
						false, false);

			} else if (piValue == 3) {
				int backColor = GuiColors.BACKGROUND.contrast(255, 0.5f)
						.getColor();
				drawEmptyCard(c, left, top, wd, hg, paint, rect, backColor);
				rect.set(l, t, r, bt);
				String sValue = Values.King.toString();
				paint.setColor(Color.BLACK);
				drawText(c, rect, bounds, sValue, paint, true, true);
				drawText(c, rect, bounds, sValue, paint, false, false);
			}

		}
	}

	private static void drawEmptyCard(Canvas c, float left, float top,
			float wd, float hg, Paint paint, RectF rect, int colorBack) {
		paint.setStyle(Style.FILL);
		float b0 = 1;
		float fRound = wd / 8;
		float b = wd / 40 + 1;

		paint.setColor(Color.argb(128, 0, 0, 0));
		rect.set(left + b * 2, top + b * 2, left + wd - b0, top + hg - b0);
		c.drawRoundRect(rect, fRound, fRound, paint);

		paint.setColor(Color.BLACK);
		rect.set(left + b, top + b, left + wd - b, top + hg - b);
		c.drawRoundRect(rect, fRound, fRound, paint);

		b = wd / 30 + 2;
		paint.setColor(colorBack);
		rect.set(left + b, top + b, left + wd - b, top + hg - b);
		c.drawRoundRect(rect, fRound, fRound, paint);
	}

	private static void drawText(Canvas c, RectF rect, Rect bounds, String s,
			Paint paint, boolean bTop, boolean bLeft) {
		paint.getTextBounds(s, 0, s.length(), bounds);
		float x;
		float y;
		if (bLeft) {
			x = rect.left - bounds.left;
		} else {
			x = rect.right - bounds.left - bounds.width();
		}
		if (bTop) {
			y = rect.top + bounds.height();
		} else {
			y = rect.bottom - bounds.bottom;
		}
		c.drawText(s, x, y, paint);
	}

}
