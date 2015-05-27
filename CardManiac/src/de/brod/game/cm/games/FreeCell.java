package de.brod.game.cm.games;

import de.brod.cardmaniac.R;
import de.brod.game.cm.Button;
import de.brod.game.cm.Card;
import de.brod.game.cm.CardsTexture;
import de.brod.game.cm.Hand;
import de.brod.opengl.IAction;

import java.util.ArrayList;
import java.util.List;

public class FreeCell extends Patience {

    private ArrayList<Card> lstNextMove = new ArrayList<Card>();

    @Override
    public void init(boolean pbLandscape, float pWd, float pHg) {

        // create the cards
        List<Card> cards = CardsTexture.create52Cards();

        button = new Button[1];
        button[0] = new Button(-pWd, pHg, -pWd + Card.wd * 3, pHg - Card.wd) {

            @Override
            public void action() {

                getGameActivity().confirm(getString(R.string.question), getString(R.string.do_you_want_to_start_a_new_game), getString(R.string.confirm_yes), new IAction() {
                    @Override
                    public void doAction() {
                        newGame(true);
                    }
                }, getString(R.string.confirm_no), null);
            }

        };
        button[0].setText(getString(R.string.new_game));

        // create 2*8 Hands
        hand = new Hand[16];
        for (int x = 0; x < 8; x++) {
            float iTop = x + (x < 4 ? -1 : 1) * 0.0f;
            if (x < 4) {
                hand[x + 8] = new Hand(iTop, Card.maxy, iTop, Card.maxy, 52) {

                    @Override
                    public void actionUp(List<Card> lstMove2) {
                        if (lstMove2.size() == 1 && lstCards.size() == 0) {
                            Card c = lstMove2.get(0);
                            addCard(c);
                        }
                    }

                    @Override
                    public void actionDown(Card pDown, List<Card> plstMove) {
                        plstMove.add(pDown);
                    }
                };
            } else {
                hand[x + 8] = new Hand(iTop, Card.maxy, iTop, Card.maxy, 52) {

                    @Override
                    public void actionUp(List<Card> lstMove2) {
                        if (lstMove2.size() == 1) {
                            Card c = lstMove2.get(0);
                            Card lastCard = getLastCard();
                            if (lastCard == null) {
                                if (c.getValue().equals(Card.Value.cA)) {
                                    addCard(c);
                                }
                            } else if (isNext(c, lastCard, true)) {
                                addCard(c);
                            }
                        }
                    }

                    @Override
                    public void actionDown(Card pDown, List<Card> plstMove) {
                        plstMove.add(pDown);
                    }
                };
                hand[x + 8].setText("A");
                hand[x + 8].setTextColor(128, 128, 255, 64);
            }
            hand[x] = new Hand(x, Card.maxy - 1, x, 0, 10) {

                @Override
                public void actionUp(List<Card> lstMove2) {
                    Card lastCard = getLastCard();
                    if (lastCard != null
                            && !isNext(lastCard, lstMove2.get(0), false)) {
                        return;
                    }
                    for (Card c : lstMove2) {
                        addCard(c);
                    }
                }

                @Override
                public void actionDown(Card pDown, List<Card> plstMove) {
                    Card found = null;
                    for (Card c : lstCards) {
                        if (c.equals(pDown)) {
                            found = c;
                            plstMove.add(c);
                        } else if (found != null) {
                            if (!isNext(found, c, false)) {
                                plstMove.clear();
                                return;
                            }
                            found = c;
                            plstMove.add(c);
                        }
                    }
                }

            };
        }

        // init the cards
        for (int i = 0; i < cards.size(); i++) {
            Card card = cards.get(i);
            hand[i % 8].addCard(card);
        }
    }

    @Override
    public void makeNextMove() {

        nextCard:
        while (lstNextMove.size() > 0) {
            Card remove = lstNextMove.remove(0);
            for (int x = 0; x < 4; x++) {
                Card lastCard = hand[x + 12].getLastCard();
                if (getNumber(lastCard) + 1 == getNumber(remove)) {
                    if (lastCard == null
                            || getColor(lastCard) == getColor(remove)) {
                        hand[x + 12].addCard(remove);
                        continue nextCard;
                    }
                }
            }
        }
    }

    @Override
    public boolean hasNextMove() {
        lstNextMove.clear();
        int minCard = 0;
        for (int x = 0; x < 4; x++) {
            Card lastCard = hand[x + 12].getLastCard();
            int number = getNumber(lastCard) + 1;
            if (x == 0 || minCard > number) {
                minCard = number;
            }
        }
        for (int x = 0; x < 12; x++) {
            Card lastCard = hand[x].getLastCard();
            if (getNumber(lastCard) == minCard) {
                lstNextMove.add(lastCard);
            }
        }
        return lstNextMove.size() > 0;
    }

}
