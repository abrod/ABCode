package de.brod.cardmaniac.card;

import android.graphics.Color;
import de.brod.cardmaniac.card.set.CardSet;
import de.brod.cardmaniac.card.set.CardType;
import de.brod.opengl.ISprite;
import de.brod.opengl.Sprite;

public class Card<CARDTYPE extends CardType> {

	private Hand<CARDTYPE>	_hand;
	private Sprite			_sprite;
	private CARDTYPE		_cardType;
	private boolean			_visible	= true;
	private boolean			_selected	= false;
	private int[]			_gridCoords;
	private float			rotation;

	public Card(CARDTYPE pCardType, CardSet<CARDTYPE> pCardSet, int iX, int iY,
			int iX2, int iY2) {
		_sprite = pCardSet.createSprite(iX, iY);
		_cardType = pCardType;
		_gridCoords = new int[] { iX, iY, iX2, iY2 };
		_visible = true;
	}

	public void moveTo(Hand<CARDTYPE> hand) {
		if (_hand != null) {
			_hand.removeCard(this);
		}
		_hand = hand;
		_hand.addCard(this);
	}

	public void cardMoveTo(float x, float y) {
		_sprite.moveTo(_cardType.getX(x), _cardType.getY(y));
	}

	public ISprite getSprite() {
		return _sprite;
	}

	public boolean isVisible() {
		return _visible;
	}

	public void setVisible(boolean visible) {
		if (_visible != visible) {
			_visible = visible;
			if (visible) {
				_sprite.setGrid(_gridCoords[0], _gridCoords[1]);
			} else {
				_sprite.setGrid(_gridCoords[2], _gridCoords[3]);
			}
		}
	}

	public void mouseDown(float eventX, float eventY) {
		_sprite.mouseDown(eventX, eventY);
		if (_hand != null) {
			_hand.setDirty(true);
		}
	}

	public Hand<CARDTYPE> getHand() {
		return _hand;
	}

	public boolean isSelected() {
		return _selected;
	}

	public void setSelected(boolean selected) {
		if (_selected != selected) {
			_selected = selected;
			if (selected) {
				_sprite.setColor(Color.argb(255, 128, 128, 128));
			} else {
				_sprite.setColor(Color.argb(255, 255, 255, 255));
			}
		}
	}
}
