/*
 * ******************************************************************************
 * Copyright (c) 2013 Andreas Brod
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * *****************************************************************************
 */
package de.brod.gui.shape;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.khronos.opengles.GL10;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import de.brod.gui.IAction;
import de.brod.gui.Texture;

public class Button extends Sprite {

	public enum Type {
		bluetoth(0, 0), statistcs(1, 0), wlan(2, 0), sound_off(3, 0), sound_on(
				4, 0), stop(5, 0), attention(0, 1), yes(1, 1), previous(2, 1), no(
				3, 1), up(4, 1), down(5, 1), next(0, 2), right(1, 2), left(2, 2), reload(
				3, 2), info(4, 2), question(5, 2), search(0, 3), settings(1, 3), star_on(
				2, 3), star_off(3, 3), sort(4, 3), delete(5, 3), undo(0, 4), redo(
				1, 4), people2(2, 4), people3(3, 4), people(4, 4), menu(5, 4);

		private int x, y;

		private Type(int px, int py) {
			x = px;
			y = py;
		}

		public Button createButton(float px, float py, float pfHeight,IAction action) {
			Button button = new Button(this.toString(), pfHeight, action);
			button.setCell(x, y, x + 6, y);
			button.setPosition(px, py);
			return button;
		}

		public Button createButton(float px, float py, IAction action) {
			Button button = new Button(this.toString(), action);
			button.setCell(x, y, x + 6, y);
			button.setPosition(px, py);
			return button;
		}
		
		public Button createButton(int i, int piCount, boolean bTop,
				Align align, IAction action) {
			float px;
			float py;
			if (bTop) {
				py = maxHeight - height / 2;
			} else {
				py = -maxHeight + height / 2;
			}
			if (align.equals(Align.RIGHT)) {
				px = maxWidth - width * (i + 0.5f);
			} else if (align.equals(Align.LEFT)) {
				px = -maxWidth + width * (i + 0.5f);
			} else {
				px = maxWidth - (maxWidth * (i + 0.5f) * 2) / piCount;
			}

			return createButton(px, py, action);
		}
	}

	private static Texture texture = null, emptyTexture = null;

	public static float width = 0;

	public static float height = 0;

	public static float maxHeight;

	public static float maxWidth;
	public static Button createTextButton(float px, float py, float pfHeight, String psText,
			IAction action) {
		if (pfHeight==0) pfHeight=1;
		       else pfHeight=height/pfHeight;
		Button button = new Button(emptyTexture, width/pfHeight, height/pfHeight, psText, action);
		button.setCell(0, 0, 1, 0);
		button.setPosition(px, py);
		button.setText(psText);
		return button;
	}
	private static void drawEmptyButton(Bitmap bitmap) {
		int h = bitmap.getHeight();
		Canvas c = new Canvas(bitmap);
		float r = h / 10f;
		Paint paint = new Paint();
		RectF rect = new RectF(r, r, h - r, h - r);
		RectF rect2 = new RectF(r + h, r, h - r + h, h - r);

		paint.setColor(Color.argb(64, 255, 255, 255));
		paint.setStyle(Style.FILL);
		c.drawRoundRect(rect, r, r, paint);

		// paint.setColor(Color.argb(32, 255,255,255));
		// paint.setStyle(Style.FILL);
		// c.drawRoundRect(rect2, r, r, paint);

		paint.setColor(Color.argb(128, 0, 0, 0));
		paint.setStyle(Style.STROKE);
		paint.setStrokeWidth(Math.max(1, r / 2));
		c.drawRoundRect(rect, r, r, paint);

		paint.setColor(Color.argb(32, 0, 0, 0));
		paint.setStyle(Style.STROKE);
		paint.setStrokeWidth(Math.max(1, r / 2));
		c.drawRoundRect(rect2, r, r, paint);

	}
	public static void init(AssetManager assetManager, GL10 gl, int pWidth,
			int pHeight, float pfTitleHeight) {
		float ratio = pWidth * 1f / pHeight;
		if (ratio < 1) { // width
			maxHeight = 1 / ratio;
			maxWidth = 1;
		} else {
			maxWidth = ratio;
			maxHeight = 1;
		}
		width = pfTitleHeight;
		height = width;
		InputStream open;
		try {
			// create an empty texture
			int wdEmpty = 8;
			int minWd = Math.min(pWidth, pHeight) / 8;
			while (wdEmpty < minWd) {
				wdEmpty *= 2;
			}
			Bitmap bitmap = Bitmap.createBitmap(wdEmpty * 2, wdEmpty,
					Config.ARGB_8888);
			drawEmptyButton(bitmap);
			emptyTexture = new Texture(gl, bitmap, 2, 1);
			bitmap.recycle();
			// open the icons
			open = assetManager.open("icons.png");
			bitmap = BitmapFactory.decodeStream(open);
			open.close();
			texture = new Texture(gl, bitmap, 12, 5);
			bitmap.recycle();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private Text createText = null;

	private int foreColor = -1;
	private String type;

	IAction action;

	private boolean enabled = true;

	private Button(String psType, float pfHeight,IAction action) {
		this(texture, pfHeight* width/height, pfHeight, psType, action);
	}
	private Button(String psType, IAction action) {
		this(texture, width, height, psType, action);
	}
	public Button(Texture pTexture, float piWidth, float piHeight,
			String psType, IAction action) {
		super(pTexture, piWidth, piHeight);
		this.type = psType;
		this.action = action;
		setMoveable(false);
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void pressed() {
		// setColor(Color.BLUE);
		if (enabled && action != null) {
			action.action();
		}
	}
	
	public void release() {
		// setColor(Color.WHITE);
	}

	public void resize(float f) {
		setSize(width * f, height * f);
	}

	@Override
	public void setColor(int col) {
		if (createText == null) {
			super.setColor(col);
		} else {
			foreColor = col;
			createText.setColor(col);
		}
	}

	public void setEnabled(boolean pbEnabled) {
		if (enabled != pbEnabled) {
			this.enabled = pbEnabled;
			setAngle(pbEnabled ? 0 : 180);
		}
		if (createText != null) {
			createText
					.setColor(pbEnabled ? foreColor : Color.argb(64, 0, 0, 0));
		}
	}

	public void setText(String psText) {
		float px = getX();
		float py = getY();
		float h = getHeight();
		createText = Text.createText(psText, px, py, h);
		createText.setPosition(px - createText.getTextWdith() / 2,
				createText.getY() - h / 2);
		clear();
		add(createText);
	}

	public void setTextColor(int col) {
		foreColor = col;
		setColor(col);
	}

	@Override
	public String toString() {
		return type;
	}
}
