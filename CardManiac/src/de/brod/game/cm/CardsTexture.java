package de.brod.game.cm;

import android.graphics.*;
import de.brod.game.cm.Card.Value;
import de.brod.opengl.OpenGLCell;
import de.brod.opengl.OpenGLTexture;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CardsTexture extends OpenGLTexture {

    static int countX = 5;
    static int countY = 3;
    private static CardsTexture[] tex = new CardsTexture[de.brod.game.cm.Card.Color
            .length() + 1];

    de.brod.game.cm.Card.Color color;

    public CardsTexture(de.brod.game.cm.Card.Color piCount) {
        super(countX, countY);
        color = piCount;
    }

    @Override
    protected Bitmap createBitmap(int piScreenWidth, int piScreenHeight) {
        String binaryString = Integer
                .toBinaryString((piScreenWidth + piScreenHeight) / 2);
        int maxX = (int) Math.pow(2, binaryString.length());

        Bitmap bitmap = Bitmap
                .createBitmap(maxX, maxX, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();

        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        float dx = maxX * 1f / countX;
        float dy = maxX * 1f / countY;
        int textSize = (int) (dy / 6);

        for (int i = 0; i < countX; i++) {
            for (int j = 0; j < countY; j++) {
                RectF rectF = new RectF(i * dx + 1, j * dy + 1, (i + 1) * dx - 1,
                        (j + 1) * dy - 1);
                drawCard(i + j * countX, canvas, rectF, paint, dx, textSize);
            }
        }
        return bitmap;
    }

    private void drawCard(int iCardNumber, Canvas c, RectF r, Paint p,
                          float dx, int pTextSize) {
        Value[] values = Card.Value.values();
        p.setColor(Color.argb(64, 0, 0, 0));
        float dx10 = dx / 10;
        float dx20 = dx / 20;
        c.drawRoundRect(r, dx10, dx10, p);
        float dx40 = dx20 / 2;
        float dx80 = dx40 / 2;
        r = new RectF(r.left + dx80, r.top + dx40, r.right - dx40, r.bottom
                - dx80);
        p.setColor(Color.WHITE);
        c.drawRoundRect(r, dx10, dx10, p);

        if (iCardNumber > values.length) {
            return;
        }

        int iColor = color.ordinal();

        if (iColor < 2) {
            p.setColor(Color.BLACK);
        } else {
            p.setColor(Color.RED);
        }
        p.setTextSize(pTextSize * 1.3f);

        Rect bounds = new Rect();
        Value cardVal = values[iCardNumber % values.length];
        String sValue = cardVal.getText();
        float border = Math.max(1, dx20);

        p.getTextBounds(sValue, 0, sValue.length(), bounds);

        c.drawText(sValue, r.right - bounds.width() - border * 2, r.bottom
                - border, p);
        c.drawText(sValue, r.left + border, r.top + p.getTextSize(), p);

        String sCol = color.getText();
        p.getTextBounds(sCol, 0, sCol.length(), bounds);
        c.drawText(sCol, r.right - bounds.width() - border * 2,
                r.top + p.getTextSize(), p);
        c.drawText(sCol, r.left + border, r.bottom - border, p);

        p.setTextSize(pTextSize * 2);
        p.getTextBounds(sCol, 0, sCol.length(), bounds);
        c.drawText(sCol, r.left + (r.width() - border) / 2 - bounds.centerX(),
                r.centerY() - bounds.centerY(), p);

    }

    public OpenGLCell createCell(int i) {
        return createCell(i % countX, i / countX);
    }

    public static List<Card> create52Cards() {
        List<Card> listCards = new ArrayList<Card>();
        for (de.brod.game.cm.Card.Color col : de.brod.game.cm.Card.Color
                .values()) {
            if (col.ordinal() >= 4) {
                continue;
            }
            CardsTexture texture = getTexture(col);
            for (int j = 0; j < 13; j++) {
                listCards.add(texture.createCard(Card.Value.values()[j]));
            }
        }
        Collections.shuffle(listCards);
        return listCards;
    }

    public Card createCard(Value value) {
        return new Card(this, value, 0, 0);
    }

    private static CardsTexture getTexture(de.brod.game.cm.Card.Color pColor) {
        int ordinal = pColor.ordinal();
        if (tex[ordinal] == null) {
            tex[ordinal] = new CardsTexture(pColor);
        }
        return tex[ordinal];
    }

}
