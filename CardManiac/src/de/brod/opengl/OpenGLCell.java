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

    public OpenGLCell(OpenGLTexture pTextures, float x, float y) {
        textures = pTextures.textures;
        t = pTextures;
        textureBuffer = new FloatBuffer[2];
        for (int i = 0; i < textureBuffer.length; i++) {
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(8 * 4);
            byteBuffer.order(ByteOrder.nativeOrder());
            textureBuffer[i] = byteBuffer.asFloatBuffer();
        }
        setGrid(x, y);
    }

    protected void setGrid(float x, float y) {
        setGrid(x, y, x + 1, y + 1);
    }

    protected void setGrid(float x, float y, float xMax, float yMax) {

        textureBuffer[0].position(0);

        float x1 = x / t.cx;
        float y1 = y / t.cy;
        float y2 = yMax / t.cy;
        float x2 = xMax / t.cx;

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
