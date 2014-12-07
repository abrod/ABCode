package de.brod.cardmaniac;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Color;
import de.brod.cardmaniac.card.Card;
import de.brod.cardmaniac.card.Hand;
import de.brod.cardmaniac.game.FreeCell;
import de.brod.cardmaniac.game.Game;
import de.brod.opengl.IAction;
import de.brod.opengl.ISprite;
import de.brod.opengl.OpenGLActivity;
import de.brod.opengl.Rect;

public class MainActivity extends OpenGLActivity {

	private Game						_game;
	private Hashtable<ISprite, Card<?>>	_htCards	= new Hashtable<ISprite, Card<?>>();
	private int							_color;
	private List<ISprite>				_lstSprites;
	private List<Rect>					_lstRectangles;
	private List<Hand<?>>				_lstHands	= new ArrayList<Hand<?>>();
	private Mover						_mover		= new Mover();

	@Override
	public boolean actionDown(float eventX, float eventY) {

		// check if there are selected cards
		List<Card<?>> selectedCards = _mover.getSelectedCards();
		Hand<?> handSelected = null;
		if (selectedCards.size() > 0) {
			handSelected = selectedCards.get(0).getHand();
			if (actionUp(selectedCards, eventX, eventY, false)) {
				_mover.clearSelected();
				return true;
			}
			_mover.clearSelected();
		}

		_mover.clear();
		Card<?> card = getCardAt(null, eventX, eventY);

		if (card != null) {
			if (card.getHand().equals(handSelected)) {
				// de-select
				return true;
			}
			List<Card<?>> lstMovingCards = _game.actionDown(card);
			if (lstMovingCards.size() > 0) {
				_mover.setMovingCards(lstMovingCards, eventX, eventY);
				sort();
				return true;
			}
			return false;
		}

		return false;
	}

	private Card<?> getCardAt(List<Card<?>> movingCards, float eventX,
			float eventY) {
		for (int i = _lstSprites.size() - 1; i >= 0; i--) {
			ISprite iSprite = _lstSprites.get(i);
			if (iSprite.touches(eventX, eventY)) {
				Card<?> card = _htCards.get(iSprite);
				if (movingCards == null || !movingCards.contains(card)) {
					return card;
				}
			}
		}
		return null;
	}

	@Override
	public boolean actionMove(float eventX, float eventY) {
		if (_mover.moveCards(eventX, eventY)) {
			return true;
		}
		return false;
	}

	@Override
	public boolean actionUp(float eventX, float eventY) {

		List<Card<?>> movingCards = _mover.getMovingCards();
		if (movingCards.size() > 0) {
			actionUp(movingCards, eventX, eventY, true);
			return true;
		}
		return false;
	}

	private boolean actionUp(List<Card<?>> movingCards, float eventX,
			float eventY, boolean pbSelect) {
		if (movingCards.size() > 0) {
			Hand<?> handFrom = movingCards.get(0).getHand();
			Card<?> cardTo = getCardAt(movingCards, eventX, eventY);
			Hand<?> handTo;
			if (cardTo == null) {
				handTo = getHandAt(eventX, eventY);
			} else {
				handTo = cardTo.getHand();
			}
			if (handTo != null) {
				if (!handTo.equals(handFrom)) {
					// the hand has to be changed
					_game.actionUp(movingCards, cardTo, handTo);
				} else if (pbSelect) {
					// select the cards
					_mover.selectMovingCards();
				} else {
					return false;
				}
				_mover.finish();
				_game.organize();
				sort();
				return true;
			}
		}
		return false;
	}

	private Hand<?> getHandAt(float eventX, float eventY) {
		for (int i = 0; i < _lstRectangles.size(); i++) {
			if (_lstRectangles.get(i).touches(eventX, eventY)) {
				return _lstHands.get(i);
			}
		}
		// not found
		return null;
	}

	@Override
	public void fillMenuActions(List<IAction> plstMenuActions) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getColor() {
		return _color;
	}

	@Override
	public void initSprites(GL10 gl, List<ISprite> lstSprites,
			List<Rect> lstRectangles) {
		_color = Color.argb(255, 0, 102, 0);
		_lstSprites = lstSprites;
		_lstRectangles = lstRectangles;

		_game = new FreeCell();
		_game.initCardSet(gl);

		init();

	}

	private void init() {
		_game.initHands();

		_game.clearHands();
		_game.newGame();

		_htCards.clear();
		_lstSprites.clear();
		_lstRectangles.clear();
		_lstHands.clear();

		List<Hand<?>> hands = _game.getHands();
		for (Hand<?> hand : hands) {
			hand.organize();
			for (Card<?> card : hand.getCards()) {
				ISprite sprite = card.getSprite();
				_lstSprites.add(sprite);
				_htCards.put(sprite, card);
			}
			_lstRectangles.add(hand.getRect());
			_lstHands.add(hand);
		}

		sort();
	}

	private void sort() {
		Collections.sort(_lstSprites);
	}

	@Override
	public boolean onDrawFrame() {
		// TODO Auto-generated method stub
		return false;
	}

}
