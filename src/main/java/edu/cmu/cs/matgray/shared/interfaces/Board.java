/*
 * @author <a href="mailto:matgray@cmu.edu">Mat Gray</a>
 * Licensed under a Creative Commons
 * Attribution-NonCommercial-ShareAlike 3.0 Unported License.
 */

package edu.cmu.cs.matgray.shared.interfaces;

import java.io.Serializable;

public interface Board extends Serializable {

    /**
     * Dimension of the board
     */
    public static int DIMENSION = 15;

    /**
     * Get the ScrabblePiece at a certain coordinate (top left is 0,0)
     *
     * @param location the location of the slot
     * @return the ScrabblePiece, or null if empty;
     */
    public Slot getSlotAtLocation(Location location);

    public void setSlotAtLocation(Location location, Slot s);
}
