package de.brod.opengl;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Rect;
import de.brod.opengl.TextGrid.CharType;

public class Text<E> extends Mesh<E> implements ISprite<E> {

	private char	_cText;
	private float	_height;
	private Text<E>	_next;
	private float	_xCenter;
	private float	_yCenter;
	private float	fLeft;
	private float	fMeasureText;
	private float	offsetY;

	private E		reference;
	private float	w;

	public Text(String psText, float x, float y, float height) {
		this(psText, x, y, height, true);
	}

	private Text(String psText, float x, float y, float height,
			boolean pbInitText) {
		_height = height;
		_next = null;
		if (psText.length() == 0) {
			_cText = ' ';
		} else {
			_cText = psText.charAt(0);
			if (psText.length() > 1) {
				_next = new Text<E>(psText.substring(1), x, y, _height, false);
			}
		}

		if (pbInitText) {
			TextGrid.initText(this);
			setPosition(x, y, 0);
		}
	}

	@Override
	public int compareTo(ISprite<E> pAnother) {
		if (!(pAnother instanceof Text)) {
			return -1;
		}
		Text<E> another = (Text<E>) pAnother;
		int iComp = _xy - another._xy;
		if (iComp == 0) {
			iComp = _id - another._id;
		}
		return iComp;
	}

	@Override
	public void draw(GL10 gl) {
		super.draw(gl);
		if (_next != null) {
			_next.draw(gl);
		}
	}

	public String getChar() {
		return String.valueOf(_cText);
	}

	public Text<E> getNext() {
		return _next;
	}

	@Override
	public E getReference() {
		// TODO Auto-generated method stub
		return reference;
	}

	public float getTextWidth() {
		if (_next != null) {
			return _next.getTextWidth() + fMeasureText;
		}
		return fMeasureText;
	}

	public void init() {
		TextGrid.initText(this);
		setPosition(_xCenter, _yCenter, 0);
	}

	public void init(CharType charType) {
		float fact = _height / TextGrid.txtHeightBMP;
		float fCorrect = 1.2f;
		fMeasureText = charType.measureText * fact * fCorrect;

		Rect bounds = charType.bounds;
		fLeft = bounds.left * fact * fCorrect;
		offsetY = (-bounds.bottom + bounds.height() / 2f) * fact;
		float h = bounds.height() * fact / 2;
		w = bounds.width() * fact / 2 * fCorrect;
		setSize(w, h);
		setTextureCoordinates(charType.character, TextGrid.getTextureId(),
				charType.getTextureCoordinates());
	}

	@Override
	public boolean isPositionChanged() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void moveTo(float eventX, float eventY) {
		// TODO Auto-generated method stub

	}

	@Override
	public void savePosition() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setColor(int pr, int pg, int pb, int pa) {
		super.setColor(pr, pg, pb, pa);
		if (_next != null) {
			_next.setColor(pr, pg, pb, pa);
		}
	}

	@Override
	public ISprite<E> setGrid(int piPosX, int piPosY) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean setMovePosition(float f) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setOffset(float eventX, float eventY) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setPosition(float pX, float py, float rotX) {
		_xCenter = pX;
		_yCenter = py;

		float px = pX - getTextWidth() / 2;
		setPositionLeft(px, py);
		Text<E> n = _next;
		float ix = px + fMeasureText;
		while (n != null) {
			n.setPositionLeft(ix, py);
			ix += n.fMeasureText;
			n = n._next;
		}
	}

	private void setPositionLeft(float px, float py) {
		setXY(px + fLeft + w, py + offsetY, 0);
	}

	@Override
	public void setReference(E rect) {
		reference = rect;
	}

	public void setSize2(float width, float height) {
		float wd = getTextWidth() / width;
		if (wd > 0) {
			_height = height / wd;
			Text<E> next = _next;
			while (next != null) {
				next._height = _height;
				next = next._next;
			}
			TextGrid.initText(this);
			setPosition(_xCenter, _yCenter, 0);
		}
	}

}