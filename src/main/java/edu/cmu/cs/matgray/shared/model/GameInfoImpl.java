/*
 * @author <a href="mailto:matgray@cmu.edu">Mat Gray</a>
 * Licensed under a Creative Commons
 * Attribution-NonCommercial-ShareAlike 3.0 Unported License.
 */

package edu.cmu.cs.matgray.shared.model;

import edu.cmu.cs.matgray.shared.interfaces.GameInfo;

import java.io.Serializable;

public class GameInfoImpl implements GameInfo, Serializable {

    GameInfoImpl() {
    }

    private String name;
    private int serverNumber;

    public GameInfoImpl(String name, int serverNumber) {
        this.serverNumber = serverNumber;
        this.name = name;
    }

    @Override
    public String getServerName() {
        return name;
    }

    @Override
    public int getServerNumber() {
        return serverNumber;
    }
}
