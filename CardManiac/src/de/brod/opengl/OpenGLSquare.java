package de.brod.opengl;

import android.content.Context;

import javax.microedition.khronos.opengles.GL10;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class OpenGLSquare implements Comparable<OpenGLSquare> {

    private static short[] _indices = {0, 1, 2, 0, 2, 3};
    private static int[] _points = {0, 1, 2, 3, 0};
    private static float[] _edges = {-1, 1, -1, -1, 1, -1, 1, 1};

    public boolean move(float f) {
        if (_d == 0) {
            return false;
        } else if (f >= 1 || f >= _d || _d <= 0.05f) {
            _x = _xEnd;
            _y = _yEnd;
            _d = 0;
            refreshView();
            return false;
        }
        float d = f / _d;
        _x = _xStart + _dx * d;
        _y = _yStart + _dy * d;
        refreshView();
        return true;
    }

    private static class Line {
        float x, y, dx, dy;

        void set(float x1, float y1, float x2, float y2) {
            x = x1;
            y = y1;
            dx = x2 - x1;
            dy = y2 - y1;
        }

        float getX(float d) {
            return x + dx * d;
        }

        float getY(float d) {
            return y + dy * d;
        }
    }

    private FloatBuffer _vertexBuffer;
    private ShortBuffer _indexBuffer;

    private float _x, _y, _width, _height;
    private float _xStart, _yStart, _xEnd, _yEnd, _dx, _dy, _d;
    private float _touchX, _touchY;
    private float _r, _g, _b, _a;
    private float _rY;
    private long _color;

    private OpenGLCell _cell;

    private float[] _range = new float[4];
    private float[] _verticles = new float[4 * 3];

    private boolean _visible;
    private Line _line = new Line();
    private float _xy = -123456;
    private int _iLevel = 0, _iOrder = 0;
    private float _rotY = 0;
    private int _iUp = 0;
    private OpenGLText _openGLText;

    public OpenGLSquare(float px, float py, float wd, float hg, OpenGLCell cell) {
        _x = px;
        _y = py;
        _width = wd / 2;
        _height = hg / 2;
        _cell = cell;

        // a float is 4 bytes, therefore we multiply the number if vertices with 4.
        ByteBuffer vbb = ByteBuffer.allocateDirect(3 * 4 * 4);
        vbb.order(ByteOrder.nativeOrder());
        _vertexBuffer = vbb.asFloatBuffer();
        refreshView();

        // short is 2 bytes, therefore we multiply the number if vertices with 2.
        ByteBuffer ibb = ByteBuffer.allocateDirect(_indices.length * 2);
        ibb.order(ByteOrder.nativeOrder());
        _indexBuffer = ibb.asShortBuffer();
        _indexBuffer.put(_indices);
        _indexBuffer.position(0);

        clearColor();
    }

    protected void setXY(float x, float y) {
        _xEnd = Math.max(_width - OpenGLView._wd,
                Math.min(OpenGLView._wd - _width, x));
        _yEnd = Math.max(_height - OpenGLView._hg,
                Math.min(OpenGLView._hg - _height, y));

        _xStart = _x;
        _yStart = _y;
        _dx = _xEnd - _xStart;
        _dy = _yEnd - _yStart;
        _d = Math.min(_dx * _dx + _dy * _dy, 1);
        // y=touchY+eventY;
        // refreshView();
        if (_openGLText != null) {
            _openGLText.setXY(x, y);
        }
    }

    void moveTo(float eventX, float eventY) {
        _x = Math.max(_width - OpenGLView._wd,
                Math.min(OpenGLView._wd - _width, _touchX + eventX));
        _y = Math.max(_height - OpenGLView._hg,
                Math.min(OpenGLView._hg - _height, _touchY + eventY));
        // y=touchY+eventY;
        refreshView();
        if (_openGLText != null) {
            _openGLText.moveTo(eventX, eventY);
        }
    }

    boolean touches(float eventX, float eventY) {
        if (_visible) {
            return false;
        }
        if (eventX < _range[0] || eventX > _range[1]) {
            return false;
        }
        if (eventY < _range[2] || eventY > _range[3]) {
            return false;
        }
        boolean oddNodes = false;
        float x1, y1, x2 = 0, y2 = 0;
        for (int a = 0; a < _points.length; a++) {
            int i = _points[a] * 3;
            x1 = _verticles[i];
            y1 = _verticles[i + 1];
            if (a > 0) {
                if (((y1 < eventY) && (y2 >= eventY)) || (y1 >= eventY)
                        && (y2 < eventY)) {
                    if ((eventY - y1) / (y2 - y1) * (x2 - x1) < (eventX - x1)) {
                        oddNodes = !oddNodes;
                    }
                }
            }
            x2 = x1;
            y2 = y1;
        }
        _touchX = _x - eventX;
        _touchY = _y - eventY;
        return oddNodes;
    }

    void setTouch(float eventX, float eventY) {
        _touchX = _x - eventX;
        _touchY = _y - eventY;
    }

    public OpenGLSquare setColor(int pr, int pg, int pb, int pa) {
        _r = pr / 255f;
        _g = pg / 255f;
        _b = pb / 255f;
        _a = pa / 255f;
        _color = ((pr * 256) + pg) * 256 + pb;
        if (_openGLText != null) {
            _openGLText.setColor(pr, pg, pb, pa);
        }
        return this;
    }

    public OpenGLSquare clearColor() {
        _color = 0;
        _r = 1;
        _g = 1;
        _b = 1;
        _a = 1;

        if (_openGLText != null) {
            _openGLText.clearColor();
        }

        return this;
    }


    /**
     * This function draws our square on screen.
     */
    void draw(GL10 gl, OpenGLSquare old, Context context) {

        if (_cell != null) {
            // Specifies the location and data format of an array of vertex
            // coordinates to use when rendering.
            if (old == null || old._color != _color) {
                gl.glColor4f(_r, _g, _b, _a);
            }

            gl.glVertexPointer(3, GL10.GL_FLOAT, 0, _vertexBuffer);
            _cell.draw(gl, _iUp, context);

            gl.glDrawElements(GL10.GL_TRIANGLES, _indices.length,
                    GL10.GL_UNSIGNED_SHORT, _indexBuffer);
        }
        if (_openGLText != null) {
            _openGLText.draw(gl, old, context);
        }

    }

    public boolean isVisible() {
        return _visible;
    }

    public void setVisible(boolean visible) {
        this._visible = visible;
        if (_openGLText != null) {
            _openGLText.setVisible(visible);
        }
    }

    void refreshView() {
        float xy = _x - _y * 4f;
        if (xy == _xy && _rY == _rotY) {
            return;
        }
        _xy = xy;
        _rY = _rotY;
        int iPos = 0;
        for (int k = 0; k < _edges.length; k++) {
            float x = (_x + _width * _edges[k]);
            k++;
            float y = _y + _height * _edges[k];
            _verticles[iPos] = x;
            iPos++;
            _verticles[iPos] = y;
            iPos += 2;
        }
        _iUp = 0;
        if (_rotY > 0) {
            float dy;
            if (_rotY > 0.5) {
                dy = 1 - _rotY;
                _iUp = 1;
            } else {
                dy = _rotY;
            }
            float r2 = 1 - _rotY;
            for (int i = 0; i <= 3; i += 3) {
                int i1 = i + 1;
                int i2 = i + 6;
                int i3 = i2 + 1;
                _line.set(_verticles[i], _verticles[i1], _verticles[i2],
                        _verticles[i3]);
                if (_iUp == 1) {
                    _verticles[i] = _line.getX(_rotY);
                    _verticles[i2] = _line.getX(r2);
                    _verticles[i1] = _line.getY(1 + dy);
                    _verticles[i3] = _line.getY(0);
                } else {
                    _verticles[i] = _line.getX(_rotY);
                    _verticles[i2] = _line.getX(r2);
                    _verticles[i1] = _line.getY(0);
                    _verticles[i3] = _line.getY(1 + dy);
                }
            }
        }
        _vertexBuffer.position(0);
        _vertexBuffer.put(_verticles);
        _vertexBuffer.position(0);
        // min max X
        _range[0] = _verticles[0];
        _range[1] = _verticles[0];
        for (int i = 3; i <= 9; i += 3) {
            _range[0] = Math.min(_range[0], _verticles[i]);
            _range[1] = Math.max(_range[1], _verticles[i]);
        }
        // min max y
        _range[2] = _verticles[1];
        _range[3] = _verticles[1];
        for (int i = 4; i <= 10; i += 3) {
            _range[2] = Math.min(_range[2], _verticles[i]);
            _range[3] = Math.max(_range[3], _verticles[i]);
        }
    }

    @Override
    public int compareTo(OpenGLSquare another) {
        int dif = _iLevel - another._iLevel;
        if (dif != 0) {
            return dif;
        }
        float diff = _xy - another._xy;
        if (diff < 0) {
            return -1;
        }
        if (diff > 0) {
            return 1;
        }
        return _iOrder - another._iOrder;
    }

    protected void setLevel(int level) {
        _iLevel = level;
    }

    public int getOrder() {
        return _iOrder;
    }

    public void setOrder(int piOrder) {
        this._iOrder = piOrder;
    }

    public void setRotateY(float pRotY) {
        if (_rotY != pRotY) {
            _rotY = pRotY;
            refreshView();
            if (_openGLText != null) {
                _openGLText.setRotateY(pRotY);
            }
        }
    }

    public void clear() {
        if (_openGLText != null) {
            _openGLText.clear();
        }
    }

    public void setText(String psText) {
        clear();
        _openGLText = new OpenGLText(_x, _y, _width * 2, _height * 2, psText);
    }
}
