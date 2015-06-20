package de.brod.game.cm.games;

import de.brod.cardmaniac.R;
import de.brod.game.cm.Button;
import de.brod.game.cm.GameActivity;
import de.brod.game.cm.Hand;
import de.brod.opengl.IAction;
import de.brod.opengl.IMoves;

import java.util.List;

public abstract class Game implements IMoves {

    public Hand[] hand;
    public Button[] button;
    private GameActivity gameActivity;

    public abstract void init(boolean pbLandscape, float pWd, float pHg);

    protected void newGame(boolean pbLoadOld) {
        gameActivity.newGame(pbLoadOld);
    }

    public void setActivity(GameActivity pGameActivity) {
        gameActivity = pGameActivity;
    }

    public GameActivity getGameActivity() {
        return gameActivity;
    }


    String getString(int piCounter) {
        return gameActivity.getString(piCounter);
    }

    public void addMenuActions(List<IAction> lst) {
        IAction newGame = new IAction() {

            @Override
            public String getTitle() {
                return getString(R.string.new_game);
            }

            @Override
            public void doAction() {
                getGameActivity().confirm(getString(R.string.question), getString(R.string.do_you_want_to_start_a_new_game), getString(R.string.confirm_yes), new IAction() {
                    @Override
                    public String getTitle() {
                        return getString(R.string.confirm);
                    }

                    @Override
                    public void doAction() {
                        newGame(false);
                    }
                }, getString(R.string.confirm_no), null);
            }
        };
        lst.add(newGame);

    }
}
