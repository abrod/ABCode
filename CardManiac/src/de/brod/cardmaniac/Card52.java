package de.brod.cardmaniac;

import de.brod.cardmaniac.Cards52.CardColor;
import de.brod.cardmaniac.Cards52.CardValue;
import de.brod.gui.GuiGrid;

public class Card52 extends Card {

	private final CardColor color;
	private final CardValue cardValue;
	private boolean redBack;

	Card52(GuiGrid grid, float px, float py, float wd, float hg, CardColor color, CardValue cardValue,
			boolean redBack) {
		super(grid, px, py, wd, hg);
		this.color = color;
		this.cardValue = cardValue;
		this.redBack = redBack;
	}

	public CardColor getColor() {
		return color;
	}

	public CardValue getCardValue() {
		return cardValue;
	}

	@Override
	public String toString() {
		return color.toString() + cardValue.toString();
	}

	public boolean isRed() {
		return color.isRed();
	}

	@Override
	public int getUniqueId() {
		int val = cardValue.ordinal();
		int col = color.ordinal();
		return val * CardColor.length() + col + (redBack ? 100 : 0);
	}
}
