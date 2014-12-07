package de.brod.opengl;

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

public class TextGrid {

	static class CharType {

		float	x;
		float	y;
		Rect	bounds;
		float	measureText;
		char	character;

		public CharType(char pcCharacter, float px, float py, Rect pBounds,
				float pfMeasureText) {
			character = pcCharacter;
			x = px;
			y = py;
			bounds = pBounds;
			measureText = pfMeasureText;
		}

		public float[] getTextureCoordinates() {
			// set the textures
			float x1 = (x + bounds.left) * wdPctBMP;
			float x2 = (x + bounds.right) * wdPctBMP;

			float y1 = (y + bounds.bottom) * wdPctBMP;
			float y2 = (y + bounds.top) * wdPctBMP;

			float[] cc = new float[] { x1, y1, //
					x1, y2, //
					x2, y1, //
					x2, y2, //
			};
			return cc;
		}

	}

	static int									wdBMP;
	static float								wdPctBMP;
	static int									txtHeightBMP;
	static int									txtBottomBMP;
	private static Hashtable<String, CharType>	htCharacters;
	private static int							textTexId	= -1;

	private static CharType drawText(Canvas canvas, char character,
			Paint paint, Point cursor) {
		int x = cursor.x;
		int y = cursor.y;
		String text = String.valueOf(character);
		Rect bounds = new Rect();
		paint.getTextBounds(text, 0, 1, bounds);
		float measureText = paint.measureText(text, 0, text.length());
		int offsetX;
		if (bounds.left < 0) {
			offsetX = -bounds.left;
		} else {
			offsetX = 0;
		}
		int size = 4 + bounds.right + offsetX;// (int)
		// paint.measureText(text);
		if (x + size >= wdBMP) {
			x = 0;
			y += txtHeightBMP;
		}
		float offsetY = txtHeightBMP - txtBottomBMP;
		canvas.drawText(text, x + offsetX + 2, y + offsetY, paint);
		cursor.set(x + size, y);
		CharType charType = new CharType(character, x + offsetX + 2, y
				+ offsetY, bounds, measureText);
		return charType;
	}

	public static int getTextureId() {
		return textTexId;
	}

	public static void init(GL10 gl, int pWidth, int pHeight, Context activity) {

		if (textTexId >= 0) {
			Grid.unloadBitmap(gl, textTexId);
		}
		int min = Math.min(pWidth, pHeight) * 2;
		wdBMP = 512;
		while (wdBMP < min) {
			wdBMP = wdBMP * 2;
		}
		wdPctBMP = 1f / wdBMP;
		Bitmap bitmap = Bitmap.createBitmap(wdBMP, wdBMP, Config.ARGB_8888);
		// create canvas to draw to
		Canvas canvas = new Canvas(bitmap);
		Paint paint = new Paint();
		canvas.drawColor(Color.argb(0, 0, 0, 0));
		// c.drawColor(Color.argb(255, 255, 128, 0));
		paint.setColor(Color.WHITE);
		// for (int i = 0; i < wdBMP; i += 10) {
		// canvas.drawLine(i, 0, 0, i, paint);
		// canvas.drawLine(i, wdBMP - 1, wdBMP - 1, i, paint);
		// }
		float textSize = wdBMP / 9;
		paint.setTextSize(textSize);

		Rect bounds = new Rect();
		Point cursor = new Point(0, 0);
		htCharacters = new Hashtable<String, CharType>();

		StringBuilder sb = new StringBuilder();
		for (char ch = ' '; ch <= 127; ch++) {
			sb.append(ch);
		}
		// Clubs=9827, Spades=9824, Hearts=9829, Diamonds=9830
		sb.append((char) 9827);
		sb.append((char) 9824);
		sb.append((char) 9829);
		sb.append((char) 9830);
		sb.append((char) 189);
		String sAllCharacters = sb.toString();
		// correct the textSize
		paint.getTextBounds(sAllCharacters, 0, sb.length(), bounds);
		paint.setTextSize(textSize * textSize / bounds.height());
		paint.getTextBounds(sAllCharacters, 0, sb.length(), bounds);
		txtBottomBMP = bounds.bottom;
		txtHeightBMP = bounds.height();

		for (char character : sAllCharacters.toCharArray()) {
			CharType charType = drawText(canvas, character, paint, cursor);
			htCharacters.put(String.valueOf(character), charType);
		}

		textTexId = Grid.loadBitmap(bitmap, gl);

		bitmap.recycle();
	}

	public static void initText(Text text) {
		if (htCharacters == null) {
			return;
		}
		String psText = text.getChar();
		CharType charType = htCharacters.get(psText);
		if (charType == null) {
			charType = htCharacters.get(" ");
		}

		text.init(charType);

		Text next = text.getNext();
		if (next != null) {
			initText(next);
		}
	}

}