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

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import de.brod.gui.Texture;

public class Rectangle extends Sprite {

	private static Texture texture;

	public static void init(GL10 gl, float fTitleHeight) {
		// create an empty bitmap
		Bitmap bmp = Bitmap.createBitmap(128, 128, Config.ARGB_8888);
		new Canvas(bmp).drawColor(Color.WHITE);
		texture = new Texture("SYS_WHITE", gl, bmp, 3, 3);
		bmp.recycle();
	}

	public Rectangle(float px, float py, float width, float height) {
		super(texture, width, height);
		setCenter(false);
		setPosition(px, py);
		setCell(1, 1, 1, 1);
	}
}
