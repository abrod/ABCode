package de.brod.cardmaniac;

public class CardSet {

	protected static float	CARD_WIDTH	= 1 / 4f;
	protected static float	CARD_HEIGHT	= 3 / 8f;

	public float getX(float i) {
		float wdLeft = CARD_WIDTH / 2 - 1;
		float wdScreen = 2 - CARD_WIDTH;
		return wdLeft + wdScreen * i / 7;
	}

	public float getY(float i) {
		float hgTop = CARD_HEIGHT / 2 - 1;
		float hgScreen = 2 - CARD_HEIGHT;
		return hgTop + hgScreen * i / 4;
	}
}
