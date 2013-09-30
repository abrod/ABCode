package de.brod.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;
import de.brod.gui.shape.Button;
import de.brod.gui.shape.Container;
import de.brod.gui.shape.Menu;
import de.brod.gui.shape.MenuItem;
import de.brod.gui.shape.Sprite;
import de.brod.gui.shape.Text;
import de.brod.xml.XmlObject;

public abstract class GuiView<SPRITE extends Sprite> extends GLSurfaceView {

	private class ActionThread extends Thread {

		private IAction a;

		public ActionThread(IAction action) {
			a = action;
		}

		@Override
		public void run() {
			System.out.println("Start action");
			// reset the slides
			for (Container sprite : lstSprites) {
				sprite.savePosition();
				sprite.setMoving(false);
			}

			a.action();

			resetSlidePositions();
			// reset the actionThread
			actionThread = null;
			System.out.println("finished action " + lstSlides.size() + "/"
					+ lstSprites.size());
			update(false);
		}
	}

	/** The OpenGL view */
	protected StateHandler stateHandler;
	List<Container> lstButtons;

	List<MenuItem> lstMenuItems;

	private List<Container> lstSprites;
	private List<SPRITE> lstMoves = new ArrayList<SPRITE>();
	private List<Container> lstSlides = new ArrayList<Container>();
	Sprite area;
	Menu menu;
	private long startSlidingTime;

	private ActionThread actionThread;
	private Button pressedButton;

	public GuiView(Context context) {
		super(context);
	}

	protected abstract String getApplicationName();

	protected abstract void getMenuItems(List<String> menuItems);

	protected abstract IAction getNextAction();

	public abstract float getX(MotionEvent event);

	public abstract float getY(MotionEvent event);

	public abstract void initGroup(Sprite root, XmlObject lastHistoryEntry);

	protected abstract void initTextures(GL10 gl, int width2, int height2,
			int statusBarHeight);

	protected abstract boolean isInstanceOf(Container sprite);

	private boolean isThinking() {
		return actionThread != null && actionThread.isAlive();
	}

	protected abstract void menuPressed(String sItem, StateHandler stateHandler);

	@SuppressWarnings("unchecked")
	private boolean mouseDown(float eventX, float eventY) {
		if (isThinking()) {
			return false;
		}
		pressedButton = null;
		if (lstSlides.size() > 0) {
			for (Container sprite : lstSlides) {
				sprite.slide(1);
			}
			lstSlides.clear();
		}
		lstMoves.clear();
		if (lstMenuItems.size() > 0) {
			String sMenuItem = "";
			for (MenuItem item : lstMenuItems) {
				if (item.touches(eventX, eventY)) {
					sMenuItem = item.getText();
				}
			}
			menu.clear();
			lstMenuItems.clear();
			if (sMenuItem.length() > 0) {
				menuPressed(sMenuItem, stateHandler);
				reload();
			}
			return true;
		}
		for (int i = lstButtons.size() - 1; i >= 0; i--) {
			Container sprite = lstButtons.get(i);
			if (sprite.touches(eventX, eventY)) {
				if (sprite instanceof Button) {
					System.out.println("Button " + sprite + " pressed");
					pressedButton = (Button) sprite;
					pressedButton.pressed();
					return true;
				} else if (sprite instanceof Text) {
					System.out.println("Text " + sprite + " pressed");
					return true;
				}
			}
		}
		for (int i = lstSprites.size() - 1; i >= 0; i--) {
			Container sprite = lstSprites.get(i);
			if (sprite.touches(eventX, eventY)) {
				if (isInstanceOf(sprite) && sprite.isMoveable()) {
					System.out.println("touch " + i + " " + sprite.toString());
					lstMoves.add((SPRITE) sprite);
					mouseDown(lstMoves);
					for (Sprite moveItem : lstMoves) {
						moveItem.setMoving(true);
					}
					return true;
				}
			}
		}
		return false;
	}

	protected abstract void mouseDown(List<SPRITE> plstMoves);

	private boolean mouseMove(float eventX, float eventY) {
		if (isThinking()) {
			return false;
		}
		if (lstMoves.size() > 0) {
			for (Sprite moveItem : lstMoves) {
				if (moveItem.isMoveable()) {
					moveItem.touchMove(eventX, eventY);
				}
			}
			return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	private boolean mouseUp(float eventX, float eventY) {
		if (isThinking()) {
			return false;
		}
		if (pressedButton != null) {
			pressedButton.release();
			pressedButton = null;
		}

		if (lstMoves.size() > 0) {

			// reset the slides
			for (Container sprite : lstSprites) {
				sprite.savePosition();
			}
			for (Sprite moveItem : lstMoves) {
				moveItem.setMoving(false);
			}
			SPRITE selected = null;
			for (int i = lstSprites.size() - 1; i >= 0; i--) {
				Sprite sprite = (Sprite) lstSprites.get(i);
				if (!isInstanceOf(sprite)) {
					// ignore
				} else if (!lstMoves.contains(sprite)
						&& sprite.touches(eventX, eventY)) {
					selected = (SPRITE) sprite;
					break;
				}
			}
			mouseUp(lstMoves, selected);

			resetSlidePositions();
			return true;
		}
		return false;
	}

	protected abstract void mouseUp(List<SPRITE> plstMoves, SPRITE sprite);

	@Override
	public synchronized boolean onTouchEvent(MotionEvent event) {
		float eventX = getX(event);
		float eventY = getY(event);

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if (mouseDown(eventX, eventY)) {
				// Schedules a repaint.
				sortSprites();
				requestRender();
			}
			break;
		case MotionEvent.ACTION_MOVE:
			if (mouseMove(eventX, eventY)) {
				// Schedules a repaint.
				requestRender();
			}
			break;
		case MotionEvent.ACTION_UP:
			if (mouseUp(eventX, eventY)) {
				// Schedules a repaint.
				update(true);
			}
			break;
		default:
			return false;
		}

		return true;
	}

	public void openMenu() {
		System.out.println("open menu");

		menu.clear();

		List<String> menuItems = new ArrayList<String>();

		getMenuItems(menuItems);

		for (String string : menuItems) {
			menu.addItem(string);
		}
		menu.finish(lstMenuItems);
	}

	public void processNextStep() {
		if (lstSlides.size() > 0) {
			float d = (System.currentTimeMillis() - startSlidingTime) / 500f;
			for (int i = 0; i < lstSlides.size();) {
				if (lstSlides.get(i).slide(d)) {
					lstSlides.remove(i);
				} else {
					i++;
				}
			}
			if (lstSlides.size() == 0) {
				sortSprites();
			}
			requestRender();
		} else if (!isThinking()) {
			IAction action = getNextAction();
			if (action != null) {
				// if thread is not active
				actionThread = new ActionThread(action);
				actionThread.start();
			}
		}
	}

	public void reload() {
		area.clear();
		initGroup(area, stateHandler.getLastHistoryEntry());
		lstSprites = area.getChildren();
		sortSprites();

	}

	private void resetSlidePositions() {
		lstSlides.clear();
		for (Container sprite : lstSprites) {
			if (sprite.resetPosition()) {
				lstSlides.add(sprite);
			}
		}
		if (lstSlides.size() > 0) {
			startSlidingTime = System.currentTimeMillis();
		}
		lstMoves.clear();
	}

	protected abstract void saveState(XmlObject historyEntry);

	private void sortSprites() {
		Collections.sort(lstSprites);
	}

	public void update(boolean pbSetUndoPoint) {
		sortSprites();

		XmlObject historyEntry = stateHandler.createEmptyHistoryEntry();
		saveState(historyEntry);
		stateHandler.addHistory(historyEntry, pbSetUndoPoint);

		requestRender();
	}
}
