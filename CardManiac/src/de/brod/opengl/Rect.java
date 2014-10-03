package de.brod.opengl;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;

public class Rect {

	private static Grid<Rect>	_grid;

	public static void onSurfaceChanged(OpenGLActivity pActivity, GL10 gl,
			int width, int height) {

		Bitmap bmp = Bitmap.createBitmap(256, 256, Config.ARGB_8888);
		Canvas c = new Canvas(bmp);

		Paint paint = new Paint();
		int bd = 10;
		float r = bd * 2;
		paint.setStrokeWidth(bd * 1.5f);

		float wd1 = 255 - bd - r / 3;
		float bd2 = bd + r / 2;
		float wd2 = 255 - bd - r / 4;

		RectF rect = new RectF(bd, bd, wd1, wd1);

		paint.setColor(Color.BLACK);
		paint.setStyle(Style.STROKE);
		c.drawRoundRect(rect, r, r, paint);

		rect = new RectF(bd2, bd2, wd2, wd2);

		paint.setColor(Color.WHITE);
		paint.setStyle(Style.FILL_AND_STROKE);
		c.drawRoundRect(rect, r, r, paint);

		_grid = new Grid<Rect>(3, 3, gl, bmp);
		bmp.recycle();
	}

	private List<Sprite<Rect>>	_sprite	= new ArrayList<Sprite<Rect>>();
	private Sprite<Rect>		_centerSprite;
	private boolean				_down;
	private float				_width, _height, _x, _y;
	private boolean				_bRoundBorder;

	public Rect(float px, float py, float pwidth, float pheight,
			boolean pbRoundBorder) {
		_width = pwidth;
		_height = pheight;
		_x = px;
		_y = py;
		_bRoundBorder = pbRoundBorder;
		_centerSprite = _grid.createSprite(1, 1, _x, _y, _width, _height);
		if (pbRoundBorder) {
			// addSprite(centerSprite);
			float min = Math.min(_width, _height) / 3;
			float x1 = (_width - min) / 2;
			float y1 = (_height - min) / 2;
			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < 3; j++) {
					float xp = _x + x1 * (i - 1);
					float yp = _y - y1 * (j - 1);
					float widthp = i == 1 ? (_width - min * 2) : min;
					float heightp = j == 1 ? (_height - min * 2) : min;
					Sprite<Rect> spriteItem = _grid.createSprite(i, j, xp, yp,
							widthp, heightp);
					addSprite(spriteItem);
				}
			}
			_down = true;

		} else {
			addSprite(_centerSprite);
		}
	}

	private void addSprite(Sprite<Rect> sprite) {
		sprite.setReference(this);
		_sprite.add(sprite);
	}

	public void draw(GL10 gl) {
		for (ISprite<Rect> sprite : _sprite) {
			sprite.draw(gl);
		}
	}

	public boolean isDown() {
		return _down;
	}

	public void setColor(int color) {
		int r = Color.red(color);
		int g = Color.green(color);
		int b = Color.blue(color);
		int a = Color.alpha(color);
		setColor(r, g, b, a);
	}

	public void setColor(int pr, int pg, int pb, int pa) {
		for (ISprite<Rect> sprite : _sprite) {
			sprite.setColor(pr, pg, pb, pa);
		}
	}

	public void setDown(boolean pbDown) {
		if (_down != pbDown && _bRoundBorder) {
			_down = pbDown;
			float min = Math.min(_width, _height) / 3;
			float x1 = (_width - min) / 2;
			float y1 = (_height - min) / 2;
			float a = pbDown ? 1 : -1;
			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < 3; j++) {
					Sprite<Rect> iSprite = _sprite.get(i * 3 + j);
					float xp = _x + a * x1 * (i - 1);
					float yp = _y - a * y1 * (j - 1);
					float widthp = i == 1 ? (_width - min * 2) : min;
					float heightp = j == 1 ? (_height - min * 2) : min;
					iSprite.setSize(widthp * a / 2, heightp * a / 2);
					iSprite.setXY(xp, yp);
				}
			}
		}
	}

	public boolean touches(float eventX, float eventY) {
		return _centerSprite.touches(eventX, eventY);
	}

}