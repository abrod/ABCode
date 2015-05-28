package de.brod.game.cm;

import android.content.Context;
import de.brod.game.cm.games.FreeCell;
import de.brod.game.cm.games.Game;
import de.brod.opengl.IMoves;
import de.brod.opengl.OpenGLActivity;
import de.brod.opengl.OpenGLSquare;

import java.io.*;
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
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(bytes);

        // organize and save
        try {
            out.write(game.hand.length);
            for (Hand h : game.hand) {
                // save the state
                out.write(h.getId());
                List<Card> cards = h.getCards();
                out.write(cards.size());
                for (Card c : cards) {
                    out.write(c.getId());
                }
                // organize
                if (h.dirty) {
                    h.organize();
                }
            }

            // final save
            FileOutputStream fileOut = openFileOutput(game.getClass().getName() + ".txt", Context.MODE_PRIVATE);
            fileOut.write(bytes.toByteArray());
            fileOut.close();

        } catch (IOException e) {
            // should not happen on outputstream
        }
    }

    @Override
    protected IMoves getAction() {
        return game;
    }

    public void newGame(boolean pbLoadOld) {
        game.init(_wd > _hg, _wd, _hg);

        setHandAndButtons();

        if (pbLoadOld) {
            try {
                DataInputStream in = new DataInputStream(
                        openFileInput(game.getClass().getName() + ".txt"));
                int iCountHands = in.readInt();
                for (int i = 0; i < iCountHands; i++) {

                }
            } catch (Exception ex) {
                // ignore
            }
        }
        requestRender();
    }
}
