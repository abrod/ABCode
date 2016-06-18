package de.brod.carddealer;

import java.util.List;

import de.brod.carddealer.game.FreeCell;
import de.brod.carddealer.game.Game;
import de.brod.opengl.Button;
import de.brod.opengl.GLActivity;
import de.brod.opengl.Shape;
import de.brod.opengl.Shapes;

public class MainActivity extends GLActivity {

	private Game game;

	@Override
	protected void actionDown(Shapes shapes) {
		// remove all except first
		while (shapes.size() > 1) {
			shapes.remove(0);
		}
		List<Card> cards = game.getCards(shapes);

		if (cards.size() == 1) {
			shapes.clear();
			for (Card card : game.actionDown(cards.get(0))) {
				shapes.add(card.getRectangle());
			}
			for (Shape shape : shapes) {
				shape.setZ(0.5f);
			}
		} else {
			shapes.clear();
		}
	}

	@Override
	protected void actionUp(Shapes selected, Shapes up) {
		List<CardRow> cardRows = game.getCardRows(up);
		List<Card> cards = game.getCards(selected);
		if (cards.isEmpty()) {
			// make nothing
		} else if (cardRows.size() > 0) {
			game.actionUp(cards, cardRows.get(cardRows.size() - 1));
		}
		// get all touched cardrows
		for (Card card : cards) {
			CardRow cardRow = card.getCardRow();
			if (!cardRows.contains(cardRow)) {
				cardRows.add(cardRow);
			}
		}
		// and reorganize
		for (CardRow cardRow : cardRows) {
			cardRow.organize();
		}

		for (Shape shape : selected) {
			shape.setZ(0f);
		}
	}

	@Override
	protected void init(Shapes meshes, float wd, float hg) {

		game = new FreeCell(getResources(), wd, hg);

		game.newGame();

		for (Button button : game.getButtons()) {
			meshes.add(button);
		}
		for (CardRow cardRow : game.getCardRows()) {
			meshes.add(cardRow.getRectangle());
		}

		for (Card card : game.getCards()) {
			meshes.add(card.getRectangle());
		}
	}

}
