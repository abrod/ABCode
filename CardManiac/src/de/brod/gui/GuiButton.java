package de.brod.gui;

public class GuiButton extends GuiRectangle {

	private IAction	action;

	public GuiButton(float x, float y, float pfwidth, float pfheight,
			IAction pAction) {
		super(x, y, pfwidth, pfheight, pAction.getTitle());
		action = pAction;
	}

	public void doAction() {
		action.doAction();
	}
}
