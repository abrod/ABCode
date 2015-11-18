package de.brod.cardmaniac;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import de.brod.cardmaniac.game.IGame;
import de.brod.cardmaniac.game.NextMove;
import de.brod.cardmaniac.game.Solitair;
import de.brod.gui.GuiActivity;
import de.brod.gui.IGuiQuad;

public class MainActivity extends GuiActivity {

	private List<Card> _lstActionCards = new ArrayList<Card>();
	private List<Hand<?>> _lstHands = new ArrayList<Hand<?>>();
	private float startX, startY;
	private IGame game;
	private List<Card> _lstSelctedCards = new ArrayList<Card>();
	private NextMoveThread nextMoveThread;

	@Override
	protected void initActivity(Bundle savedInstanceState) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void createQuads(List<IGuiQuad> lstQuads, float wd, float hg, int width, int height) {

		game = new Solitair();
		game.init(this, wd, hg, width, height);

		game.loadGame();

		addGuiItemFromGame(lstQuads, game);

		moveCardsWithinHands(false);
	}

	private void addGuiItemFromGame(List<IGuiQuad> lstQuads, IGame game) {
		_lstHands.clear();
		_lstHands.addAll(game.getHands());

		for (Hand<?> hand : _lstHands) {
			lstQuads.add(hand);
			lstQuads.addAll(hand.getCards());
		}
		lstQuads.addAll(game.getButtons());

	}

	private void moveCardsWithinHands(boolean slide) {
		for (Hand hand : _lstHands) {
			hand.moveCards(slide);
		}
		if (slide){
			game.saveGame();
		}
		sortQuads();
		if (!isThinking()) {
			NextMove nextMove = game.getNextMoveThread();
			if (nextMove != null) {
				nextMoveThread = new NextMoveThread(nextMove);
				nextMoveThread.start();
			}
		}
	}

	class NextMoveThread extends Thread {
		private final NextMove nextMove;
		private boolean running = true;

		public NextMoveThread(NextMove nextMove) {
			this.nextMove = nextMove;
		}

		public void run() {
			try {
				nextMove.calculateNextMove();
				do {
					Thread.sleep(50);
				} while (containsSlidingSquares());

				if (nextMove.makeNextMove()) {
					running = false;
					moveCardsWithinHands(true);
					requestRender();
				}
			} catch (InterruptedException e) {
				// stopped
			}

		}

		public boolean isRunning() {
			return running && isAlive();
		}
	}

	@Override
	public boolean isThinking() {
		if (nextMoveThread != null) {
			if (nextMoveThread.isRunning()) {
				return true;
			}
			nextMoveThread = null;
		}
		return false;
	}

	@Override
	public float[] getColorsRGB() {
		return new float[] { 0, 0, 0.3f };
	}

	@Override
	public boolean actionDown(float eventX, float eventY) {
		startX = eventX;
		startY = eventY;
		_lstActionCards.clear();
		if (_lstSelctedCards.size() > 0) {
			for (Card card : _lstSelctedCards) {
				card.setSelected(false);
			}
			List<Hand> lstQuadsAt = getQuadsAt(eventX, eventY, 1, Hand.class);
			if (lstQuadsAt.size() > 0) {
				Hand<?> handNew = lstQuadsAt.get(0);
				if (!handNew.equals(_lstSelctedCards.get(0).getHand())) {
					game.actionUp(_lstSelctedCards, handNew);
					_lstSelctedCards.clear();
					moveCardsWithinHands(true);
					return true;
				}
			}

			_lstSelctedCards.clear();
		}
		_lstActionCards.addAll(getQuadsAt(eventX, eventY, 1, Card.class));
		if (_lstActionCards.size() > 0) {
			IGuiQuad guiQuad = _lstActionCards.get(0);
			Card card = (Card) guiQuad;
			_lstActionCards.clear();
			List<? extends Card> lst = game.actionDown(card);
			_lstActionCards.addAll(lst);
			for (Card c : _lstActionCards) {
				c.setOffset(eventX, eventY);
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean actionMove(float eventX, float eventY) {
		if (_lstActionCards.size() > 0) {
			for (IGuiQuad guiQuad : _lstActionCards) {
				guiQuad.moveTo(eventX, eventY);
			}
			sortQuads();
			return true;
		}
		return false;
	}

	@Override
	public boolean actionUp(float eventX, float eventY) {
		if (_lstActionCards.size() > 0) {
			Hand<?> hand = _lstActionCards.get(0).getHand();
			List<Hand> lstQuadsAt = getQuadsAt(eventX, eventY, 1, Hand.class);
			if (lstQuadsAt.size() > 0) {
				Hand<?> handNew = lstQuadsAt.get(0);
				if (!handNew.equals(hand)) {
					game.actionUp(_lstActionCards, handNew);
				} else {
					_lstSelctedCards.clear();
					_lstSelctedCards.addAll(_lstActionCards);
					for (Card card : _lstSelctedCards) {
						card.setSelected(true);
					}
				}
			}
		}

		moveCardsWithinHands(true);
		return true;
	}

	public void newGame() {
		game.resetGame();
		moveCardsWithinHands(true);
		requestRender();
	}

}
