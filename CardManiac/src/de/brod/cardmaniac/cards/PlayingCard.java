package de.brod.cardmaniac.cards;

import java.util.Collections;
import java.util.List;

import android.graphics.Color;

public class PlayingCard extends Card {

	private CardValue	value;
	private CardColor	color;

	protected PlayingCard(CardSet cardSet, CardValue x, CardColor y) {
		super(cardSet, x.ordinal(), y.ordinal(), 3, 4);
		setValue(x);
		setColor(y);
	}

	public enum CardColor {
		spades(9827, Color.BLACK), clubs(9824, Color.BLACK), hearts(9829,
				Color.RED), diamonds(9830, Color.RED);

		private String	_s;
		private int		_col;

		CardColor(int piValue, int piColor) {
			_s = String.valueOf((char) piValue);
			_col = piColor;
		}

		public String getChar() {
			return _s;
		}

		public int getColor() {
			return _col;
		}

	}

	public enum CardValue {
		ass("A"), c2("2"), c3("3"), c4("4"), c5("5"), c6("6"), c7("7"), c8("8"), c9(
				"9"), c10("10"), jack("B"), queen("D"), king("K"), ;

		String	_s;

		CardValue(String s) {
			_s = s;
		}

		public String getText() {
			return _s;
		}
	}

	@Override
	public String toString() {
		return getValue()._s + getColor()._s + "("
				+ (Math.round(posX * 100) / 100f) + "x"
				+ (Math.round(posY * 100) / 100f) + ")";
	}

	public CardValue getValue() {
		return value;
	}

	public void setValue(CardValue value) {
		this.value = value;
	}

	public CardColor getColor() {
		return color;
	}

	public void setColor(CardColor color) {
		this.color = color;
	}

	public static void fill32Cards(CardSet cardSet, List<Card> plstCards) {
		fillCards(
				cardSet,
				plstCards,
				PlayingCard.CardColor.values(),
				new PlayingCard.CardValue[] { PlayingCard.CardValue.ass,
					PlayingCard.CardValue.c7, PlayingCard.CardValue.c8,
					PlayingCard.CardValue.c9, PlayingCard.CardValue.c10,
					PlayingCard.CardValue.jack,
					PlayingCard.CardValue.queen, PlayingCard.CardValue.king });

	}

	public static void fill52Cards(CardSet cardSet, List<Card> plstCards) {
		fillCards(
				cardSet,
				plstCards,
				PlayingCard.CardColor.values(),
				new PlayingCard.CardValue[] { PlayingCard.CardValue.ass,
					PlayingCard.CardValue.c2, PlayingCard.CardValue.c3,
					PlayingCard.CardValue.c4, PlayingCard.CardValue.c5,
					PlayingCard.CardValue.c6, PlayingCard.CardValue.c7,
					PlayingCard.CardValue.c8, PlayingCard.CardValue.c9,
					PlayingCard.CardValue.c10, PlayingCard.CardValue.jack,
					PlayingCard.CardValue.queen, PlayingCard.CardValue.king });
	}

	private static void fillCards(CardSet cardSet, List<Card> plstCards,
			CardColor[] cardColor, CardValue[] cardValues) {
		Hand tempHand = new Hand(cardSet, 0, 0, 0, 0, 0, false, 999);
		for (CardColor color : cardColor) {
			for (CardValue value : cardValues) {
				PlayingCard createCard = tempHand.createCard(value, color);
				createCard.moveTo(null);
				plstCards.add(createCard);
			}
		}
		Collections.shuffle(plstCards);

	}
}
