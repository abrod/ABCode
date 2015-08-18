package de.brod.cardmaniac;

import de.brod.gui.GuiGrid;
import de.brod.gui.GuiQuad;

public abstract class Card extends GuiQuad {

	private Hand	hand;

	Card(GuiGrid grid, float px, float py, float wd, float hg) {
		super(grid, px, py, wd, hg);
		hand = null;
	}

	public Hand getHand() {
		return hand;
	}

	public void setHand(Hand hand) {
		this.hand = hand;
	}

}
