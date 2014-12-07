package de.brod.opengl;

public class Sprite extends Mesh implements ISprite {

	class Coords {
		float	x, y;
	}

	private Coords	offset	= new Coords();
	private boolean	_bMoving;
	private Grid	grid;
	private int		iGridTexttureId;
	private int		iGridFrame;
	private float[]	gridTextureCoords;

	public Sprite(Grid pGrid, float x, float y, float width, float height) {
		grid = pGrid;
		setSize(width / 2, height / 2);
		setXY(x, y, 0);
	}

	@Override
	public int compareTo(ISprite pAnother) {
		if (!(pAnother instanceof Sprite)) {
			return -1;
		}
		Sprite another = (Sprite) pAnother;
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
	public void moveTo(float eventX, float eventY) {
		float x = eventX;
		float y = getY(eventY, false);
		x = getX(x, y, false);
		setPosition(x + offset.x, y + offset.y, 0);
	}

	@Override
	public Sprite setGrid(int piPosX, int piPosY) {
		iGridFrame = grid.getFrame(piPosX, piPosY);
		iGridTexttureId = grid.getTexttureId();
		gridTextureCoords = grid.getTextureCoords(iGridFrame);
		setTextureCoordinates(iGridFrame, iGridTexttureId, gridTextureCoords);
		return this;
	}

	@Override
	public void mouseDown(float eventX, float eventY) {
		float x = eventX;
		float y = getY(eventY, false);
		x = getX(x, y, false);
		offset.x = _x - x;
		offset.y = _y - y;
		_bMoving = true;
	}

	@Override
	public void mouseUp() {
		offset.x = 0;
		offset.y = 0;
		_bMoving = false;
	}

}