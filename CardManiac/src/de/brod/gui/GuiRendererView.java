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

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint.Align;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLU;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import de.brod.cm.R;
import de.brod.gui.action.IAction;
import de.brod.gui.action.IDialogAction;
import de.brod.gui.shape.Button;
import de.brod.gui.shape.Button.Type;
import de.brod.gui.shape.Frame;
import de.brod.gui.shape.Menu;
import de.brod.gui.shape.MenuItem;
import de.brod.gui.shape.Rectangle;
import de.brod.gui.shape.Sprite;
import de.brod.gui.shape.Text;
import de.brod.tools.StateHandler;

public abstract class GuiRendererView<SPRITE extends Sprite> extends
		GuiView<SPRITE> implements Renderer {

	private class ButtonAction implements IAction {
		public Type type;

		public ButtonAction(Type pType) {
			type = pType;
		}

		@Override
		public void action() {
			buttonPressed(type);
		}

	}

	private static float r = 0;
	private static float g = 0;
	private static float b = 0;

	private int width, height;
	float wd, hg;
	private GuiActivity activity;
	private Sprite root;
	private Texture iconTexture;
	private float fTitleHeight;
	protected StateHandler globalStateHandler;

	protected Hashtable<Button.Type, Button> htTitleButtons = new Hashtable<Button.Type, Button>();

	public GuiRendererView(GuiActivity context) {

		super(context);
		activity = context;

		globalStateHandler = new StateHandler(new File(activity.getFilesDir(),
				"Gui.Settings.xml"));

		// set the color
		int baseColor = globalStateHandler.getAttributeAsInt("baseColor");
		if (baseColor == 1) {
			// blue
			GuiColors.setBackColor("0099CC");
		} else if (baseColor == 2) {
			// lavender
			GuiColors.setBackColor("9933CC");
		} else if (baseColor == 3) {
			// orange
			GuiColors.setBackColor("FF8800");
		} else if (baseColor == 4) {
			// lavender
			GuiColors.setBackColor("CC0000");
		} else if (baseColor == 5) {
			// lavender
			GuiColors.setBackColor("333333");
		} else {
			// default
			// green
			GuiColors.setBackColor("669900");
		}
		GuiColors bACKGROUND = GuiColors.BACKGROUND;
		r = bACKGROUND.red;
		g = bACKGROUND.green;
		b = bACKGROUND.blue;

		// set our renderer to be the main renderer with
		// the current activity context
		setRenderer(this);
		setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
	}

	protected abstract boolean backButtonPressed();

	protected void buttonPressed(Type type) {
		if (type.equals(Type.menu)) {
			openMenu();
		}
	}

	public void showMessage(final String psTitle, final String psMessage,
			final IDialogAction[] sButtons) {
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				AlertDialog.Builder dlgAlert = new AlertDialog.Builder(activity);

				dlgAlert.setMessage(psMessage);
				dlgAlert.setTitle(psTitle);
				if (sButtons.length > 0) {
					dlgAlert.setPositiveButton(sButtons[0].getName(),
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									sButtons[0].action();
									// repaint
									requestRender();
								}
							});
					if (sButtons.length == 2) {
						dlgAlert.setNegativeButton(sButtons[1].getName(),
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										sButtons[1].action();
										// repaint
										requestRender();
									}
								});
					} else if (sButtons.length > 2) {
						dlgAlert.setNeutralButton(sButtons[1].getName(),
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										sButtons[1].action();
										// repaint
										requestRender();
									}
								});
						dlgAlert.setNegativeButton(sButtons[2].getName(),
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										sButtons[2].action();
										// repaint
										requestRender();
									}
								});
					}

				} else {
					dlgAlert.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// make nothing
								}
							});
				}
				dlgAlert.setCancelable(true);
				dlgAlert.create().show();
			}
		});
	}

	Sprite createButton(Hashtable<Type, Button> htTitleButtons, Type pType,
			int i, int maxCount, boolean bTop, Align align) {
		ButtonAction buttonAction = new ButtonAction(pType);
		Button createButton = pType.createButton(i, maxCount, bTop, align,
				buttonAction);
		htTitleButtons.put(pType, createButton);
		return createButton;

	}

	public Activity getActivity() {
		return activity;
	}

	protected abstract List<Type> getButtonsBottom();

	public List<Type> getButtonsTop() {
		ArrayList<Type> arrayList = new ArrayList<Type>();
		arrayList.add(Button.Type.menu);
		return arrayList;
	}

	@Override
	public float getX(MotionEvent event) {
		return (event.getX() * 2f * wd / width - wd);
	}

	@Override
	public float getY(MotionEvent event) {
		return (hg - event.getY() * 2f * hg / height);
	}

	protected void initApplication() {
		applicationStateHandler = new StateHandler(new File(
				activity.getFilesDir(), getApplicationName() + ".xml"));
		root = new Sprite();

		Sprite title = new Sprite();
		root.add(title);
		area = new Sprite();
		root.add(area);
		menu = new Menu();
		root.add(menu);

		// createa a title bar
		Rectangle titleBar = new Rectangle(-Button.maxWidth, Button.maxHeight
				- fTitleHeight, Button.maxWidth * 2, fTitleHeight);
		titleBar.setColor(GuiColors.TITLE_BACK);
		title.add(titleBar);

		// create a title text
		float fMoveLeft = 0.5f;
		// create a title icon
		IAction backAction = null;
		if (showBackButton()) {
			backAction = new IAction() {
				@Override
				public void action() {
					backButtonPressed();
				}

			};
		} else {
			fMoveLeft = 0;
		}
		float fontHeight = fTitleHeight * 0.7f;
		Text textTitle = Text.createText(getApplicationName(), -Button.maxWidth
				+ fTitleHeight * (1.1f + fMoveLeft), Button.maxHeight
				- (fTitleHeight + fontHeight) / 2, fontHeight);

		textTitle.setColor(GuiColors.TITLE_TEXT);
		title.add(textTitle);

		Sprite icon = new Button(iconTexture, fTitleHeight, fTitleHeight,
				"Icon", backAction);
		icon.setPosition(-Button.maxWidth + fTitleHeight * (0.5f + fMoveLeft),
				Button.maxHeight - (fTitleHeight / 2));
		title.add(icon);

		if (fMoveLeft > 0) {
			Button leftButton = Button.Type.left.createButton(-Button.maxWidth
					+ fTitleHeight * (0.25f), Button.maxHeight
					- (fTitleHeight / 2), backAction);
			leftButton.resize(0.7f);
			title.add(leftButton);
		}

		// add the buttons
		List<Button.Type> lstButtonTop = getButtonsTop();
		List<Button.Type> lstButtonBottom = getButtonsBottom();

		// portait mode
		if (width < height) {
			Rectangle titleBar2 = new Rectangle(-Button.maxWidth,
					-Button.maxHeight, Button.maxWidth * 2, fTitleHeight);
			titleBar2.setColor(GuiColors.TITLE_BACK);
			title.add(titleBar2);
			int maxCount = lstButtonBottom.size();
			for (int i = 0; i < maxCount; i++) {
				title.add(createButton(htTitleButtons, lstButtonBottom.get(i),
						i, maxCount, false, Align.CENTER));
			}
		} else {
			lstButtonTop.addAll(lstButtonBottom);
			lstButtonBottom.clear();
		}
		int maxCount = Math.min(lstButtonTop.size(), 5);

		for (int i = 0; i < lstButtonTop.size(); i++) {
			title.add(createButton(htTitleButtons, lstButtonTop.get(i), i,
					maxCount, true, Align.RIGHT));
		}

		lstTitleItems = title.getChildren();
		lstMenuItems = new ArrayList<MenuItem>();
		reload();

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * android.opengl.GLSurfaceView.Renderer#onDrawFrame(javax.microedition.
	 * khronos.opengles.GL10)
	 */
	@Override
	public synchronized void onDrawFrame(GL10 gl) {
		// Clears the screen and depth buffer.
		gl.glClearColor(r, g, b, 1.0f);
		// Clears the screen and depth buffer.
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		// Replace the current matrix with the identity matrix
		gl.glLoadIdentity();
		// Translates 4 units into the screen.
		// gl.glTranslatef(0, 0, -4);

		// Counter-clockwise winding.
		gl.glFrontFace(GL10.GL_CCW);
		// Enable face culling.
		gl.glEnable(GL10.GL_CULL_FACE);
		// What faces to remove with the face culling.
		gl.glCullFace(GL10.GL_BACK);
		// Enabled the vertices buffer for writing and to be used during
		// rendering.
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		// Specifies the location and data format of an array of vertex

		gl.glEnable(GL10.GL_TEXTURE_2D);
		// Enable the texture state
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

		// Draw our scene.
		// for (Sprite sp : lstSprites) {
		// sp.draw(gl);
		// }
		root.draw(gl);

		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

		// Disable the vertices buffer.
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);

		// Disable face culling.
		gl.glDisable(GL10.GL_CULL_FACE);

		processNextStep();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * android.opengl.GLSurfaceView.Renderer#onSurfaceChanged(javax.microedition
	 * .khronos.opengles.GL10, int, int)
	 */
	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		this.width = width;
		this.height = height;

		// Sets the current view port to the new size.
		gl.glViewport(0, 0, width, height);
		// Select the projection matrix
		gl.glMatrixMode(GL10.GL_PROJECTION);
		// Reset the projection matrix
		gl.glLoadIdentity();
		float ratio = width * 1f / height;
		wd = 1;
		hg = 1;
		if (ratio < 1) {
			hg = 1 / ratio;
		} else {
			wd = ratio;
		}
		// Calculate the aspect ratio of the window
		GLU.gluOrtho2D(gl, -wd, wd, -hg, hg);

		// GLU.gluPerspective(gl, 45.0f, (float) width / (float) height, 0.1f,
		// 1000.0f);
		// Select the modelview matrix
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		// Reset the modelview matrix
		gl.glLoadIdentity();

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * android.opengl.GLSurfaceView.Renderer#onSurfaceCreated(javax.microedition
	 * .khronos.opengles.GL10, javax.microedition.khronos.egl.EGLConfig)
	 */
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		// Set the background color to black ( rgba ).
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f);
		// Enable Smooth Shading, default not really needed.
		gl.glShadeModel(GL10.GL_SMOOTH);
		// Depth buffer setup.
		gl.glClearDepthf(1.0f);
		// Enables depth testing.
		gl.glEnable(GL10.GL_DEPTH_TEST);
		// The type of depth testing to do.
		gl.glDepthFunc(GL10.GL_LEQUAL);
		// Really nice perspective calculations.
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);

		// Enable blending using premultiplied alpha.
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);
		// init the textures
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		width = metrics.widthPixels;
		height = metrics.heightPixels;

		// init
		int statusBarHeight = Math.round(48 * metrics.density);
		fTitleHeight = statusBarHeight * 2f / Math.min(height, width);

		Button.init(activity.getAssets(), gl, width, height, fTitleHeight);
		Frame.init(activity.getAssets(), gl, width, height, fTitleHeight);
		Text.init(gl, width, height, fTitleHeight, activity);
		Rectangle.init(gl, fTitleHeight);

		// add the current image
		Bitmap bitmapIcon = Texture.base2image(BitmapFactory.decodeResource(
				activity.getResources(), R.drawable.ic_launcher));
		iconTexture = new Texture(gl, bitmapIcon, 1, 1);
		bitmapIcon.recycle();

		// init the textures
		initTextures(gl, width, height, statusBarHeight);

		initApplication();

	}

	protected abstract boolean showBackButton();

	public void changeGlobalColor(int baseColor) {
		baseColor = baseColor % 6;
		globalStateHandler.setAttibute("baseColor", baseColor);
		activity.restartView();
	}
}
