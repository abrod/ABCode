package de.brod.opengl;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Color;

public class OpenGLButton extends Rect {

	private IAction			_action;
	private float			_height, _x, _y;
	private Text<String>	_text;
	private int				_textColorR, _textColorG, _textColorB;

	public OpenGLButton(String psText, float x, float y, float width,
			float height, IAction pAction) {
		super(x, y, width, height, true);
		_x = x;
		_y = y - height * 0.25f;
		_height = height * 0.7f;
		_action = pAction;
		_textColorR = 255;
		_textColorG = 255;
		_textColorB = 255;
		_text = new Text<String>(psText, _x, _y, _height);
		setColor(128, 128, 128, 128);
		setEnabled(true);
	}

	@Override
	public void draw(GL10 gl) {
		super.draw(gl);
		_text.draw(gl);
	}

	public IAction getAction() {
		return _action;
	}

	@Override
	public void setDown(boolean pbDown) {
		if (pbDown) {
			_text.setPosition(_x, _y);
		} else {
			float fAddHeight = _height / 20;
			_text.setPosition(_x - fAddHeight, _y + fAddHeight);
		}
		super.setDown(pbDown);
	}

	@Override
	public void setEnabled(boolean b) {
		if (b) {
			_text.setColor(_textColorR, _textColorG, _textColorB, 255);
		} else {
			_text.setColor(_textColorR, _textColorG, _textColorB, 64);
		}
		super.setEnabled(b);
	}

	public void setTextColor(int color) {
		_textColorR = Color.red(color);
		_textColorG = Color.green(color);
		_textColorB = Color.blue(color);
		setEnabled(isEnabled());
	}
}
