package de.brod.cm;

import de.brod.gui.GuiActivity;
import de.brod.gui.GuiRendererView;

public class CardManiacActivity extends GuiActivity {

	@Override
	protected GuiRendererView<?> createGuiRendererView() {
		return new CardManiacView(this);
	}
}
