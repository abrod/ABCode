package de.brod.cardmaniac;

import java.util.ArrayList;
import java.util.List;

import de.brod.gui.GuiRectangle;

public abstract class Hand<CARD extends Card> extends GuiRectangle {

	private final List<CARD> lstCards = new ArrayList<CARD>();
	private final float x, y, dx, dy;
	private int max;

	/**
	 * Create a hand object. The range for this object is
	 * <ul>
	 * <li>x = [0,7]
	 * <li>y = [0,4]
	 * </ul>
	 *
	 * @param x1
	 *            xmin
	 * @param y1
	 *            ymin
	 * @param x2
	 *            xmax
	 * @param y2
	 *            ymax
	 * @param psText
	 *            (alternative Text)
	 * @param pMax
	 */
	public Hand(float x1, float y1, float x2, float y2, String psText, int pMax) {
		super(CardSet.getX((x1 + x2) / 2f), CardSet.getY((y1 + y2) / 2), CardSet.getWidth(Math.abs(x1 - x2) + 1f),
				CardSet.getHeight(Math.abs(y1 - y2) + 1f), psText);
		setColor(64, 200, 200, 200);
		setDown(true);
		x = CardSet.getX(x1);
		y = CardSet.getY(y1);
		dx = CardSet.getX(x2) - x;
		dy = CardSet.getY(y2 + 0.00001f) - y;
		max = pMax;
	}

	public void addCards(List<? extends CARD> lsCards) {
		for (CARD card : lsCards) {
			addCard(card);
		}
	}

	public void addCard(CARD card) {
		Hand<?> hand = card.getHand();
		if (hand == null) {
			lstCards.add(card);
		} else if (!hand.equals(this)) {
			hand.lstCards.remove(card);
			hand.setIndexWithinCards();
			lstCards.add(card);
			setIndexWithinCards();
		}
		card.setHand(this);
	}

	private void setIndexWithinCards() {
		for (int i = 0; i < lstCards.size(); i++) {
			lstCards.get(i).setIndex(i);
		}
	}

	public void moveCards(boolean bSlide) {
		int size = Math.max(max, lstCards.size() - 1);
		for (int i = 0; i < lstCards.size(); i++) {
			Card card = lstCards.get(i);
			if (bSlide) {
				if (i == 0) {
					card.slideTo(x, y);
				} else {
					card.slideTo(x + dx * i / size, y + dy * i / size);
				}
			} else {
				if (i == 0) {
					card.moveTo(x, y);
				} else {
					card.moveTo(x + dx * i / size, y + dy * i / size);
				}
			}
		}
	}

	public List<CARD> getCards() {
		return lstCards;
	}

	public CARD getLastCard() {
		int location = lstCards.size() - 1;
		if (location >= 0) {
			return lstCards.get(location);
		}
		return null;
	}

	public abstract void actionDown(CARD card, List<CARD> lst);

	public abstract void actionUp(List<? extends CARD> lstCardsToAdd);

}
