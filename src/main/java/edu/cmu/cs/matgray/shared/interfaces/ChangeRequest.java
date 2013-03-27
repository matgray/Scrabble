/*
 * @author <a href="mailto:matgray@cmu.edu">Mat Gray</a>
 * Licensed under a Creative Commons
 * Attribution-NonCommercial-ShareAlike 3.0 Unported License.
 */

package edu.cmu.cs.matgray.shared.interfaces;

import edu.cmu.cs.matgray.shared.exception.IllegalMoveException;

import java.io.Serializable;
import java.util.Map;

public interface ChangeRequest extends Serializable {

    public void addCardToBoard(Location location, Card card);

    public void addToBoard(Location location, ScrabblePiece piece)
            throws IllegalMoveException;

    public Map<Location, ScrabblePiece> getChangesToBoard();

    public Map<Location, Card> getAddedCards();

    public void removeFromBoard(Location location);

    public void removeCardFromBoard(Location l);

    public void revert();

    public void refill();
}
