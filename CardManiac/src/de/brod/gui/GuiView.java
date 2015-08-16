package de.brod.gui;

import java.util.Collections;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.annotation.SuppressLint;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

public class GuiView extends GLSurfaceView implements GLSurfaceView.Renderer {

	private List<IGuiQuad>	_lstQuads;
	private GuiActivity		_context;

	static float			_wd, _hg, _dx, _dy;
	int						width, height;

	public GuiView(GuiActivity context) {
		super(context);
		_context = context;
		setEGLConfigChooser(8, 8, 8, 8, 16, 0);

		setRenderer(this);
		setRenderMode(RENDERMODE_WHEN_DIRTY);

		GuiGrid.setView(this);

	}

	public void setQuads(List<IGuiQuad> plstQuads) {
		if (_lstQuads != null) {
			// close all old quads
			for (IGuiQuad guiQuad : _lstQuads) {
				guiQuad.close();
			}
		}
		// set the new Quads
		_lstQuads = plstQuads;
	}

	@Override
	public void onDrawFrame(GL10 pGL10) {
		synchronized (this) {

			boolean slideSquares = _context.slideSquares(false);

			float colors[] = _context.getColorsRGB();
			if (_context.isThinking()) {
				// Set the background color to black ( rgba ).
				pGL10.glClearColor(colors[0], colors[1], colors[2], 0.9f);
			} else {
				// Set the background color to black ( rgba ).
				pGL10.glClearColor(colors[0], colors[1], colors[2], 1f);
			}

			// Clears the screen and depth buffer.
			pGL10.glClear(GL10.GL_COLOR_BUFFER_BIT | //
					GL10.GL_DEPTH_BUFFER_BIT);

			// Replace the current matrix with the identity matrix
			pGL10.glLoadIdentity();

			// Counter-clockwise winding.
			pGL10.glFrontFace(GL10.GL_CCW);
			// Enable face culling.
			pGL10.glEnable(GL10.GL_CULL_FACE);
			// What faces to remove with the face culling.
			pGL10.glCullFace(GL10.GL_BACK);
			// Enabled the vertex buffer for writing and to be used during rendering.
			pGL10.glEnableClientState(GL10.GL_VERTEX_ARRAY);
			pGL10.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

			// Translates 4 units into the screen.
			// gl.glTranslatef(0, 0, -4);
			pGL10.glColor4x(255, 255, 255, 255);

			// draw the rectangles
			for (IGuiQuad quad : _lstQuads) {
				quad.draw(pGL10);
			}

			// Disable the vertices buffer.
			pGL10.glDisableClientState(GL10.GL_VERTEX_ARRAY);
			pGL10.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

			// Disable face culling.
			pGL10.glDisable(GL10.GL_CULL_FACE);

			if (slideSquares) {
				requestRender();
			}
		}
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int piWidth, int piHeight) {

		width = piWidth;
		height = piHeight;

		// prevent 0 divise
		if (height == 0) {
			height = 1;
		}
		// screenWidth = width;
		// screenHeight = height;
		float ratio = ((float) width) / height;

		// Sets the current view port to the new size.
		gl.glViewport(0, 0, width, height);
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		if (ratio > 1) { // Landscape
			_wd = ratio;
			_hg = 1;
		} else {
			_wd = 1;
			ratio = 1 / ratio;
			_hg = ratio;
		}

		// _wd = 1f;
		// _hg = 1.5f;
		_dx = 2f * _wd / width;
		_dy = 2f * _hg / height;

		gl.glOrthof(-_wd, _wd, -_hg, _hg, 1f, -1f);

		// Select the projection matrix
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		// Reset the projection matrix
		gl.glLoadIdentity();

		_context.reloadAll();
	}

	@Override
	public void onSurfaceCreated(GL10 pGL10, EGLConfig pConfig) {

		pGL10.glEnable(GL10.GL_TEXTURE_2D);
		// Enable Smooth Shading, default not really needed.
		pGL10.glShadeModel(GL10.GL_SMOOTH);
		// Depth buffer setup.
		pGL10.glClearDepthf(1.0f);
		// Really nice perspective calculations.
		pGL10.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
		pGL10.glEnable(GL10.GL_BLEND);
		// Enables depth testing.
		pGL10.glDisable(GL10.GL_DEPTH_TEST);
		pGL10.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		pGL10.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE,
		/* GL10.GL_REPLACE */GL10.GL_MODULATE);

	}

	public void sortQuads() {
		if (_lstQuads != null) {
			Collections.sort(_lstQuads, GuiQuad.COMPERATOR);
		}
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		synchronized (this) {
			float eventX = event.getX() * _dx - _wd;
			float eventY = _hg - event.getY() * _dy;

			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				if (_context.actionDown(eventX, eventY)) {
					requestRender();
				}
				return true;
			}

			if (event.getAction() == MotionEvent.ACTION_MOVE) {
				if (_context.actionMove(eventX, eventY)) {
					requestRender();
				}
				return true;

			}
			if (event.getAction() == MotionEvent.ACTION_UP) {
				if (_context.actionUp(eventX, eventY)) {
					requestRender();
				}
				return true;
			}

			return true;
		}
	}

}
