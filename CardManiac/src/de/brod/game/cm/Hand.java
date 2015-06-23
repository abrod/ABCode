package de.brod.game.cm;

import de.brod.opengl.OpenGLRectangle;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class Hand extends OpenGLRectangle {

    private final int id;
    float x1, x2, y1, y2;
    protected int count, hidden;

    protected List<Card> lstCards = new ArrayList<Card>();

    public boolean dirty;

    public Hand(int id, float px1, float py1, float px2, float py2, int piCount) {
        this(id, px1, py1, px2, py2, piCount, false);
    }

    public Hand(int id, float px1, float py1, float px2, float py2, int piCount,
                boolean pbLandscape) {
        super(Card.getX(Math.min(px1, px2)) - (pbLandscape ? Card.hg : Card.wd)
                / 2, Card.getY(Math.min(py1, py2))
                - (!pbLandscape ? Card.hg : Card.wd) / 2, Card.getX(Math.max(
                px1, px2)) + (pbLandscape ? Card.hg : Card.wd) / 2, Card
                .getY(Math.max(py1, py2))
                + (!pbLandscape ? Card.hg : Card.wd)
                / 2);
        this.id = id;
        x1 = px1;
        x2 = px2;
        y2 = py2;
        y1 = py1;
        count = piCount;
    }

    public abstract void actionUp(List<Card> lstMove2);

    public abstract void actionDown(Card pDown, List<Card> plstMove);

    public void addCard(Card card) {
        if (card.hand != null) {
            card.hand.lstCards.remove(card);
            if (card.hand != this) {
                card.hand.setHidden();
                card.hand.dirty = true;
            }
        }
        lstCards.add(card);
        card.hand = this;
        setHidden();
        dirty = true;
    }

    public Card getLastCard() {
        int x = lstCards.size() - 1;
        if (x >= 0) {
            return lstCards.get(x);
        }
        return null;
    }

    @Override
    public void organize() {
        float dx = (x2 - x1) / (Math.max(lstCards.size(), count) - 1);
        float dy = (y2 - y1) / (Math.max(lstCards.size(), count) - 1);

        float x = x1;
        float y = y1;
        for (int i = 0; i < lstCards.size(); i++) {
            Card card = lstCards.get(i);
            card.setPosition(i, x, y);
            x += dx;
            y += dy;
        }
        dirty = false;
        setHidden();
    }

    public List<Card> getCards() {
        return lstCards;
    }

    public int getCardCount() {
        return lstCards.size();
    }

    public int getId() {
        return id;
    }

    public int getHidden() {
        return hidden;
    }

    public void setHidden(int piHidden) {
        if (this.hidden != piHidden) {
            dirty = true;
            this.hidden = piHidden;
            organize();
        }
    }

    protected void setHidden() {
        for (int i = 0; i < lstCards.size(); i++) {
            lstCards.get(i).setHidden(i < hidden);
        }
    }

    public void write(DataOutputStream out) throws IOException {
        out.writeInt(hidden);
    }

    public void read(DataInputStream in) throws IOException {
        hidden = in.readInt();
    }
}
