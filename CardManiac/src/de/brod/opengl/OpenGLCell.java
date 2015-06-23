package de.brod.opengl;

import android.content.Context;

import javax.microedition.khronos.opengles.GL10;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class OpenGLCell {
    private FloatBuffer[] textureBuffer;    // buffer holding the texture coordinates

    private int[] textures;

    OpenGLTexture t;

    public OpenGLCell(OpenGLTexture pTextures, float x1, float y1, float x2, float y2) {
        textures = pTextures.textures;
        t = pTextures;
        textureBuffer = new FloatBuffer[2];
        for (int i = 0; i < textureBuffer.length; i++) {
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(8 * 4);
            byteBuffer.order(ByteOrder.nativeOrder());
            textureBuffer[i] = byteBuffer.asFloatBuffer();
        }
        setGrid(x1, y1, x2, y2);
    }

    public OpenGLCell(OpenGLTexture pTextures, float x, float y) {
        this(pTextures, x, y, x, y);
    }

    protected void setGrid(float x1, float y1, float x2, float y2) {
        setGrid(x1, y1, x1 + 1, y1 + 1, x2, y2, x2 + 1, y2 + 1);
    }

    protected void setGrid(float px1, float py1, float px1Max, float py1Max, float px2, float py2, float px2Max, float py2Max) {

        textureBuffer[0].position(0);

        float x1 = px1 / t.cx;
        float y1 = py1 / t.cy;
        float y2 = py1Max / t.cy;
        float x2 = px1Max / t.cx;

        // bottom left  (V1)
        textureBuffer[0].put(x1);
        textureBuffer[0].put(y1);
        // top left     (V2)
        textureBuffer[0].put(x1);
        textureBuffer[0].put(y2);
        // top right    (V4)
        textureBuffer[0].put(x2);
        textureBuffer[0].put(y2);
        // bottom right (V3)
        textureBuffer[0].put(x2);
        textureBuffer[0].put(y1);

        textureBuffer[0].position(0);

        textureBuffer[1].position(0);

        if (px2 != px1 || py1 != py2) {
            x2 = px2 / t.cx;
            y1 = py2 / t.cy;
            y2 = py2Max / t.cy;
            x1 = px2Max / t.cx;
        } else {
            x1 = px2 / t.cx;
            y1 = py2 / t.cy;
            y2 = py2Max / t.cy;
            x2 = px2Max / t.cx;
        }

        // top left     (V2)
        textureBuffer[1].put(x1);
        textureBuffer[1].put(y2);
        // bottom left  (V1)
        textureBuffer[1].put(x1);
        textureBuffer[1].put(y1);
        // bottom right (V3)
        textureBuffer[1].put(x2);
        textureBuffer[1].put(y1);
        // top right    (V4)
        textureBuffer[1].put(x2);
        textureBuffer[1].put(y2);

        textureBuffer[1].position(0);

    }

    public void draw(GL10 gl, int i, Context context) {
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureBuffer[i]);
    }

}
