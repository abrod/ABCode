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
	private Sprite[] ul=new Sprite[9] ;

	private static float border=0;

	public Frame(float px, float py, float width, float height) {
		for (int j=0;j<3;j++)
		for (int i=0;i<3;i++)
		{
			int a=j*3+i;
		ul[a] = new Sprite(emptyTexture, border, border);
		ul[a].setCell(i, j, 3+i, j);
		add(ul[a]);
		}
		
		setDimension(px,py,width,height);
	}

	public void setDimension(float x, float y, float width, float height) {
		x-=border;y-=border;
		width+=border*2;height+=border*2;
		ul[6].setDimension(x + border / 2, y + border / 2, border, border);
		ul[7].setDimension(x + width / 2, y + border / 2, width-border*2, border);
		ul[8].setDimension(x +width- border / 2, y + border / 2, border, border);
		
		ul[3].setDimension(x + border / 2, y + height / 2, border, height-border*2);
		ul[4].setDimension(x + width / 2, y + height / 2, width-border*2,height-border*2);
		ul[5].setDimension(x +width- border / 2, y + height / 2, border, height-border*2);
		
		ul[0].setDimension(x + border / 2, y + height-border / 2, border, border);
		ul[1].setDimension(x + width / 2, y + height-border / 2, width-border*2, border);
		ul[2].setDimension(x +width- border / 2, y + height-border / 2, border, border);
		
	}

	public static void init(AssetManager assetManager, GL10 gl, int pWidth,
			int pHeight, float pfTitleHeight) {
		border = pfTitleHeight/3;
		// create an empty texture
		int wdEmpty = 8;
		int minWd = Math.min(pWidth, pHeight) / 8;
		while (wdEmpty < minWd) {
			wdEmpty *= 2;
		}
		Bitmap bitmap = Bitmap.createBitmap(wdEmpty * 2, wdEmpty,
				Config.ARGB_8888);
		Button.drawEmptyButton(bitmap,220);
		emptyTexture = new Texture(gl, bitmap, 6, 3);
		bitmap.recycle();
	}

}
