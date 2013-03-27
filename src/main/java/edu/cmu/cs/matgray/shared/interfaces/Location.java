/*
 * @author <a href="mailto:matgray@cmu.edu">Mat Gray</a>
 * Licensed under a Creative Commons
 * Attribution-NonCommercial-ShareAlike 3.0 Unported License.
 */

package edu.cmu.cs.matgray.shared.interfaces;

import java.io.Serializable;
import java.util.List;

public interface Location extends Serializable {

    public List<Location> validAdjacentLocations();

    int getX();

    int getY();

    Location up();

    Location down();

    Location left();

    Location right();
}
