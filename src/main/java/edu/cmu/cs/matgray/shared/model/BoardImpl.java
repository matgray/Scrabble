/*
 * @author <a href="mailto:matgray@cmu.edu">Mat Gray</a>
 * Licensed under a Creative Commons
 * Attribution-NonCommercial-ShareAlike 3.0 Unported License.
 */

package edu.cmu.cs.matgray.shared.model;

import edu.cmu.cs.matgray.shared.interfaces.Board;
import edu.cmu.cs.matgray.shared.interfaces.Location;
import edu.cmu.cs.matgray.shared.interfaces.Slot;

public class BoardImpl implements Board {
    private Slot[][] board;

    public BoardImpl() {
        board = new Slot[Board.DIMENSION][Board.DIMENSION];
    }

    @Override
    public Slot getSlotAtLocation(Location location) {
        try {
            return board[location.getX()][location.getY()];
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void setSlotAtLocation(Location location, Slot s) {
        board[location.getX()][location.getY()] = s;
    }
}
