package de.brod.cardmaniac.table;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;
import de.brod.cardmaniac.CardColor;
import de.brod.cardmaniac.CardValue;
import de.brod.cardmaniac.MainActivity;
import de.brod.opengl.Grid;
import de.brod.opengl.Sprite;

public class Deck {

	private Grid<Card> grid;
	private float _iCardsCount, _cardWidth, _cardHeight;
	private List<Sprite<Card>> _lstSprites = new ArrayList<Sprite<Card>>();

	public Deck(MainActivity mainActivity, GL10 gl, int piCardsCount) {
		_iCardsCount = piCardsCount;
		setCardWidth(2f / _iCardsCount);
		setCardHeight(getCardWidth());
		// setCardHeight(getCardWidth() / 0.75f);
		// Bitmap bitmap = mainActivity.loadBitmap(R.drawable.cards);
		Bitmap bitmap = DeckBmp.createBitmap();
		grid = new Grid<Card>(13, 5, gl, bitmap);
		bitmap.recycle();
	}

	public Card createCard(CardValue val, CardColor col, boolean pbBackFlag) {
		return new Card(this, val, col, getCardWidth(), getCardHeight(),
				pbBackFlag);
	}

	Sprite<Card> createSprite(int i, float x, float y, float wd, float hg) {
		Sprite<Card> createSprite = grid.createSprite(i % 13, i / 13, x, y, wd,
				hg);
		_lstSprites.add(createSprite);
		return createSprite;
	}

	public List<Sprite<Card>> getAllSprites() {
		return _lstSprites;
	}

	public float getCardWidth() {
		return _cardWidth;
	}

	private void setCardWidth(float cardWidth) {
		this._cardWidth = cardWidth;
	}

	public float getCardHeight() {
		return _cardHeight;
	}

	private void setCardHeight(float cardHeight) {
		this._cardHeight = cardHeight;
	}

	public float getX(float pfX) {
		float x = pfX / (_iCardsCount - 1) * (2 - _cardWidth)
				- (1 - _cardWidth / 2);
		return x;
	}

	public float getY(float pfY) {
		float y = pfY / (_iCardsCount - 1) * (2 - _cardHeight)
				- (1 - _cardHeight / 2);
		return y;
	}

	public void clear() {
		_lstSprites.clear();
	}
}
