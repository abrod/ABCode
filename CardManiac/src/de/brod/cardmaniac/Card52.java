package de.brod.cardmaniac;

import de.brod.cardmaniac.Cards52.CardColor;
import de.brod.cardmaniac.Cards52.CardValue;
import de.brod.gui.GuiGrid;

public class Card52 extends Card {

	private CardColor	color;
	private CardValue	cardValue;

	Card52(GuiGrid grid, float px, float py, float wd, float hg,
			CardColor color, CardValue cardValue) {
		super(grid, px, py, wd, hg);
		this.color = color;
		this.cardValue = cardValue;
	}


}
