package de.brod.cardmaniac;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Color;
import de.brod.cardmaniac.cards.Button;
import de.brod.cardmaniac.cards.Card;
import de.brod.cardmaniac.cards.Hand;
import de.brod.cardmaniac.games.Game;
import de.brod.cardmaniac.games.ITurn;
import de.brod.cardmaniac.games.MauMau;
import de.brod.opengl.IAction;
import de.brod.opengl.ISprite;
import de.brod.opengl.OpenGLActivity;
import de.brod.opengl.Rect;

public class MainActivity extends OpenGLActivity {

	class MoverThread extends Thread {

		private ITurn	_turn;

		public MoverThread(ITurn turn) {
			_turn = turn;
		}

		@Override
		public void run() {
			_turn.calculateNextMove();
		}

		public void finish() {
			_turn.executeNextMove();
		}

	}

	class Mover {

		private List<ISprite<?>>	_lstMoves	= new ArrayList<ISprite<?>>();
		private long				_startTime;
		private MoverThread			moverThread	= null;

		public void stopMoving() {
			for (ISprite<?> s : _lstMoves) {
				s.setMovePosition(1);
			}
			_lstMoves.clear();
		}

		public void start() {
			// save the positions
			for (ISprite<?> sprite : _lstSprites) {
				sprite.savePosition();
			}
			// organize the hands (will set the new positions)
			_game.organize();

			// clear the move positions
			stopMoving();
			// set the new positions
			_startTime = System.currentTimeMillis();
			for (ISprite<?> sprite : _lstSprites) {
				if (sprite.isPositionChanged()) {
					_lstMoves.add(sprite);
				}
			}
			// set the correct order
			sortCards();
			// create the next turn
			ITurn turn = _game.getNextTurn();
			if (turn != null) {
				moverThread = new MoverThread(turn);
				moverThread.start();
			} else {
				moverThread = null;
			}
		}

		public boolean checkMoves() {
			if (_lstMoves.size() > 0) {
				float f = (System.currentTimeMillis() - _startTime) / 1000f;
				for (int i = 0; i < _lstMoves.size();) {
					ISprite<?> sprite = _lstMoves.get(i);
					if (sprite.setMovePosition(f)) {
						i++;
					} else {
						_lstMoves.remove(i);
					}
				}
				return true;
			}

			return isRunning();
		}

		public synchronized boolean isRunning() {
			if (moverThread != null) {
				if (!moverThread.isAlive()) {
					moverThread.finish();
					// restart (will change moverThread)
					start();
				}
				return true;
			}
			return false;
		}

	}

	private int					_color;
	private List<ISprite<?>>	_lstSprites;
	private List<Card>			_lstSelected	= new ArrayList<Card>();
	private Game				_game;
	private List<Hand>			_hands;
	private Mover				_mover			= new Mover();
	private List<Button>		_buttons;
	private Button				_selButton;

	@Override
	public boolean actionDown(float eventX, float eventY) {

		_mover.stopMoving();

		_selButton = null;
		clearSelected();

		if (_mover.isRunning()) {
			return true;
		}
		for (Button button : _buttons) {
			if (button.touches(eventX, eventY)) {
				_selButton = button;
				button.setDown(true);
				return true;
			}
		}

		// try to find a card at position
		Card card = getCardAt(eventX, eventY, null);
		Hand hand = getHandAt(eventX, eventY, card);

		if (card != null) {

			if (playCards(card, hand)) {
				// cards played
				return true;
			}

			_game.mouseClick(card, _lstSelected);

			for (Card c : _lstSelected) {
				c.setSelected(true);
				c.getSprite().setOffset(eventX, eventY);
			}
			return true;
		} else if (_lstSelected.size() > 0) {

			if (playCards(null, hand)) {
				// cards played
				return true;
			}
			// de-select
			clearSelected();
			return true;
		}

		return false;
	}

	private boolean playCards(Card cardTo, Hand handTo) {
		if (_lstSelected.size() > 0 && handTo != null) {
			if (!_lstSelected.get(0).getHand().equals(handTo)) {
				if (_game.playCard(_lstSelected, cardTo, handTo)) {
					clearSelected();
					_mover.start();
					return true;
				}
			}
		}
		return false;
	}

	private void clearSelected() {
		for (Card c : _lstSelected) {
			c.setSelected(false);
		}
		_lstSelected.clear();

	}

	private Hand getHandAt(float eventX, float eventY, Card card) {
		if (card != null) {
			return card.getHand();
		} else {
			for (Hand h : _hands) {
				if (h.getRectangle().touches(eventX, eventY)) {
					return h;
				}
			}
		}
		return null;
	}

	private Card getCardAt(float eventX, float eventY, List<Card> plstIgnore) {
		Card card = null;
		for (ISprite<?> sprite : _lstSprites) {
			if (sprite.touches(eventX, eventY)) {
				Card c = (Card) sprite.getReference();
				if (plstIgnore == null || !plstIgnore.contains(c)) {
					card = c;
				}
			}
		}
		return card;
	}

	@Override
	public boolean actionMove(float eventX, float eventY) {
		if (_selButton != null) {
			_selButton.setDown(_selButton.touches(eventX, eventY));
			return true;
		}
		if (_lstSelected.size() > 0) {
			for (Card c : _lstSelected) {
				c.setDirty();
				c.getSprite().moveTo(eventX, eventY);
			}
			sortCards();
			return true;
		}
		return false;
	}

	private void sortCards() {
		Collections.sort(_lstSprites);
	}

	@Override
	public boolean actionUp(float eventX, float eventY) {
		if (_selButton != null) {
			if (_selButton.touches(eventX, eventY)) {
				_selButton.performAction();
				_mover.start();
			}
			_selButton.setDown(false);
			return true;
		}
		if (_lstSelected.size() > 0) {
			Card card = getCardAt(eventX, eventY, _lstSelected);
			Hand hand = getHandAt(eventX, eventY, card);
			if (!playCards(card, hand)) {
				// reset
				_mover.start();
			}
			return true;
		}
		return false;
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
	public void initSprites(GL10 gl, List<ISprite<?>> lstSprites,
			List<Rect> lstRectangles) {
		_color = Color.argb(255, 0, 102, 0);
		_lstSprites = lstSprites;
		// init the game
		_game = new MauMau();
		//_game = new FreeCell();

		_game.init(gl, this);

		_hands = _game.initHands();
		List<Card> cards = _game.initCards();

		_game.newGame(cards);

		for (Card card : cards) {
			lstSprites.add(card.getSprite());
		}
		for (Hand hand : _hands) {
			Rect rectangle = hand.getRectangle();
			if (rectangle != null) {
				lstRectangles.add(rectangle);
			}
		}

		_buttons = _game.initButtons();
		for (Button button : _buttons) {
			lstRectangles.add(button.getRect());
		}

		_mover.start();
		// mover will be started within organize
		_mover.stopMoving();
	}

	@Override
	public boolean onDrawFrame() {
		return _mover.checkMoves();
	}

}
