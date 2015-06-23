package de.brod.game.cm.games;

import de.brod.game.cm.Card;
import de.brod.game.cm.CardsTexture;
import de.brod.game.cm.Hand;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

/**
 * Created by Andreas on 23.06.2015.
 */
public class Solitair extends Patience {

    private List<Card> lstNextMove = new ArrayList<>();

    @Override
    public void init(boolean pbLandscape, float pWd, float pHg) {
        // create the cards
        List<Card> cards = CardsTexture.create52Cards(0);

        // create 2*8 Hands
        hand = new Hand[7 + 2 + 4];


        hand[0] = new Hand(0, 0, Card.maxy, 1, Card.maxy, 20) {

            @Override
            public void actionUp(List<Card> lstMove2) {
                // adding not allowed
                if (lstMove2.get(0).hand == this) {
                    hand[1].addCard(lstMove2.get(0));
                    checkHand0();
                }
            }

            @Override
            public void actionDown(Card pDown, List<Card> plstMove) {
                plstMove.add(pDown);
            }
        };

        hand[1] = new Hand(1, 2f, Card.maxy, 2.7f, Card.maxy, 4) {

            @Override
            public void actionUp(List<Card> lstMove2) {
                Hand hand = lstMove2.get(0).hand;
                if (hand == Solitair.this.hand[0]) {
                    for (Card c : lstMove2) {
                        addCard(c);
                    }
                    checkHand0();
                } else {
                    showMoveIsNotAllowed();
                }
            }

            @Override
            public void actionDown(Card pDown, List<Card> plstMove) {
                plstMove.add(pDown);
            }
        };


        for (int x = 0; x < 4; x++) {
            hand[x + 2] = new Hand(x + 2, x + 4, Card.maxy, x + 4, Card.maxy, 52) {

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
                    } else {
                        organize();
                    }
                }

                @Override
                public void actionDown(Card pDown, List<Card> plstMove) {
                    plstMove.add(pDown);
                }

            };
        }

        for (int x = 0; x < 7; x++) {
            float px = x / 7f * 8f;
            hand[x + 6] = new Hand(x + 8, px, Card.maxy - 1, px, 0, 10) {

                @Override
                public void actionUp(List<Card> lstMove2) {
                    if (getCardCount() == 0) {
                        if (!lstMove2.get(0).getValue().equals(Card.Value.cK)) {
                            showMoveIsNotAllowed();
                            return;
                        }
                    }

                    Card lastCard = getLastCard();
                    if (lastCard != null
                            && !isNext(lastCard, lstMove2.get(0), false)) {
                        return;
                    }
                    for (Card c : lstMove2) {
                        addCard(c);
                    }
                    organize();
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
        Stack<Card> pStack = new Stack<Card>();
        pStack.addAll(cards);

        hand[0].setHidden(52);

        for (int i = 0; i < 7; i++) {
            Hand hand = this.hand[i + 6];
            for (int j = 0; j <= i; j++) {
                hand.addCard(pStack.pop());
            }
            hand.setHidden(i);
        }

        while (pStack.size() > 0) {
            hand[0].addCard(pStack.pop());
        }
    }

    private void checkHand0() {
        if (hand[0].getCardCount() == 0) {
            List<Card> lst = new ArrayList<>();
            for (int i = 0; i < hand[1].getCards().size() - 1; i++) {
                lst.add(hand[1].getCards().get(i));
            }
            Collections.shuffle(lst);
            for (Card c : lst) {
                hand[0].addCard(c);
            }
        }
    }

    @Override
    public void organize() {
        for (int i = 0; i < 7; i++) {
            Hand h = hand[i + 6];
            int hidden = h.getHidden();
            if (hidden >= h.getCardCount()) {
                h.setHidden(h.getCardCount() - 1);
                h.organize();
            }
        }
    }

    @Override
    public void makeNextMove() {
        boolean bChanged = false;
        nextCard:
        while (lstNextMove.size() > 0) {
            Card remove = lstNextMove.remove(0);
            for (int x = 0; x < 4; x++) {
                Card lastCard = hand[x + 2].getLastCard();
                if (getNumber(lastCard) + 1 == getNumber(remove)) {
                    if (lastCard == null
                            || getColor(lastCard) == getColor(remove)) {
                        hand[x + 2].addCard(remove);
                        bChanged = true;
                        continue nextCard;
                    }
                }
            }
        }
        if (bChanged) {
            this.organize();
        }
    }

    @Override
    public boolean hasNextMove() {
        lstNextMove.clear();
        int minCard = 0;
        for (int x = 0; x < 4; x++) {
            Card lastCard = hand[x + 2].getLastCard();
            int number = getNumber(lastCard) + 1;
            if (x == 0 || minCard > number) {
                minCard = number;
            }
        }

        int[] iHandMoves = {1, 6, 7, 8, 9, 10, 11, 12};
        for (int x : iHandMoves) {
            Card lastCard = hand[x].getLastCard();
            if (lastCard != null && getNumber(lastCard) == minCard) {
                lstNextMove.add(lastCard);
            }
        }
        return lstNextMove.size() > 0;
    }
}
