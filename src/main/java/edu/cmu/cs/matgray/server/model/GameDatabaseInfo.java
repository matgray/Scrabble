/*
 * @author <a href="mailto:matgray@cmu.edu">Mat Gray</a>
 * Licensed under a Creative Commons
 * Attribution-NonCommercial-ShareAlike 3.0 Unported License.
 */

package edu.cmu.cs.matgray.server.model;

import edu.cmu.cs.matgray.shared.model.GameImpl;

/**
 * This class is returned from the PersistenceService in order to describe a
 * game
 */
public class GameDatabaseInfo {
    public GameImpl getGame() {
        return game;
    }

    public void setGame(GameImpl game) {
        this.game = game;
    }

    public int getGameID() {
        return gameID;
    }

    public void setGameID(int gameID) {
        this.gameID = gameID;
    }

    GameImpl game;
    int gameID;

    public GameDatabaseInfo(GameImpl g, int id) {
        this.game = g;
        this.gameID = id;
    }
}
