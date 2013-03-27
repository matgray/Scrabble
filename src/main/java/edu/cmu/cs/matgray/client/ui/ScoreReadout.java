/*
 * @author <a href="mailto:matgray@cmu.edu">Mat Gray</a>
 * Licensed under a Creative Commons
 * Attribution-NonCommercial-ShareAlike 3.0 Unported License.
 */

package edu.cmu.cs.matgray.client.ui;

import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.TextColumn;
import edu.cmu.cs.matgray.client.Scrabble;
import edu.cmu.cs.matgray.client.event.UpdateUIEvent;
import edu.cmu.cs.matgray.client.event.UpdateUIEventHandler;
import edu.cmu.cs.matgray.shared.interfaces.Player;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * This class is contained in the HUD and reads out
 * information about the current game
 */
public class ScoreReadout extends CellTable<Player> implements UpdateUIEventHandler {
    TextColumn<Player> nameColumn;
    TextColumn<Player> scoreColumn;
    TextColumn<Player> moveColumn;

    public ScoreReadout() {
        nameColumn = new TextColumn<Player>() {
            @Override
            public String getValue(Player player) {
                return player.getName();
            }
        };

        scoreColumn = new TextColumn<Player>() {
            @Override
            public String getValue(Player player) {
                return String.valueOf(player.getScore());
            }
        };

        moveColumn = new TextColumn<Player>() {
            @Override
            public String getValue(Player player) {
                if (player.equals(Scrabble.game.getCurrentTurn())) {
                    return "←";
                }
                return "";
            }
        };

        this.addColumn(nameColumn, "Name");
        this.addColumn(scoreColumn, "Score");
        this.addColumn(moveColumn, "");
        Scrabble.globalEventBus.addHandler(UpdateUIEvent.TYPE, this);
    }


    @Override
    public void gameUpdated(UpdateUIEvent event) {
        Scrabble.LOGGER.publish(new LogRecord(Level.INFO,
                "Player List Updated"));
        ArrayList<Player> temp = new ArrayList<Player>();
        temp.addAll(Scrabble.game.getPlayers().values());
        this.setRowData(0, temp);
    }
}
