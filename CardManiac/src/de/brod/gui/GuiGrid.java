package de.brod.gui;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;
import android.util.Log;

public abstract class GuiGrid {

	public static void setView(GuiView guiView) {
		_view = guiView;
	}

	private static GuiView _view;
	private float _xMax, _yMax;
	private int[] _textures;
	List<GuiQuad> _lstGuiQuads;
	private boolean bReload;
	private GuiQuad guiQuad;

	public GuiGrid(float pfCountX, float pfCountY) {
		_lstGuiQuads = new ArrayList<GuiQuad>();
		_xMax = pfCountX;
		_yMax = pfCountY;
		_textures = null;
		bReload = true;
	}

	void remove(GuiQuad guiQuad) {
		_lstGuiQuads.remove(guiQuad);
		if (_lstGuiQuads.size() == 0) {
			bReload = true;
		}
	}

	public boolean bindTexture(GL10 gl) {

		if (bReload) {
			bReload = false;
			if (_textures != null) {
				Log.d("Texture", "Delete " + _textures[0]);
				gl.glDeleteTextures(_textures.length, _textures, 0);
			}
			int width = _view.width;
			int height = _view.height;
			Bitmap bitmap = createBitmap(width, height);
			_textures = new int[] { GuiTexture.bind(gl, bitmap) };
			Log.d("Texture", "Init " + _textures[0]);
		}
		gl.glBindTexture(GL10.GL_TEXTURE_2D, _textures[0]);
		return true;
	}

	protected abstract Bitmap createBitmap(int width, int height);

	public void fillTexture(float pfX, float pfY, FloatBuffer textureBuffer) {
		textureBuffer.position(0);

		float x1 = pfX / _xMax;
		float y1 = pfY / _yMax;
		float x2 = (pfX + 1) / _xMax;
		float y2 = (pfY + 1) / _yMax;

		// bottom left (V1)
		textureBuffer.put(x1);
		textureBuffer.put(y1);
		// top left (V2)
		textureBuffer.put(x1);
		textureBuffer.put(y2);
		// top right (V4)
		textureBuffer.put(x2);
		textureBuffer.put(y2);
		// bottom right (V3)
		textureBuffer.put(x2);
		textureBuffer.put(y1);

		textureBuffer.position(0);

	}

	public void assignQuad(GuiQuad guiQuad) {
		this.guiQuad = guiQuad;
	}

	public GuiQuad getGuiQuad() {
		return guiQuad;
	}

}
