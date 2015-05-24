package de.brod.opengl;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLUtils;
import android.util.DisplayMetrics;

import javax.microedition.khronos.opengles.GL10;
import java.util.ArrayList;
import java.util.List;

public abstract class OpenGLTexture {
    /**
     * The texture pointer
     */
    int[] textures = new int[1];

    float cx = 1, cy = 1;
    private static List<OpenGLTexture> lstTextures = new ArrayList<OpenGLTexture>();

    public OpenGLTexture(int countx, int county) {
        cx = countx;
        cy = county;
        lstTextures.add(this);
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

        Bitmap bitmap = createBitmap(width, height);
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

    protected abstract Bitmap createBitmap(int piScreenWidth, int piScreenHeight);

    public OpenGLCell createCell(float x, float y) {
        return new OpenGLCell(this, x, y);
    }

    public OpenGLCell createCell(float x, float y, float xMax, float yMax) {
        OpenGLCell cell = new OpenGLCell(this, x, y);
        cell.setGrid(x, y, xMax, yMax);
        return cell;
    }
}