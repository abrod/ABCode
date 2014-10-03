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
import de.brod.opengl.ISprite;
import de.brod.opengl.ISubAction;
import de.brod.opengl.OpenGLActivity;
import de.brod.opengl.OpenGLButton;
import de.brod.opengl.Rect;

public class MainActivity extends OpenGLActivity {

	public static class MainConfig implements Serializable {

		private static final long	serialVersionUID	= 3401991076230505645L;
		public String				gameClassName;
	}

	class Mover {

		List<ISprite<Card>>	lstMoverSprite	= new ArrayList<ISprite<Card>>();
		private long		start;

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
			log("end (" + lstMoverSprite.size() + "/" + sprites.size()
					+ " changed).");
			sortCards();
			if (bNext) {
				INextMove r = _game.getNextMove();
				if (r != null) {
					_updateThread = new UpdateThread(r);
					_updateThread.start();
				}
				clearSelectedCards();
			}
		}

		public void finish() {
			log("finish");
			for (ISprite<Card> sprite : lstMoverSprite) {
				sprite.setMovePosition(1f);
			}
			lstMoverSprite.clear();
			sortCards();
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

		private void log(String msg) {
			// Log.d("Mover", msg);
		}

		public void start() {
			log("start");
			for (ISprite<Card> sprite : _game.getSprites()) {
				sprite.savePosition();
			}
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

	}

	class UpdateThread extends Thread {
		private INextMove	r;

		UpdateThread(INextMove r) {
			this.r = r;
		}

		@Override
		public void run() {
			if (r.hasNext()) {
				r.startMove();
				_mover.waitFor();
				_mover.start();
				// organize hands
				for (Hand hand : _game.getHands()) {
					hand.organize();
				}
				_mover.end(true);
				saveGame();
				// sort
				requestRender();
			}
		}
	}

	private int						_color;
	private Deck					_deck;
	private Game					_game;
	private StateReader<GameState>	_gameReader;
	private ArrayList<OpenGLButton>	_lstButtons			= new ArrayList<OpenGLButton>();
	private List<ISprite<?>>		_lstOriginalSprites;
	private List<Rect>				_lstRectangles;
	private List<ISprite<Card>>		_lstSelectedCards	= new ArrayList<ISprite<Card>>();
	private MainConfig				_mainConfig;
	private Mover					_mover				= new Mover();
	private StateReader<MainConfig>	_stateReader;
	private UpdateThread			_updateThread;
	private OpenGLButton			selButton;

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
		List<ISprite<Card>> lstSprites = getTouchingCards(eventX, eventY);
		int size = lstSprites.size();
		Card card0 = null;
		if (size > 0) {
			ISprite<Card> sprite0 = lstSprites.get(size - 1);
			card0 = sprite0.getReference();
		}
		_mover.finish();
		for (ISprite<Card> sprite : _game.getSprites()) {
			Card card = sprite.getReference();
			if (!card.equals(card0)) {
				card.setSelected(false);
			}
		}
		selButton = null;
		if (card0 == null) {
			clearSelectedCards();
			// check if button is pressed
			for (OpenGLButton button : _lstButtons) {
				if (button.touches(eventX, eventY)) {
					if (button.isEnabled()) {
						selButton = button;
						button.setDown(true);
					}
					break;
				}
			}
		} else {
			_mover.start();
			boolean bSelected = !card0.isSelected();
			clearSelectedCards();
			card0.setSelected(bSelected);
			if (bSelected) {
				// get all selected cards
				boolean selectCards = _game
						.actionDown(card0, _lstSelectedCards);

				for (ISprite<Card> sprite : _lstSelectedCards) {
					sprite.setOffset(eventX, eventY);
				}
				if (selectCards) {
					_mover.end(true);
				}
			}
		}
		return true;
	}

	@Override
	public boolean actionMove(float eventX, float eventY) {
		if (selButton != null) {
			selButton.setDown(selButton.touches(eventX, eventY));
			return true;

		} else if (_lstSelectedCards.size() > 0) {
			for (ISprite<Card> sprite : _lstSelectedCards) {
				sprite.moveTo(eventX, eventY);
			}
			sortCards();
			return true;
		}
		return false;
	}

	@Override
	public boolean actionUp(float eventX, float eventY) {
		if (selButton != null) {
			if (selButton.touches(eventX, eventY)) {
				IAction action = selButton.getAction();
				if (action != null) {
					_mover.start();
					action.doAction();
					for (Hand hand : _game.getHands()) {
						hand.organize();
					}
					saveGame();
					_mover.end(true);
					sortCards();
				}
			}
			selButton.setDown(false);
			selButton = null;
			return true;
		}
		if (_lstSelectedCards.size() > 0) {
			HashSet<Hand> hsHands = new HashSet<Hand>();
			for (ISprite<Card> sprite : _lstSelectedCards) {
				Hand hand = sprite.getReference().getHand();
				if (hand != null) {
					hsHands.add(hand);
				}
			}
			Hand h = _game.getHandAt(eventX, eventY);
			boolean bNext = false;
			if (h != null && !hsHands.contains(h)) {
				bNext = _game.actionUp(_lstSelectedCards, h);
				hsHands.add(h);
			}
			for (Hand hand : hsHands) {
				hand.organize();
			}
			saveGame();

			_mover.end(bNext);
			sortCards();
			return true;
		}
		return false;
	}

	private void clearSelectedCards() {
		for (ISprite<Card> sprite : _lstSelectedCards) {
			sprite.getReference().setSelected(false);
		}
		_lstSelectedCards.clear();
	}

	@Override
	public void fillMenuActions(List<IAction> plstMenuActions) {

		plstMenuActions.add(new ISubAction() {

			@Override
			public IAction[] getSubItems() {

				List<Class<? extends Game>> lstClasses = Game.getGameClasses();

				IAction[] iActions = new IAction[lstClasses.size()];
				for (int i = 0; i < iActions.length; i++) {
					iActions[i] = newGameAction(lstClasses.get(i));
				}
				return iActions;
			}

			@Override
			public String getTitle() {
				return "Select Game ...";
			}

			private IAction newGameAction(final Class<? extends Game> pGameClass) {
				IAction iAction = new IAction() {

					@Override
					public void doAction() {
						initGame(pGameClass, true);
					}

					@Override
					public String getTitle() {
						String name = pGameClass.getSimpleName();
						// name = name.substring(name.lastIndexOf(".")+1);
						return name;
					}

				};
				return iAction;
			}

		});
		plstMenuActions.add(new IAction() {

			@Override
			public void doAction() {
				confirm("Do you really want to start a new game ?",
						new Runnable() {
							@Override
							public void run() {
								initGame(_game.getClass(), false);
							}
						});

			}

			@Override
			public String getTitle() {
				return "New";
			}
		});

		plstMenuActions.add(new IAction() {

			@Override
			public void doAction() {
				// TODO Auto-generated method stub

			}

			@Override
			public String getTitle() {
				return "Options";
			}

		});
		plstMenuActions.add(new IAction() {

			@Override
			public void doAction() {
				// TODO Auto-generated method stub

			}

			@Override
			public String getTitle() {
				return "Exit";
			}

		});
	}

	@Override
	public int getColor() {
		return _color;
	}

	private List<ISprite<Card>> getTouchingCards(float eventX, float eventY) {
		List<ISprite<Card>> lst = new ArrayList<ISprite<Card>>();
		for (ISprite<Card> sprite : _game.getSprites()) {
			if (sprite.touches(eventX, eventY)) {
				lst.add(sprite);
			}
		}

		Collections.sort(lst);
		return lst;
	}

	void initGame(Class<? extends Game> pClass, boolean pbLoadState) {

		_mainConfig.gameClassName = pClass.getName();
		_stateReader.saveState("lastGame", _mainConfig);

		_lstOriginalSprites.clear();
		_lstRectangles.clear();

		try {
			_game = pClass.newInstance();
		} catch (Exception e) {
			// fallback
			_game = new FreeCell();
		}
		_deck.clear();

		GameState state = pbLoadState ? _gameReader.readState(_game.getName())
				: null;
		// game = new Solitaire();
		_game.initGame(_deck, state);

		_lstOriginalSprites.addAll(_game.getSprites());
		List<Rect> rectangles = _game.getRectangles();
		_lstRectangles.addAll(rectangles);
		int color = getColor();
		int red = Color.red(color);
		int green = Color.green(color);
		int blue = Color.blue(color);
		for (Rect rect : rectangles) {
			rect.setColor(red / 2, green / 2, blue / 2, 128);
		}
		// init the buttons
		_lstButtons.clear();
		_game.initButtons(_deck, _lstButtons);
		_lstRectangles.addAll(_lstButtons);
		sortCards();

	}

	@Override
	public void initSprites(GL10 gl, List<ISprite<?>> lstSprites,
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

	private synchronized boolean isThreadRunning() {
		if (_updateThread == null) {
			return false;
		}
		if (_updateThread.isAlive()) {
			return true;
		}
		_updateThread = null;
		return false;
	}

	@Override
	public boolean onDrawFrame() {
		boolean running = _mover.isRunning();
		return running;
	}

	protected void saveGame() {
		GameState state = _game.getState();
		// Log.i("State", state.toString());
		_gameReader.saveState(_game.getName(), state);
	}

	void sortCards() {
		Collections.sort(_lstOriginalSprites);
	}
}
