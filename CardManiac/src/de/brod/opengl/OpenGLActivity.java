package de.brod.opengl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.Window;
import android.view.WindowManager;

public abstract class OpenGLActivity extends Activity {
	private OpenGLView mGLView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		mGLView = new OpenGLView(this);
		setContentView(mGLView);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onPause() {
		super.onPause();
		mGLView.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mGLView.onResume();
	}

	public void requestRender() {
		mGLView.requestRender();
	}

	public abstract void initSprites(GL10 gl, List<ISprite<?>> lstSprites,
			List<Rect> lstRectangles);

	public Bitmap loadBitmap(int pDrawSource) {
		InputStream is;
		Bitmap bitmap;
		is = getResources().openRawResource(pDrawSource);

		bitmap = BitmapFactory.decodeStream(is);
		try {
			is.close();
			is = null;
		} catch (IOException e) {
		}
		return bitmap;
	}

	public abstract boolean actionDown(float eventX, float eventY);

	public abstract boolean actionMove(float eventX, float eventY);

	public abstract boolean actionUp(float eventX, float eventY);

	public abstract int getColor();

	public abstract boolean onDrawFrame();

	Hashtable<Integer, IAction> htActions = new Hashtable<Integer, IAction>();

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		// menu.add(0, MENU_ADD, Menu.NONE,
		// R.string.your-add-text).setIcon(R.drawable.your-add-icon);

		List<IAction> lstMenuActions = new ArrayList<IAction>();
		fillMenuActions(lstMenuActions);
		int iGroup = 0;
		int iCount = 0;
		for (int i = 0; i < lstMenuActions.size(); i++) {
			IAction action = lstMenuActions.get(i);
			if (action instanceof ISubAction) {
				SubMenu addSubMenu = menu.addSubMenu(action.getTitle());
				for (IAction subAction : ((ISubAction) action).getSubItems()) {
					iCount++;
					htActions.put(Integer.valueOf(iCount), subAction);
					addSubMenu.add(iGroup, iCount, Menu.NONE,
							subAction.getTitle());
				}
				iGroup++;
			} else {
				iCount++;
				htActions.put(Integer.valueOf(iCount), action);
				menu.add(iGroup, iCount, Menu.NONE, action.getTitle());
			}
		}
		return true;
	}

	public abstract void fillMenuActions(List<IAction> plstMenuActions);

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		try {
			IAction iAction = htActions.get(Integer.valueOf(item.getItemId()));
			iAction.doAction();

			requestRender();
		} catch (Exception ex) {
			// ignore
		}
		return true;

	}

	protected void confirm(String psText, final Runnable run) {
		new AlertDialog.Builder(this)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setTitle("Confirm")
				.setMessage(psText)
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								run.run();
								requestRender();
							}
						}).setNegativeButton("No", null).show();
	}

	public class StateReader<E extends Serializable> {

		private String _sKey;

		public StateReader(String psKey) {
			_sKey = psKey;
		}

		public void saveState(String string, E state) {
			File file = getFile(string);
			try {
				// ObjectInputStream is = new ObjectInputStream(new
				// FileInputStream(
				// file));
				ObjectOutputStream out = new ObjectOutputStream(
						new FileOutputStream(file));
				out.writeObject(state);
				out.close();
			} catch (Exception e) {
				// could not write
				e.printStackTrace();
				// Log.e("OpenGLActivity", e.getLocalizedMessage());
			}
		}

		public E readState(String string) {
			File file = getFile(string);
			try {
				ObjectInputStream is = new ObjectInputStream(
						new FileInputStream(file));
				Object readObject = is.readObject();
				is.close();
				return (E) readObject;
			} catch (Exception e) {
				// could not read
				e.printStackTrace();
				// Log.e("OpenGLActivity", e.getLocalizedMessage());
				return null;
			}

		}

		private File getFile(String string) {
			return new File(getFilesDir(), _sKey + "." + string + ".txt");
		}
	}

}
