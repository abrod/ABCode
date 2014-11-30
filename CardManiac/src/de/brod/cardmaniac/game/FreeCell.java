package de.brod.cardmaniac.game;

import java.util.List;

import de.brod.cardmaniac.card.Card;
import de.brod.cardmaniac.card.Hand;
import de.brod.cardmaniac.card.set.PlayingCards;

public class FreeCell extends Game<PlayingCards> {

	private PlayingCards			playingCards;
	private Hand<PlayingCards>[]	topLeft;
	private Hand<PlayingCards>[]	topRight;
	private Hand<PlayingCards>[]	bottom;

	public FreeCell() {
		playingCards = new PlayingCards(8);
	}

	@Override
	protected PlayingCards getCardType() {
		return playingCards;
	}

	@Override
	protected void createHands() {
		topLeft = new Hand[4];
		topRight = new Hand[4];
		bottom = new Hand[8];
		float f = 0.05f;
		for (int i = 0; i < 8; i++) {
			if (i < 4) {
				topLeft[i] = createHand(i - f, 7, i - f, 7, 0, 1, 99);
			} else {
				topRight[i - 4] = createHand(i + f, 7, i + f, 7, 0, 1, 99);
			}
			bottom[i] = createHand(i, 7f - 4f / 3 - f * 2, i, 0, 0, 8, i + 1);
		}
	}

	@Override
	public void newGame() {
		List<Card<PlayingCards>> lstCards = playingCards
				.get52Cards(getCardSet());
		for (int i = 0; i < lstCards.size(); i++) {
			Card<PlayingCards> card = lstCards.get(i);
			card.moveTo(bottom[i % 8]);
		}
	}

}
