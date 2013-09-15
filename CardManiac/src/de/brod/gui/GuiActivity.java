package de.brod.gui;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public abstract class GuiActivity extends Activity {

	private GuiRendererView<?> glSurfaceView;

	protected abstract GuiRendererView<?> createGuiRendererView();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// requesting to turn the title OFF
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		// making it full screen
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		// Initiate the Open GL view and
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);

		// create an instance with this activity
		glSurfaceView = createGuiRendererView();

		setContentView(glSurfaceView);
	}

}
