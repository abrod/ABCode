package de.brod.cardmaniac;

import android.graphics.Color;

public enum CardColor {

	kreuz(9827, Color.BLACK), pik(9824, Color.BLACK), herz(9829, Color.RED), karo(
			9830, Color.RED);

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
