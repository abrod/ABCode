package de.brod.cardmaniac;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import de.brod.gui.GuiActivity;
import de.brod.gui.GuiButton;
import de.brod.gui.GuiRectangle;
import de.brod.gui.IGuiQuad;

public class MainActivity extends GuiActivity {

	private List<Card>					_lstActionCards	= new ArrayList<Card>();
	private List<Hand<? extends Card>>	_lstHands		= new ArrayList<Hand<? extends Card>>();
	private float						startX, startY;
	private GuiButton					button;

	@Override
	protected void initActivity(Bundle savedInstanceState) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void createQuads(List<IGuiQuad> lstQuads, float wd, float hg,
			int width, int height) {
		Cards52 cards52 = new Cards52();
		List<Card52> create52Cards = cards52.create52Cards();
		lstQuads.addAll(create52Cards);

		List<Hand<Card52>> hands = new ArrayList<Hand<Card52>>();
		for (int i = 0; i < 4; i++) {
			float y = i * 4f / 3;
			hands.add(new Hand<Card52>(0, y, 7 - i, y, String.valueOf(i + 1)));
		}

		for (int i = 0; i < create52Cards.size(); i++) {
			Card52 card = create52Cards.get(i);
			hands.get(i % hands.size()).addCard(card);
		}
		for (Hand<Card52> hand : hands) {
			lstQuads.add(hand);
			_lstHands.add(hand);
		}
		moveCardsWithinHands();
		float wdButton = 1 / 2f * 2;
		float hgButton = 1 / 4f;
		float x = (wd - wdButton) / 2f;
		float y = (hg - hgButton) / 2f;
		GuiButton guiButton = new GuiButton(x, y, wdButton, hgButton, "Show");
		lstQuads.add(guiButton);
	}

	private void moveCardsWithinHands() {
		for (Hand<? extends Card> hand : _lstHands) {
			hand.moveCards();
		}
		sortQuads();
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
		_lstActionCards.clear();
		_lstActionCards.addAll(getQuadsAt(eventX, eventY, 1, Card.class));
		if (_lstActionCards.size() > 0) {
			IGuiQuad guiQuad = _lstActionCards.get(0);
			if (guiQuad instanceof GuiButton) {
				button = (GuiButton) guiQuad;
				(button).setDown(true);
			}
			// don't move rectangles
			if (guiQuad instanceof GuiRectangle) {
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
		if (_lstActionCards.size() > 0) {
			Hand<? extends Card> hand = _lstActionCards.get(0).getHand();
			@SuppressWarnings("rawtypes")
			List<Hand> lstQuadsAt = getQuadsAt(eventX, eventY, 1, Hand.class);
			if (lstQuadsAt.size() > 0) {
				@SuppressWarnings("unchecked")
				Hand<Card> handNew = lstQuadsAt.get(0);
				if (!handNew.equals(hand)) {
					handNew.addCard(_lstActionCards.get(0));
				}
			}
		}

		moveCardsWithinHands();
		return true;
	}

}
