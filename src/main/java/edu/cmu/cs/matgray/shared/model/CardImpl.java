/*
 * @author <a href="mailto:matgray@cmu.edu">Mat Gray</a>
 * Licensed under a Creative Commons
 * Attribution-NonCommercial-ShareAlike 3.0 Unported License.
 */

package edu.cmu.cs.matgray.shared.model;

import edu.cmu.cs.matgray.shared.interfaces.Card;

import java.util.Set;

public class CardImpl implements Card {
    public static Card getCardByType(Set<Card> cards, CARD_TYPE type) {
        for (Card c : cards) {
            if (c.getCardType() == type) {
                return c.copy();
            }
        }
        return null;
    }

    public static String getCardName(Set<Card> cards, CARD_TYPE ct) {
        for (Card c : cards) {
            if (c.getCardType() == ct) {
                return c.getCardText();
            }
        }
        return null;
    }

    String cardText;
    CARD_TYPE type;
    int cost;

    public CardImpl() {
    }

    public CardImpl(CARD_TYPE type, String text, int cost) {
        this.cardText = text;
        this.type = type;
        this.cost = cost;
    }

    @Override
    public String getCardText() {
        return this.cardText;
    }

    @Override
    public CARD_TYPE getCardType() {
        return this.type;
    }

    @Override
    public int getCardCost() {
        return this.cost;
    }

    @Override
    public Card copy() {
        return new CardImpl(this.type, this.cardText, this.cost);
    }
}
