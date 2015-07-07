package de.brod.opengl;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLUtils;
import android.util.DisplayMetrics;

import javax.microedition.khronos.opengles.GL10;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public abstract class OpenGLTexture {

    private static Stack<Integer> hsFree = new Stack<>();

    protected static void checkTextures(GL10 gl) {
        while (hsFree.size() > 0) {
            int[] tex = {hsFree.pop().intValue()};
            gl.glDeleteTextures(1, tex, 0);
            // BindTexture(GL10.GL_TEXTURE_2D, textures[0]);
        }
    }

    private final boolean _bFreeAutomatically;
    /**
     * The texture pointer
     */
    int[] textures = new int[1];

    float cx = 1, cy = 1;
    private static List<OpenGLTexture> lstTextures = new ArrayList<OpenGLTexture>();

    public OpenGLTexture(int countx, int county, boolean pbFreeAutomatically) {
        cx = countx;
        cy = county;
        lstTextures.add(this);
        _bFreeAutomatically = pbFreeAutomatically;
    }

    public static void initTextures(GL10 gl, Context context) {
        for (OpenGLTexture openGLTexture : lstTextures) {
            openGLTexture.initTexture(gl, context);
        }
    }

    void initTexture(GL10 gl, Context context) {

        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        Bitmap bitmap = resizeBitmap(createBitmap(width, height));
        // loading texture

        // generate one texture pointer
        gl.glGenTextures(1, textures, 0);
        // ...and bind it to our array
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);

        // create nearest filtered texture
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,
                GL10.GL_NEAREST);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,
                GL10.GL_LINEAR);

        // Use Android GLUtils to specify a two-dimensional texture image from our bitmap
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);

        // Clean up
        bitmap.recycle();
    }

    private Bitmap resizeBitmap(Bitmap bitmap) {
        int wd = bitmap.getWidth();
        int hg = bitmap.getHeight();

        int wd2 = get2exp(wd);
        int hg2 = get2exp(hg);
        if (wd != wd2 || hg != hg2) {
            // create a new bitmap
            Bitmap newBmp = Bitmap.createScaledBitmap(bitmap, wd2, hg2, true);
            bitmap.recycle();
            return newBmp;
        }
        return bitmap;
    }

    private int get2exp(int wOrig) {
        if (wOrig < 4)
            return 4;
        int wNew = 256;
        if (wNew > wOrig) {
            while (wNew > wOrig) {
                wNew /= 2;
            }
            return wNew * 2;
        } else {
            while (wNew < wOrig) {
                wNew *= 2;
            }
        }
        return wNew;
    }


    protected abstract Bitmap createBitmap(int piScreenWidth, int piScreenHeight);

    public OpenGLCell createCell(float x, float y, float xBack, float yBack) {
        return new OpenGLCell(this, x, y, xBack, yBack);
    }

    public OpenGLCell createRectangle(float x, float y, float xMax, float yMax) {
        OpenGLCell cell = new OpenGLCell(this, x, y);
        cell.setGrid(x, y, xMax, yMax, x, y, xMax, yMax);
        return cell;
    }

    public void clear() {
        if (_bFreeAutomatically) {
            synchronized (hsFree) {
                if (!hsFree.contains(textures[0])) {
                    hsFree.add(textures[0]);
                }
            }
        }
    }
}
