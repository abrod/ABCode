package de.brod.cardmaniac.game;

import java.util.Collection;
import java.util.List;

import de.brod.cardmaniac.Card;
import de.brod.cardmaniac.Hand;
import de.brod.gui.GuiButton;

public interface IGame {

	void init(float wd, float hg, int width, int height);

	Collection<? extends Hand> getHands();

	Collection<? extends GuiButton> getButtons();

	List<Card> actionDown(Card card);

	void actionUp(List<Card> lstActionCards, Hand handTo);
}
