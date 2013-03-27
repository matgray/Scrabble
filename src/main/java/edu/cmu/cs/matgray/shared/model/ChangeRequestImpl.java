/*
 * @author <a href="mailto:matgray@cmu.edu">Mat Gray</a>
 * Licensed under a Creative Commons
 * Attribution-NonCommercial-ShareAlike 3.0 Unported License.
 */

package edu.cmu.cs.matgray.shared.model;

import edu.cmu.cs.matgray.shared.exception.IllegalMoveException;
import edu.cmu.cs.matgray.shared.interfaces.*;

import java.util.HashMap;
import java.util.Map;

public class ChangeRequestImpl implements ChangeRequest {

    Map<Location, ScrabblePiece> changes = new HashMap<Location, ScrabblePiece>();
    Map<Location, Card> newCards = new HashMap<Location, Card>();
    Game changedGame = null;
    Player currentPlayer = null;

    public ChangeRequestImpl() {
    }

    public ChangeRequestImpl(Game game, String andrewID) {
        this.changedGame = game;
        this.currentPlayer = changedGame.getPlayers().get(andrewID);
    }

    @Override
    public void addCardToBoard(Location location, Card card) {
        Slot s = changedGame.getGameBoard().getSlotAtLocation(location);
        s.setCard(card);
        newCards.put(location, card);
        int newCount = currentPlayer.getCards().get
                (card.getCardType()) - 1;

        currentPlayer.getCards().put(card
                .getCardType(), newCount);
    }


    public Map<Location, Card> getAddedCards() {
        return newCards;
    }

    @Override
    public void addToBoard(Location location, ScrabblePiece piece)
            throws IllegalMoveException {

        if (changedGame.getGameBoard().getSlotAtLocation(location).
                getOwner() != null) {
            throw new IllegalMoveException();
        }

        currentPlayer.getPieces().remove(piece);

        changedGame.getGameBoard().getSlotAtLocation(location).setPiece
                (piece, currentPlayer);

        changes.put(location, piece);
    }

    @Override
    public Map<Location, ScrabblePiece> getChangesToBoard() {
        return changes;
    }

    @Override
    public void removeFromBoard(Location location) {

        ScrabblePiece p = changes.get(location);

        changes.remove(location);

        if (p instanceof WildScrabblePiece) {
            ((WildScrabblePiece) p).reset();
        }

        currentPlayer.getPieces().add(p);

        changedGame.getGameBoard().getSlotAtLocation(location).setPiece(null,
                null);
    }

    @Override
    public void removeCardFromBoard(Location l) {
        Card card = newCards.get(l);
        newCards.remove(l);
        changedGame.getGameBoard().getSlotAtLocation(l).setCard(null);
        int newCount = currentPlayer.getCards().get
                (card.getCardType()) + 1;

        currentPlayer.getCards().put(card.getCardType(), newCount);
    }

    @Override
    public void revert() {
        for (Location l : changes.keySet()) {
            changedGame.getGameBoard().getSlotAtLocation(l).setPiece(null,
                    null);
        }
        for (Location l : newCards.keySet()) {
            changedGame.getGameBoard().getSlotAtLocation(l).setCard(null);
        }
    }

    @Override
    public void refill() {
        for (Location l : changes.keySet()) {
            changedGame.getGameBoard().getSlotAtLocation(l).setPiece(changes
                    .get(l), currentPlayer);
        }

        for (Location l : newCards.keySet()) {
            changedGame.getGameBoard().getSlotAtLocation(l).setCard
                    (newCards.get(l));
        }
    }
}
