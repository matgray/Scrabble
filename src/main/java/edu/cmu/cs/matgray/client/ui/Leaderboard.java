/*
 * @author <a href="mailto:matgray@cmu.edu">Mat Gray</a>
 * Licensed under a Creative Commons
 * Attribution-NonCommercial-ShareAlike 3.0 Unported License.
 */

package edu.cmu.cs.matgray.client.ui;

import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.view.client.ListDataProvider;
import edu.cmu.cs.matgray.client.Scrabble;
import edu.cmu.cs.matgray.client.event.LeaderboardFetchedEvent;
import edu.cmu.cs.matgray.client.event.LeaderboardFetchedEventHandler;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class Leaderboard extends CellTable<Map.Entry<String, Integer>>
        implements LeaderboardFetchedEventHandler {
    TextColumn<Map.Entry<String, Integer>> deptName;
    TextColumn<Map.Entry<String, Integer>> deptScore;

    public Leaderboard() {
        super();
        this.deptName = new TextColumn<Map.Entry<String, Integer>>() {
            @Override
            public String getValue(Map.Entry<String, Integer> deptEntry) {
                return deptEntry.getKey();
            }
        };

        this.deptScore = new TextColumn<Map.Entry<String, Integer>>() {
            @Override
            public String getValue(Map.Entry<String, Integer> stringIntegerEntry) {
                return String.valueOf(stringIntegerEntry.getValue());
            }
        };

        Scrabble.globalEventBus.addHandler(LeaderboardFetchedEvent.TYPE, this);

        Scrabble.scrabbleService.getLeaderboard(
                new AsyncCallback<Map<String, Integer>>() {
                    @Override
                    public void onFailure(Throwable throwable) {
                        Scrabble.LOGGER.publish(new LogRecord(Level.SEVERE,
                                throwable.getMessage()));
                        Window.alert("Could not fetch leaderboard from server");
                    }

                    @Override
                    public void onSuccess(Map<String, Integer> stringIntegerMap) {
                        Scrabble.globalEventBus.fireEvent(new LeaderboardFetchedEvent
                                (stringIntegerMap));
                    }
                });
    }

    @Override
    public void onLeaderboardFetched(LeaderboardFetchedEvent event) {

        deptScore.setDefaultSortAscending(true);
        this.addColumn(deptName, "Department Name");
        this.addColumn(deptScore, "Score");

        ListDataProvider<Map.Entry<String, Integer>> dataProvider =
                new ListDataProvider<Map.Entry<String, Integer>>();

        dataProvider.addDataDisplay(this);

        List<Map.Entry<String, Integer>> dataProviderList =
                dataProvider.getList();

        dataProviderList.addAll(event.getLeaderboard().entrySet());

        ColumnSortEvent.ListHandler
                <Map.Entry<String, Integer>> scoreSortHandler =
                new ColumnSortEvent.ListHandler
                        <Map.Entry<String, Integer>>(dataProviderList);

        /**
         * Define sorting by dept score
         */
        scoreSortHandler.setComparator(deptScore, new Comparator
                <Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String,
                    Integer> o1, Map.Entry<String,
                    Integer> o2) {
                int x = o1.getValue();
                int y = o2.getValue();
                return (x < y) ? -1 : ((x == y) ? 0 : 1);
            }
        });

        this.addColumnSortHandler(scoreSortHandler);

        ColumnSortEvent.ListHandler
                <Map.Entry<String, Integer>> nameSortHandler =

                new ColumnSortEvent.ListHandler
                        <Map.Entry<String, Integer>>(dataProviderList);

        this.addColumnSortHandler(nameSortHandler);


        this.getColumn(0).setSortable(false);
        this.getColumn(1).setSortable(true);
        this.setRowData(0, dataProviderList);

        this.getColumnSortList().push(deptScore);
        ColumnSortEvent.fire(this, this.getColumnSortList());

    }
}
