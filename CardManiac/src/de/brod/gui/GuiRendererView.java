package de.brod.gui;

import java.io.File;
import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import de.brod.gui.shape.Button;
import de.brod.gui.shape.Menu;
import de.brod.gui.shape.MenuItem;
import de.brod.gui.shape.Rectangle;
import de.brod.gui.shape.Sprite;
import de.brod.gui.shape.Text;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLU;
import android.util.DisplayMetrics;
import android.view.MotionEvent;

public abstract class GuiRendererView<SPRITE extends Sprite> extends
		GuiView<SPRITE> implements Renderer {

	private float width, height, wd, hg;
	private Context activity;
	private Sprite root;

	public GuiRendererView(Context context) {
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
		int width = metrics.widthPixels;
		int height = metrics.heightPixels;

		// init
		int statusBarHeight = Math.round(48 * metrics.density);
		float fTitleHeight = statusBarHeight * 2f / Math.min(height, width);

		Button.init(activity.getAssets(), gl, width, height, fTitleHeight);
		Text.init(gl, width, height, fTitleHeight, activity);
		Rectangle.init(gl, fTitleHeight);

		initTextures(gl, width, height, statusBarHeight);

		stateHandler = new StateHandler(new File(activity.getFilesDir(),
				getApplicationName() + ".conf.xml"));

		root = new Sprite();
		Sprite title = new Sprite();
		root.add(title);
		area = new Sprite();
		root.add(area);
		menu = new Menu();
		root.add(menu);

		Rectangle titleBar = new Rectangle(-Button.maxWidth, Button.maxHeight
				- fTitleHeight, Button.maxWidth * 2, fTitleHeight);
		titleBar.setColor(Color.argb(128, 0, 0, 0));
		title.add(titleBar);

		float fontHeight = fTitleHeight * 0.7f;
		Text textHallo = Text.createText(getApplicationName(), -Button.maxWidth
				+ fTitleHeight / 2, Button.maxHeight
				- (fontHeight + (fTitleHeight - fontHeight) / 2), fontHeight);

		textHallo.setColor(Color.GREEN);
		title.add(textHallo);

		// add the buttons
		title.add(Button.Type.menu.createButton(0, 5, true, Align.RIGHT,
				new IAction() {
					@Override
					public void action() {
						openMenu();
					}
				}));
		title.add(Button.Type.people.createButton(1, 5, true, Align.RIGHT,
				new IAction() {
					@Override
					public void action() {
					}
				}));
		lstButtons = title.getChildren();
		lstMenuItems = new ArrayList<MenuItem>();
		reload();
	}

}
