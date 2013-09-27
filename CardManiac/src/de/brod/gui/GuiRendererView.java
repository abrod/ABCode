package de.brod.gui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLU;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import de.brod.cm.R;
import de.brod.gui.shape.Button;
import de.brod.gui.shape.Button.Type;
import de.brod.gui.shape.Menu;
import de.brod.gui.shape.MenuItem;
import de.brod.gui.shape.Rectangle;
import de.brod.gui.shape.Sprite;
import de.brod.gui.shape.Text;

public abstract class GuiRendererView<SPRITE extends Sprite> extends
		GuiView<SPRITE> implements Renderer {

	private int width, height;
	float wd, hg;
	private Activity activity;
	private Sprite root;
	private Texture iconTexture;
	private float fTitleHeight;

	public Activity getActivity() {
		return activity;
	}

	public GuiRendererView(Activity context) {
		super(context);
		activity = context;
		// set our renderer to be the main renderer with
		// the current activity context
		setRenderer(this);
		setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
	}

	@Override
	public float getX(MotionEvent event) {
		return (event.getX() * 2f * wd / width - wd);
	}

	@Override
	public float getY(MotionEvent event) {
		return (hg - event.getY() * 2f * hg / height);
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
		gl.glClearColor(0.1f, 0.4f, 0.2f, 1.0f);
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
		Text.init(gl, width, height, fTitleHeight, activity);
		Rectangle.init(gl, fTitleHeight);

		// add the current image
		Bitmap bitmap = Texture.base2image(BitmapFactory.decodeResource(
				activity.getResources(), R.drawable.ic_launcher));
		iconTexture = new Texture(gl, bitmap, 1, 1);
		bitmap.recycle();

		// init the textures
		initTextures(gl, width, height, statusBarHeight);

		initApplication();

	}

	protected void initApplication() {

		stateHandler = new StateHandler(new File(activity.getFilesDir(),
				getApplicationName() + ".conf.xml"));

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
		titleBar.setColor(Color.argb(128, 0, 0, 0));
		title.add(titleBar);

		// create a title text
		float fMoveLeft = 0.5f;
		float fontHeight = fTitleHeight * 0.7f;
		Text textTitle = Text.createText(getApplicationName(), -Button.maxWidth
				+ fTitleHeight * (1.1f + fMoveLeft), Button.maxHeight
				- (fTitleHeight + fontHeight) / 2, fontHeight);

		textTitle.setColor(Color.WHITE);
		title.add(textTitle);

		// create a title icon
		IAction backAction = new IAction() {
			@Override
			public void action() {
				// TODO Auto-generated method stub

			}
		};
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
			titleBar2.setColor(Color.argb(128, 0, 0, 0));
			title.add(titleBar2);
			int maxCount = lstButtonBottom.size();
			for (int i = 0; i < maxCount; i++) {
				final Type type = lstButtonBottom.get(i);
				IAction action = new IAction() {
					@Override
					public void action() {
						buttonPressed(type);
					}
				};
				title.add(type.createButton(i, maxCount, false, Align.CENTER,
						action));
			}

		} else {
			lstButtonTop.addAll(lstButtonBottom);
			lstButtonBottom.clear();
		}
		int maxCount = Math.min(lstButtonTop.size(), 5);

		for (int i = 0; i < lstButtonTop.size(); i++) {
			final Type type = lstButtonTop.get(i);
			IAction action = new IAction() {
				@Override
				public void action() {
					buttonPressed(type);
				}
			};
			title.add(type.createButton(i, maxCount, true, Align.RIGHT, action));
		}

		lstButtons = title.getChildren();
		lstMenuItems = new ArrayList<MenuItem>();
		reload();

	}

	protected abstract List<Type> getButtonsBottom();

	protected void buttonPressed(Type type) {
		if (type.equals(Type.menu)) {
			openMenu();
		}
	}

	public List<Type> getButtonsTop() {
		ArrayList<Type> arrayList = new ArrayList<Type>();
		arrayList.add(Button.Type.menu);
		return arrayList;
	}

}
