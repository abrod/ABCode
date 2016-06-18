package de.brod.carddealer.game;

import java.util.Collection;
import java.util.List;

import de.brod.carddealer.Card;
import de.brod.carddealer.CardRow;
import de.brod.opengl.Button;
import de.brod.opengl.Shapes;

public interface Game {

	List<Card> actionDown(Card card);

	void actionUp(List<Card> cards, CardRow cardRow);

	Collection<Button> getButtons();

	Collection<CardRow> getCardRows();

	List<CardRow> getCardRows(Shapes up);

	Collection<Card> getCards();

	List<Card> getCards(Shapes shapes);

	void newGame();

}
