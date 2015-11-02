package de.brod.gui;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public abstract class GuiActivity extends Activity {
	private GuiView _view;
	private ArrayList<IGuiQuad> lstQuads;

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		// no title
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		// full screen
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		// init the configuration
		initActivity(savedInstanceState);

		// set the view
		_view = new GuiView(this);
		setContentView(_view);
		// ... the view will call the reload all method
	}

	public void reloadAll() {
		// create a list of quads
		lstQuads = new ArrayList<IGuiQuad>();
		createQuads(lstQuads, _view._wd * 2, _view._hg * 2, _view.width, _view.height);

		_view.setQuads(lstQuads);
		requestRender();
	}

	protected abstract void createQuads(List<IGuiQuad> lstQuads, float wd, float hg, int width, int height);

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

	@SuppressWarnings("unchecked")
	public <T> List<T> getQuadsAt(float eventX, float eventY, int iSize, Class<T> pClass) {
		List<T> lstFound = new ArrayList<T>();
		for (int j = lstQuads.size() - 1; j >= 0; j--) {
			IGuiQuad guiQuad = lstQuads.get(j);

			if (guiQuad.touches(eventX, eventY) && pClass.isInstance(guiQuad)) {
				lstFound.add((T) guiQuad);
				if (lstFound.size() >= iSize) {
					break;
				}
			}
		}
		return lstFound;
	}

	@SuppressWarnings("unchecked")
	public <T> T getQuadAt(float eventX, float eventY, Class<T> pClass) {
		for (int j = lstQuads.size() - 1; j >= 0; j--) {
			IGuiQuad guiQuad = lstQuads.get(j);

			if (guiQuad.touches(eventX, eventY) && pClass.isInstance(guiQuad)) {
				return ((T) guiQuad);
			}
		}
		return null;
	}

	public boolean containsSlidingSquares() {
		return _view.containsSlidingSquares();
	}
}
