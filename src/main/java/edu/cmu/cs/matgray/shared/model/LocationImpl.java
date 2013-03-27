/*
 * @author <a href="mailto:matgray@cmu.edu">Mat Gray</a>
 * Licensed under a Creative Commons
 * Attribution-NonCommercial-ShareAlike 3.0 Unported License.
 */

package edu.cmu.cs.matgray.shared.model;


import edu.cmu.cs.matgray.shared.interfaces.Board;
import edu.cmu.cs.matgray.shared.interfaces.Location;

import java.util.ArrayList;
import java.util.List;

public class LocationImpl implements Location {


    @Override
    public List<Location> validAdjacentLocations() {
        List<Location> locations = new ArrayList<Location>(4);
        if (this.up() != null) {
            locations.add(this.up());
        }
        if (this.down() != null) {
            locations.add(this.down());
        }
        if (this.left() != null) {
            locations.add(this.left());
        }
        if (this.right() != null) {
            locations.add(this.right());
        }
        return locations;
    }

    private int x;

    public int getY() {
        return y;
    }

    @Override
    public Location left() {
        if (x - 1 < 0) {
            return null;
        }
        return new LocationImpl(x - 1, y);
    }

    @Override
    public Location right() {
        if (x + 1 >= Board.DIMENSION) {
            return null;
        }
        return new LocationImpl(x + 1, y);
    }

    @Override
    public Location up() {
        if (y - 1 < 0) {
            return null;
        }
        return new LocationImpl(x, y - 1);
    }

    @Override
    public Location down() {
        if (y + 1 >= Board.DIMENSION) {
            return null;
        }
        return new LocationImpl(x, y + 1);
    }

    public int getX() {
        return x;
    }

    private int y;

    public LocationImpl(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public LocationImpl() {
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof LocationImpl) {
            LocationImpl l = (LocationImpl) o;
            return (this.getX() == l.getX() && this.y == l.getY());
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 17;
        hash = 31 * hash + x;
        hash = 31 * hash + y;
        return hash;
    }
}
