package de.brod.game.cm;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import de.brod.cardmaniac.R;
import de.brod.game.cm.games.FreeCell;
import de.brod.game.cm.games.Game;
import de.brod.opengl.IMenuAction;
import de.brod.opengl.IMoves;
import de.brod.opengl.OpenGLActivity;
import de.brod.opengl.OpenGLSquare;
import de.brod.opengl.OpenGLTexture;

public class GameActivity extends OpenGLActivity<Card, Hand, Button> {

    public Game game;
    private List<Card> _lstSquares;
    private List<Hand> _lstIDrawArea;
    private List<Button> _lstButtons;
    private float _wd, _hg;
    private float[] _colors;

    @Override
    protected void initConfiguration(Bundle savedInstanceState) {
        loadLocale();
    }

    @Override
    protected void init(float pWd, float pHg, List<Card> lstSquares,
                        List<Hand> lstIDrawArea, List<Button> lstButtons) {
        _colors = new float[]{0, 0, 0.3f};
        this._lstSquares = lstSquares;
        this._lstIDrawArea = lstIDrawArea;
        this._lstButtons = lstButtons;
        _wd = pWd;
        _hg = pHg;
        Card.initValues(getString(R.string.la_jack_queen_king_ace));
        selectGame(loadLastGameClass());
        //
    }

    public void selectGame(Class<? extends Game> pGameClass) {
        try {
            game = pGameClass.newInstance();
        } catch (Exception e) {
            // fallback
            game = new FreeCell();
        }
        saveGameName();
        game.setActivity(this);

        newGame(true);

        game.organize();
    }

    private void setHandAndButtons() {
        clearAll();

        // organize Hands
        if (game.hand != null) {
            for (Hand h : game.hand) {
                h.organize();
                _lstIDrawArea.add(h);
                for (Card c : h.lstCards) {
                    c.move(1);
                    _lstSquares.add(c);
                }
            }
        }
        // add the buttons
        if (game.button != null) {
            for (Button button : game.button) {
                _lstButtons.add(button);
            }
        }
    }

    @Override
    public void actionDown(Card pDown, List<Card> plstMove) {
        pDown.hand.actionDown(pDown, plstMove);
    }

    @Override
    public float[] getColorsRGB() {
        return _colors;
    }

    private Class<? extends Game> loadLastGameClass() {
        try {
            Log.d("LastGame.txt", "Load");

            BufferedReader in = new BufferedReader(new InputStreamReader(openFileInput("LastGame.txt")));
            String s = in.readLine();
            in.close();
            for (Class<?> gc : Game.gameClasses) {
                if (gc.getName().equals(s)) {
                    return (Class<? extends Game>) gc;
                }
            }
        } catch (FileNotFoundException e) {
            // ignore
            Log.d("LastGame.txt", "... not found");
        } catch (Exception e) {
            // could not read
            e.printStackTrace();
        }
        return FreeCell.class;
    }

    private void saveGameName() {
        FileOutputStream fileOut = null;
        try {
            Log.d("LastGame.txt", "Start " + game.getClass().getName());
            fileOut = openFileOutput("LastGame.txt", Context.MODE_PRIVATE);
            fileOut.write(game.getClass().getName().getBytes());
            fileOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void saveState() {
        try {
            Log.d("saveCards", "Start " + getFileName());
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(bytes);

            out.writeInt(game.hand.length);
            for (Hand h : game.hand) {
                // save the state
                out.writeInt(h.getId());
                h.write(out);
                List<Card> cards = h.getCards();
                out.writeInt(cards.size());
                for (Card c : cards) {
                    out.writeInt(c.getId());
                }
            }
            // final save
            FileOutputStream fileOut = openFileOutput(getFileName(), Context.MODE_PRIVATE);
            fileOut.write(bytes.toByteArray());
            fileOut.close();

        } catch (IOException e) {
            // should not happen on outputstream
            Log.e("saveCards", "Error", e);
        }
    }

    @Override
    protected void actionUp(List<Card> lstMove2, Hand openGLRectangle) {
        if (openGLRectangle != null) {
            openGLRectangle.dirty = true;
            if (lstMove2.size() > 0) {
                openGLRectangle.actionUp(lstMove2);
            }
        }
        for (OpenGLSquare square : lstMove2) {
            ((Card) square).hand.dirty = true;
        }
        // organize
        for (Hand h : game.hand) {
            // organize
            if (h.dirty) {
                h.organize();
            }
        }

        // save
        saveState();

    }


    @Override
    protected IMoves getAction() {
        return game;
    }

    @Override
    protected List<IMenuAction> getMenuActions() {
        List<IMenuAction> lst = new ArrayList<IMenuAction>();

        game.addMenuActions(lst);
        lst.add(getSelectLanguageMenu());
        return lst;
    }

    private IMenuAction getSelectLanguageMenu() {
        return new IMenuAction() {
            @Override
            public List<IMenuAction> getSubMenu() {
                List<IMenuAction> lst = new ArrayList<IMenuAction>();
                final Hashtable<String, String> htCodes = new Hashtable<String, String>();
                // https://developers.google.com/igoogle/docs/i18n?csw=1
                htCodes.put(getString(R.string.la_english), "us");
                htCodes.put(getString(R.string.la_german), "de");
                htCodes.put(getString(R.string.la_french), "fr");
                htCodes.put(getString(R.string.la_italian), "it");
                htCodes.put(getString(R.string.la_spanish), "es");
                htCodes.put(getString(R.string.la_russian), "ru");
                htCodes.put(getString(R.string.la_chinese), "zh");

                for (final String sLanguage : htCodes.keySet()) {
                    lst.add(new IMenuAction() {
                        @Override
                        public List<IMenuAction> getSubMenu() {
                            return null;
                        }

                        @Override
                        public String getTitle() {
                            return sLanguage;
                        }

                        @Override
                        public void doAction() {
                            selectLocale(htCodes.get(sLanguage));
                            restart();
                        }
                    });
                }
                return lst;
            }

            @Override
            public String getTitle() {
                return getString(R.string.select_language);
            }

            @Override
            public void doAction() {

            }
        };
    }

    public void newGame(boolean pbLoadOld) {

        Card.init(_wd, _hg);
        game.init(_wd > _hg, _wd, _hg);

        Button button = new Button(-_wd, _hg, -_wd + Card.wd, _hg - Card.wd) {

            @Override
            public void action() {
                openPopupMenu(getSelectLanguageMenu());
            }
        };
        button.setIcon(new OpenGLTexture(1, 1, true) {

            @Override
            protected Bitmap createBitmap(int piScreenWidth, int piScreenHeight) {
                return BitmapFactory.decodeResource(getResources(), R.drawable.flag);
            }
        }, 3, 2);

        game.addButton(button);


        if (pbLoadOld) {
            loadCards();
            setHandAndButtons();
            sortSqares();
            requestRender();
        } else {
            setHandAndButtons();
            sortSqares();
            requestRender();
        }
    }


    private void loadCards() {
        try {
            Log.d("loadCards", "Start " + getFileName());
            Hashtable<Integer, Hand> htHands = new Hashtable<Integer, Hand>();
            Hashtable<Integer, Card> htCards = new Hashtable<Integer, Card>();
            for (Hand h : game.hand) {
                htHands.put(Integer.valueOf(h.getId()), h);
                for (Card c : h.lstCards) {
                    htCards.put(Integer.valueOf(c.getId()), c);
                }
            }
            DataInputStream in = new DataInputStream(
                    openFileInput(getFileName()));
            int iCountHands = in.readInt();
            for (int i = 0; i < iCountHands; i++) {
                int iHand = in.readInt();
                Hand hand = htHands.get(Integer.valueOf(iHand));
                hand.read(in);
                int iCountCards = in.readInt();
                for (int j = 0; j < iCountCards; j++) {
                    int iCard = in.readInt();
                    Card card = htCards.get(Integer.valueOf(iCard));
                    hand.addCard(card);
                }
                hand.organize();
            }
        } catch (Exception ex) {
            // ignore
            Log.e("loadCards", "Error", ex);
            newGame(false);
        }
    }

    public String getFileName() {
        return game.getClass().getName() + ".txt";
    }
}
