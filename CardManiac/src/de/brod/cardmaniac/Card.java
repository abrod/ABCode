package de.brod.cardmaniac;

import de.brod.gui.GuiGrid;
import de.brod.gui.GuiQuad;

public abstract class Card extends GuiQuad {

	private Hand<?>	hand;
	private boolean	selected	= false;

	Card(GuiGrid grid, float px, float py, float wd, float hg) {
		super(grid, px, py, wd, hg);
		hand = null;
	}

	public Hand<?> getHand() {
		return hand;
	}

	public void setHand(Hand<?> hand) {
		this.hand = hand;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		if (selected != this.selected) {
			if (!selected) {
				setColor(255, 255, 255, 255);
			} else {
				setColor(255, 192, 192, 192);
			}
			this.selected = selected;
		}
	}

}
