package de.brod.gui;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.Comparator;

import javax.microedition.khronos.opengles.GL10;

public class GuiQuad implements IGuiQuad {

	private static class GuiQuadComparator implements Comparator<IGuiQuad> {

		@Override
		public int compare(IGuiQuad lhs, IGuiQuad rhs) {
			float keyL = lhs.getXY();
			float keyR = rhs.getXY();
			if (keyL < keyR) {
				return -1;
			}
			if (keyL > keyR) {
				return 1;
			}
			return 0;
		}
	}

	private static class Line {
		float	x, y, dx, dy;

		void set(float x1, float y1, float x2, float y2) {
			x = x1;
			y = y1;
			dx = x2 - x1;
			dy = y2 - y1;
		}

		float getX(float d) {
			return x + dx * d;
		}

		float getY(float d) {
			return y + dy * d;
		}
	}

	static final Comparator<IGuiQuad>	COMPERATOR	= new GuiQuadComparator();
	private static final short[]		_indices	= { 0, 1, 2, 0, 2, 3 };
	private static final int[]			_points		= { 0, 1, 2, 3, 0 };
	private static final float[]		_edges		= { -1, 1, -1, -1, 1, -1,
			1, 1									};

	private float						_x, _y, _width, _height,
			_xy = -12345678;
	private float						_rY, _rotY;
	private int							_iUp;
	private GuiGrid						_grid;
	private float						_r, _g, _b, _a;

	private FloatBuffer					_vertexBuffer;
	private ShortBuffer					_indexBuffer;
	private FloatBuffer[]				_textureBuffer;

	private Line						_line		= new Line();
	private float[]						_range		= new float[4];
	private float[]						_verticles	= new float[4 * 3];
	private boolean						_visible	= true;
	private float						_touchX;
	private float						_touchY;

	protected GuiQuad(GuiGrid grid, float px, float py, float wd, float hg) {
		_grid = grid;

		_x = px;
		_y = py;
		_width = wd / 2;
		_height = hg / 2;

		_r = 1;
		_g = 1;
		_b = 1;
		_a = 1;

		// a float is 4 bytes, therefore we multiply the number if vertices with 4.
		ByteBuffer vbb = ByteBuffer.allocateDirect(3 * 4 * 4);
		vbb.order(ByteOrder.nativeOrder());
		_vertexBuffer = vbb.asFloatBuffer();

		// short is 2 bytes, therefore we multiply the number if vertices with 2.
		ByteBuffer ibb = ByteBuffer.allocateDirect(_indices.length * 2);
		ibb.order(ByteOrder.nativeOrder());
		_indexBuffer = ibb.asShortBuffer();
		_indexBuffer.put(_indices);
		_indexBuffer.position(0);

		// init the texture buffers
		_textureBuffer = new FloatBuffer[2];
		for (int i = 0; i < _textureBuffer.length; i++) {
			ByteBuffer byteBuffer = ByteBuffer.allocateDirect(8 * 4);
			byteBuffer.order(ByteOrder.nativeOrder());
			_textureBuffer[i] = byteBuffer.asFloatBuffer();
		}

		refreshView();
	}

	void refreshView() {
		float xy = _x - _y * 4f;
		if (xy == _xy && _rY == _rotY) {
			return;
		}
		_xy = xy;
		_rY = _rotY;
		int iPos = 0;
		for (int k = 0; k < _edges.length; k++) {
			float x = (_x + _width * _edges[k]);
			k++;
			float y = _y + _height * _edges[k];
			_verticles[iPos] = x;
			iPos++;
			_verticles[iPos] = y;
			iPos += 2;
		}
		_iUp = 0;
		if (_rotY > 0) {
			float dy;
			if (_rotY > 0.5) {
				dy = 1 - _rotY;
				_iUp = 1;
			} else {
				dy = _rotY;
			}
			float r2 = 1 - _rotY;
			for (int i = 0; i <= 3; i += 3) {
				int i1 = i + 1;
				int i2 = i + 6;
				int i3 = i2 + 1;
				_line.set(_verticles[i], _verticles[i1], _verticles[i2],
						_verticles[i3]);
				if (_iUp == 1) {
					_verticles[i] = _line.getX(_rotY);
					_verticles[i2] = _line.getX(r2);
					_verticles[i1] = _line.getY(1 + dy);
					_verticles[i3] = _line.getY(0);
				} else {
					_verticles[i] = _line.getX(_rotY);
					_verticles[i2] = _line.getX(r2);
					_verticles[i1] = _line.getY(0);
					_verticles[i3] = _line.getY(1 + dy);
				}
			}
		}
		_vertexBuffer.position(0);
		_vertexBuffer.put(_verticles);
		_vertexBuffer.position(0);
		// min max X
		_range[0] = _verticles[0];
		_range[1] = _verticles[0];
		for (int i = 3; i <= 9; i += 3) {
			_range[0] = Math.min(_range[0], _verticles[i]);
			_range[1] = Math.max(_range[1], _verticles[i]);
		}
		// min max y
		_range[2] = _verticles[1];
		_range[3] = _verticles[1];
		for (int i = 4; i <= 10; i += 3) {
			_range[2] = Math.min(_range[2], _verticles[i]);
			_range[3] = Math.max(_range[3], _verticles[i]);
		}
	}

	@Override
	public void close() {
		_grid.remove(this);
	}

	@Override
	public void draw(GL10 gl) {

		if (_grid.bindTexture(gl)) {
			gl.glColor4f(_r, _g, _b, _a);
			gl.glVertexPointer(3, GL10.GL_FLOAT, 0, _vertexBuffer);
			gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, _textureBuffer[_iUp]);
			gl.glDrawElements(GL10.GL_TRIANGLES, _indices.length,
					GL10.GL_UNSIGNED_SHORT, _indexBuffer);
		}
	}

	public GuiQuad setGrid(float pfX, float pfY, boolean pbTop) {
		if (pbTop) {
			_grid.fillTexture(pfX, pfY, _textureBuffer[0]);
		} else {
			_grid.fillTexture(pfX, pfY, _textureBuffer[1]);
		}
		return this;
	}

	@Override
	public boolean touches(float eventX, float eventY) {
		if (!_visible) {
			return false;
		}
		_touchX = _x - eventX;
		_touchY = _y - eventY;
		if (eventX < _range[0] || eventX > _range[1]) {
			return false;
		}
		if (eventY < _range[2] || eventY > _range[3]) {
			return false;
		}
		boolean oddNodes = false;
		float x1, y1, x2 = 0, y2 = 0;
		for (int a = 0; a < _points.length; a++) {
			int i = _points[a] * 3;
			x1 = _verticles[i];
			y1 = _verticles[i + 1];
			if (a > 0) {
				if (((y1 < eventY) && (y2 >= eventY)) || (y1 >= eventY)
						&& (y2 < eventY)) {
					if ((eventY - y1) / (y2 - y1) * (x2 - x1) < (eventX - x1)) {
						oddNodes = !oddNodes;
					}
				}
			}
			x2 = x1;
			y2 = y1;
		}
		return oddNodes;
	}

	public boolean isVisible() {
		return _visible;
	}

	public void setVisible(boolean visible) {
		this._visible = visible;
	}

	@Override
	public void moveTo(float eventX, float eventY) {
		_x = Math.max(_width - GuiView._wd,
				Math.min(GuiView._wd - _width, _touchX + eventX));
		_y = Math.max(_height - GuiView._hg,
				Math.min(GuiView._hg - _height, _touchY + eventY));
		refreshView();
	}

	@Override
	public float getXY() {
		return _xy;
	}

}
