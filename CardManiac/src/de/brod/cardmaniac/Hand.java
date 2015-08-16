package de.brod.cardmaniac;

import java.util.ArrayList;
import java.util.List;

import de.brod.gui.GuiRectangle;

public class Hand<CARD extends Card> extends GuiRectangle {

	private final List<CARD>	lstCards	= new ArrayList<CARD>();
	private final float			x, y, dx, dy;
	private int					max;

	public Hand(float x1, float y1, float x2, float y2, String psText) {
		super(CardSet.getX((x1 + x2) / 2f), CardSet.getY((y1 + y2) / 2),
				CardSet.getWidth(Math.abs(x1 - x2) + 1f), CardSet
						.getHeight(Math.abs(y1 - y2) + 1f), psText);
		setColor(64, 200, 200, 200);
		setDown(true);
		x = CardSet.getX(x1);
		y = CardSet.getY(y1);
		dx = CardSet.getX(x2) - x;
		dy = CardSet.getY(y2) - y;
		max = Math.round(Math.max(Math.abs(x2 - x1), Math.abs(y2 - y1)) * 1.5f) + 1;
	}

	public void addCard(CARD card) {
		Hand<? extends Card> hand = card.getHand();
		if (hand == null) {
			lstCards.add(card);
		} else if (!hand.equals(this)) {
			hand.lstCards.remove(card);
			lstCards.add(card);
		}
		card.setHand(this);
	}

	public void moveCards() {
		int size = Math.max(max, lstCards.size() - 1);
		for (int i = 0; i < lstCards.size(); i++) {
			CARD card = lstCards.get(i);
			if (i == 0) {
				card.slideTo(x, y);
			} else {
				card.slideTo(x + dx * i / size, y + dy * i / size);
			}
		}
	}
}
