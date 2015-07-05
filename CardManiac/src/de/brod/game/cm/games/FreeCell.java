package de.brod.game.cm.games;

import de.brod.cardmaniac.R;
import de.brod.game.cm.Card;
import de.brod.game.cm.CardsTexture;
import de.brod.game.cm.Hand;

import java.util.ArrayList;
import java.util.List;

public class FreeCell extends Patience {

    private ArrayList<Card> lstNextMove = new ArrayList<Card>();

    @Override
    public void init(boolean pbLandscape, float pWd, float pHg) {

        // create the cards
        List<Card> cards = CardsTexture.create52Cards(0);

//        button = new Button[1];
//        button[0] = new Button(-pWd, pHg, -pWd + Card.wd * 3, pHg - Card.wd) {
//
//            @Override
//            public void action() {
//
//                getGameActivity().confirm(getString(R.string.question), getString(R.string.do_you_want_to_start_a_new_game), getString(R.string.confirm_yes), new IAction() {
//                    @Override
//                    public String getTitle() {
//                        return "Confirm";
//                    }
//
//                    @Override
//                    public void doAction() {
//                        newGame(false);
//                    }
//                }, getString(R.string.confirm_no), null);
//            }
//
//        };
//        button[0].setText(getString(R.string.new_game));

        // create 2*8 Hands
        hand = new Hand[16];
        for (int x = 0; x < 8; x++) {
            float iTop = x + (x < 4 ? -1 : 1) * 0.0f;
            if (x < 4) {
                hand[x + 8] = new Hand(x + 8, iTop, Card.maxy, iTop, Card.maxy, 52) {

                    @Override
                    public void actionUp(List<Card> lstMove2) {
                        if (lstCards.size() > 0) {
                            showMoveIsNotAllowed();
                        } else if (lstMove2.size() != 1) {
                            showMoveIsNotAllowed();
                        } else {
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
                hand[x + 8] = new Hand(x + 8, iTop, Card.maxy, iTop, Card.maxy, 52) {

                    @Override
                    public void actionUp(List<Card> lstMove2) {
                        int cardCount = getCardCount();
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
                        if (cardCount == getCardCount()) {
                            showMoveIsNotAllowed();
                        }
                    }

                    @Override
                    public void actionDown(Card pDown, List<Card> plstMove) {
                        plstMove.add(pDown);
                    }
                };
                setStackText(hand[x + 8], Card.Value.cA.getText());
            }
            hand[x] = new Hand(x, x, Card.maxy - 1, x, 0, 10) {

                @Override
                public void actionUp(List<Card> lstMove2) {
                    Card lastCard = getLastCard();
                    if (lastCard != null
                            && !isNext(lastCard, lstMove2.get(0), false)) {
                        if (!lastCard.hand.equals(this))
                            showMoveIsNotAllowed();
                        return;
                    }
                    for (Card c : lstMove2) {
                        addCard(c);
                    }
                }

                @Override
                public void actionDown(Card pDown, List<Card> plstMove) {
                    Card found = null;
                    List<Card> lst = new ArrayList<>();
                    for (Card c : lstCards) {
                        if (c.equals(pDown)) {
                            found = c;
                            lst.add(c);
                        } else if (found != null) {
                            if (!isNext(found, c, false)) {
                                plstMove.clear();
                                return;
                            }
                            found = c;
                            lst.add(c);
                        }
                    }
                    int iCount1 = 1;
                    for (int i = 8; i < 12; i++) {
                        if (hand[i].getCardCount() == 0)
                            iCount1++;
                    }
                    int iCount2 = 1;
                    for (int i = 0; i < 8; i++) {
                        if (hand[i].getCardCount() == 0)
                            iCount2++;
                    }
                    int iCount = iCount1;
                    for (int i = 2; i <= iCount2 && 0 > iCount1; i++) {
                        iCount += iCount1;
                        iCount1--;
                    }

                    if (lst.size() > iCount) {
                        getGameActivity().showText(getString(R.string.only_2_moves_possible).replace("2", String.valueOf(iCount)));
                    } else {
                        plstMove.addAll(lst);
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
