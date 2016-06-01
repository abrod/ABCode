package de.brod.opengl;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLU;

public class GLView extends GLSurfaceView implements Renderer {

	private Shapes		meshes	= new Shapes();

	protected float		wd, hg, width, height;

	private GLActivity	activity;

	private float[]		color	= { 0 / 255f, 35 / 255f, 102 / 255f };

	public GLView(GLActivity activity) {
		super(activity);
		this.activity = activity;

		// set config for RGB data, alpha, depth, stencil.
		// ... it must be called before setRenderer(Renderer) is called
		setEGLConfigChooser(8, 8, 8, 8, 16, 0);

		// set the render
		setRenderer(this);
		setRenderMode(RENDERMODE_WHEN_DIRTY);
	}

	public void addMesh(ShapeBase mesh) {
		meshes.add(mesh);
	}

	public Shapes getMeshes() {
		return meshes;
	}

	public float getX(float x) {
		float a = x / width * wd * 2 - wd;
		return a;
	}

	public float getY(float y) {
		float a = hg - y / height * hg * 2;
		return a;
	}

	/*
	 * (non-Javadoc)
	 * @see android.opengl.GLSurfaceView.Renderer#onDrawFrame(javax.
	 * microedition.khronos.opengles.GL10)
	 */
	@Override
	public void onDrawFrame(GL10 gl) {
		// Clear color and depth buffer.
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		gl.glClearColor(color[0], color[1], color[2], 1);
		// Reset the matrix
		gl.glLoadIdentity();

		// Enable face culling.
		gl.glEnable(GL10.GL_CULL_FACE);

		// Enabled the vertices buffer for writing and to be used during
		// rendering.
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

		meshes.sort();

		meshes.draw(gl);

		// Disable the vertices buffer.
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		// Disable face culling.
		gl.glDisable(GL10.GL_CULL_FACE);

	}

	/*
	 * (non-Javadoc)
	 * @see android.opengl.GLSurfaceView.Renderer#onSurfaceChanged(javax.
	 * microedition.khronos.opengles.GL10, int, int)
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
		float min = Math.min(width, height);
		wd = width / min;
		hg = height / min;
		// Calculate the aspect ratio of the window
		GLU.gluOrtho2D(gl, -wd, wd, -hg, hg);
		// Select the modelview matrix
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		// Reset the modelview matrix
		gl.glLoadIdentity();

		// remove old meshes
		meshes.clear();
		activity.init(meshes, wd, hg);
	}

	/*
	 * (non-Javadoc)
	 * @see android.opengl.GLSurfaceView.Renderer#onSurfaceCreated(javax.
	 * microedition.khronos.opengles.GL10, javax.microedition.khronos.
	 * egl.EGLConfig)
	 */
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		// Set the background color to black ( rgba ).
		// gl.glClearColor(0 / 255f, 35 / 255f, 102 / 255f, 1);
		// Enable Smooth Shading, default not really needed.
		gl.glShadeModel(GL10.GL_SMOOTH);
		// Depth buffer setup.
		gl.glClearDepthf(1.0f);
		// Enables depth testing.
		// gl.glEnable(GL10.GL_DEPTH_TEST);
		// The type of depth testing to do.
		// gl.glDepthFunc(GL10.GL_LEQUAL);
		// Really nice perspective calculations.
		// gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);

		// Counter-clockwise winding.
		gl.glFrontFace(GL10.GL_CCW);
		// What faces to remove with the face culling.
		gl.glCullFace(GL10.GL_BACK);

	}

	public void setTouchedMeshes(float x, float y, Shapes selectedMeshes) {
		selectedMeshes.clear();
		for (Shape mesh : meshes) {
			if (mesh.touch(x, y)) {
				selectedMeshes.add(mesh);
			}
		}
	}
}
