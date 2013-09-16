package de.brod.gui.shape;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.khronos.opengles.GL10;

import de.brod.gui.IAction;
import de.brod.gui.Texture;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint.Align;

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

		public Button createButton(float px, float py, IAction action) {
			Button button = new Button(this, action);
			button.setCell(x, y);
			button.setPosition(px, py);
			button.setMoveable(false);
			return button;
		}

		public Sprite createButton(int i, int piCount, boolean bTop,
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

	private static Texture texture = null;
	static float width = 0;
	static float height = 0;
	public static float maxHeight;
	public static float maxWidth;

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
			open = assetManager.open("icons.png");
			Bitmap bitmap = BitmapFactory.decodeStream(open);
			open.close();
			texture = new Texture(gl, bitmap, 12, 5);
			bitmap.recycle();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private Type type;
	IAction action;

	private Button(Type type, IAction action) {
		super(texture, width, height);
		this.type = type;
		this.action = action;
	}

	public void pressed() {
		// setColor(Color.BLUE);
		action.action();
	}

	@Override
	public String toString() {
		return type.toString();
	}

	public void release() {
		// setColor(Color.WHITE);
	}
}
