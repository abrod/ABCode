package de.brod.game.cm.games;

import de.brod.game.cm.Button;
import de.brod.game.cm.GameActivity;
import de.brod.game.cm.Hand;
import de.brod.opengl.IMoves;

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

}
