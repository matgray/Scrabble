/*
 * @author <a href="mailto:matgray@cmu.edu">Mat Gray</a>
 * Licensed under a Creative Commons
 * Attribution-NonCommercial-ShareAlike 3.0 Unported License.
 */

package edu.cmu.cs.matgray.client.ui;

import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HorizontalPanel;
import edu.cmu.cs.matgray.client.Scrabble;
import edu.cmu.cs.matgray.client.event.JoinServerEvent;
import edu.cmu.cs.matgray.shared.interfaces.GameInfo;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * This class lets you browse the available games
 */
public class GameBrowser extends CellTable<GameInfo> {

    /**
     * Column that reads out gameName
     */
    TextColumn<GameInfo> gameName;

    /**
     * Column that holds connect button
     */
    Column connectColumn;

    public GameBrowser() {
        super();
        gameName = new TextColumn<GameInfo>() {
            @Override
            public String getValue(GameInfo gameInfo) {
                return gameInfo.getServerName();
            }
        };

        ButtonCell buttonCell = new ButtonCell() {
            // You can select the button column
            @Override
            public boolean handlesSelection() {
                return true;
            }
        };

        connectColumn = new Column<GameInfo, String>(buttonCell) {
            @Override
            public String getValue(GameInfo object) {
                // The value to display in the button.
                return "Connect";
            }
        };

        connectColumn.setHorizontalAlignment(HorizontalPanel.ALIGN_RIGHT);

        connectColumn.setFieldUpdater(new FieldUpdater<GameInfo, String>() {
            public void update(int index, GameInfo object, String value) {
                // Value is the button value.  Object is the row object.
                Scrabble.globalEventBus.fireEvent(new JoinServerEvent(object
                        .getServerNumber()));

            }
        });

        this.addColumn(gameName, "Game Name");
        this.addColumn(connectColumn);
        this.setColumnWidth(gameName, 300.0, com.google.gwt.dom.client
                .Style.Unit.PX);
        this.setColumnWidth(connectColumn, 300.0, com.google.gwt.dom.client
                .Style.Unit.PX);

        /**
         * Get a game list and display it
         */
        final GameBrowser gameBrowser = this;
        Scrabble.scrabbleService.getGameList(
                new AsyncCallback<List<GameInfo>>() {
                    @Override
                    public void onFailure(Throwable throwable) {
                        Scrabble.LOGGER.publish(new LogRecord(Level.SEVERE,
                                throwable.getMessage()));
                        Window.alert("There was a problem fetching the list " +
                                "of active games");
                    }

                    @Override
                    public void onSuccess(List<GameInfo> gameInfos) {
                        /**
                         * Render data to table
                         */
                        gameBrowser.setRowData(0, gameInfos);
                    }
                });
    }
}
