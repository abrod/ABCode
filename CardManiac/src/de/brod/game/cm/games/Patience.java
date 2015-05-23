package de.brod.game.cm.games;

import de.brod.game.cm.Card;
import de.brod.game.cm.Card.Color;
import de.brod.game.cm.Card.Value;

public abstract class Patience extends Game {
    boolean isNext(Card found, Card c, boolean bExactColor) {
        if (getNumber(found) - 1 != getNumber(c)) {
            return false;
        }

        int col1 = getColor(found);
        int col2 = getColor(c);
        if (bExactColor) {
            if (col1 != col2) {
                return false;
            }
        } else if (col1 / 2 == col2 / 2) {
            return false;
        }
        return true;
    }

    int getNumber(Card c) {
        if (c == null) {
            return -1;
        }
        Value v = c.getValue();
        if (v.equals(Value.cA)) {
            return 0;
        }
        return v.ordinal() + 1;
    }

    int getColor(Card card) {
        if (card == null) {
            return -1;
        }
        Color c = card.getColor();
        if (c.equals(Color.clubs)) {
            return 0;
        } else if (c.equals(Color.spades)) {
            return 1;
        } else if (c.equals(Color.hearts)) {
            return 2;
        } else if (c.equals(Color.diamonds)) {
            return 3;
        }
        return -1;
    }

}
