package de.brod.opengl;

import javax.microedition.khronos.opengles.GL10;

public class OpenGLButton extends Rect {

	private IAction			_action;
	private boolean			_enabled;
	private float			_height, _x, _y;
	private Text<String>	_text;

	public OpenGLButton(String psText, float x, float y, float width,
			float height, IAction pAction) {
		super(x, y, width, height, true);
		_x = x;
		_y = y - height * 0.22f;
		_height = height * 0.7f;
		_text = new Text<String>(psText, _x, _y, _height);
		_action = pAction;
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

	public boolean isEnabled() {
		return _enabled;
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

	public void setEnabled(boolean b) {
		if (b) {
			setColor(128, 128, 128, 128);
			_text.setColor(255, 255, 255, 255);
		} else {
			setColor(128, 128, 128, 32);
			_text.setColor(255, 255, 255, 64);
		}
		_enabled = b;
	}
}
