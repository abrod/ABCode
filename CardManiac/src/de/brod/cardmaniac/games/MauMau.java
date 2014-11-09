package de.brod.cardmaniac.games;

import java.util.ArrayList;
import java.util.List;

import de.brod.cardmaniac.cards.Button;
import de.brod.cardmaniac.cards.Card;
import de.brod.cardmaniac.cards.Hand;
import de.brod.cardmaniac.cards.PlayingCard;

public class MauMau extends Game {

	private Hand[]			players;
	private Hand			stack;
	private Hand			tolon;
	private List<Button>	buttons;
	private Button[]		buttonIcon;
	private float			maxY;

	@Override
	void fillCards(List<Card> plstCards) {
		fill32Cards(plstCards);
	}

	@Override
	public List<Hand> initHands() {
		maxY = getMaxY();
		float left = 1 + 0.1f;
		float right = 7 - left;
		float bottom = 1 + 0.1f;
		float top = maxY - bottom;
		players = new Hand[] { addHand(left, 0, right, 0, 10, true, 99),
				addHand(0, top, 0, bottom, 10, true, 0),
				addHand(left, maxY, right, maxY, 10, true, 0),
				addHand(7, top, 7, bottom, 10, true, 0) };
		float d = 0.15f;
		stack = addHand(2 - d, maxY / 2, 3 - d, maxY / 2, 10, true, 0);
		tolon = addHand(4 + d, maxY / 2, 5 + d, maxY / 2, 10, true, 99);
		return getHands();
	}

	@Override
	public void newGame(List<Card> cards) {
		int p = 0;
		for (int i = 0; i < cards.size(); i++) {
			if (players[p].getCardCount() < 6) {
				cards.get(i).moveTo(players[p]);
				p = (p + 1) % players.length;
			} else {
				cards.get(i).moveTo(stack);
			}
		}
		stack.getLastCard().moveTo(tolon);
	}

	@Override
	public void mouseClick(Card card, List<Card> plstSelected) {
		plstSelected.add(card);
	}

	@Override
	public boolean playCard(List<Card> plstSelected, Card cardTo, Hand handTo) {
		for (Card card : plstSelected) {
			card.moveTo(handTo);
		}
		return true;
	}

	@Override
	public List<Button> initButtons() {
		buttons = new ArrayList<Button>();
		float y = 1.4f;

		buttons.add(new Button(cardSet, 1.5f, y, 2.9f, y, "Draw") {

			@Override
			public void pressed() {
				// TODO Auto-generated method stub

			}
		});

		buttons.add(new Button(cardSet, 4.1f, y, 5.5f, y, "Skip") {

			@Override
			public void pressed() {
				// TODO Auto-generated method stub

			}
		});

		y = maxY - y;

		final PlayingCard.CardColor[] colors = PlayingCard.CardColor.values();
		buttonIcon = new Button[colors.length];
		for (int i = 0; i < colors.length; i++) {
			float x = 1.5f + i * 1.33f;
			final de.brod.cardmaniac.cards.PlayingCard.CardColor cardColor = colors[i];
			buttonIcon[i] = new Button(cardSet, x, y, x, y, cardColor.getChar()) {

				@Override
				public void pressed() {
					// TODO Auto-generated method stub

				}
			};
			buttonIcon[i].setTextColor(cardColor.getColor());
			buttonIcon[i].setEnabled(false);
			buttons.add(buttonIcon[i]);

		}
		return buttons;
	}

	@Override
	public ITurn getNextTurn() {
		// TODO Auto-generated method stub
		return null;
	}
}
