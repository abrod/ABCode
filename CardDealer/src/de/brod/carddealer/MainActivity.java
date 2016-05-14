package de.brod.carddealer;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

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

	private GLSurfaceView mGLView;
	private GLRenderer renderer;

	private boolean hasGLES20() {
		ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		ConfigurationInfo info = am.getDeviceConfigurationInfo();
		return info.reqGlEsVersion >= 0x20000;
	}

	private void initialize() {
		if (hasGLES20()) {
			mGLView = new GLSurfaceView(this);
			mGLView.setEGLContextClientVersion(2);
			mGLView.setPreserveEGLContextOnPause(true);
			renderer = new GLRenderer();
			mGLView.setRenderer(renderer);
			setContentView(mGLView);
		} else {
			// Time to get a new phone, OpenGL ES 2.0 not supported.
			setContentView(R.layout.activity_main);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setFullScreenFlags();

		initialize();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mGLView.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mGLView.onResume();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			renderer.actionDown(event.getX(), event.getY());
		} else if (event.getAction() == MotionEvent.ACTION_UP) {
		} else if (event.getAction() == MotionEvent.ACTION_MOVE) {
		}

		return true;
	}

	private void setFullScreenFlags() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}
}
