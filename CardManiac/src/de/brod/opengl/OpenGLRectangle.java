package de.brod.opengl;

import android.content.Context;
import android.graphics.*;
import android.graphics.Bitmap.Config;
import android.graphics.Paint.Style;

import javax.microedition.khronos.opengles.GL10;

public abstract class OpenGLRectangle {

    private static OpenGLRectangleTexture buttonRectTexture;
    private float x1, x2, y1, y2;
    private OpenGLSquare[][] openGLSquare;
    private OpenGLSquare openGLText;
    private int iUp;
    private float cx, cy, wd, hg;
    private OpenGLSquare _icon;

    static class OpenGLRectangleTexture extends OpenGLTexture {

        public OpenGLRectangleTexture() {
            super(6, 3, false);
        }

        @Override
        protected Bitmap createBitmap(int piScreenWidth, int piScreenHeight) {
            Bitmap bitmap = Bitmap.createBitmap(512, 256, Config.ARGB_8888);
            Canvas c = new Canvas(bitmap);
            draw(c, 0, 255, Color.WHITE, Color.BLACK);
            draw(c, 256, 255, Color.BLACK, 0);
            return bitmap;
        }

        private void draw(Canvas c, int dx, int width, int white, int black) {
            Paint p = new Paint();
            int wd = 10;
            int d = 5;
            int corner = 30;

            RectF rect = new RectF(dx + d, d, dx + width - d - wd, width - d
                    - wd);
            p.setStyle(Style.STROKE);
            p.setStrokeWidth(wd);
            if (white != 0) {
                p.setColor(white);
                c.drawRoundRect(rect, corner, corner, p);
            }

            rect = new RectF(dx + d + wd, d + wd, dx + width - d, width - d);
            d = 7;
            int d2 = 7;
            if (black != 0) {
                p.setColor(black);
                c.drawRoundRect(rect, corner, corner, p);
            } else {
                d2 = 5;
            }

            rect = new RectF(dx + d, d, dx + width - d2, width - d2);
            p.setARGB(255, 192, 192, 192);
            p.setStyle(Style.FILL);
            c.drawRoundRect(rect, corner, corner, p);

        }
    }

    static {
        buttonRectTexture = new OpenGLRectangleTexture();
    }

    public OpenGLRectangle(float px1, float py1, float px2, float py2) {
        this.x1 = Math.min(px1, px2);
        this.x2 = Math.max(px1, px2);
        this.y1 = Math.min(py1, py2);
        this.y2 = Math.max(py1, py2);
        cx = (x1 + x2) / 2;
        cy = (y1 + y2) / 2;
        wd = Math.abs(x2 - x1);
        hg = Math.abs(y2 - y1);
        float min = Math.min(wd, hg) * 0.05f;
        cx -= min / 2;
        cy -= min / 2;
        wd -= min;
        hg -= min;
        if (wd == hg) {
            float hg2 = hg / 2;
            float hOff = hg2 / 2;

            openGLSquare = new OpenGLSquare[][]{
                    {
                            new OpenGLSquare(cx, cy + hOff, wd, hg2,
                                    buttonRectTexture.createRectangle(0, 0, 3, 1)),
                            new OpenGLSquare(cx, cy - hOff, wd, hg2,
                                    buttonRectTexture.createRectangle(0, 2, 3, 3))},
                    {
                            new OpenGLSquare(cx, cy + hOff, wd, hg2,
                                    buttonRectTexture.createRectangle(3, 0, 6, 1)),
                            new OpenGLSquare(cx, cy - hOff, wd, hg2,
                                    buttonRectTexture.createRectangle(3, 2, 6, 3))}};
        } else if (wd < hg) {
            float hg2 = wd / 3;
            float h2 = hg - hg2 * 2;
            float hOff = h2 / 2 + hg2 / 2;

            openGLSquare = new OpenGLSquare[][]{
                    {
                            new OpenGLSquare(cx, cy + hOff, wd, hg2,
                                    buttonRectTexture.createRectangle(0, 0, 3, 1)),
                            new OpenGLSquare(cx, cy, wd, h2,
                                    buttonRectTexture.createRectangle(0, 1, 3, 2)),
                            new OpenGLSquare(cx, cy - hOff, wd, hg2,
                                    buttonRectTexture.createRectangle(0, 2, 3, 3))},
                    {
                            new OpenGLSquare(cx, cy + hOff, wd, hg2,
                                    buttonRectTexture.createRectangle(3, 0, 6, 1)),
                            new OpenGLSquare(cx, cy, wd, h2,
                                    buttonRectTexture.createRectangle(3, 1, 6, 2)),
                            new OpenGLSquare(cx, cy - hOff, wd, hg2,
                                    buttonRectTexture.createRectangle(3, 2, 6, 3))}};
        } else {
            float wd2 = hg / 3;
            float w2 = wd - wd2 * 2;
            float wOff = w2 / 2 + wd2 / 2;

            openGLSquare = new OpenGLSquare[][]{
                    {
                            new OpenGLSquare(cx - wOff, cy, wd2, hg,
                                    buttonRectTexture.createRectangle(0, 0, 1, 3)),
                            new OpenGLSquare(cx, cy, w2, hg,
                                    buttonRectTexture.createRectangle(1, 0, 2, 3)),
                            new OpenGLSquare(cx + wOff, cy, wd2, hg,
                                    buttonRectTexture.createRectangle(2, 0, 3, 3))},
                    {
                            new OpenGLSquare(cx - wOff, cy, wd2, hg,
                                    buttonRectTexture.createRectangle(3, 0, 4, 3)),
                            new OpenGLSquare(cx, cy, w2, hg,
                                    buttonRectTexture.createRectangle(4, 0, 5, 3)),
                            new OpenGLSquare(cx + wOff, cy, wd2, hg,
                                    buttonRectTexture.createRectangle(5, 0, 6, 3))}};
        }
        initColor();
    }

    public void setUp(boolean pbUp) {
        int iUpNew = pbUp ? 0 : 1;
        if (iUp != iUpNew) {
            iUp = iUpNew;
            if (openGLText == null) {
                // ignore
            } else if (pbUp) {
                openGLText.moveTo(cx, cy);
            } else {
                float min = Math.min(wd, hg) / 30;
                openGLText.moveTo(cx + min, cy - min);
            }
            if (_icon == null) {
                // ignore
            } else if (pbUp) {
                _icon.moveTo(cx, cy);
            } else {
                float min = Math.min(wd, hg) / 30;
                _icon.moveTo(cx + min, cy - min);
            }

        }
    }

    protected void initColor() {
        setColor(64, 64, 64, 64);
    }

    protected abstract void organize();

    public boolean touches(float eventX, float eventY) {
        return !(x2 < eventX || x1 > eventX || y2 < eventY || y1 > eventY);
    }

    /**
     * This function draws our square on screen.
     */
    void draw(GL10 gl, Context context) {
        for (OpenGLSquare glSquare : openGLSquare[iUp]) {
            glSquare.draw(gl, null, context);
        }
        if (openGLText != null) {
            openGLText.draw(gl, null, context);
        }
        if (_icon != null) {
            _icon.draw(gl, null, context);
        }
    }

    protected void setColor(int pr, int pg, int pb, int pa) {
        for (OpenGLSquare glSquare : openGLSquare[iUp]) {
            glSquare.setColor(pr, pg, pb, pa);
        }
    }

    public void setText(String psText) {
        if (openGLText == null) {
            openGLText = new OpenGLSquare(cx, cy, wd, hg, null);
        }
        openGLText.setText(psText);
    }

    public void setTextColor(int pr, int pg, int pb, int pa) {
        if (openGLText != null) {
            openGLText.setColor(pr, pg, pb, pa);
        }
    }

    public void clear() {
        for (OpenGLSquare[] openGLSquares : openGLSquare) {
            for (OpenGLSquare openGLSquare : openGLSquares) {
                openGLSquare.clear();
            }
        }
    }

    public void setIcon(OpenGLTexture pOpenGLTextTexture, float pWd, float pHg) {
        OpenGLCell cell = new OpenGLCell(pOpenGLTextTexture, 1, 1);
        float ratio = pWd / pHg;
        if (ratio >= 1)
            _icon = new OpenGLSquare(cx, cy, wd * 0.8f, hg * 0.8f / ratio, cell);
        else
            _icon = new OpenGLSquare(cx, cy, wd * 0.8f * ratio, hg * 0.8f, cell);
    }
}
