package de.brod.game.cm;

import android.content.Context;
import android.util.Log;
import de.brod.game.cm.games.FreeCell;
import de.brod.game.cm.games.Game;
import de.brod.opengl.IAction;
import de.brod.opengl.IMoves;
import de.brod.opengl.OpenGLActivity;
import de.brod.opengl.OpenGLSquare;

import java.io.*;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class GameActivity extends OpenGLActivity<Card, Hand, Button> {

    Game game;
    private List<Card> _lstSquares;
    private List<Hand> _lstIDrawArea;
    private List<Button> _lstButtons;
    private float _wd, _hg;

    @Override
    protected void init(float pWd, float pHg, List<Card> lstSquares,
                        List<Hand> lstIDrawArea, List<Button> lstButtons) {

        this._lstSquares = lstSquares;
        this._lstIDrawArea = lstIDrawArea;
        this._lstButtons = lstButtons;
        _wd = pWd;
        _hg = pHg;

        game = new FreeCell();

        game.setActivity(this);

        newGame(true);

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
    protected void saveState() {
        try {
            Log.d("saveCards", "Start " + getFileName());
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(bytes);

            out.writeInt(game.hand.length);
            for (Hand h : game.hand) {
                // save the state
                out.writeInt(h.getId());
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
    protected List<IAction> getMenuActions() {
        List<IAction> lst = new ArrayList<>();
        lst.add(new IAction() {
            @Override
            public String getTitle() {
                return "Info";
            }

            @Override
            public void doAction() {
                showText("Info pressed");
            }
        });
        game.addMenuActions(lst);
        return lst;
    }

    public void newGame(boolean pbLoadOld) {

        Card.init(_wd, _hg);
        game.init(_wd > _hg, _wd, _hg);

        setHandAndButtons();

        if (pbLoadOld) {
            loadCards();
        }
        requestRender();
    }

    private void loadCards() {
        try {
            Log.d("loadCards", "Start " + getFileName());
            Hashtable<Integer, Hand> htHands = new Hashtable<>();
            Hashtable<Integer, Card> htCards = new Hashtable<>();
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

                int iCountCards = in.readInt();
                for (int j = 0; j < iCountCards; j++) {
                    int iCard = in.readInt();
                    Card card = htCards.get(Integer.valueOf(iCard));
                    hand.addCard(card);
                }
            }

        } catch (Exception ex) {
            // ignore
            Log.e("loadCards", "Error", ex);
        }
    }

    public String getFileName() {
        return game.getClass().getName() + ".txt";
    }
}
