package de.brod.cardmaniac;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import de.brod.cardmaniac.game.IGame;
import de.brod.cardmaniac.game.Solitair;
import de.brod.gui.GuiActivity;
import de.brod.gui.GuiButton;
import de.brod.gui.GuiRectangle;
import de.brod.gui.IGuiQuad;

public class MainActivity extends GuiActivity {

	private List<Card>	_lstActionCards	= new ArrayList<Card>();
	private List<Hand>	_lstHands		= new ArrayList<Hand>();
	private float		startX, startY;
	private GuiButton	button;
	private IGame		game;

	@Override
	protected void initActivity(Bundle savedInstanceState) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void createQuads(List<IGuiQuad> lstQuads, float wd, float hg,
			int width, int height) {

		game = new Solitair();
		game.init(wd, hg, width, height);

		addGuiItemFromGame(lstQuads, game);

		moveCardsWithinHands();
	}

	private void addGuiItemFromGame(List<IGuiQuad> lstQuads, IGame game) {
		_lstHands.clear();
		_lstHands.addAll(game.getHands());

		for (Hand hand : _lstHands) {
			lstQuads.add(hand);
			lstQuads.addAll(hand.getCards());
		}
		lstQuads.addAll(game.getButtons());

	}

	private void moveCardsWithinHands() {
		for (Hand hand : _lstHands) {
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
			} else if (guiQuad instanceof Card) {
				Card card = (Card) guiQuad;
				_lstActionCards.clear();
				List<Card> lst = game.actionDown(card);
				_lstActionCards.addAll(lst);
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
			Hand hand = _lstActionCards.get(0).getHand();
			List<Hand> lstQuadsAt = getQuadsAt(eventX, eventY, 1, Hand.class);
			if (lstQuadsAt.size() > 0) {
				Hand handNew = lstQuadsAt.get(0);
				if (!handNew.equals(hand)) {
					game.actionUp(_lstActionCards,handNew);
				}
			}
		}

		moveCardsWithinHands();
		return true;
	}

}
