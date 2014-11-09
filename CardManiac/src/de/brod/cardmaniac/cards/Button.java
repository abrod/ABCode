package de.brod.cardmaniac.cards;

import de.brod.opengl.IAction;
import de.brod.opengl.OpenGLButton;
import de.brod.opengl.Rect;

public abstract class Button {

	private OpenGLButton	_buttonRect;

	public Button(CardSet pDeck, float x1, float y1, float x2, float y2,
			final String psText) {

		float f = 1f;
		float wd2 = pDeck.getCardWidth() / 2;
		float hg2 = wd2 * 0.6f;

		float xMin = pDeck.transformX(Math.min(x1, x2)) - wd2;
		float xMax = pDeck.transformX(Math.max(x1, x2)) + wd2;

		float yMin = pDeck.transformY(Math.min(y1, y2)) - hg2;
		float yMax = pDeck.transformY(Math.max(y1, y2)) + hg2;
		_buttonRect = new OpenGLButton(psText, (xMin + xMax) / 2,
				(yMin + yMax) / 2, (xMax - xMin) * f, (yMax - yMin) * f,
				new IAction() {

			@Override
			public String getTitle() {
				return psText;
			}

			@Override
			public void doAction() {
				pressed();
			}
		});
		_buttonRect.setDown(false);
	}

	public abstract void pressed();

	public Rect getRect() {
		return _buttonRect;
	}

	public void setTextColor(int color) {
		_buttonRect.setTextColor(color);
	}

	public void setEnabled(boolean b) {
		_buttonRect.setEnabled(b);
	}

	public boolean touches(float eventX, float eventY) {
		if (_buttonRect.isEnabled()) {
			return _buttonRect.touches(eventX, eventY);
		}
		return false;
	}

	public void performAction() {
		IAction action = _buttonRect.getAction();
		if (action != null) {
			action.doAction();
		}
	}

	public void setDown(boolean b) {
		_buttonRect.setDown(b);
	}

}
