package de.brod.cm;

import java.util.ArrayList;
import java.util.List;

import de.brod.gui.shape.Button;
import de.brod.gui.shape.Sprite;

public class Buttons extends CardContainer {

	private List<Button> lstButtons = new ArrayList<Button>();

	public Buttons(int piId) {
		super(piId, -1, -1, 1, 1);
	}

	@Override
	public void addAllSpritesTo(Sprite sprite) {
		for (Button c : lstButtons) {
			sprite.add(c);
		}
		super.addAllSpritesTo(sprite);
	}

	public void add(Button button) {
		lstButtons.add(button);
	}

	@Override
	public void clear() {
		// make nothing
	}

	@Override
	public void organize() {
		// make nothing
	}

	@Override
	public String getName() {
		return "Buttons";
	}

}
