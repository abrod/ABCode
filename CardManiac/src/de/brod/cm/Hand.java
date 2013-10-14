package de.brod.cm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import android.graphics.Color;
import de.brod.cm.Card.Colors;
import de.brod.cm.Card.Values;
import de.brod.gui.shape.Sprite;
import de.brod.gui.shape.Text;
import de.brod.xml.XmlObject;

public class Hand {

	List<Card> lstCards = new ArrayList<Card>();
	private float[] pos;
	private int iCardCount;
	private Card c0;
	private int id;
	private Text text = null;
	private boolean bCenter = false;
	private boolean bLandScape;
	private float angle = 0;
	private XmlObject settings = null;

	/**
	 * Create a Hand object. The
	 *
	 * @param piId
	 * @param px1
	 * @param py1
	 * @param px2
	 * @param py2
	 * @param piCardCount
	 */
	public Hand(int piId, float px1, float py1, float px2, float py2,
			int piCardCount) {
		id = piId;
		float x1 = Card.getX(px1);
		float y1 = Card.getY(py1);
		float x2 = Card.getX(px2);
		float y2 = Card.getY(py2);
		pos = new float[] { x1, y1, x2 - x1, y2 - y1 };
		bLandScape = Math.abs(pos[2]) > Math.abs(pos[3]);
		iCardCount = piCardCount;
		c0 = createCard(Card.Values.Ace, Card.Colors.Empty);
		c0.setMoveable(false);
		lstCards.clear();
	}

	public void add(Card card) {
		lstCards.add(card);
		card.hand = this;
		card.setAngle(angle);
	}

	public void addAllCards(Sprite sprite) {
		sprite.add(c0);
		for (Card c : lstCards) {
			sprite.add(c);
		}
		if (text != null) {
			sprite.add(text);
		}
	}

	public void setText(String psText) {
		if (psText.length() == 0) {
			text = null;
		} else {
			float fTitleHeight = Card.getCardHeight() / 4;
			text = Text.createText(psText, pos[0],
					pos[1] - pos[3] - Card.getCardHeight() / 2 - fTitleHeight,
					fTitleHeight);
			text.setMoveable(false);
			text.setColor(Color.WHITE);
		}
	}

	public void clear() {
		lstCards.clear();
	}

	public Card[] create52Cards() {
		for (int i = 0; i < 13; i++) {
			for (int j = 0; j < 4; j++) {
				createCard(Values.getValue(i), Colors.getValue(j));
			}
		}
		shuffleCards();
		return lstCards.toArray(new Card[0]);
	}

	public Card[] create32Cards() {
		for (int i = 0; i < 13; i++) {
			for (int j = 0; j < 4; j++) {
				createCard(Values.getValue(i), Colors.getValue(j));
			}
			if (i == 0) {
				i += 5;
			}
		}
		shuffleCards();
		return lstCards.toArray(new Card[0]);
	}

	public Card createCard(Card.Values value, Card.Colors color) {
		Card c = new Card(this);
		c.setPosition(pos[0], pos[1]);
		c.setValue(value, color);
		add(c);
		return c;
	}

	public int getCardCount() {
		return lstCards.size();
	}

	public List<Card> getCards() {
		return lstCards;
	}

	public int getId() {
		return id;
	}

	public Card getLastCard() {
		int size = lstCards.size();
		if (size > 0) {
			return lstCards.get(size - 1);
		}
		return null;
	}

	public void loadState(XmlObject xmlHand) {
		StringTokenizer st = new StringTokenizer(xmlHand.getAttribute("cards"),
				" ");
		while (st.hasMoreTokens()) {
			String sCard = st.nextToken();
			Card card = new Card(this);
			card.setValue(Integer.parseInt(sCard));
			add(card);
		}
		XmlObject[] arrSettings = xmlHand.getObjects("Settings");
		if (arrSettings.length > 0) {
			settings = arrSettings[0];
		} else {
			settings = null;
		}
	}

	public void organize() {
		if (lstCards.size() > 0) {
			float x = pos[0];
			float y = pos[1];

			float dx = pos[2];
			float dy = pos[3];

			int size = Math.max(iCardCount - 1, lstCards.size() - 1);
			if (size > 0) {
				dx = dx / size;
				dy = dy / size;
			}
			int pId = id * 1000;
			c0.setId(pId);
			if (bCenter) {
				if (bLandScape) {
					c0.setPosition(x + pos[2] / 2, y);
					float offSetX = pos[2] - dx * (lstCards.size() - 1);
					if (offSetX > 0) {
						x += offSetX / 2;
					}
				} else {
					c0.setPosition(x, y + pos[3] / 2);
					float offSetY = pos[3] - dy * (lstCards.size() - 1);
					if (offSetY > 0 && pos[3] > 0) {
						y += offSetY / 2;
					} else if (offSetY < 0 && pos[3] < 0) {
						y += offSetY / 2;
					}

				}

			} else {
				c0.setPosition(x, y);
			}
			for (int i = 0; i < lstCards.size(); i++) {
				Card c = lstCards.get(i);
				pId++;
				c0.setId(pId);
				c.setPosition(x, y);
				x += dx;
				y += dy;
			}
		}
	}

	public void remove(Card card) {
		lstCards.remove(card);
	}

	public void saveState(XmlObject xmlHand) {
		StringBuilder sb = new StringBuilder();
		for (Card c : lstCards) {
			if (sb.length() > 0) {
				sb.append(" ");
			}
			sb.append(String.valueOf(c.getValueId()));
		}
		xmlHand.setAttribute("cards", sb.toString());
		XmlObject[] objects = xmlHand.getObjects("Settings");
		if (objects.length == 0 && settings == null) {
			// no settings
		} else {
			// remove settings
			xmlHand.deleteObjects("Settings");
			if (settings != null) {
				xmlHand.addObject(settings);
			}
		}
	}

	public void shuffleCards() {
		Collections.shuffle(lstCards);
	}

	public void setCenter(boolean b) {
		bCenter = b;
	}

	public void setAngle(float pAngle) {
		angle = pAngle;
		for (Card c : lstCards) {
			c.setAngle(angle);
		}
	}

	public Card getStackCard() {
		return c0;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("@" + getId());
		for (Card c : lstCards) {
			sb.append(",");
			sb.append(c.toString());
		}
		return sb.toString();
	}

	public XmlObject getSettings() {
		if (settings == null) {
			settings = new XmlObject("Settings");
		}
		return settings;
	}
}
