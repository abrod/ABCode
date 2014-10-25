package de.brod.cardmaniac;

import java.util.ArrayList;
import java.util.List;

import de.brod.cardmaniac.game.Game;
import de.brod.cardmaniac.game.INextMove;
import de.brod.cardmaniac.table.Card;
import de.brod.cardmaniac.table.Hand;
import de.brod.opengl.ISprite;

public class Mover {

	private ArrayList<ISprite<Card>>	lstMoverSprite	= new ArrayList<ISprite<Card>>();
	private long						start;
	private Game						_game;
	private UpdateThread				_updateThread;
	private MainActivity				_mainActivity;

	public Mover(MainActivity mainActivity) {
		_mainActivity = mainActivity;

	}

	public void finish() {
		for (ISprite<Card> sprite : lstMoverSprite) {
			sprite.setMovePosition(1f);
		}
		lstMoverSprite.clear();
		_mainActivity.sortCards();
	}

	public void start() {
		_game = _mainActivity._game;
		for (ISprite<Card> sprite : _game.getSprites()) {
			sprite.savePosition();
		}
	}

	public void end(boolean bNext) {
		lstMoverSprite.clear();
		start = System.currentTimeMillis();
		List<ISprite<Card>> sprites = _game.getSprites();
		for (ISprite<Card> sprite : sprites) {
			if (sprite.isPositionChanged()) {
				sprite.setMovePosition(0f);
				lstMoverSprite.add(sprite);
			}
		}
		_mainActivity.sortCards();
		if (bNext) {
			INextMove r = _game.getNextMove();
			if (r != null) {
				_updateThread = new UpdateThread(r, this);
				_updateThread.start();
			}
			_mainActivity.clearSelectedCards();
		}
	}

	public boolean isRunning() {
		if (lstMoverSprite.size() > 0) {
			float d = 1000f;
			float f = (System.currentTimeMillis() - start) / d;
			if (f < 1) {
				for (int i = 0; i < lstMoverSprite.size();) {
					if (lstMoverSprite.get(i).setMovePosition(f)) {
						i++;
					} else {
						lstMoverSprite.remove(i);
					}
				}
				if (lstMoverSprite.size() == 0) {
					_mainActivity.sortCards();
				}
			} else {
				finish();
			}
			return true;
		}
		return false;
	}

	public boolean isThreadRunning() {
		if (_updateThread == null) {
			return false;
		}
		if (_updateThread.isAlive()) {
			return true;
		}
		_updateThread = null;
		return false;
	}

	List<Hand> getHands() {
		return _game.getHands();
	}

	public void saveGame() {
		_mainActivity.saveGame();
	}

	public void waitFor() {
		try {
			while (lstMoverSprite.size() > 0) {
				Thread.sleep(50);
			}
		} catch (InterruptedException e) {
			// Interrupted
		}
	}

	public void requestRender() {
		_mainActivity.requestRender();
	}

}
