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
	private Sprite ul;

	public Frame(float px, float py, float width, float height) {
		ul = new Sprite(emptyTexture, width, height);
		ul.setCell(0, 0, 3, 0);
		add(ul);
	}

	public void setDimension(float x, float y, float width, float height) {
		ul.setDimension(x + width / 2, y + height / 2, width, height);
	}

	public static void init(AssetManager assetManager, GL10 gl, int pWidth,
			int pHeight, float pfTitleHeight) {
		// create an empty texture
		int wdEmpty = 8;
		int minWd = Math.min(pWidth, pHeight) / 8;
		while (wdEmpty < minWd) {
			wdEmpty *= 2;
		}
		Bitmap bitmap = Bitmap.createBitmap(wdEmpty * 2, wdEmpty,
				Config.ARGB_8888);
		Button.drawEmptyButton(bitmap);
		emptyTexture = new Texture(gl, bitmap, 6, 3);
		bitmap.recycle();
	}

}
