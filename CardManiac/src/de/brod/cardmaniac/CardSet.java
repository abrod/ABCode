package de.brod.cardmaniac;

public class CardSet {

	protected static float	CARD_WIDTH	= 1 / 4f;
	protected static float	CARD_HEIGHT	= 3 / 8f;

	public static float getX(float i) {
		float wdLeft = CARD_WIDTH / 2 - 1;
		float wdScreen = 2 - CARD_WIDTH;
		return wdLeft + wdScreen * i / 7;
	}

	public static float getY(float i) {
		float hgTop = CARD_HEIGHT / 2 - 1;
		float hgScreen = 2 - CARD_HEIGHT;
		return hgTop + hgScreen * i / 4;
	}

	public static float getWidth(float f) {
		return f * CARD_WIDTH;
	}

	public static float getHeight(float f) {
		return f * CARD_HEIGHT;
	}
}
