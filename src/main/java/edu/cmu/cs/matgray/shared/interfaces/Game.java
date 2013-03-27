/*
 * @author <a href="mailto:matgray@cmu.edu">Mat Gray</a>
 * Licensed under a Creative Commons
 * Attribution-NonCommercial-ShareAlike 3.0 Unported License.
 */

package edu.cmu.cs.matgray.shared.interfaces;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface Game extends Serializable {

    public String getName();

    public Set<ScrabblePiece> availablePieces();

    public static int MAX_PLAYERS = 4;

    public static int PIECES_PER_PLAYER = 7;

    public Map<String, Player> getPlayers();

    public void nextTurn();

    public boolean addPlayer(String name, String andrewID, String dept);

    public Player getCurrentTurn();

    public Board getGameBoard();

    public void setAvailablePieces(Set<ScrabblePiece> pieces);

    public List<ScrabblePiece> popPieces(int n);

    public Set<Card> getAvailableCards();

    public boolean isOver();
}
