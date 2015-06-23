package de.brod.game.cm.games;

import de.brod.cardmaniac.R;
import de.brod.game.cm.Button;
import de.brod.game.cm.GameActivity;
import de.brod.game.cm.Hand;
import de.brod.opengl.IAction;
import de.brod.opengl.IMoves;

import java.util.List;

public abstract class Game implements IMoves {

    public Class<?>[] gameClasses = {FreeCell.class, Solitair.class};

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
        lst.add(actionNewGame());
        lst.add(actionSelectGame());
    }

    private IAction actionSelectGame() {
        return new IAction() {

            @Override
            public String getTitle() {
                return getString(R.string.select_game);
            }

            @Override
            public void doAction() {
                for (int i = 0; i < gameClasses.length; i++) {
                    GameActivity gameActivity = getGameActivity();
                    if (gameActivity.game.getClass().equals(gameClasses[i])) {
                        Class<? extends Game> gameClass = (Class<? extends Game>) gameClasses[(i + 1) % gameClasses.length];
                        gameActivity.selectGame(gameClass);
                        return;
                    }
                }
            }
        };
    }


    private IAction actionNewGame() {
        return new IAction() {

            @Override
            public String getTitle() {
                return getString(R.string.new_game);
            }

            @Override
            public void doAction() {
                getGameActivity().confirm(getString(R.string.question),
                        getString(R.string.do_you_want_to_start_a_new_game),
                        getString(R.string.confirm_yes),
                        new IAction() {
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
    }

    void showMoveIsNotAllowed() {
        getGameActivity().showText(getString(R.string.this_move_is_not_allowed));
    }

    public void organize() {
        // make nothing specail
    }
}
