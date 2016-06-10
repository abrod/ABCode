package de.brod.carddealer;

import de.brod.opengl.Shape;

public class CardRow {
	private Shape	rect;
	float			x1;
	float			x2;
	float			y1;
	float			y2;
	int				count;

	public CardRow(Shape rect, float x1, float x2, float y1, float y2, int count) {
		this.rect = rect;
		this.x1 = x1;
		this.x2 = x2;
		this.y1 = y1;
		this.y2 = y2;
		this.count = count;
	}

	public Shape getRectangle() {
		return rect;
	}
}
