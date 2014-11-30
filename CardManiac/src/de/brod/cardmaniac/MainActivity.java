package de.brod.cardmaniac;

import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Color;
import de.brod.cardmaniac.card.Hand;
import de.brod.cardmaniac.game.FreeCell;
import de.brod.cardmaniac.game.Game;
import de.brod.opengl.IAction;
import de.brod.opengl.ISprite;
import de.brod.opengl.OpenGLActivity;
import de.brod.opengl.Rect;

public class MainActivity extends OpenGLActivity {

	private Game<?>	_game;
	private int		_color;

	@Override
	public boolean actionDown(float eventX, float eventY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean actionMove(float eventX, float eventY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean actionUp(float eventX, float eventY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void fillMenuActions(List<IAction> plstMenuActions) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getColor() {
		return _color;
	}

	@Override
	public void initSprites(GL10 gl, List<ISprite<?>> lstSprites,
			List<Rect> lstRectangles) {
		_color = Color.argb(255, 0, 102, 0);

		_game = new FreeCell();
		_game.initCardSet(gl);

		_game.initHands();

		_game.clearHands();
		_game.newGame();

		List<Hand<?>> lstHand = _game.getHands();
		for (Hand<?> hand : lstHand) {
			hand.fillSprites(lstSprites);
			lstRectangles.add(hand.getRect());
		}

	}

	@Override
	public boolean onDrawFrame() {
		// TODO Auto-generated method stub
		return false;
	}

}
