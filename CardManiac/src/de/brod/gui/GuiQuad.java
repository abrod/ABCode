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
		float	xStart, yStart, dx, dy;

		void set(float x1, float y1, float x2, float y2) {
			xStart = x1;
			yStart = y1;
			dx = x2 - x1;
			dy = y2 - y1;
		}

		float getX(float d) {
			return xStart + dx * d;
		}

		float getY(float d) {
			return yStart + dy * d;
		}
	}

	static final Comparator<IGuiQuad>	COMPERATOR	= new GuiQuadComparator();
	private static final short[]		_indices	= { 0, 1, 2, 0, 2, 3 };
	private static final int[]			_points		= { 0, 1, 2, 3, 0 };
	private static final float[]		_edges		= { -1, 1, -1, -1, 1, -1,
			1, 1									};

	private float						_x, _y, _width, _height,
			_xy = -12345678, _xyOrder = -12345678;
	private float						_rY, _rotY;
	private int							_iUp, level;
	private GuiGrid						_grid;
	private float[]						rgba		= { 1, 1, 1, 1 };

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
		float xy = _x - _y * 2f;
		if (isPositionChanged(xy)) {
			return;
		}
		initVerticlesAndMembers(xy);
		rotateVerticlesAndSetXYOrder();
		putVerticlesIntoBuffer();
		defineRange();
	}

	private boolean isPositionChanged(float xy) {
		return xy == _xy && _rY == _rotY;
	}

	private void putVerticlesIntoBuffer() {
		_vertexBuffer.position(0);
		_vertexBuffer.put(_verticles);
		_vertexBuffer.position(0);
	}

	private void defineRange() {
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

	private void initVerticlesAndMembers(float xy) {
		_xy = xy;
		_iUp = 0;
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
	}

	private void rotateVerticlesAndSetXYOrder() {
		if (_rotY > 0) {
			float dy;
			_line.set(_verticles[0], _verticles[1], _verticles[6],
					_verticles[7]);
			if (_rotY > 0.5) {
				_xyOrder = -2 - _x - _y;
				dy = 1 - _rotY;
				_iUp = 1;
			} else {
				_xyOrder = _x - _y;
				dy = _rotY;
			}
			_verticles[4 + 7 * _iUp] = _line.getY(1 + dy / 2f);
			_verticles[1 + 9 * _iUp] = _line.getY(-dy / 2f);
			_verticles[0] = _line.getX(dy);
			_verticles[3] = _line.getX(dy);
			_verticles[6] = _line.getX(1 - dy);
			_verticles[9] = _line.getX(1 - dy);
		} else {
			_xyOrder = _x - _y;
		}
	}

	public void setRotationY(float pRotation) {
		_rotY = pRotation;
		refreshView();
	}

	@Override
	public void close() {
		_grid.remove(this);
	}

	@Override
	public void draw(GL10 gl) {

		if (_grid.bindTexture(gl)) {
			gl.glColor4f(rgba[0], rgba[1], rgba[2], rgba[3]);
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

	public GuiQuad setGrid(float pfX, float pfY) {
		_grid.fillTexture(pfX, pfY, _textureBuffer[0]);
		_grid.fillTexture(pfX, pfY, _textureBuffer[1]);
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
		return _xyOrder + level * 1000;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	@Override
	public void setColor(int a, int r, int g, int b) {
		rgba[0] = r / 255f;
		rgba[1] = g / 255f;
		rgba[2] = b / 255f;
		rgba[3] = a / 255f;
	}

	@Override
	public void slideTo(float X, float Y) {
		_x = Math.max(_width - GuiView._wd, Math.min(GuiView._wd - _width, X));
		_y = Math
				.max(_height - GuiView._hg, Math.min(GuiView._hg - _height, Y));
		refreshView();
	}

}
