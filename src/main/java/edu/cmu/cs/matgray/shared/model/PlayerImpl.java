/*
 * @author <a href="mailto:matgray@cmu.edu">Mat Gray</a>
 * Licensed under a Creative Commons
 * Attribution-NonCommercial-ShareAlike 3.0 Unported License.
 */

package edu.cmu.cs.matgray.shared.model;

import edu.cmu.cs.matgray.shared.interfaces.Card;
import edu.cmu.cs.matgray.shared.interfaces.Game;
import edu.cmu.cs.matgray.shared.interfaces.Player;
import edu.cmu.cs.matgray.shared.interfaces.ScrabblePiece;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerImpl implements Player {
    private String name = null;
    private String andrewID = null;
    private int score = -1;
    private List<ScrabblePiece> pieces = new ArrayList(Game.PIECES_PER_PLAYER);
    private Map<Card.CARD_TYPE, Integer> cards = new HashMap<Card.CARD_TYPE, Integer>();
    private String department;

    /**
     * Initialize a new player with a name
     *
     * @param name the name of the player
     */
    public PlayerImpl(String name, String andrewID, String department,
                      int score) {
        this.name = name;
        this.score = score;
        this.andrewID = andrewID;
        for (Card.CARD_TYPE type : Card.CARD_TYPE.values()) {
            cards.put(type, 0);
        }
        this.department = department;
    }

    public PlayerImpl() {
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getAndrewID() {
        return andrewID;
    }

    @Override
    public void setScore(int score) {
        this.score = score;
    }


    @Override
    public int getScore() {
        return this.score;
    }

    @Override
    public void addToPieces(List<ScrabblePiece> pieces) {
        this.pieces.addAll(pieces);
    }

    @Override
    public List<ScrabblePiece> getPieces() {
        return this.pieces;
    }

    @Override
    public void addToCards(Card.CARD_TYPE cardType) {
        int oldValue = cards.get(cardType);
        cards.put(cardType, oldValue + 1);
    }

    @Override
    public Map<Card.CARD_TYPE, Integer> getCards() {
        return this.cards;
    }

    @Override
    public String getDepartmentName() {
        return this.department;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Player) {
            return ((Player) o).getAndrewID().equals(this.getAndrewID());
        }
        return false;
    }
}
