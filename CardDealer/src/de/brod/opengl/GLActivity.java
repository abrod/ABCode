package de.brod.opengl;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

public abstract class GLActivity extends Activity {
	private GLView glView;
	private Shapes selectedMeshes = new Shapes();

	protected abstract void init(Shapes meshes, float wd, float hg);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setFullScreenFlags();

		// create a view
		glView = new GLView(this);
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
			for (Shape mesh : selectedMeshes) {
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
