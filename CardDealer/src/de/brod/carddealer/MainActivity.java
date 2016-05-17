package de.brod.carddealer;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import de.brod.opengl.GLView;

/**
 *
 *
 * Added the following to AndroidManifest.xml
 *
 * <uses-feature android:glEsVersion="0x00020000" android:required="true" />
 *
 * @author Andreas
 *
 */
public class MainActivity extends Activity {

	private GLView glView;

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

		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			glView.actionDown(event.getX(), event.getY());
		} else if (event.getAction() == MotionEvent.ACTION_UP) {
			glView.actionUp(event.getX(), event.getY());
		} else if (event.getAction() == MotionEvent.ACTION_MOVE) {
			glView.actionMove(event.getX(), event.getY());
		}

		return true;
	}

	private void setFullScreenFlags() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}
}
