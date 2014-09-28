package de.brod.cardmaniac.table;

import de.brod.opengl.IAction;
import de.brod.opengl.OpenGLButton;

public class Button {

	private float[] _pX;
	private float[] _pY;
	private OpenGLButton _buttonRect;

	public Button(Deck pDeck, float x1, float y1, float x2, float y2,
			String psText, IAction pAction) {
		_pX = new float[] { x1, x2 - x1,
				pDeck.getX(Math.min(x1, x2)) - pDeck.getCardWidth() / 2,
				pDeck.getX(Math.max(x1, x2)) + pDeck.getCardWidth() / 2 };
		_pY = new float[] { y1, y2 - y1,
				pDeck.getY(Math.min(y1, y2)) - pDeck.getCardHeight() / 3,
				pDeck.getY(Math.max(y1, y2)) + pDeck.getCardHeight() / 3 };
		float f = 1f;
		_buttonRect = new OpenGLButton((_pX[2] + _pX[3]) / 2,
				(_pY[2] + _pY[3]) / 2, (_pX[3] - _pX[2]) * f, (_pY[3] - _pY[2])
						* f, pAction);
	}

	public OpenGLButton getButton() {
		return _buttonRect;
	}

}
