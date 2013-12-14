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

import javax.microedition.khronos.opengles.GL10;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import de.brod.gui.Texture;

public class Frame extends Container {

	private static Texture emptyTexture;
	private Sprite[] ul = new Sprite[9];
	private float _width;
	private float _height;
	private float x, y;

	public static float border = 0;

	public Frame(float px, float py, float width, float height) {
		for (int j = 0; j < 3; j++) {
			for (int i = 0; i < 3; i++) {
				int a = j * 3 + i;
				ul[a] = new Sprite(emptyTexture, border, border);
				ul[a].setCell(i, j, 3 + i, j);
				add(ul[a]);
			}
		}

		setDimension(px, py, width, height, false);
	}

	public void setDimension(float px, float py, float width, float height,
			boolean pbAddBorder) {

		x = px;
		y = py;
		y = py;
		if (height < 0) {
			y += height;
			height = -height;
		}
		if (width < 0) {
			x += width;
			width = -width;
		}
		_width = width;
		_height = height;
		if (pbAddBorder) {
			x -= border / 2;
			y -= border / 2;
			width += border;
			height += border;
		}
		// x -= border;
		// y -= border;
		// width += border * 2;
		// height += border * 2;

		float left = x + border / 2;
		float center = x + width / 2;
		float right = x + width - border / 2;

		float bottom = y + border / 2;
		float middle = y + height / 2;
		float top = y + height - border / 2;

		float maxW = width - border * 2;
		float maxH = height - border * 2;

		ul[0].setDimension(left, top, border, border);
		ul[1].setDimension(center, top, maxW, border);
		ul[2].setDimension(right, top, border, border);

		ul[3].setDimension(left, middle, border, maxH);
		ul[4].setDimension(center, middle, maxW, maxH);
		ul[5].setDimension(right, middle, border, maxH);

		ul[6].setDimension(left, bottom, border, border);
		ul[7].setDimension(center, bottom, maxW, border);
		ul[8].setDimension(right, bottom, border, border);

	}

	public static void init(AssetManager assetManager, GL10 gl, int pWidth,
			int pHeight, float pfTitleHeight) {
		border = pfTitleHeight / 3;
		// create an empty texture
		int wdEmpty = 8;
		int minWd = Math.min(pWidth, pHeight) / 8;
		while (wdEmpty < minWd) {
			wdEmpty *= 2;
		}
		Bitmap bitmap = Bitmap.createBitmap(wdEmpty * 2, wdEmpty,
				Config.ARGB_8888);
		Button.drawEmptyButton(bitmap, 220);
		emptyTexture = new Texture("SYS_FRAME", gl, bitmap, 6, 3);
		bitmap.recycle();
	}

	@Override
	public boolean touches(float eventX, float eventY) {
		if (x > eventX || y > eventY) {
			return false;
		}
		if (x + _width < eventX || y + _height < eventY) {
			return false;
		}
		return true;
	}

	public float getWidth() {
		return _height;
	}

	public float getHeight() {
		return _width;
	}

	public void setValue(int i) {
		// TODO Auto-generated method stub

	}

}
