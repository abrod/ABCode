package de.brod.cardmaniac.cards;

import android.graphics.Color;

public class PlayingCard extends Card {

	private CardValue	value;
	private CardColor	color;

	protected PlayingCard(CardSet cardSet, CardValue x, CardColor y) {
		super(cardSet, x.ordinal(), y.ordinal(), 3, 4);
		setValue(x);
		setColor(y);
	}

	public enum CardColor {
		spades(9827, Color.BLACK), clubs(9824, Color.BLACK), hearts(9829,
				Color.RED), diamonds(9830, Color.RED);

		private String	_s;
		private int		_col;

		CardColor(int piValue, int piColor) {
			_s = String.valueOf((char) piValue);
			_col = piColor;
		}

		public String getChar() {
			return _s;
		}

		public int getColor() {
			return _col;
		}

	}

	public enum CardValue {
		ass("A"), c2("2"), c3("3"), c4("4"), c5("5"), c6("6"), c7("7"), c8("8"), c9(
				"9"), c10("10"), jack("B"), queen("D"), king("K"), ;

		String	_s;

		CardValue(String s) {
			_s = s;
		}

		public String getText() {
			return _s;
		}
	}

	@Override
	public String toString() {
		return getValue()._s + getColor()._s + "("
				+ (Math.round(posX * 100) / 100f) + "x"
				+ (Math.round(posY * 100) / 100f) + ")";
	}

	public CardValue getValue() {
		return value;
	}

	public void setValue(CardValue value) {
		this.value = value;
	}

	public CardColor getColor() {
		return color;
	}

	public void setColor(CardColor color) {
		this.color = color;
	}
}
