package de.brod.cardmaniac.game;

import java.util.ArrayList;
import java.util.List;

import de.brod.cardmaniac.Card;
import de.brod.cardmaniac.Hand;
import de.brod.gui.GuiButton;
import de.brod.gui.IAction;

public abstract class Game<CARD extends Card> implements IGame<CARD> {

	private List<GuiButton>	lstButtons	= new ArrayList<GuiButton>();

	@Override
	public List<? extends GuiButton> getButtons() {
		return lstButtons;
	}

	public void createGuiButton(float x, float y, float wdButton,
			float hgButton, final String string) {
		GuiButton guiButton = new GuiButton(x, y, wdButton, hgButton,
				new IAction() {

					@Override
					public String getTitle() {
						return string;
					}

					@Override
					public void doAction() {
						// make nothing
					}

				});
		lstButtons.add(guiButton);
	}

	protected ArrayList<Hand<CARD>>	hands	= new ArrayList<Hand<CARD>>();

	@Override
	public List<? extends Hand<CARD>> getHands() {
		return hands;
	}

	@Override
	public List<? extends Card> actionDown(CARD card) {
		@SuppressWarnings("unchecked")
		Hand<CARD> hand = (Hand<CARD>) card.getHand();
		List<CARD> lst = new ArrayList<CARD>();
		hand.actionDown(card, lst);
		return lst;
	}

	@Override
	public void actionUp(List<? extends CARD> lstActionCards, Hand<CARD> handTo) {
		handTo.actionUp(lstActionCards);
	}

}
