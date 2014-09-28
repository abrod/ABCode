package de.brod.cardmaniac;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Color;
import de.brod.cardmaniac.game.FreeCell;
import de.brod.cardmaniac.game.Game;
import de.brod.cardmaniac.game.GameState;
import de.brod.cardmaniac.game.INextMove;
import de.brod.cardmaniac.table.Card;
import de.brod.cardmaniac.table.Deck;
import de.brod.cardmaniac.table.Hand;
import de.brod.opengl.IAction;
import de.brod.opengl.ISubAction;
import de.brod.opengl.OpenGLActivity;
import de.brod.opengl.OpenGLButton;
import de.brod.opengl.Rect;
import de.brod.opengl.Sprite;

public class MainActivity extends OpenGLActivity {

	class UpdateThread extends Thread {
		private INextMove r;

		UpdateThread(INextMove r) {
			this.r = r;
		}

		@Override
		public void run() {
			if (r.hasNext()) {
				r.startMove();
				mover.waitFor();
				mover.start();
				// organize hands
				for (Hand hand : game.getHands()) {
					hand.organize();
				}
				mover.end(true);
				saveGame();
				// sort
				requestRender();
			}
		}
	}

	private List<Sprite<Card>> _lstSelectedCards = new ArrayList<Sprite<Card>>();

	private List<Sprite<?>> _lstOriginalSprites;
	private Game game;
	private int _color;
	private UpdateThread updateThread;

	private Mover mover = new Mover();

	private List<Rect> _lstRectangles;

	private Deck _deck;

	private StateReader<MainConfig> _stateReader;

	private StateReader<GameState> _gameReader;

	private MainConfig _mainConfig;

	class Mover {

		List<Sprite<Card>> lstMoverSprite = new ArrayList<Sprite<Card>>();
		private long start;

		public void finish() {
			log("finish");
			for (Sprite<Card> sprite : lstMoverSprite) {
				sprite.setMovePosition(1f);
			}
			lstMoverSprite.clear();
			sortCards();
		}

		public void waitFor() {
			log("waitFor");
			try {
				while (lstMoverSprite.size() > 0) {
					Thread.sleep(50);
				}
			} catch (InterruptedException e) {
				// Interrupted
			}
		}

		public void start() {
			log("start");
			for (Sprite<Card> sprite : game.getSprites()) {
				sprite.savePosition();
			}
		}

		private void log(String msg) {
			// Log.d("Mover", msg);
		}

		public void end(boolean bNext) {
			lstMoverSprite.clear();
			start = System.currentTimeMillis();
			List<Sprite<Card>> sprites = game.getSprites();
			for (Sprite<Card> sprite : sprites) {
				if (sprite.isPositionChanged()) {
					sprite.setMovePosition(0f);
					lstMoverSprite.add(sprite);
				}
			}
			log("end (" + lstMoverSprite.size() + "/" + sprites.size()
					+ " changed).");
			sortCards();
			if (bNext) {
				INextMove r = game.getNextMove();
				if (r != null) {
					updateThread = new UpdateThread(r);
					updateThread.start();
				}
				clearSelectedCards();
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
						sortCards();
					}
					log("running (" + lstMoverSprite.size() + "/"
							+ Math.round(f * 1000f) / 10.0 + " %)");
				} else {
					finish();
				}
				return true;
			}
			return false;
		}

	}

	@Override
	public boolean actionDown(float eventX, float eventY) {
		if (isThreadRunning()) {
			// ignore
			return false;
		}
		if (_lstSelectedCards.size() > 0) {
			actionUp(eventX, eventY);
			if (_lstSelectedCards.size() == 0) {
				// consumed
				return true;
			}
		}
		List<Sprite<Card>> lstSprites = getTouchingCards(eventX, eventY);
		int size = lstSprites.size();
		Card card0 = null;
		if (size > 0) {
			Sprite<Card> sprite0 = lstSprites.get(size - 1);
			card0 = sprite0.getReference();
		}
		mover.finish();
		for (Sprite<Card> sprite : game.getSprites()) {
			Card card = sprite.getReference();
			if (!card.equals(card0)) {
				card.setSelected(false);
			}
		}
		if (card0 == null) {
			clearSelectedCards();
		} else {
			mover.start();
			boolean bSelected = !card0.isSelected();
			clearSelectedCards();
			card0.setSelected(bSelected);
			if (bSelected) {
				// get all selected cards
				boolean selectCards = game.actionDown(card0, _lstSelectedCards);

				for (Sprite<Card> sprite : _lstSelectedCards) {
					sprite.setOffset(eventX, eventY);
				}
				if (selectCards) {
					mover.end(true);
				}
			}
		}
		return true;
	}

	private void clearSelectedCards() {
		for (Sprite<Card> sprite : _lstSelectedCards) {
			sprite.getReference().setSelected(false);
		}
		_lstSelectedCards.clear();
	}

	@Override
	public boolean actionMove(float eventX, float eventY) {
		if (_lstSelectedCards.size() > 0) {
			for (Sprite<Card> sprite : _lstSelectedCards) {
				sprite.moveTo(eventX, eventY);
			}
			sortCards();
			return true;
		}
		return false;
	}

	@Override
	public boolean actionUp(float eventX, float eventY) {
		if (_lstSelectedCards.size() > 0) {
			HashSet<Hand> hsHands = new HashSet<Hand>();
			for (Sprite<Card> sprite : _lstSelectedCards) {
				Hand hand = sprite.getReference().getHand();
				if (hand != null) {
					hsHands.add(hand);
				}
			}
			Hand h = game.getHandAt(eventX, eventY);
			boolean bNext = false;
			if (h != null && !hsHands.contains(h)) {
				bNext = game.actionUp(_lstSelectedCards, h);
				hsHands.add(h);
			}
			for (Hand hand : hsHands) {
				hand.organize();
			}
			saveGame();

			mover.end(bNext);
			sortCards();
			return true;
		}
		return false;
	}

	protected void saveGame() {
		GameState state = game.getState();
		// Log.i("State", state.toString());
		_gameReader.saveState(game.getName(), state);
	}

	void sortCards() {
		Collections.sort(_lstOriginalSprites);
	}

	private synchronized boolean isThreadRunning() {
		if (updateThread == null) {
			return false;
		}
		if (updateThread.isAlive()) {
			return true;
		}
		updateThread = null;
		return false;
	}

	@Override
	public int getColor() {
		return _color;
	}

	private List<Sprite<Card>> getTouchingCards(float eventX, float eventY) {
		List<Sprite<Card>> lst = new ArrayList<Sprite<Card>>();
		for (Sprite<Card> sprite : game.getSprites()) {
			if (sprite.touches(eventX, eventY)) {
				lst.add(sprite);
			}
		}

		Collections.sort(lst);
		return lst;
	}

	public static class MainConfig implements Serializable {

		/**
		 *
		 */
		private static final long serialVersionUID = 3401991076230505645L;

		public String gameClassName;
	}

	@Override
	public void initSprites(GL10 gl, List<Sprite<?>> lstSprites,
			List<Rect> lstRectangles) {

		_color = Color.argb(255, 0, 102, 0);

		_lstOriginalSprites = lstSprites;
		_lstRectangles = lstRectangles;
		_deck = new Deck(this, gl, 8);

		_stateReader = new StateReader<MainConfig>("init");
		_gameReader = new StateReader<GameState>("game");

		_mainConfig = _stateReader.readState("lastGame");
		Class<? extends Game> lastGame = FreeCell.class;
		if (_mainConfig != null) {
			try {
				lastGame = Class.forName(_mainConfig.gameClassName).asSubclass(
						Game.class);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			_mainConfig = new MainConfig();
		}

		initGame(lastGame, true);
	}

	void initGame(Class<? extends Game> pClass, boolean pbLoadState) {

		_mainConfig.gameClassName = pClass.getName();
		_stateReader.saveState("lastGame", _mainConfig);

		_lstOriginalSprites.clear();
		_lstRectangles.clear();

		try {
			game = pClass.newInstance();
		} catch (Exception e) {
			// fallback
			game = new FreeCell();
		}
		_deck.clear();

		GameState state = pbLoadState ? _gameReader.readState(game.getName())
				: null;
		// game = new Solitaire();
		game.initGame(_deck, state);

		_lstOriginalSprites.addAll(game.getSprites());
		List<Rect> rectangles = game.getRectangles();
		_lstRectangles.addAll(rectangles);
		int color = getColor();
		int red = Color.red(color);
		int green = Color.green(color);
		int blue = Color.blue(color);
		for (Rect rect : rectangles) {
			rect.setColor(red / 2, green / 2, blue / 2, 128);
		}
		List<OpenGLButton> lstButtons = new ArrayList<OpenGLButton>();
		game.initButtons(_deck, lstButtons);
		_lstRectangles.addAll(lstButtons);
		for (Rect rect : lstButtons) {
			rect.setColor(192, 192, 192, 192);
		}
		sortCards();

	}

	@Override
	public boolean onDrawFrame() {
		boolean running = mover.isRunning();
		return running;
	}

	@Override
	public void fillMenuActions(List<IAction> plstMenuActions) {

		plstMenuActions.add(new ISubAction() {

			@Override
			public String getTitle() {
				return "Select Game ...";
			}

			@Override
			public IAction[] getSubItems() {

				List<Class<? extends Game>> lstClasses = Game.getGameClasses();

				IAction[] iActions = new IAction[lstClasses.size()];
				for (int i = 0; i < iActions.length; i++) {
					iActions[i] = newGameAction(lstClasses.get(i));
				}
				return iActions;
			}

			private IAction newGameAction(final Class<? extends Game> pGameClass) {
				IAction iAction = new IAction() {

					@Override
					public String getTitle() {
						String name = pGameClass.getSimpleName();
						// name = name.substring(name.lastIndexOf(".")+1);
						return name;
					}

					@Override
					public void doAction() {
						initGame(pGameClass, true);
					}

				};
				return iAction;
			}

		});
		plstMenuActions.add(new IAction() {

			@Override
			public String getTitle() {
				return "New";
			}

			@Override
			public void doAction() {
				confirm("Do you really want to start a new game ?",
						new Runnable() {
							@Override
							public void run() {
								initGame(game.getClass(), false);
							}
						});

			}
		});

		plstMenuActions.add(new IAction() {

			@Override
			public String getTitle() {
				return "Options";
			}

			@Override
			public void doAction() {
				// TODO Auto-generated method stub

			}

		});
		plstMenuActions.add(new IAction() {

			@Override
			public String getTitle() {
				return "Exit";
			}

			@Override
			public void doAction() {
				// TODO Auto-generated method stub

			}

		});
	}
}
