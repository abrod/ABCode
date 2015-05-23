package de.brod.opengl;

public abstract class OpenGLButton extends OpenGLRectangle {

	public OpenGLButton(float x1, float y1, float x2, float y2) {
		super(x1, y1, x2, y2);
	}

	@Override
	protected void initColor() {
		setColor(255, 255, 255, 255);
	}

	public void setPressed(boolean pbPressed) {
		setUp(!pbPressed);
	}

	public abstract void action();

}
