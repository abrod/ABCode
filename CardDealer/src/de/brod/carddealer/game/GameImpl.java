package de.brod.carddealer.game;

import android.content.res.Resources;
import de.brod.carddealer.CardSet;
import de.brod.opengl.Button;
import de.brod.opengl.ButtonAction;

public abstract class GameImpl extends CardSet implements Game {

	float	wd;
	float	hg;

	GameImpl(Resources resource, int count, float wd, float hg) {
		super(resource, count);

		this.wd = wd;
		this.hg = hg;
	}

	Button createButton(float wd, float hg) {

		float abs = Math.abs(wd - hg);
		float width = abs;
		float height = abs / 2;

		float x = wd - width / 2;
		float y = hg - height / 2;
		return createButton(width, height, x, y, -0.1f, new ButtonAction() {

			@Override
			public void doAction() {
				// make nothing
			}
		});
	}

	@Override
	public void newGame() {
		clearAll();
	}
}
