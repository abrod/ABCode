/*
 * ******************************************************************************
 * Copyright (c) 2013 Andreas Brod
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * *****************************************************************************
 */
package de.brod.cm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.StringTokenizer;

import de.brod.cm.Card.Colors;
import de.brod.cm.Card.Values;
import de.brod.gui.shape.Sprite;
import de.brod.xml.XmlObject;

public class Hand extends CardContainer {

	private List<Card> lstCards = new ArrayList<Card>();
	private int iCardCount;
	private Card c0;
	private boolean bCenter = false;
	private float angle = 0;
	private Comparator<? super Card> cardComperator = null;

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
		super(piId, px1, py1, px2, py2);
		iCardCount = piCardCount;
		c0 = createCard(Card.Values.Ace, Card.Colors.Empty);
		c0.setMoveable(false);
		lstCards.clear();
	}

	public void add(Card card) {
		if (cardComperator != null) {
			int iIndex = -1;
			for (int i = 0; i < lstCards.size(); i++) {
				if (cardComperator.compare(lstCards.get(i), card) > 0) {
					iIndex = i;
					break;
				}
			}
			if (iIndex >= 0) {
				lstCards.add(iIndex, card);
			} else {
				lstCards.add(card);
			}
		} else {
			lstCards.add(card);
		}
		card.hand = this;
		card.setAngle(angle);
	}

	@Override
	public void addAllSpritesTo(Sprite sprite) {
		sprite.add(c0);
		for (Card c : lstCards) {
			sprite.add(c);
		}
		super.addAllSpritesTo(sprite);
	}

	@Override
	public void clear() {
		lstCards.clear();
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

	public Card[] create52Cards() {
		for (int i = 0; i < 13; i++) {
			for (int j = 0; j < 4; j++) {
				createCard(Values.getValue(i), Colors.getValue(j));
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

	public Card getLastCard() {
		int size = lstCards.size();
		if (size > 0) {
			return lstCards.get(size - 1);
		}
		return null;
	}

	@Override
	public String getName() {
		return "Hand";
	}

	public Card getStackCard() {
		return c0;
	}

	@Override
	public void loadState(XmlObject xmlHand) {
		StringTokenizer st = new StringTokenizer(xmlHand.getAttribute("cards"),
				" ");
		while (st.hasMoreTokens()) {
			String sCard = st.nextToken();
			Card card = new Card(this);
			card.setValue(Integer.parseInt(sCard));
			add(card);
		}
		super.loadState(xmlHand);
		organize();
	}

	@Override
	public void organize() {
		float x = pos[0];
		float y = pos[1];

		float dx = pos[2];
		float dy = pos[3];

		int size = Math.max(iCardCount - 1, lstCards.size() - 1);
		if (size > 0) {
			dx = dx / size;
			dy = dy / size;
		}
		int pId = getId() * 1000;
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
		for (Card c : lstCards) {
			pId++;
			c.setId(pId);
			c.setPosition(x, y);
			x += dx;
			y += dy;
		}
	}

	public void remove(Card card) {
		lstCards.remove(card);
	}

	@Override
	public void saveState(XmlObject xmlHand) {
		StringBuilder sb = new StringBuilder();
		for (Card c : lstCards) {
			if (sb.length() > 0) {
				sb.append(" ");
			}
			sb.append(String.valueOf(c.getValueId()));
		}
		xmlHand.setAttribute("cards", sb.toString());
		super.saveState(xmlHand);
	}

	public void setAngle(float pAngle) {
		angle = pAngle;
		for (Card c : lstCards) {
			c.setAngle(angle);
		}
	}

	public void setCenter(boolean b) {
		bCenter = b;
	}

	public void shuffleCards() {
		Collections.shuffle(lstCards);
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

	public void setCardComperator(Comparator<? super Card> cardComperator) {
		this.cardComperator = cardComperator;
	}

}
