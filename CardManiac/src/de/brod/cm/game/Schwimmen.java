package de.brod.cm.game;

import java.util.ArrayList;
import java.util.List;

import de.brod.cm.Buttons;
import de.brod.cm.Card;
import de.brod.cm.Card.Colors;
import de.brod.cm.Card.Values;
import de.brod.cm.CardManiacView;
import de.brod.cm.Hand;
import de.brod.cm.TextAlign;
import de.brod.gui.GuiColors;
import de.brod.gui.IAction;
import de.brod.gui.shape.Button;
import de.brod.xml.XmlObject;

public class Schwimmen extends Game {

	private Buttons buttons;
	private Button stopButton;

	public Schwimmen(CardManiacView pCardManiacView) {
		super(pCardManiacView);
	}

	@Override
	protected void createTitleCards(Hand hand) {
		hand.createCard(Values.Ace, Colors.Spades);
		hand.createCard(Values.King, Colors.Spades);
		hand.createCard(Values.C10, Colors.Spades);
	}

	@Override
	public IAction getNextAction() {

		final XmlObject settings = getSettings();
		if (get(4).getCardCount() == 32) {
			return new IAction() {

				@Override
				public void action() {
					Hand h4 = get(4);
					Card[] cards = h4.getCards().toArray(new Card[0]);
					for (int i = 0; i <= 3; i++) {
						for (int j = 0; j < 3; j++) {
							cards[i * 3 + j].moveTo(get(i));
						}
					}
					for (int i = 0; i < 5; i++) {
						get(i).organize();
					}
					h4.setCovered(h4.getCardCount());
					settings.setAttribute("skip", 0);
					updateText();
				}

			};
		}
		if (settings.getAttributeAsInt("skip") >= 3) {
			return new IAction() {

				@Override
				public void action() {
					Hand h3 = get(3);
					Hand h4 = get(4);
					// move the cards to tolon
					int covered = h4.getCovered() - 3;
					for (int i = 0; i < 3; i++) {
						h3.getCards().get(0).moveTo(h4);
						h4.getCards().get(0).moveTo(h3);
					}
					h4.setCovered(Math.max(0, covered));
					h4.organize();
					h3.organize();
					settings.setAttribute("skip", 0);
					updateText();
				}
			};
		}
		final int iPlayer = settings.getAttributeAsInt("player");
		if (iPlayer > 0) {
			return new IAction() {

				@Override
				public void action() {

					List<Card> lst = get(iPlayer).getCards();
					List<Card> lst3 = get(3).getCards();
					List<Card> lstTo = new ArrayList<Card>();
					double max = count(lst);
					Card cs = null;
					Card ct = null;
					for (Card c1 : lst) {
						lstTo.clear();
						lstTo.addAll(lst);
						lstTo.remove(c1);
						for (Card c2 : lst3) {
							lstTo.add(c2);
							double m = count(lstTo);
							if (m > max) {
								max = m;
								cs = c1;
								ct = c2;
							}
							lstTo.remove(c2);
						}
					}
					if (max < count(lst3)) {
						for (int i = 0; i < 3; i++) {
							lst.get(0).moveTo(get(3));
							lst3.get(0).moveTo(get(iPlayer));
						}
						settings.setAttribute("skip", 0);
					} else if (cs != null) {
						cs.moveTo(get(3));
						ct.moveTo(get(iPlayer));
						settings.setAttribute("skip", 0);
					} else {
						lst.add(lst.remove(0));
						settings.setAttribute("skip",
								settings.getAttributeAsInt("skip") + 1);
					}
					get(iPlayer).organize();
					get(3).organize();

					settings.setAttribute("player", (iPlayer + 1) % 3);
					updateText();
				}

			};
		}

		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasHistory() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void initHands(boolean bLandscape) {
		// TODO Auto-generated method stub
		float left = 2.5f;
		float right = 4.5f;
		add(new Hand(0, left, Card.maxCardY, right, Card.maxCardY, 3));
		// add the players
		float middle = Card.maxCardY / 2;
		float top = middle - 0.8f;
		float bottom = middle + 0.8f;

		add(new Hand(1, 0, top, 0, bottom, 3));
		add(new Hand(2, 7, top, 7, bottom, 3));

		add(new Hand(3, left, middle, right, middle, 3));

		add(new Hand(4, 0f, 0, 7f, 0, 10));

		// get(1).setCovered(999);
		// get(2).setCovered(999);
		get(4).setCovered(32);

		get(1).setRotation(90f);
		get(2).setRotation(-90f);
		for (int i = 0; i < 5; i++) {
			get(i).setCenter(true);
		}
		get(0).initText(TextAlign.TOP);
		get(3).initText(TextAlign.TOP);
		get(4).initText(TextAlign.BOTTOM);

		buttons = new Buttons(99);
		Button skipButton = Button.Type.reload.createButton(
				Card.getCardWidth(), Card.getY(Card.maxCardY * 3 / 4),
				Card.getCardWidth(), new IAction() {

					@Override
					public void action() {
						XmlObject settings = getSettings();
						int iPlayer = settings.getAttributeAsInt("player");
						int iSkip = settings.getAttributeAsInt("skip");
						settings.setAttribute("skip", iSkip + 1);
						settings.setAttribute("player", iPlayer + 1);
						updateText();
					}
				});
		stopButton = Button.Type.star_on.createButton(-Card.getCardWidth(),
				Card.getY(Card.maxCardY * 3 / 4), Card.getCardWidth(),
				new IAction() {

					@Override
					public void action() {
						XmlObject settings = getSettings();
						int iStop = settings.getAttributeAsInt("stop");
						if (iStop > 0) {
							settings.setAttribute("stop", 0);
						} else {
							settings.setAttribute("stop", 1);
						}
						updateText();
					}
				});
		buttons.add(skipButton);
		buttons.add(stopButton);
		add(buttons);
	}

	private XmlObject getSettings() {
		return buttons.getSettings();
	}

	@Override
	public void initNewCards() {
		get(4).create32Cards();

		get(1).setCovered(0);
		get(2).setCovered(0);
		get(4).setCovered(32);

		getSettings().setAttribute("player", 0);
	}

	@Override
	public String getFinishedText() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void mouseDown(List<Card> plstMoves) {
		int i = plstMoves.get(0).getHand().getId();
		if (i == 0) {
			// ok
		} else if (i == 3) {
			// ok
			plstMoves.clear();
			plstMoves.addAll(get(i).getCards());
		} else {
			plstMoves.clear();
		}
	}

	@Override
	public boolean mouseUp(List<Card> pLstMoves, Hand handTo, Card cardTo) {
		if (handTo == null || cardTo == null) {
			// dont move
			return false;
		}

		Hand handFrom = pLstMoves.get(0).getHand();
		if (handTo == handFrom) {
			return false;
		}
		int i = handTo.getId();
		if (i != 0 && i != 3) {
			return false;
		}
		if (pLstMoves.size() == 1) {
			pLstMoves.get(0).moveTo(handTo);
			cardTo.moveTo(handFrom);
			getSettings().setAttribute("skip", -1);
		} else {
			for (Card c : pLstMoves) {
				c.moveTo(handTo);
				handTo.getCards().get(0).moveTo(handFrom);
			}
			getSettings().setAttribute("skip", -1);
		}
		updateText();
		return true;

	}

	private void updateText() {
		updateText(0);
		updateText(3);
		XmlObject settings = getSettings();
		String iSkip = settings.getAttribute("skip");
		int iStop = settings.getAttributeAsInt("stop");
		if (iStop > 0) {
			stopButton.setColor(GuiColors.TEXT_RED);
		} else {
			stopButton.setColor(null);
		}
		get(4).setText("Skip:" + iSkip);

	}

	private void updateText(int i) {
		double val = count(get(i).getCards());
		if (val == 30.5) {
			get(i).setText("30,5");
		} else {
			get(i).setText(String.valueOf((int) val));
		}
	}

	private double count(List<Card> cards) {
		int max = 0;
		Card.Values v0 = null;
		for (int i = 0; i < cards.size(); i++) {
			Card c = cards.get(i);
			int v = val(c);
			if (i == 0) {
				v0 = c.getValue();
			} else if (v0 != c.getValue()) {
				v0 = null;
			}
			for (int j = i + 1; j < cards.size(); j++) {
				Card c2 = cards.get(j);
				if (c.getColor().equals(c2.getColor())) {
					v += val(c2);
				}
			}
			max = Math.max(max, v);
		}
		if (v0 != null) {
			if (v0.equals(Values.Ace)) {
				return 32;
			}
			return 30.5;
		}
		return max;
	}

	private int val(Card get) {
		Card.Values v = get.getValue();
		if (v.equals(Values.Ace)) {
			return 11;
		}
		if (v.equals(Values.King)) {
			return 10;
		}
		if (v.equals(Values.Queen)) {
			return 10;
		}
		if (v.equals(Values.Jack)) {
			return 10;
		}
		if (v.equals(Values.C10)) {
			return 10;
		}
		if (v.equals(Values.C9)) {
			return 9;
		}
		if (v.equals(Values.C8)) {
			return 8;
		}
		if (v.equals(Values.C7)) {
			return 7;
		}
		return 0;
	}
}
