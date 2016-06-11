package de.brod.carddealer;

import java.util.ArrayList;
import java.util.List;

import de.brod.opengl.ButtonAction;
import de.brod.opengl.Shape;

public class CardRow implements ButtonAction {
	private Shape	rect;
	float			x1;
	float			y1;
	float			dx;
	float			dy;
	int				minCount;
	List<Card>		cards	= new ArrayList<Card>();

	public void addCard(Card card) {
		card.setCardRow(this);
		cards.add(card);
		organize();
	}

	@Override
	public void doAction() {
		// TODO Auto-generated method stub

	}

	public Shape getRectangle() {
		return rect;
	}

	protected void init(Shape rect, float x1, float x2, float y1, float y2, int count) {
		this.rect = rect;
		this.x1 = x1;
		this.dx = x2 - x1;
		this.y1 = y1;
		this.dy = y2 - y1;
		this.minCount = count;
		rect.setColor(0.5f, 0.5f, 1f, 0.5f);

	}

	void organize() {
		int size = cards.size();
		float count = Math.max(minCount, size - 1);
		for (int i = 0; i < size; i++) {
			float f = i / count;
			Card card = cards.get(i);
			card.moveTo(x1 + dx * f, y1 + dy * f);
		}
	}

	public void removeCard(Card c) {
		cards.remove(c);
		organize();
	}
}
