package de.brod.cardmaniac;

import java.util.List;

import android.os.Bundle;
import de.brod.cardmaniac.Cards52.CardColor;
import de.brod.cardmaniac.Cards52.CardValue;
import de.brod.gui.GuiActivity;
import de.brod.gui.GuiQuad;

public class MainActivity extends GuiActivity {

	@Override
	protected void initActivity(Bundle savedInstanceState) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void createQuads(List<GuiQuad> lstQuads, float wd, float hg,
			int width, int height) {
		Cards52 cards52 = new Cards52();
		lstQuads.add(cards52.createCard(CardColor.spades, CardValue.cA, 0, 0));
	}

	@Override
	public float[] getColorsRGB() {
		return new float[] { 0, 0, 0.3f };
	}

	@Override
	public boolean isThinking() {
		// TODO Auto-generated method stub
		return false;
	}

}
