package de.brod.game.cm.games;

import java.util.ArrayList;
import java.util.List;

import de.brod.cardmaniac.R;
import de.brod.game.cm.Button;
import de.brod.game.cm.GameActivity;
import de.brod.game.cm.Hand;
import de.brod.opengl.IAction;
import de.brod.opengl.IMenuAction;
import de.brod.opengl.IMoves;

public abstract class Game implements IMoves {

    public static final Class<?>[] gameClasses = {FreeCell.class, Solitair.class};

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

    public void addMenuActions(List<IMenuAction> lst) {
        lst.add(actionNewGame());
        lst.add(actionSelectGame());
    }

    private IMenuAction actionSelectGame() {
        return new IMenuAction() {

            @Override
            public List<IMenuAction> getSubMenu() {
                List<IMenuAction> lst = new ArrayList<IMenuAction>();
                for (Class<?> g : gameClasses) {
                    lst.add(getSelectGame(g));
                }
                return lst;
            }

            @Override
            public String getTitle() {
                return getString(R.string.select_game);
            }

            @Override
            public void doAction() {
                // subMenu
            }
        };
    }

    private IMenuAction getSelectGame(final Class<?> pGame) {
        return new IMenuAction() {
            @Override
            public List<IMenuAction> getSubMenu() {
                return null;
            }

            @Override
            public String getTitle() {
                String name = pGame.getName();
                return name.substring(name.lastIndexOf(".") + 1);
            }

            @Override
            public void doAction() {
                GameActivity gameActivity = getGameActivity();
                gameActivity.selectGame((Class<? extends Game>) pGame);
            }
        };
    }


    private IMenuAction actionNewGame() {
        return new IMenuAction() {

            @Override
            public List<IMenuAction> getSubMenu() {
                return null;
            }

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

    public void addButton(Button pButton) {
        if (button == null) {
            button = new Button[1];
        } else {
            // Arrays.copyOf does not work for selected API
            Button[] button2 = new Button[button.length + 1];
            for (int i = 0; i < button.length; i++) {
                button2[i] = button[i];
            }
            button = button2;
        }
        button[button.length - 1] = pButton;
    }
}
