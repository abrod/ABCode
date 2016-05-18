package de.brod.opengl;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

public abstract class GLActivity extends Activity {
	private GLView glView;
	private Meshes selectedMeshes = new Meshes();

	public void addMesh(Mesh mesh) {
		glView.addMesh(mesh);
	}

	public Meshes getMeshes() {
		return glView.getMeshes();
	}

	protected abstract void init();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setFullScreenFlags();

		// create a view
		glView = new GLView(this);
		init();
		setContentView(glView);
	}

	@Override
	protected void onPause() {
		super.onPause();
		glView.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		glView.onResume();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x = glView.getX(event.getX());
		float y = glView.getY(event.getY());
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			glView.setTouchedMeshes(x, y, selectedMeshes);
			glView.requestRender();
		} else if (event.getAction() == MotionEvent.ACTION_UP) {
			glView.requestRender();
		} else if (event.getAction() == MotionEvent.ACTION_MOVE) {
			for (Mesh mesh : selectedMeshes) {
				mesh.moveTo(x, y, 0);
			}
			glView.requestRender();
		}
		return true;
	}

	public void requestRender() {
		glView.requestRender();
	}

	private void setFullScreenFlags() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}
}
