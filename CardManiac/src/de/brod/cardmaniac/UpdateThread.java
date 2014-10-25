package de.brod.cardmaniac;

import de.brod.cardmaniac.game.INextMove;
import de.brod.cardmaniac.table.Hand;

public class UpdateThread extends Thread {

	private INextMove	_r;
	private Mover		_mover;

	public UpdateThread(INextMove r, Mover mover) {
		_r = r;
		_mover = mover;
	}

	@Override
	public void run() {
		_mover.waitFor();
		// save the position
		_mover.start();
		if (_r.hasNext()) {
			// organize hands
			for (Hand hand : _mover.getHands()) {
				hand.organize();
			}
			_mover.end(true);
			_mover.saveGame();
			// sort
		}
		_mover.requestRender();
	}
}
