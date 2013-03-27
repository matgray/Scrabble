/*
 * @author <a href="mailto:matgray@cmu.edu">Mat Gray</a>
 * Licensed under a Creative Commons
 * Attribution-NonCommercial-ShareAlike 3.0 Unported License.
 */

package edu.cmu.cs.matgray.shared.interfaces;

import java.io.Serializable;

public interface Card extends Serializable {

    public static enum CARD_TYPE {
        NEGATIVE_WORD, ADD_ROOT
    }

    public String getCardText();

    public CARD_TYPE getCardType();

    public int getCardCost();

    public Card copy();
}

