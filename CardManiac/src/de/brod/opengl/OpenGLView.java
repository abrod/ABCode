package de.brod.opengl;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

public class OpenGLView extends GLSurfaceView implements GLSurfaceView.Renderer {

	private OpenGLActivity _activity;
	private ArrayList<ISprite<?>> _lstSprites;
	private float _wd, _hg, _dx, _dy;
	private List<Rect> _lstRectangles;
	private Rect rect0;
	public static boolean LANDSCAPE;

	private OpenGLView(Context context) {
		super(context);
	}

	public OpenGLView(OpenGLActivity context) {
		super(context);
		_activity = context;
		setRenderer(this);
		setRenderMode(RENDERMODE_WHEN_DIRTY);
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		synchronized (this) {
			boolean pbRepaint = _activity.onDrawFrame();

			int col = _activity.getColor();
			// gl.glClearColor(Color.red(col) / 255f, Color.green(col) / 255f,
			// Color.blue(col) / 255f, Color.alpha(col) / 255f);
			gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
			gl.glMatrixMode(GL10.GL_MODELVIEW);
			gl.glLoadIdentity();

			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,
					GL10.GL_LINEAR);
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S,
					GL10.GL_CLAMP_TO_EDGE);
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T,
					GL10.GL_CLAMP_TO_EDGE);

			gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
			gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

			rect0.draw(gl);

			for (Rect rect : _lstRectangles) {
				rect.draw(gl);
			}

			for (ISprite<?> sprite : _lstSprites) {
				sprite.draw(gl);
			}

			gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
			gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
			if (pbRepaint) {
				requestRender();
			}
		}
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		// prevent 0 divise
		if (height == 0) {
			height = 1;
		}
		// screenWidth = width;
		// screenHeight = height;
		float ratio = ((float) width) / height;
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		if (ratio > 1) { // Landscape
			LANDSCAPE = true;
			_wd = Math.min(4f / 3f, ratio);
			_hg = 1;
		} else {
			LANDSCAPE = false;
			_wd = 1;
			_hg = Math.min(4 / 3f, 1 / ratio);
		}

		// _wd = 1;
		// _hg = 1;

		_dx = 2f * _wd / width;
		_dy = 2f * _hg / height;

		gl.glOrthof(-_wd, _wd, -_hg, _hg, 1f, -1f);
		gl.glViewport(0, 0, width, height);

		Rect.onSurfaceChanged(_activity, gl, width, height);
		TextGrid.init(gl, width, height, _activity);

		_lstSprites = new ArrayList<ISprite<?>>();
		_lstRectangles = new ArrayList<Rect>();
		_activity.initSprites(gl, _lstSprites, _lstRectangles);

		rect0 = new Rect(0, 0, _wd * 4, _hg * 2, false);
		rect0.setColor(_activity.getColor());
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig confid) {
		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glShadeModel(GL10.GL_SMOOTH);
		gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		gl.glClearDepthf(1.0f);
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
		gl.glEnable(GL10.GL_BLEND);
		gl.glDisable(GL10.GL_DEPTH_TEST);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE,
				/* GL10.GL_REPLACE */GL10.GL_MODULATE);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		synchronized (this) {
			float eventX = event.getX() * _dx - _wd;
			float eventY = _hg - event.getY() * _dy;

			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				if (_activity.actionDown(eventX, eventY)) {
					requestRender();
				}
				return true;
			}

			if (event.getAction() == MotionEvent.ACTION_MOVE) {
				if (_activity.actionMove(eventX, eventY)) {
					requestRender();
				}
				return true;

			}
			if (event.getAction() == MotionEvent.ACTION_UP) {
				if (_activity.actionUp(eventX, eventY)) {
					requestRender();
				}
				return true;
			}
			return true;
		}
	}
}
