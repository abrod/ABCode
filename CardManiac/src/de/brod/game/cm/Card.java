package de.brod.game.cm;

import de.brod.opengl.OpenGLSquare;

public class Card extends OpenGLSquare {

    private Value _val;
    private Color _col;
    public Hand hand;
    private final int _id;
    private boolean hidden;

    public Card(int piId, CardsTexture tex, Value val, float x, float y) {
        super(getX(x), getY(y), wd, hg, tex.createCell(val.ordinal(), CardsTexture.background));
        _id = piId;
        _val = val;
        _col = tex.color;
        setRotateY(0);
    }

    static float getY(float y) {
        return y * hg2 + hgOff;
    }

    static float getX(float x) {
        return x * wd2 + wdOff;
    }

    public int getId() {
        return _id;
    }

    public static void init(float pWd, float pHg) {
        int dx = 8;
        int dy = 6;

        maxx = dx - 1;

        wd = 2f / dx;
        wd2 = (2 - wd) / maxx;
        wdOff = wd / 2 - 2 + pWd;

        maxy = dy - 1;
        hg = 2f / dy;
        hg2 = (2 - hg) / maxy;
        hgOff = hg / 2 - pHg;

        // add one card size
        if (pWd > pHg) {
            wdOff -= wd / 2;
        } else {
            hgOff += wd / 2;
        }
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean pHidden) {
        if (this.hidden != pHidden) {
            this.hidden = pHidden;
            setRotateY(pHidden ? 1 : 0);
        }
    }


    public enum Value {
        c2("2"), c3("3"), c4("4"), c5("5"), c6("6"), c7("7"), c8("8"), c9("9"), c10(
                "10"), cJ("B"), cQ("D"), cK("K"), cA("A");

        private String sText;

        Value(String psText) {
            sText = psText;
        }

        public String getText() {
            return sText;
        }

        public static int length() {
            return 13;
        }
    }

    public enum Color {
        clubs(9827), spades(9824), hearts(9829), diamonds(9830), special(32);

        private String sText;

        Color(int piValue) {
            sText = String.valueOf((char) piValue);
        }

        public String getText() {
            return sText;
        }

        public static int length() {
            return 5;
        }

    }

    public static float wd, hg, hg2, hgOff, wd2, wdOff;
    public static int maxx, maxy;

    static {
        init(5f / 3f, 1f);
    }

    public Value getValue() {
        return _val;
    }

    public Color getColor() {
        return _col;
    }

    public void setPosition(int piId, float x, float y) {
        float x2 = getX(x);
        float y2 = getY(y);
        setOrder(piId);
        setXY(x2, y2);
    }
}
