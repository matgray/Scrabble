/*
 * @author <a href="mailto:matgray@cmu.edu">Mat Gray</a>
 * Licensed under a Creative Commons
 * Attribution-NonCommercial-ShareAlike 3.0 Unported License.
 */

package edu.cmu.cs.matgray.shared.model;

import edu.cmu.cs.matgray.shared.interfaces.Card;
import edu.cmu.cs.matgray.shared.interfaces.Player;
import edu.cmu.cs.matgray.shared.interfaces.ScrabblePiece;
import edu.cmu.cs.matgray.shared.interfaces.Slot;

public class SlotImpl implements Slot {
    ScrabblePiece piece = null;
    Player owner = null;
    SLOT_TYPE slotType = SLOT_TYPE.REGULAR;
    private int factor = 1;
    Card card = null;

    public SlotImpl() {

    }

    public SlotImpl(SLOT_TYPE type, int factor) {
        this.slotType = type;
        this.factor = factor;
    }

    @Override
    public Player getOwner() {
        return this.owner;
    }

    @Override
    public void setOwner(Player p) {
        this.owner = p;
    }

    @Override
    public ScrabblePiece getPiece() {
        return this.piece;
    }

    @Override
    public void setPiece(ScrabblePiece piece, Player owner) {
        this.piece = piece;
        this.owner = owner;
    }

    @Override
    public SLOT_TYPE getType() {
        return this.slotType;
    }

    @Override
    public int getFactor() {
        return this.factor;
    }

    @Override
    public String getDescription() {
        if (this.slotType == SLOT_TYPE.FACTOR_LETTER) {
            return this.getFactor() + "x Letter";
        } else if (this.slotType == SLOT_TYPE.FACTOR_WORD) {
            return this.getFactor() + "x Word";
        } else if (this.slotType == SLOT_TYPE.REGULAR) {
            return "Regular";
        } else if (this.slotType == SLOT_TYPE.ROOT) {
            return "Root";
        } else {
            return "No Description Available";
        }
    }

    @Override
    public Card getCard() {
        return card;
    }

    @Override
    public void setCard(Card card) {
        this.card = card;
    }
}
