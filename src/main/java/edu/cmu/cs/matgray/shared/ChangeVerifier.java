/*
 * @author <a href="mailto:matgray@cmu.edu">Mat Gray</a>
 * Licensed under a Creative Commons
 * Attribution-NonCommercial-ShareAlike 3.0 Unported License.
 */

package edu.cmu.cs.matgray.shared;


import edu.cmu.cs.matgray.shared.interfaces.*;
import edu.cmu.cs.matgray.shared.model.LocationImpl;

import java.io.Serializable;
import java.util.*;

public class ChangeVerifier implements Serializable {

    public ChangeVerifier() {
    }

    private Set<String> dict = new HashSet<String>();

    public void addToDictionary(String s) {
        this.dict.add(s);
    }

    private List<Location> roots(Board b) {
        List<Location> foundRoots = new ArrayList<Location>(1);
        for (int i = 0; i < Board.DIMENSION; i++) {
            for (int j = 0; j < Board.DIMENSION; j++) {
                Location location = new LocationImpl(i, j);
                Slot s = b.getSlotAtLocation(location);

                if (s.getType() == Slot.SLOT_TYPE.ROOT ||
                        (s.getCard() != null && s.getCard().getCardType() ==
                                Card.CARD_TYPE.ADD_ROOT)) {
                    foundRoots.add(location);
                }
            }
        }
        return foundRoots;
    }

    private int found(Board board, Location currentLocation,
                      Set<Location> findUs,
                      Set<Location> seen,
                      int numFound) {
        if (board.getSlotAtLocation(currentLocation).getOwner() == null ||
                seen.contains(currentLocation)) {
            return numFound;
        }
        seen.add(currentLocation);

        if (findUs.contains(currentLocation)) {
            numFound++;
        }

        for (Location location : currentLocation.validAdjacentLocations()) {
            numFound += found(board, location, findUs, seen, 0);
        }
        return numFound;
    }


    private boolean isConnected(Board board, ChangeRequest changeRequest) {
        changeRequest.refill();
        List<Location> roots = roots(board);
        if (roots.size() == 0) {
            return false;
        }
        boolean connected;
        int located = 0;
        for (Location root : roots) {
            located += found(board, root, changeRequest.getChangesToBoard()
                    .keySet(),
                    new HashSet<Location>(), 0);

        }
        connected = located == changeRequest
                .getChangesToBoard().keySet().size();
        changeRequest.revert();
        return connected;
    }

    public Set<String> getInvalidWords(Board board, ChangeRequest changeRequest) {
        changeRequest.refill();
        Set<String> invalidWords = new HashSet<String>();

        for (int sw = 0; sw < 2; sw++) {
            for (int i = 0; i < Board.DIMENSION; i++) {
                StringBuilder wordSoFar = new StringBuilder();
                for (int j = 0; j < Board.DIMENSION; j++) {
                    int x;
                    int y;

                    if (sw == 0) {
                        x = i;
                        y = j;
                    } else {
                        x = j;
                        y = i;
                    }

                    Location location = new LocationImpl(x, y);
                    if (board.getSlotAtLocation(location).getOwner() != null) {
                        wordSoFar.append(board.getSlotAtLocation(location)
                                .getPiece().getLetter());
                    } else if (wordSoFar.toString().length() > 1) {
                        if (!dict.contains(wordSoFar
                                .toString())) {
                            invalidWords.add(wordSoFar.toString());
                            break;
                        } else {
                            wordSoFar = new StringBuilder();
                        }
                    } else {
                        wordSoFar = new StringBuilder();
                    }
                }
            }
        }
        changeRequest.revert();
        return invalidWords;
    }

    public boolean areValidChanges(ChangeRequest changeRequest,
                                   Player player, Board board) {
        changeRequest.revert();
        Map<Location, ScrabblePiece> changes = changeRequest.getChangesToBoard();
        for (Location changeLocation : changes.keySet()) {
            if (changeLocation.getX() >= Board.DIMENSION ||
                    changeLocation.getY() >= Board.DIMENSION) {
                return false;
            }
            if (board.getSlotAtLocation(changeLocation).getOwner() != null) {
                return false;
            }
            changeRequest.refill();
            changeRequest.revert();
        }
        Map<Location, Card> addedCards = changeRequest.getAddedCards();

        for (Location changeLocation : addedCards.keySet()) {
            if (changeLocation.getX() >= Board.DIMENSION ||
                    changeLocation.getY() >= Board.DIMENSION) {
                return false;
            }
            if (board.getSlotAtLocation(changeLocation).getOwner() != null) {
                return false;
            }
        }
        boolean connected = isConnected(board, changeRequest);
        changeRequest.refill();
        return connected;
    }
}
