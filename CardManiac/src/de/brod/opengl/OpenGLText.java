package de.brod.opengl;

import android.content.Context;
import android.graphics.*;
import android.graphics.Bitmap.Config;

import javax.microedition.khronos.opengles.GL10;

public class OpenGLText extends OpenGLSquare {

    static class OpenGLTextTexture extends OpenGLTexture {

        private String _sText;
        private OpenGLCell _cell;
        private float _wd, _hg;

        public OpenGLTextTexture(String psText, float wd, float hg) {
            super(1, 1);
            _sText = psText;
            _wd = wd;
            _hg = hg;
        }

        public void setCell(OpenGLCell cell) {
            _cell = cell;
        }

        @Override
        protected Bitmap createBitmap(int piScreenWidth, int piScreenHeight) {

            int dx = 32;
            float wd = piScreenWidth * _wd / 1.8f;
            while (dx < wd) {
                dx *= 2;
            }

            int dy = 32;
            float hg = piScreenHeight * _hg / 1.8f;
            while (dy < hg) {
                dy *= 2;
            }

            // draw the text to own canvas
            Paint p = new Paint();
            p.setTextSize(dy);
            p.setColor(Color.BLACK);
            Rect bounds = new Rect();
            p.getTextBounds(_sText, 0, _sText.length(), bounds);
            int border = bounds.height() / 2;
            int wTxt = bounds.width() + border;
            int hTxt = bounds.height() + border;
            Bitmap bitmapTxt = Bitmap
                    .createBitmap(wTxt, hTxt, Config.ARGB_8888);
            Canvas c = new Canvas(bitmapTxt);
            c.drawText(_sText, border / 2 - bounds.left, border / 2
                    - bounds.top, p);

            Bitmap bitmap = Bitmap.createBitmap(dx, dy, Config.ARGB_8888);
            c = new Canvas(bitmap);
            Rect src = new Rect(0, 0, wTxt, hTxt);

            float f1 = wTxt * 1f / hTxt;
            float f2 = _wd / _hg;
            float f3 = dx * 1f / dy;

            float f = f2 / f1;
            System.out.println(f1 + " " + f2 + " = " + f3 + " ... " + _wd + " "
                    + _hg + " " + f);
            int py = (int) ((dy - dy * f) / 2);
            int px = 0;
            if (py < 0) {
                py = 0;
                px = (int) ((dx - dx / f) / 2);
                if (px < 0) {
                    px = 0;
                }
            }

            Rect dst = new Rect(px, py, dx - px, dy - py);

            c.drawBitmap(bitmapTxt, src, dst, p);

            bitmapTxt.recycle();
            return bitmap;
        }
    }

    static class OpenGLTextCell extends OpenGLCell {

        private OpenGLTextTexture _initTexture;

        public OpenGLTextCell(String psText, float wd, float hg) {
            super(new OpenGLTextTexture(psText, wd, hg), 0, 0);
            _initTexture = (OpenGLTextTexture) t;
            _initTexture.setCell(this);
        }

        @Override
        public void draw(GL10 gl, int i, Context context) {
            if (_initTexture != null) {
                _initTexture.initTexture(gl, context);
                _initTexture = null;
            }
            super.draw(gl, i, context);
        }
    }

    public OpenGLText(float px, float py, float wd, float hg, String psText) {
        super(px, py, wd, hg, createTextCell(psText, wd, hg));
    }

    private static OpenGLCell createTextCell(String psText, float wd, float hg) {
        return new OpenGLTextCell(psText, wd, hg);
    }
}
