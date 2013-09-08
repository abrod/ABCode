package de.brod.gui.shape;

import java.util.Hashtable;

import javax.microedition.khronos.opengles.GL10;

import de.brod.gui.Texture;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;

public class Text extends Sprite {

	private static class CharType {
		Rect bounds = new Rect();
		private int x, y, h, ox, size;

		public Text create(float pfHeight) {
			float fact = pfHeight / h;
			Text text = new Text(size * fact, pfHeight);

			text.width = (size) * fact;

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
	private Text next = null;
	private static Texture textTex;
	private static Hashtable<String, CharType> ht;
	private static int wd;

	private static Text createText(String psText, float fHeight) {
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
		int min = Math.min(pWidth, pHeight);
		wd = 512;
		while (wd < min) {
			wd = wd * 2;
		}
		Bitmap bitmap = Bitmap.createBitmap(wd, wd, Config.ARGB_8888);
		// create canvas to draw to
		Canvas c = new Canvas(bitmap);
		Paint paint = new Paint();
		c.drawColor(Color.argb(0, 0, 0, 0));
		paint.setColor(Color.WHITE);
		float w = wd / 10;
		paint.setTextSize(w);

		Rect bounds = new Rect();
		Point cursor = new Point(0, 0);
		ht = new Hashtable<String, CharType>();

		int maxHeight = 0;
		StringBuilder sb = new StringBuilder();
		for (char ch = ' '; ch <= 127; ch++) {
			sb.append(ch);
		}
		paint.getTextBounds(sb.toString(), 0, sb.length(), bounds);
		paint.setTextSize(w * w / bounds.height());
		paint.getTextBounds(sb.toString(), 0, sb.length(), bounds);
		println("Total", bounds);
		int iBottom = bounds.bottom;
		maxHeight = bounds.height() + iBottom;

		for (char ch = ' '; ch <= 127; ch++) {
			CharType charType = new CharType();
			charType.drawText(c, ch, paint, cursor, wd, maxHeight, iBottom);
			ht.put(String.valueOf(ch), charType);
		}
		textTex = new Texture(gl, bitmap, 1, 1);

		// IOTools.writeBitmap(bitmap, activity, "text.jpg");
		bitmap.recycle();
	}

	public Text(float pWidth, float pHeight) {
		super(textTex, pWidth, pHeight);
		setCenter(false);
	}

	public float getTextWdith() {
		if (next != null) {
			return next.getTextWdith() + width;
		}
		return width;
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
