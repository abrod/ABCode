package de.brod.opengl;

public class Sprite<E> extends Mesh<E> implements ISprite<E> {

	private E reference;

	private float ox, oy;
	private float _sx, _sy, _ex, _ey, _dx, _dy, _d;

	private boolean _bMoving;

	private Grid<E> grid;

	public Sprite(Grid<E> pGrid, float x, float y, float width, float height) {
		grid = pGrid;
		setSize(width / 2, height / 2);
		setXY(x, y);
	}

	@Override
	public int compareTo(ISprite<E> pAnother) {
		if (!(pAnother instanceof Sprite)) {
			return -1;
		}
		Sprite<E> another = (Sprite<E>) pAnother;
		if (_bMoving != another._bMoving) {
			if (_bMoving) {
				return 1;
			}
			return -1;
		}
		int iComp = _xy - another._xy;
		if (iComp == 0) {
			iComp = _id - another._id;
		}
		return iComp;
	}

	@Override
	public E getReference() {
		return reference;
	}

	@Override
	public boolean isPositionChanged() {
		_ex = _x;
		_ey = _y;
		_dx = _ex - _sx;
		_dy = _ey - _sy;
		_d = Math.min(1, _dx * _dx + _dy * _dy);
		_bMoving = _d > 0.01f;
		return _bMoving;
	}

	@Override
	public void moveTo(float eventX, float eventY) {
		float x = eventX;
		float y = getY(eventY, false);
		x = getX(x, y, false);
		setPosition(x + ox, y + oy);
		savePosition();
	}

	@Override
	public void savePosition() {
		_sx = _x;
		_sy = _y;
	}

	@Override
	public Sprite<E> setGrid(int piPosX, int piPosY) {
		int mFrame = grid.getFrame(piPosX, piPosY);
		setTextureCoordinates(grid.getTexttureId(),
				grid.getTextureCoords(mFrame));
		return this;
	}

	@Override
	public boolean setMovePosition(float f) {
		f = f / _d;
		if (f < 1) {
			setPosition(_sx + _dx * f, _sy + _dy * f);
			return true;
		}
		_d = 0;
		_bMoving = false;
		setPosition(_ex, _ey);
		return false;
	}

	@Override
	public void setOffset(float eventX, float eventY) {
		float x = eventX;
		float y = getY(eventY, false);
		x = getX(x, y, false);
		ox = _x - x;
		oy = _y - y;
	}

	@Override
	public void setReference(E reference) {
		this.reference = reference;
	}

}