package de.brod.opengl;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

public abstract class GLActivity extends Activity {
	private GLView	glView;
	private Shapes	selectedMeshes	= new Shapes();
	private Shapes	upMeshes		= new Shapes();
	private Button	selectedButton;

	protected abstract void actionDown(Shapes selectedMeshes2);

	protected abstract void actionUp(Shapes selected, Shapes up);

	protected abstract void init(Shapes meshes, float wd, float hg);

	private Shape lastItem(Shapes shapes) {
		if (shapes.size() > 0) {
			return shapes.get(shapes.size() - 1);
		}
		return null;
	}

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
		synchronized (GLActivity.class) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				glView.setTouchedMeshes(x, y, selectedMeshes);
				Shape lastItem = lastItem(selectedMeshes);
				if (lastItem instanceof Button) {
					selectedButton = (Button) lastItem;
					selectedButton.setPressed(true);
					selectedMeshes.clear();
				} else {
					selectedButton = null;
					actionDown(selectedMeshes);
				}
				glView.requestRender();
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				glView.setTouchedMeshes(x, y, upMeshes);
				if (selectedButton != null) {
					selectedButton.setPressed(false);
					if (upMeshes.contains(selectedButton)) {
						selectedButton.doAction();
					}
				} else {
					for (Shape shape : selectedMeshes) {
						upMeshes.remove(shape);
					}
					actionUp(selectedMeshes, upMeshes);
				}
				glView.requestRender();
			} else if (event.getAction() == MotionEvent.ACTION_MOVE) {
				if (selectedButton != null) {
					glView.setTouchedMeshes(x, y, upMeshes);
					selectedButton.setPressed(upMeshes.contains(selectedButton));
				} else {
					for (Shape mesh : selectedMeshes) {
						mesh.moveTo(x, y);
					}
				}
				glView.requestRender();
			}
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
