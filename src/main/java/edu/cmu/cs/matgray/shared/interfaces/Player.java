/*
 * @author <a href="mailto:matgray@cmu.edu">Mat Gray</a>
 * Licensed under a Creative Commons
 * Attribution-NonCommercial-ShareAlike 3.0 Unported License.
 */

package edu.cmu.cs.matgray.shared.interfaces;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface Player extends Serializable {
    /**
     * Each player chooses a name.  This returns the name that they
     * initially chose;
     *
     * @return the name of the player
     */
    public String getName();

    public String getAndrewID();

    public void setScore(int newScore);

    /**
     * Each player has a score corresponding to his/her moves.
     *
     * @return the player's score
     */
    public int getScore();

    public void addToPieces(List<ScrabblePiece> piece);

    public List<ScrabblePiece> getPieces();

    public void addToCards(Card.CARD_TYPE cardType);

    public Map<Card.CARD_TYPE, Integer> getCards();

    public String getDepartmentName();
}
