package de.brod.opengl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;

import javax.microedition.khronos.opengles.GL10;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.Toast;

public abstract class OpenGLActivity<Square extends OpenGLSquare, Rectangle extends OpenGLRectangle, Button extends OpenGLButton>
        extends Activity {

    private PopupWindow gameSelectPopupWindow;
    private Locale myLocale;
    private IMenuAction _contextMenu;
    private Hashtable<Integer, IAction> _contextMenuItems;

    public void confirm(String sTitle, String sConfirmText, String sButtonYes, final IAction iActionYes, String sButtonNo, final IAction iActionNo) {
        DialogInterface.OnClickListener listenYes = iActionYes != null ? new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                iActionYes.doAction();
            }
        } : null;
        DialogInterface.OnClickListener listenNo = iActionNo != null ? new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                iActionNo.doAction();
            }
        } : null;
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(sTitle)
                .setMessage(sConfirmText)
                .setPositiveButton(sButtonYes, listenYes)
                .setNegativeButton(sButtonNo, listenNo)
                .show();
    }

    public void showText(String sText) {
        Toast.makeText(this, sText, Toast.LENGTH_SHORT).show();
    }


    public abstract float[] getColorsRGB();

    public void drawItems(GL10 gl) {
        for (Rectangle rect : lstRectangles) {
            rect.draw(gl, this);
        }
        // draw the buttons
        for (Button rect : lstButtons) {
            rect.draw(gl, this);
        }

        Square old = null;
        // draw the squares
        for (Square s : lstSquares) {
            s.draw(gl, old, this);
            old = s;
        }

    }

    class ThinkThread extends Thread {
        IMoves action = null;
        boolean finished = false;
        OpenGLActivity activity;

        public ThinkThread(IMoves pAction, OpenGLActivity pActivity) {
            action = pAction;
            activity = pActivity;
            start();
        }

        @Override
        public void run() {
            action.makeNextMove();
            saveState();
            // draw again
            requestRender();
        }

        public boolean isFinished() {
            return finished && !isAlive();
        }

        public boolean tryToFinish() {
            if (!isAlive()) {
                synchronized (activity) {
                    for (OpenGLRectangle iDrawArea : lstRectangles) {
                        iDrawArea.organize();
                    }
                    initMover();
                }
                requestRender();
                finished = true;
            }
            return finished;
        }

    }

    protected abstract void saveState();

    private List<Square> lstSquares = new ArrayList<Square>();
    private List<Square> lstMove = new ArrayList<Square>();
    private List<Square> lstMover = new ArrayList<Square>();
    private List<Rectangle> lstRectangles = new ArrayList<Rectangle>();
    private List<Button> lstButtons = new ArrayList<Button>();

    private long moverStart;
    private ThinkThread thinkThread = null;
    private OpenGLView<Square, Rectangle, Button> view;
    private OpenGLButton _pressedButton;

    protected View getView() {
        return view;
    }

    protected boolean isThinking() {
        if (thinkThread == null) {
            return false;
        }
        synchronized (lstMover) {
            if (thinkThread.isFinished()) {
                thinkThread = null;
                return false;
            }
        }
        return true;
    }

    protected void requestRender() {
        view.requestRender();
    }

    boolean actionUp(float eventX, float eventY) {
        if (isThinking()) {
            return true;
        }
        if (_pressedButton != null) {
            if (_pressedButton.touches(eventX, eventY)) {
                _pressedButton.action();
            }
            _pressedButton.setPressed(false);
            return true;
        }
        boolean bMove = lstMove.size() > 0;
        if (bMove) {
            Rectangle selGLRectangle = null;
            for (Rectangle openGLRectangle : lstRectangles) {
                if (openGLRectangle.touches(eventX, eventY)) {
                    selGLRectangle = openGLRectangle;
                    break;
                }
            }
            actionUp(lstMove, selGLRectangle);
            initMover();
            hasNextAction();
            return true;
        } else {
            lstMover.clear();
        }

        return hasNextAction();
    }

    private boolean hasNextAction() {
        // get the next action
        IMoves action = getAction();
        if (action.hasNextMove()) {
            synchronized (lstMover) {
                // create a new thread (because thinking was false)
                thinkThread = new ThinkThread(action, this);
            }
            // repaint
            return true;
        }
        return false;
    }

    private void initMover() {
        lstMover.clear();
        moverStart = System.currentTimeMillis();
        for (Square s : lstSquares) {
            if (s.move(0)) {
                lstMover.add(s);
                s.setLevel(1);
            } else {
                s.setLevel(0);
            }
        }
        sortSqares();
    }

    protected abstract void actionUp(List<Square> lstMove2,
                                     Rectangle openGLRectangle);

    boolean actionMove(float eventX, float eventY) {
        if (isThinking()) {
            return true;
        }
        boolean bMove = lstMove.size() > 0;
        if (bMove) {
            for (OpenGLSquare s : lstMove) {
                s.moveTo(eventX, eventY);
            }
            sortSqares();
        }
        return bMove;
    }

    boolean actionDown(float eventX, float eventY) {
        lstMove.clear();
        moverStart = 0;
        if (isThinking()) {
            return true;
        }
        _pressedButton = null;
        slideSquares(true);
        for (OpenGLButton button : lstButtons) {
            if (button.touches(eventX, eventY)) {
                _pressedButton = button;
                button.setPressed(true);
                return true;
            }
        }
        for (int i = lstSquares.size() - 1; i >= 0; i--) {
            Square s = lstSquares.get(i);
            if (s.touches(eventX, eventY)) {
                actionDown(s, lstMove);
                break;
            }
        }
        for (OpenGLSquare s : lstSquares) {
            s.setLevel(0);
        }
        boolean bMove = lstMove.size() > 0;
        if (bMove) {
            // set the touch flags
            for (OpenGLSquare s : lstMove) {
                s.setLevel(1);
                s.setTouch(eventX, eventY);
            }
            // sortSqares();
        }
        return bMove;
    }

    public abstract void actionDown(Square pDown, List<Square> plstMove);

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        // no title
        initConfiguration(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // set the view
        view = new OpenGLView<Square, Rectangle, Button>(this);

        setContentView(view);
    }

    protected abstract void initConfiguration(Bundle savedInstanceState);

    protected void sortSqares() {
        Collections.sort(lstSquares);
    }

    protected abstract void init(float pWd, float pHg, List<Square> lstSquares,
                                 List<Rectangle> lstIDrawArea, List<Button> lstButtons);

    public void refreshView(float pWd, float pHg) {

        for (OpenGLRectangle iDrawArea : lstRectangles) {
            iDrawArea.organize();
        }
        for (Square square : lstSquares) {
            square.move(1);
            square.setLevel(0);
            square.refreshView();
        }
        sortSqares();
    }

    boolean slideSquares(boolean pbMoveToEnd) {
        float f;
        if (moverStart <= 0 || pbMoveToEnd) {
            f = 1;
        } else {
            f = (System.currentTimeMillis() - moverStart) / 1000f;
        }
        boolean bchange = false;
        for (int i = 0; i < lstMover.size(); ) {
            Square c = lstMover.get(i);
            if (c.move(f)) {
                i++;
            } else {
                c.setLevel(0);
                bchange = true;
                lstMover.remove(i);
            }
        }
        if (bchange) {
            sortSqares();
        }
        boolean b = lstMover.size() > 0;
        synchronized (lstMover) {
            if (!b && thinkThread != null) {
                // try to finish
                if (thinkThread.tryToFinish()) {
                    thinkThread = null;
                    // check next action
                    hasNextAction();
                    return true;
                }
            }
        }
        return b;
    }

    protected abstract IMoves getAction();

    public void onViewCreate() {
        lstRectangles.clear();
        lstSquares.clear();
        lstMove.clear();
        lstButtons.clear();

        Display display = getWindowManager().getDefaultDisplay();
        float width = display.getWidth();
        float height = display.getHeight();
        if (width > height) {
            width = width / height;
            height = 1;
        } else {
            height = height / width;
            width = 1;
        }

        init(width, height, lstSquares, lstRectangles, lstButtons);

        sortSqares();
    }

    public void clearAll() {
        synchronized (this) {
            for (Square square : lstSquares) {
                square.clear();
            }
            for (Rectangle square : lstRectangles) {
                square.clear();
            }
            for (Button square : lstButtons) {
                square.clear();
            }
            lstSquares.clear();
            lstRectangles.clear();
            lstButtons.clear();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        htMenuActions.clear();
        addMenuItems(htMenuActions, menu, getMenuActions());
        return true;
    }

    private void addMenuItems(Hashtable<Integer, IAction> htMenuActions, Menu pMenu, List<IMenuAction> lst) {
        Log.d("Create Menu", lst.size() + " entries");
        for (int i = 0; i < lst.size(); i++) {
            IMenuAction action = lst.get(i);
            addMenuItem(htMenuActions, pMenu, action);
        }
    }

    private void addMenuItem(Hashtable<Integer, IAction> htMenuActions, Menu pMenu, IMenuAction pAction) {
        int iCounter = htMenuActions.size() + 1;
        List<IMenuAction> lst = pAction.getSubMenu();
        if (lst == null || lst.size() == 0) {
            pMenu.add(0, iCounter, 0, pAction.getTitle());
            htMenuActions.put(Integer.valueOf(iCounter), pAction);
        } else {
            SubMenu subMenu = pMenu.addSubMenu(pAction.getTitle());
            for (IMenuAction mAction : lst) {
                addMenuItem(htMenuActions, subMenu, mAction);
            }
        }
    }

    protected abstract List<IMenuAction> getMenuActions();

    Hashtable<Integer, IAction> htMenuActions = new Hashtable<Integer, IAction>();

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        IAction action = htMenuActions.get(Integer.valueOf(item.getItemId()));
        if (action != null) {
            action.doAction();
            return true;
        }
        return false;
    }

    public void selectLocale(String psLocale) {
        // supported languages
        // https://developers.google.com/igoogle/docs/i18n?csw=1
        Log.d("Select Locale", psLocale);
        Locale locale = new Locale(psLocale);
        if (psLocale.equalsIgnoreCase("")) {
			return;
		}
        myLocale = new Locale(psLocale);
        saveLocale(psLocale);
        Locale.setDefault(myLocale);
        android.content.res.Configuration config = new android.content.res.Configuration();
        config.locale = myLocale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
    }

    public void restart() {
        Intent i = getBaseContext().getPackageManager()
                .getLaunchIntentForPackage(getBaseContext().getPackageName());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finish();
        startActivity(i);
    }

    public void saveLocale(String lang) {
        SharedPreferences prefs = getSharedPreferences("CommonPrefs", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("Language", lang);
        editor.commit();
    }

    public void loadLocale() {
        SharedPreferences prefs = getSharedPreferences("CommonPrefs", Activity.MODE_PRIVATE);
        String language = prefs.getString("Language", "");
        selectLocale(language);
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (_contextMenu == null) {
			return;
		}
        List<IMenuAction> subMenu = _contextMenu.getSubMenu();
        _contextMenuItems = new Hashtable<Integer, IAction>();
        if (subMenu != null && subMenu.size() > 0) {
            menu.setHeaderTitle(_contextMenu.getTitle());
            addMenuItems(_contextMenuItems, menu, subMenu);
        } else {
            addMenuItem(_contextMenuItems, menu, _contextMenu);
        }
    }

    protected void openPopupMenu(IMenuAction pMenu) {
        _contextMenu = pMenu;
        View view = getView();
        registerForContextMenu(view);
        openContextMenu(view);
        unregisterForContextMenu(view);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (_contextMenuItems == null) {
			return false;
		}
        IAction iAction = _contextMenuItems.get(item.getItemId());
        if (iAction == null) {
			return false;
		}
        iAction.doAction();
        return true;
    }
}
