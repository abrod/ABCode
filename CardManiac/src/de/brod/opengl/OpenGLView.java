package de.brod.opengl;

import android.annotation.SuppressLint;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class OpenGLView<Square extends OpenGLSquare, Rectangle extends OpenGLRectangle, Button extends OpenGLButton>
        extends GLSurfaceView implements GLSurfaceView.Renderer {

    private OpenGLActivity<Square, Rectangle, Button> a;

    static boolean LANDSCAPE;

    static float _wd;
    static float _hg;
    private float _dx;
    private float _dy;

    public OpenGLView(OpenGLActivity<Square, Rectangle, Button> activity) {
        super(activity);
        a = activity;

        setRenderer(this);
        setRenderMode(RENDERMODE_WHEN_DIRTY);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * android.opengl.GLSurfaceView.Renderer#onSurfaceCreated(javax.
     * microedition.khronos.opengles.GL10, javax.microedition.khronos.
     * egl.EGLConfig)
     */
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

        gl.glEnable(GL10.GL_TEXTURE_2D);
        // Enable Smooth Shading, default not really needed.
        gl.glShadeModel(GL10.GL_SMOOTH);
        // Depth buffer setup.
        gl.glClearDepthf(1.0f);
        // Really nice perspective calculations.
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
        gl.glEnable(GL10.GL_BLEND);
        // Enables depth testing.
        gl.glDisable(GL10.GL_DEPTH_TEST);
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE,
        /* GL10.GL_REPLACE */GL10.GL_MODULATE);

        a.onViewCreate();

        OpenGLTexture.initTextures(gl, a);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * android.opengl.GLSurfaceView.Renderer#onDrawFrame(javax.
     * microedition.khronos.opengles.GL10)
     */
    @Override
    public void onDrawFrame(GL10 gl) {
        synchronized (this) {

            OpenGLText.OpenGLTextTexture.checkTextures(gl);

            boolean slideSquares = a.slideSquares(false);

            float colors[] = a.getColorsRGB();
            if (a.isThinking()) {
                // Set the background color to black ( rgba ).
                gl.glClearColor(colors[0], colors[1], colors[2], 0.9f);
            } else {
                // Set the background color to black ( rgba ).
                gl.glClearColor(colors[0], colors[1], colors[2], 1f);
            }

            // Clears the screen and depth buffer.
            gl.glClear(GL10.GL_COLOR_BUFFER_BIT | //
                    GL10.GL_DEPTH_BUFFER_BIT);

            // Replace the current matrix with the identity matrix
            gl.glLoadIdentity();

            // Counter-clockwise winding.
            gl.glFrontFace(GL10.GL_CCW);
            // Enable face culling.
            gl.glEnable(GL10.GL_CULL_FACE);
            // What faces to remove with the face culling.
            gl.glCullFace(GL10.GL_BACK);
            // Enabled the vertex buffer for writing and to be used during rendering.
            gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
            gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

            // Translates 4 units into the screen.
            // gl.glTranslatef(0, 0, -4);
            gl.glColor4x(255, 255, 255, 255);
            // draw the rectangles
            a.drawItems(gl);

            // Disable the vertices buffer.
            gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
            gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

            // Disable face culling.
            gl.glDisable(GL10.GL_CULL_FACE);

            if (slideSquares) {
                requestRender();
            }
        }

    }

    /*
     * (non-Javadoc)
     *
     * @see
     * android.opengl.GLSurfaceView.Renderer#onSurfaceChanged(javax.
     * microedition.khronos.opengles.GL10, int, int)
     */
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

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
            LANDSCAPE = true;
            _wd = ratio;
            _hg = 1;
        } else {
            LANDSCAPE = false;
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

        a.refreshView(_wd, _hg);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        synchronized (this) {
            float eventX = event.getX() * _dx - _wd;
            float eventY = _hg - event.getY() * _dy;

            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (a.actionDown(eventX, eventY)) {
                    requestRender();
                }
                return true;
            }

            if (event.getAction() == MotionEvent.ACTION_MOVE) {
                if (a.actionMove(eventX, eventY)) {
                    requestRender();
                }
                return true;

            }
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (a.actionUp(eventX, eventY)) {
                    requestRender();
                }
                return true;
            }

            return true;
        }
    }
}
