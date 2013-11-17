/*
 * ******************************************************************************
 * Copyright (c) 2013 Andreas Brod
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * *****************************************************************************
 */
package de.brod.gui;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.KeyEvent;
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
		restartView();
	}

	public void restartView() {
		// create a new instance with this activity
		glSurfaceView = createGuiRendererView();

		setContentView(glSurfaceView);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		System.out.println("KeyCode: " + keyCode);
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			glSurfaceView.openMenu();
			glSurfaceView.requestRender();
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (glSurfaceView.backButtonPressed()) {
				glSurfaceView.requestRender();
				return true;
			}
			// else handle default back
		}
		return super.onKeyDown(keyCode, event);
	}
}
