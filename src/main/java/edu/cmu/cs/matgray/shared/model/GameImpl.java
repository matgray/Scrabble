/*
 * @author <a href="mailto:matgray@cmu.edu">Mat Gray</a>
 * Licensed under a Creative Commons
 * Attribution-NonCommercial-ShareAlike 3.0 Unported License.
 */

package edu.cmu.cs.matgray.shared.model;

import edu.cmu.cs.matgray.shared.interfaces.*;

import java.util.*;

public class GameImpl implements Game {

    private int turnNumber = 0;
    private Map<String, Player> players;
    private List<String> moveOrder;
    private Board board;
    private Set<ScrabblePiece> availablePieces = null;
    private String name = null;
    private Set<Card> availableCards = null;

    public GameImpl() {
        this.board = new BoardImpl();
        this.availablePieces = new HashSet<ScrabblePiece>();
        init();
    }

    public GameImpl(String name) {
        this.name = name;
        this.board = new BoardImpl();
        init();
    }

    public GameImpl(Board b) {
        this.board = b;
        init();
    }

    private void init() {
        this.players = new HashMap<String, Player>();
        moveOrder = new ArrayList<String>();
    }

    public List<ScrabblePiece> popPieces(int n) {
        Random random = new Random();
        List<ScrabblePiece> pieces = new ArrayList<ScrabblePiece>(n);
        for (int i = 0; i < n; i++) {
            int r = random.nextInt(this.availablePieces().size());
            int j = 0;

            for (ScrabblePiece piece : this.availablePieces()) {
                if (j == r) {
                    pieces.add(piece);
                    this.availablePieces().remove(piece);
                    break;
                }
                j++;
            }
        }
        return pieces;
    }

    @Override
    public Set<Card> getAvailableCards() {
        return this.availableCards;
    }

    @Override
    public boolean isOver() {
        if (getPlayers().size() == 0) {
            return true;
        }
        for (Player p : getPlayers().values()) {
            if (p.getPieces().size() > 0) {
                return false;
            }
        }
        return true;
    }

    public void setAvailableCards(Set<Card> cards) {
        this.availableCards = cards;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Set<ScrabblePiece> availablePieces() {
        return availablePieces;
    }

    @Override
    public void setAvailablePieces(Set<ScrabblePiece> pieces) {
        this.availablePieces = pieces;
    }

    @Override
    public Map<String, Player> getPlayers() {
        return this.players;
    }

    @Override
    public void nextTurn() {
        turnNumber++;
    }

    @Override
    public boolean addPlayer(String name, String andrewID, String dept) throws
            IllegalArgumentException {
        if (players.size() < MAX_PLAYERS) {
            Player p = new PlayerImpl(name, andrewID, dept, 0);
            players.put(andrewID, p);
            moveOrder.add(andrewID);
            return true;
        }
        return false;
    }

    @Override
    public Player getCurrentTurn() {
        return this.getPlayers().get(moveOrder.get(turnNumber % players
                .size()));
    }

    @Override
    public Board getGameBoard() {
        return this.board;
    }
}
