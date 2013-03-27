/*
 * @author <a href="mailto:matgray@cmu.edu">Mat Gray</a>
 * Licensed under a Creative Commons
 * Attribution-NonCommercial-ShareAlike 3.0 Unported License.
 */

package edu.cmu.cs.matgray.server;

import edu.cmu.cs.matgray.shared.interfaces.*;
import edu.cmu.cs.matgray.shared.model.CardImpl;
import edu.cmu.cs.matgray.shared.model.LocationImpl;

import java.util.HashSet;
import java.util.Set;

/**
 * This class modifies the game
 */
public class GameModificationService {

    private GameModificationService() {
    }

    /**
     * Place pieces onto the board.
     *
     * @param g        the game to alter
     * @param changes  the changes to put into the game
     * @param andrewID the andrewID of the player making the changes
     */
    public static void placePieces(Game g, ChangeRequest changes,
                                   String andrewID) {
        Player currentPlayer = g.getPlayers().get(andrewID);

        for (Location location : changes.getChangesToBoard().keySet()) {
            ScrabblePiece changedPiece = changes.getChangesToBoard().get(location);
            g.getGameBoard().getSlotAtLocation(location).setPiece
                    (changedPiece, currentPlayer);


            for (ScrabblePiece piece : currentPlayer.getPieces()) {
                if (piece.equals(changedPiece)) {
                    currentPlayer.getPieces().remove(piece);
                    break;
                }
            }
        }

        for (Location location : changes.getAddedCards().keySet()) {
            Card c = changes.getAddedCards().get(location);

            g.getGameBoard().getSlotAtLocation(location).setCard(c);

            /**
             * Update player's card count
             */
            int newCount = currentPlayer.getCards().get(c.getCardType()) - 1;
            currentPlayer.getCards().put(c.getCardType(), newCount);
        }

        g.nextTurn();
    }

    /**
     * Refill the players of a game's pieces to the correct number
     *
     * @param game the game to alter
     */
    public static void refillPieces(Game game) {
        for (Player p : game.getPlayers().values()) {
            if (p.getPieces().size() < Game.PIECES_PER_PLAYER) {
                p.addToPieces(game.popPieces(Game.PIECES_PER_PLAYER - p
                        .getPieces().size()));
            }
        }
    }

    /**
     * Calculate the score at a location
     *
     * @param location the location in questions
     * @param board    the board to look on
     * @param count    the cu
     * @return the score of the slot
     */
    private static Integer calculateSlotScore(Location location, Board board,
                                              Set<Location> count,
                                              Set<Location> locations) {
        if (board.getSlotAtLocation(location).getPiece() == null) {
            return null;
        } else {
            if (!count.contains(location)) {
                count.add(location);

                int factor = 1;
                if (board.getSlotAtLocation(location).getType() == Slot
                        .SLOT_TYPE.FACTOR_LETTER && locations.contains
                        (location)) {
                    factor = board.getSlotAtLocation(location).getFactor();
                }

                return factor * board.getSlotAtLocation(location).getPiece().getValue();
            }
        }
        return 0;
    }

    private static int setFactor(Board board, Location location,
                                 Set<Location> changes) {
        Slot s = board.getSlotAtLocation(location);
        int factor = 1;
        /**
         * only count factor if it's in the change set
         */
        if (s.getType() == Slot.SLOT_TYPE.FACTOR_WORD &&
                changes.contains(location)) {
            factor *= s.getFactor();
        }
        if (s.getCard() != null) {
            if (s.getCard().getCardType() == Card.CARD_TYPE.NEGATIVE_WORD &&
                    changes.contains(location)) {
                factor *= -1;
            }
        }
        return factor;
    }

    /**
     * Calculate the number of additional points to reward a player given a
     * ChangeRequest
     *
     * @param board         the board to calculate points off of
     * @param changeRequest the changes made
     * @return the number of points to award the player who created the
     *         ChangeRequest,
     */
    public static int additionalPoints(Board board,
                                       ChangeRequest changeRequest,
                                       String andrewID) {
        changeRequest.refill();

        Set<Location> seen = new HashSet<Location>();
        int total = 0;
        /**
         * Count letters to the right of the change
         */
        for (Location change : changeRequest.getChangesToBoard().keySet()) {

            int factor = 1;
            int word = 0;
            for (int x = change.getX(); x < Board.DIMENSION; x++) {
                LocationImpl location = new LocationImpl(x, change.getY());
                factor *= setFactor(board, location,
                        changeRequest.getChangesToBoard().keySet());
                Integer more = calculateSlotScore(location, board, seen,
                        changeRequest.getChangesToBoard().keySet());
                /**
                 * If we reach an empty slot, stop adding
                 */
                if (more == null) {
                    break;
                }
                word += more;
            }
            total += factor * word;
            factor = 1;
            word = 0;
            /**
             * Count letters to the left of the change
             */
            for (int x = change.getX(); x >= 0; x--) {
                LocationImpl location = new LocationImpl(x, change.getY());
                factor *= setFactor(board, location,
                        changeRequest.getChangesToBoard().keySet());
                Integer more = calculateSlotScore(location, board, seen,
                        changeRequest.getChangesToBoard().keySet());
                /**
                 * Stop adding on empty slot
                 */
                if (more == null) {
                    break;
                }
                word += more;
            }
            total += factor * word;
            factor = 1;
            word = 0;
            /**
             * Count letters below a change
             */
            for (int y = change.getY(); y < Board.DIMENSION; y++) {
                LocationImpl location = new LocationImpl(change.getX(), y);
                factor *= setFactor(board, location,
                        changeRequest.getChangesToBoard().keySet());
                Integer more = calculateSlotScore(location, board, seen,
                        changeRequest.getChangesToBoard().keySet());
                /**
                 * Stop adding on empty slot
                 */
                if (more == null) {
                    break;
                }
                word += more;
            }
            total += factor * word;
            factor = 1;
            word = 0;
            /**
             * Count letters above a change
             */
            for (int y = change.getY(); y >= 0; y--) {
                LocationImpl location = new LocationImpl(change.getX(), y);
                factor *= setFactor(board, location,
                        changeRequest.getChangesToBoard().keySet());
                Integer more = calculateSlotScore(location, board, seen,
                        changeRequest.getChangesToBoard().keySet());
                /**
                 * Stop adding on empty slot
                 */
                if (more == null) {
                    break;
                }
                word += more;
            }
            total += factor * word;
        }
        return total;
    }

    /**
     * Purchase a card
     *
     * @param g        the game
     * @param andrewID the andrewID of the purchaser
     * @param type     the type of card to be purchased
     * @return the game with the newly purchased card
     */
    public static Game purchaseCard(Game g, String andrewID,
                                    Card.CARD_TYPE type) {
        Player currentPlayer = g.getPlayers().get(andrewID);
        currentPlayer.addToCards(type);
        int newScore = currentPlayer.getScore() - CardImpl.getCardByType(g
                .getAvailableCards(), type).getCardCost();

        currentPlayer.setScore(newScore);
        return g;
    }

}
