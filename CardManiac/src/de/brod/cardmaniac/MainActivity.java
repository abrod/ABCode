package de.brod.cardmaniac;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.os.Bundle;
import de.brod.gui.GuiActivity;
import de.brod.gui.GuiButton;
import de.brod.gui.IGuiQuad;

public class MainActivity extends GuiActivity {

	private List<IGuiQuad>	_lstActionCards	= new ArrayList<IGuiQuad>();
	private float			startX, startY;
	private GuiButton		button;

	@Override
	protected void initActivity(Bundle savedInstanceState) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void createQuads(List<IGuiQuad> lstQuads, float wd, float hg,
			int width, int height) {
		Cards52 cards52 = new Cards52();
		List<Card> create52Cards = cards52.create52Cards();
		Collections.shuffle(create52Cards);
		lstQuads.addAll(create52Cards);
		for (int i = 0; i < create52Cards.size(); i++) {
			create52Cards.get(i).moveTo(cards52.getX((i % 13) * 8f / 12),
					cards52.getY((i / 13) * 4f / 3));
		}
		float wdButton = 1 / 2f * 2;
		float hgButton = 1 / 4f;
		lstQuads.add(new GuiButton((wd - wdButton) / 2f, (hg - hgButton) / 2f,
				wdButton, hgButton, "Show"));
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
		startX = eventX;
		startY = eventY;
		button = null;
		getQuadsAt(_lstActionCards, eventX, eventY, 1);
		if (_lstActionCards.size() > 0) {
			IGuiQuad guiQuad = _lstActionCards.get(0);
			if (guiQuad instanceof GuiButton) {
				button = (GuiButton) guiQuad;
				(button).setDown(true);
				_lstActionCards.clear();
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean actionMove(float eventX, float eventY) {
		if (_lstActionCards.size() > 0) {
			for (IGuiQuad guiQuad : _lstActionCards) {
				guiQuad.moveTo(eventX, eventY);
			}
			sortQuads();
			return true;
		}
		return false;
	}

	@Override
	public boolean actionUp(float eventX, float eventY) {
		if (button != null) {
			button.setDown(false);
			if (button.touches(eventX, eventY)) {
				button.doAction();
			}
			return true;
		}
		float dx = eventX - startX;
		float dy = eventY - startY;
		if ((dx * dx + dy * dy) < 1 / 64f) {
			// select
		}

		// TODO Auto-generated method stub
		return true;
	}

}
