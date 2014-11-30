package de.brod.cardmaniac.card;

import de.brod.cardmaniac.card.set.CardSet;
import de.brod.cardmaniac.card.set.CardType;
import de.brod.opengl.ISprite;
import de.brod.opengl.Sprite;

public class Card<CARDTYPE extends CardType> {

	private Hand<CARDTYPE>			_hand;
	private Sprite<Card<CARDTYPE>>	_sprite;
	private CARDTYPE				_cardType;
	private boolean					_visible	= true;
	private int[]					_gridCoords;

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

	public void moveTo(float x, float y, int i) {
		_sprite.setPosition(_cardType.getX(x), _cardType.getY(y), 0);
	}

	public ISprite<?> getSprite() {
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

}
