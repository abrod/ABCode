package de.brod.cm;

import de.brod.cm.Card.Colors;
import de.brod.cm.Card.Values;
import de.brod.gui.shape.Frame;
import de.brod.gui.shape.Sprite;
import de.brod.gui.*;

public class CardFrame extends Frame implements ICard {

	private Hand _hand;

	public CardFrame(Hand pHand, float px, float py, float width, float height) {
		super(px, py, width, height);
		setColor(GuiColors.MENUITEM_BACK);
		_hand = pHand;
	}

	@Override
	public Hand getHand() {
		return _hand;
	}

	@Override
	public void setRotation(float angle) {
		// not supported
	}

	@Override
	public void setCovered(boolean b) {
		// not supported
	}

	@Override
	public void setHand(Hand hand) {
		_hand = hand;
	}

	@Override
	public void moveTo(Hand h) {
		// not supported
	}

	@Override
	public Values getValue() {
		return Values.Ace;
	}

	@Override
	public Colors getColor() {
		return Colors.Empty;
	}

	@Override
	public int getValueId() {
		return 0;
	}

	@Override
	public boolean isCovered() {
		// not covered
		return false;
	}

	@Override
	public void setPosition(float x, float y) {
		// not supported
	}

	@Override
	public void addTo(Sprite sprite) {
		sprite.add(this);
	}

}
