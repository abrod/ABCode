package de.brod.cardmaniac.cards;

import android.graphics.Color;
import de.brod.opengl.ISprite;
import de.brod.opengl.Sprite;

public class Card {

	int[]					front, back;
	float					posX, posY;
	private Sprite<Card>	sprite;
	private float			rotX		= 0;
	private CardSet			_cardSet;
	private Hand			_hand;
	private boolean			_selected	= false;
	private boolean			visible		= true;

	protected Card(CardSet cardSet, int x, int y, int xBack, int yBack) {
		_cardSet = cardSet;
		front = new int[] { x, y };
		back = new int[] { xBack, yBack };
		posX = 0;
		posY = 0;
		sprite = cardSet.createSprite(this, x, y, posX, posY);
	}

	public void setPosition(float x, float y) {
		posX = _cardSet.transformX(x);
		posY = _cardSet.transformY(y);
		sprite.setPosition(posX, posY, rotX);
	}

	public void moveTo(Hand hand) {
		if (_hand != null) {
			_hand.remove(this);
		}
		_hand = hand;
		if (_hand != null) {
			_hand.add(this);
		}
	}

	public ISprite<?> getSprite() {
		return sprite;
	}

	public void setDirty() {
		if (_hand != null) {
			_hand.setDirty(true);
		}
	}

	public boolean isSelected() {
		return _selected;
	}

	public void setSelected(boolean selected) {
		this._selected = selected;
		if (selected) {
			sprite.setColor(Color.LTGRAY);
		} else {
			sprite.setColor(Color.WHITE);
		}
	}

	public Hand getHand() {
		return _hand;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean pbVisible) {
		visible = pbVisible;
		if (visible) {
			sprite.setGrid(front[0], front[1]);
		} else {
			sprite.setGrid(back[0], back[1]);
		}
	}

	public void setId(int i) {
		sprite.setId(i);
	}

}
