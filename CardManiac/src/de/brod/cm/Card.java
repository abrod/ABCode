package de.brod.cm;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;
import de.brod.gui.Texture;
import de.brod.gui.shape.Sprite;

public class Card extends Sprite {

	public enum Colors {
		Clubs(9827, 0), Spades(9824, 1), Hearts(9829, 2), Diamonds(9830, 3), Empty(
				0, 4);

		public static Colors getRandom() {
			return values()[(int) (4 * Math.random())];
		}

		public static String getString(int piValue) {
			return getValue(piValue)._s;
		}

		public static Colors getValue(int piValue) {
			for (Colors v : Colors.values()) {
				if (v._id == piValue) {
					return v;
				}
			}
			return null;
		}

		private String _s;
		private int _id;

		private Colors(int piColor, int ord) {
			_s = String.valueOf((char) piColor);
			_id = ord;
		}

		public int getId() {
			return _id;
		}

		@Override
		public String toString() {
			return _s;
		}

	}

	public enum Values {
		Ace("A", 0), C2("2", 1), C3("3", 2), C4("4", 3), C5("5", 4), C6("6", 5), C7(
				"7", 6), C8("8", 7), C9("9", 8), C10("10", 9), Jack("B", 10), Queen(
				"D", 11), King("K", 12);

		public static Values getRandom() {
			return values()[(int) (13 * Math.random())];
		}

		public static String getString(int piValue) {
			return getValue(piValue)._s;
		}

		public static Values getValue(int piValue) {
			for (Values v : Values.values()) {
				if (v._id == piValue) {
					return v;
				}
			}
			return null;
		}

		private String _s;
		private int _id;

		private Values(String s, int ord) {
			_s = s;
			_id = ord;
		}

		public int getId() {
			return _id;
		}

		@Override
		public String toString() {
			return _s;
		}
	}

	private static Texture cardTextures = null;
	private static float cardWidth, cardHeight;
	private static float offsetY;
	public static float maxCardY;
	private static float ym;

	public static float getCardHeight() {
		return cardHeight;
	}

	public static float getCardWidth() {
		return cardWidth;
	}

	public static float getX(float x) {
		return cardWidth * (x + 0.5f) - 1;
	}

	public static float getY(float y) {
		return ym - offsetY - cardHeight * (y + 0.5f);
	}

	public static void init(GL10 gl, int width, int height, float piOffsetY) {
		float ratio = width * 1f / height;
		int amountOfCardsPerWidth = 8;
		ym = 1f;
		if (ratio < 1) {// width<height
			ym = 1 / ratio;
		}

		offsetY = piOffsetY;
		cardWidth = (2f / amountOfCardsPerWidth);
		cardHeight = (cardWidth * 3 / 2);
		int barAmount = width < height ? 2 : 1;
		maxCardY = (ym * 2 - offsetY * barAmount - cardHeight) / cardHeight;

		Bitmap bitmap = CardImage.createBitmap(Math.min(width, height), 0,
				amountOfCardsPerWidth);
		cardTextures = new Texture(gl, bitmap, 6, 4);
		bitmap.recycle();

		bitmap = CardImage.createBitmap(Math.min(width, height), 24,
				amountOfCardsPerWidth);
		Texture cardTexture1 = new Texture(gl, bitmap, 6, 4);
		bitmap.recycle();

		bitmap = CardImage.createBitmap(Math.min(width, height), 48,
				amountOfCardsPerWidth);
		Texture cardTexture2 = new Texture(gl, bitmap, 6, 4);
		bitmap.recycle();
		// cleanup
		System.gc();
		cardTextures.add(cardTexture1);
		cardTextures.add(cardTexture2);
	}

	Values value;
	Colors color;
	private Hand hand;
	private int valueId;

	public Card(Hand pHand) {
		super(cardTextures, cardWidth, cardHeight);
		hand = pHand;
	}

	public Colors getColor() {
		return color;
	}

	public Hand getHand() {
		return hand;
	}

	public Values getValue() {
		return value;
	}

	public int getValueId() {
		return valueId;
	}

	public void moveTo(Hand pHand) {
		hand.remove(this);
		pHand.add(this);
		hand = pHand;
	}

	public void setValue(int parseInt) {
		setValue(Values.getValue(parseInt % 13), Colors.getValue(parseInt / 13));
	}

	public void setValue(Values pValue, Colors pColor) {
		this.value = pValue;
		this.color = pColor;
		valueId = value._id + color._id * 13;
		setCell(valueId % 6, valueId / 6);
	}

	@Override
	public String toString() {
		return valueId + " " + value.toString() + "" + color.toString();
	}
}
