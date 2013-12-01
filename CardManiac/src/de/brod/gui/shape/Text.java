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
package de.brod.gui.shape;

import java.util.Hashtable;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import de.brod.gui.GuiColors;
import de.brod.gui.Texture;

public class Text extends Sprite {

	private static class CharType {
		Rect bounds = new Rect();
		private int x, y, h, ox, size, boundLeft, boundRight;

		public Text create(float pfHeight) {
			float fact = pfHeight / h;
			Text text = new Text(size * fact, pfHeight);

			text.width = (size) * fact;
			text.boundLeft = boundLeft * fact;
			text.boundRight = boundRight * fact;

			text.setTextureBuffer(x, y + h, x + size, y);

			return text;
		}

		private void drawText(Canvas c, char ch, Paint paint, Point cursor,
				int pwd, int piMaxHeight, int pfBottom) {
			h = piMaxHeight - 1;
			x = cursor.x;
			y = cursor.y;
			String text = String.valueOf(ch);
			paint.getTextBounds(text, 0, 1, bounds);
			ox = Math.max(0, -bounds.left);
			boundLeft = bounds.left;
			boundRight = bounds.right;

			size = 4 + bounds.left + ox + bounds.width();// (int)
			// paint.measureText(text);
			println(ch + " " + x + " " + y + " " + size, bounds);
			if (x + size >= pwd) {
				x = 0;
				y += piMaxHeight;
			}
			c.drawText(text, x + ox + 2, y + piMaxHeight - pfBottom, paint);
			cursor.set(x + size, y);
		}

	}

	private static void println(String string, Rect bounds2) {
		// System.out.println(string + " [" + bounds2.left + " " +
		// bounds2.width()
		// + " " + bounds2.right + "/" + bounds2.top + " "
		// + bounds2.height() + " " + bounds2.bottom + "]");
	}

	public float width;
	private float boundLeft, boundRight;

	private Text next = null;
	private static Texture textTex;
	private static Hashtable<String, CharType> ht;
	private static int wd;

	private static Text createText(String psText, float fHeight) {
		if (psText.length() == 0) {
			psText = " ";
		}
		CharType charType = ht.get(psText.substring(0, 1));
		if (charType == null) {
			charType = ht.get(" ");
		}
		Text t = charType.create(fHeight);
		if (psText.length() > 1) {
			t.setNext(createText(psText.substring(1), fHeight));
		}
		return t;

	}

	public static Text createText(String string, float x, float y,
			float fTitleHeight) {
		Text createText = createText(string, fTitleHeight);
		createText.setPosition(x, y);
		return createText;
	}

	public static void init(GL10 gl, int pWidth, int pHeight,
			float pfTitleHeight, Context activity) {
		int min = Math.min(pWidth, pHeight) * 2;
		wd = 512;
		while (wd < min) {
			wd = wd * 2;
		}
		Bitmap bitmap = Bitmap.createBitmap(wd, wd, Config.ARGB_8888);
		// create canvas to draw to
		Canvas c = new Canvas(bitmap);
		Paint paint = new Paint();
		c.drawColor(GuiColors.EMPTY.getColor());
		paint.setColor(Color.WHITE);
		float w = wd / 9;
		paint.setTextSize(w);

		Rect bounds = new Rect();
		Point cursor = new Point(0, 0);
		ht = new Hashtable<String, CharType>();

		int maxHeight = 0;
		StringBuilder sb = new StringBuilder();
		for (char ch = ' '; ch <= 127; ch++) {
			sb.append(ch);
		}
		// Clubs=9827, Spades=9824, Hearts=9829, Diamonds=9830
		String sAddChar = "" + (char) 9827 + "" + (char) 9824 + ""
				+ (char) 9829 + "" + (char) 9830 + "" + (char) 189;
		sb.append(sAddChar);
		paint.getTextBounds(sb.toString(), 0, sb.length(), bounds);
		paint.setTextSize(w * w / bounds.height());
		paint.getTextBounds(sb.toString(), 0, sb.length(), bounds);
		println("Total", bounds);
		int iBottom = bounds.bottom;
		maxHeight = bounds.height() + iBottom;

		for (char ch : sb.toString().toCharArray()) {
			CharType charType = new CharType();
			charType.drawText(c, ch, paint, cursor, wd, maxHeight, iBottom);
			ht.put(String.valueOf(ch), charType);
		}
		textTex = new Texture("SYS_TEXT", gl, bitmap, 1, 1);

		// try
		// {
		// IOTools.writeBitmap(bitmap, activity, "text.jpg");
		// }
		// catch (Exception e)
		// {}
		bitmap.recycle();
	}

	public Text(float pWidth, float pHeight) {
		super(textTex, pWidth, pHeight);
		setCenter(false);
	}

	@Override
	public int compareTo(Container another) {
		if (!(another instanceof Text)) {
			return 1;
		}
		return super.compareTo(another);
	}

	public float getTextWdith() {
		if (next != null) {
			return next.getTextWidth() + width + boundLeft;
		}
		return width + boundLeft;
	}

	public float getTextWidth() {
		if (next != null) {
			return next.getTextWdith() + width;
		}
		return width + boundRight;
	}

	private void setNext(Text createText) {
		clear();
		add(createText);
		next = createText;
	}

	@Override
	public void setPosition(float px, float py) {
		super.setPosition(px, py);
		if (next != null) {
			next.setPosition(px + width, py);
		}
	}

}
