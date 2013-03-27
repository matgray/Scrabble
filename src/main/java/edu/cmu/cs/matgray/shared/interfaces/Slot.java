/*
 * @author <a href="mailto:matgray@cmu.edu">Mat Gray</a>
 * Licensed under a Creative Commons
 * Attribution-NonCommercial-ShareAlike 3.0 Unported License.
 */

package edu.cmu.cs.matgray.shared.interfaces;


import java.io.Serializable;

/**
 * A board is made up of slots. Slots contain pieces.
 */
public interface Slot extends Serializable {

    public static enum SLOT_TYPE {REGULAR, FACTOR_LETTER, FACTOR_WORD, ROOT}

    public Player getOwner();

    public void setOwner(Player p);

    public ScrabblePiece getPiece();

    public void setPiece(ScrabblePiece piece, Player owner);

    public SLOT_TYPE getType();

    public int getFactor();

    public String getDescription();

    public Card getCard();

    public void setCard(Card card);
}
