package de.brod.cardmaniac.game;

import java.util.List;

import de.brod.cardmaniac.Card;
import de.brod.cardmaniac.Hand;
import de.brod.gui.GuiButton;

public interface IGame<CARD extends Card> {

	void init(float wd, float hg, int width, int height);

	List<? extends Hand<CARD>> getHands();

	List<? extends GuiButton> getButtons();

	List<? extends Card> actionDown(CARD card);

	void actionUp(List<? extends CARD> lstActionCards, Hand<CARD> handTo);
}
