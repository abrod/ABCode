package de.brod.cardmaniac;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import de.brod.cardmaniac.Cards52.CardColor;
import de.brod.cardmaniac.Cards52.CardValue;
import de.brod.gui.GuiActivity;
import de.brod.gui.GuiQuad;

public class MainActivity extends GuiActivity<Card> {

	private List<Card>	_lstActionCards	= new ArrayList<Card>();

	@Override
	protected void initActivity(Bundle savedInstanceState) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void createQuads(List<Card> lstQuads, float wd, float hg,
			int width, int height) {
		Cards52 cards52 = new Cards52();
		lstQuads.add(cards52.createCard(CardColor.spades, CardValue.cA, 0, 0));
		for (int j = 0; j < 4; j++) {
			for (int i = 0; i < 8; i++) {
				lstQuads.add(cards52.createCard(CardColor.values()[j],
						CardValue.values()[i], cards52.getX(i),
						cards52.getY(j * 4f / 3)));
			}
		}
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

	@Override
	public boolean actionDown(float eventX, float eventY) {
		getQuadsAt(_lstActionCards, eventX, eventY, 1);
		return _lstActionCards.size() > 0;
	}

	@Override
	public boolean actionMove(float eventX, float eventY) {
		if (_lstActionCards.size() > 0) {
			for (GuiQuad guiQuad : _lstActionCards) {
				guiQuad.moveTo(eventX, eventY);
			}
			sortQuads();
			return true;
		}
		return false;
	}

	@Override
	public boolean actionUp(float eventX, float eventY) {
		// TODO Auto-generated method stub
		return false;
	}

}
