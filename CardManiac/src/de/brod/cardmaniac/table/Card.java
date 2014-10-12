package de.brod.cardmaniac.table;

import de.brod.cardmaniac.CardColor;
import de.brod.cardmaniac.CardValue;
import de.brod.opengl.ISprite;

public class Card {

	private CardValue		val;
	private CardColor		col;

	private Hand			hand		= null;
	private ISprite<Card>	sprite;
	private float			wd2, hg2;
	private boolean			selected	= false;
	private boolean			visible		= true;
	private Deck			_pDeck;
	private boolean			bBackFlag;

	Card(Deck pDeck, CardValue val, CardColor col, float width, float height,
			boolean pbBackFlag) {
		_pDeck = pDeck;
		wd2 = width / 2;
		hg2 = height / 2;
		bBackFlag = pbBackFlag;
		sprite = pDeck.createSprite(0, 0, 0, wd2 * 2, hg2 * 2);
		sprite.setReference(this);
		setCardValue(val);
		setCardColor(col);
		setGrid();
	}

	public CardColor getCardColor() {
		return col;
	}

	public CardValue getCardValue() {
		return val;
	}

	public Hand getHand() {
		return hand;
	}

	public ISprite<Card> getSprite() {
		return sprite;
	}

	public boolean isSelected() {
		return selected;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setCardColor(CardColor col) {
		this.col = col;
	}

	public void setCardValue(CardValue val) {
		this.val = val;
	}

	private void setGrid() {
		int i;
		if (visible) {
			int iCol = getCardColor().ordinal();
			int iVal = getCardValue().ordinal();
			i = iVal * 4 + iCol;
			if (getCardValue().equals(CardValue.joker)) {
				i = 52 + iCol % 3;
			} else if (getCardValue().equals(CardValue.deck)) {
				i = 55 + iCol / 2;
			}
		} else {
			i = 55 + (bBackFlag ? 0 : 1);
		}
		sprite.setGrid(i % 13, i / 13);
	}

	public void setHand(Hand hand) {
		this.hand = hand;
	}

	public void setPosition(float px, float py, int piId) {
		// float x = 2f * px / 8 - 1f;
		// float y = (2f * py / 8 - 1f) * (1 - hg2 + wd2);
		// sprite.setPosition(x + wd2, y + wd2);
		sprite.setPosition(_pDeck.getX(px), _pDeck.getY(py), 0);
		sprite.setId(piId);
	}

	public void setSelected(boolean pbSelected) {
		if (selected == pbSelected) {
			return;
		}
		selected = pbSelected;
		if (pbSelected) {
			sprite.setColor(128, 128, 192, 255);
		} else {
			sprite.setColor(255, 255, 255, 255);
		}
	}

	public void setVisible(boolean b) {
		if (visible != b) {
			visible = b;
			setGrid();
		}
	}

	@Override
	public String toString() {
		return col + "-" + val;
	}

}
