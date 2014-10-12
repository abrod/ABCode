package de.brod.opengl;

public class Sprite<E> extends Mesh<E> implements ISprite<E> {

	class Coords {
		float			x, y;
		private int		iGridFrame;
		private float[]	gridTextureCoords;
	}

	private E		reference;

	private Coords	offset	= new Coords();
	private Coords	start	= new Coords();
	private Coords	end		= new Coords();
	private Coords	delta	= new Coords();
	private double	_fMoveLength;
	private boolean	_bMoving, _bChangeSide;
	private Grid<E>	grid;
	private int		iGridTexttureId;
	private int		iGridFrame;
	private float[]	gridTextureCoords;

	public Sprite(Grid<E> pGrid, float x, float y, float width, float height) {
		grid = pGrid;
		setSize(width / 2, height / 2);
		setXY(x, y, 0);
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
		end.x = _x;
		end.y = _y;
		end.iGridFrame = iGridFrame;

		delta.x = end.x - start.x;
		delta.y = end.y - start.y;

		_fMoveLength = Math.min(1, delta.x * delta.x + delta.y * delta.y);

		_bChangeSide = start.iGridFrame != end.iGridFrame;
		if (_bChangeSide) {
			end.gridTextureCoords = gridTextureCoords;
			_bMoving = true;
		} else {
			_bMoving = _fMoveLength > 0.01f;
			if (_bMoving) {
				_fMoveLength = Math.sqrt(_fMoveLength);
			}
		}

		if (_bMoving && _fMoveLength < 0.5f) {
			_fMoveLength = 0.5f;
		}
		return _bMoving;
	}

	@Override
	public void moveTo(float eventX, float eventY) {
		float x = eventX;
		float y = getY(eventY, false);
		x = getX(x, y, false);
		setPosition(x + offset.x, y + offset.y, 0);
		savePosition();
	}

	@Override
	public void savePosition() {
		start.x = _x;
		start.y = _y;
		start.iGridFrame = iGridFrame;
		start.gridTextureCoords = gridTextureCoords;
	}

	@Override
	public Sprite<E> setGrid(int piPosX, int piPosY) {
		iGridFrame = grid.getFrame(piPosX, piPosY);
		iGridTexttureId = grid.getTexttureId();
		gridTextureCoords = grid.getTextureCoords(iGridFrame);
		setTextureCoordinates(iGridFrame, iGridTexttureId, gridTextureCoords);
		return this;
	}

	@Override
	public boolean setMovePosition(float pf) {
		float f = (float) (pf / _fMoveLength);
		float rotX;
		if (_bChangeSide) {
			if (f < 0.5f) {
				rotX = f;
				setTextureCoordinates(start.iGridFrame, iGridTexttureId,
						start.gridTextureCoords);
			} else {
				rotX = 1 - f;
				setTextureCoordinates(end.iGridFrame, iGridTexttureId,
						end.gridTextureCoords);
			}
		} else {
			rotX = 0;
		}
		if (f < 1) {
			setPosition(start.x + delta.x * f, start.y + delta.y * f, rotX);
			return true;
		}
		_fMoveLength = 0;
		_bMoving = false;
		setPosition(end.x, end.y, 0);
		return false;
	}

	@Override
	public void setOffset(float eventX, float eventY) {
		float x = eventX;
		float y = getY(eventY, false);
		x = getX(x, y, false);
		offset.x = _x - x;
		offset.y = _y - y;
	}

	@Override
	public void setReference(E reference) {
		this.reference = reference;
	}

}