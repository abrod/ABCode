package de.brod.cardmaniac.game;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.brod.gui.GuiButton;
import de.brod.gui.IAction;

public abstract class Game implements IGame {

	private List<GuiButton>	lstButtons	= new ArrayList<GuiButton>();

	@Override
	public Collection<? extends GuiButton> getButtons() {
		return lstButtons;
	}

	public void createGuiButton(float x, float y, float wdButton,
			float hgButton, String string) {
		GuiButton guiButton = new GuiButton(x, y, wdButton, hgButton,
				new IAction() {

					@Override
					public String getTitle() {
						return "Show";
					}

					@Override
					public void doAction() {
						// make nothing
					}

				});
		lstButtons.add(guiButton);
	}
}
