/*
 * ******************************************************************************
 * Copyright (c) 2013 Andreas Brod
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * *****************************************************************************
 */
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
			menu.activateBack();
			requestRender();

			a.action();
			menu.clear();

			resetSlidePositions();
			// reset the actionThread
			actionThread = null;
			System.out.println("finished action " + lstSlides.size() + "/"
					+ lstSprites.size());
			update(false);
		}
	}

	/** The OpenGL view */
	protected StateHandler applicationStateHandler;
	List<Container> lstTitleItems;
	List<MenuItem> lstMenuItems;

	private List<Container> lstSprites;
	private List<SPRITE> lstMoves = new ArrayList<SPRITE>();
	private List<SPRITE> lstSelected = new ArrayList<SPRITE>();
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
				menuPressed(sMenuItem, applicationStateHandler);
				reload();
			}
			return true;
		}
		// reset the slides
		for (Container spritec : lstSprites) {
			spritec.savePosition();
		}
		for (int i = lstTitleItems.size() - 1; i >= 0; i--) {
			Container sprite = lstTitleItems.get(i);
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
			if (sprite.isVisible() && sprite.touches(eventX, eventY)) {
				if (isInstanceOf(sprite)) {
					System.out.println("touch " + i + " " + sprite.toString());
					if (lstSelected.size() > 0) {

						for (Sprite moveItem : lstSelected) {
							moveItem.setColor(GuiColors.ITEM_WHITE);
							moveItem.setMoving(false);
						}
						if (mouseUp(lstSelected, (SPRITE) sprite)) {
							for (SPRITE spritec : lstSelected) {
								spritec.setColor(GuiColors.ITEM_WHITE);
							}
							lstSelected.clear();
							resetSlidePositions();
							update(true);
							return true;
						}
						for (SPRITE spritec : lstSelected) {
							spritec.setColor(GuiColors.ITEM_WHITE);
						}
						lstSelected.clear();
					}
					if (sprite.isMoveable()) {
						lstMoves.add((SPRITE) sprite);
						mouseDown(lstMoves);
						for (Sprite moveItem : lstMoves) {
							moveItem.setMoving(true);
							moveItem.setColor(GuiColors.ITEM_SELECTED);
						}
						return true;
					}
				} else if (sprite instanceof Button) {
					Button button = (Button) sprite;
					System.out.println("Button " + button + " pressed");
					button.pressed();
					resetSlidePositions();
					update(true);
					return true;
				}
			}
		}
		if (lstSelected.size() > 0) {
			for (SPRITE spritec : lstSelected) {
				spritec.setColor(GuiColors.ITEM_WHITE);
			}
			lstSelected.clear();
			return true;
		}
		return true;
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
			lstSelected.clear();
			lstSelected.addAll(lstMoves);
			for (Sprite moveItem : lstMoves) {
				moveItem.setMoving(false);
			}
			SPRITE selected = null;
			for (int i = lstSprites.size() - 1; i >= 0; i--) {
				Container sprite = lstSprites.get(i);
				if (!isInstanceOf(sprite)) {
					// ignore
				} else if (!lstMoves.contains(sprite)
						&& sprite.touches(eventX, eventY)) {
					selected = (SPRITE) sprite;
					break;
				}
			}
			if (mouseUp(lstMoves, selected)) {
				for (SPRITE sprite : lstSelected) {
					sprite.setColor(GuiColors.ITEM_WHITE);
				}
				lstSelected.clear();
			}

			resetSlidePositions();
			return true;
		}
		return false;
	}

	protected abstract boolean mouseUp(List<SPRITE> plstMoves, SPRITE sprite);

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

	public synchronized void processNextStep() {
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
				sortSprites();
				// if thread is not active
				actionThread = new ActionThread(action);
				actionThread.start();
			}
		}
	}

	public void reload() {
		area.clear();
		lstMoves.clear();
		XmlObject lastHistoryEntry = applicationStateHandler
				.getLastHistoryEntry();
		initGroup(area, lastHistoryEntry);
		lstSprites = area.getChildren();
		if (lastHistoryEntry == null) {
			update(true);
		} else {
			sortSprites();
			requestRender();
		}
	}

	protected boolean resetSlidePositions() {
		lstSlides.clear();
		for (Container sprite : lstSprites) {
			if (sprite.resetPosition()) {
				lstSlides.add(sprite);
			}
		}
		lstMoves.clear();
		if (lstSlides.size() > 0) {
			startSlidingTime = System.currentTimeMillis();
			sortSprites();
			return true;
		}
		return false;
	}

	protected abstract void saveState(XmlObject historyEntry);

	private void sortSprites() {
		Collections.sort(lstSprites);
	}

	public void update(boolean pbSetUndoPoint) {
		sortSprites();

		XmlObject historyEntry = applicationStateHandler
				.createEmptyHistoryEntry();
		saveState(historyEntry);
		applicationStateHandler.addHistory(historyEntry, pbSetUndoPoint);

		requestRender();
	}
}
