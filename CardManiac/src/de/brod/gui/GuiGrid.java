package de.brod.gui;

import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;

public abstract class GuiGrid {

	private static GuiView	_view;
	private float			_xMax, _yMax;
	private int[]			textures;

	public GuiGrid(float pfCountX, float pfCountY) {
		_xMax = pfCountX;
		_yMax = pfCountY;
		textures = null;
	}

	void remove(GuiQuad guiQuad) {
		// TODO Auto-generated method stub

	}

	public GuiQuad createQuad(float pfX, float pfY, float px, float py,
			float wd, float hg) {
		GuiQuad guiQuad = new GuiQuad(this, px, py, wd, hg);
		guiQuad.setGrid(pfX, pfY, true);
		guiQuad.setGrid(pfX, pfY, false);
		return guiQuad;
	}

	public void bindTexture(GL10 gl) {
		if (textures == null) {
			int width = _view.width;
			int height = _view.height;
			Bitmap bitmap = createBitmap(width, height);

			textures = new int[] { GuiTexture.bind(gl, bitmap) };
		}
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);
	}

	protected abstract Bitmap createBitmap(int width, int height);

	public void fillTexture(float pfX, float pfY, FloatBuffer textureBuffer) {
		textureBuffer.position(0);

		float x1 = pfX / _xMax;
		float y1 = pfY / _yMax;
		float x2 = (pfX + 1) / _xMax;
		float y2 = (pfY + 1) / _yMax;

		// bottom left  (V1)
		textureBuffer.put(x1);
		textureBuffer.put(y1);
		// top left     (V2)
		textureBuffer.put(x1);
		textureBuffer.put(y2);
		// top right    (V4)
		textureBuffer.put(x2);
		textureBuffer.put(y2);
		// bottom right (V3)
		textureBuffer.put(x2);
		textureBuffer.put(y1);

		textureBuffer.position(0);

	}

	public static void setView(GuiView guiView) {
		_view = guiView;
	}
}
