/*
 * @author <a href="mailto:matgray@cmu.edu">Mat Gray</a>
 * Licensed under a Creative Commons
 * Attribution-NonCommercial-ShareAlike 3.0 Unported License.
 */

package edu.cmu.cs.matgray.shared.model;


import edu.cmu.cs.matgray.shared.interfaces.Game;
import edu.cmu.cs.matgray.shared.interfaces.GameUpdate;

import java.sql.Timestamp;

public class GameUpdateImpl implements GameUpdate {
    Timestamp timestamp;
    Game g;
    int id = -1;

    public GameUpdateImpl() {
    }

    public GameUpdateImpl(Game g, Timestamp t, int gameID) {
        this.g = g;
        this.timestamp = t;
        this.id = gameID;
    }

    public Game getGame() {
        return g;
    }

    public Timestamp getTimeStamp() {
        return timestamp;
    }

    public int getID() {
        return id;
    }
}
