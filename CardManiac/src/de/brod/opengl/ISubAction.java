package de.brod.opengl;

public abstract class ISubAction implements IAction {

	public abstract IAction[] getSubItems();

	@Override
	public void doAction() {
		// no action
	}

}
