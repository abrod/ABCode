package de.brod.gui;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public abstract class GuiActivity extends Activity {
	private GuiView				_view;
	private ArrayList<GuiQuad>	lstQuads;

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		// no title
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		// full screen
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		// init the configuration
		initActivity(savedInstanceState);

		// set the view
		_view = new GuiView(this);
		setContentView(_view);
		// ... the view will call the reload all method
	}

	public void reloadAll() {
		// create a list of quads
		lstQuads = new ArrayList<GuiQuad>();
		createQuads(lstQuads, _view._wd * 2, _view._hg * 2, _view.width,
				_view.height);

		_view.setQuads(lstQuads);
		requestRender();
	}

	protected abstract void createQuads(List<GuiQuad> lstQuads, float wd,
			float hg, int width, int height);

	public void sortQuads() {
		_view.sortQuads();
	}

	protected void requestRender() {
		_view.sortQuads();
		_view.requestRender();
	}

	protected abstract void initActivity(Bundle savedInstanceState);

	public abstract float[] getColorsRGB();

	public abstract boolean isThinking();

	public boolean slideSquares(boolean b) {
		return false;
	}

	public abstract boolean actionDown(float eventX, float eventY);

	public abstract boolean actionMove(float eventX, float eventY);

	public abstract boolean actionUp(float eventX, float eventY);

	public void getQuadsAt(List<GuiQuad> lstFound, float eventX, float eventY,
			int iSize) {
		lstFound.clear();
		for (int j = lstQuads.size() - 1; j >= 0; j--) {
			GuiQuad guiQuad = lstQuads.get(j);
			if (guiQuad.touches(eventX, eventY)) {
				lstFound.add(guiQuad);
				if (lstFound.size() >= iSize) {
					break;
				}
			}
		}
	}
}
