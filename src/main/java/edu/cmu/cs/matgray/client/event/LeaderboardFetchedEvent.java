/*
 * @author <a href="mailto:matgray@cmu.edu">Mat Gray</a>
 * Licensed under a Creative Commons
 * Attribution-NonCommercial-ShareAlike 3.0 Unported License.
 */

package edu.cmu.cs.matgray.client.event;

import com.google.gwt.event.shared.GwtEvent;

import java.util.Map;

/**
 * Event for when the leaderboard is fetched from the server
 */
public class LeaderboardFetchedEvent extends GwtEvent<LeaderboardFetchedEventHandler> {
    public static final Type<LeaderboardFetchedEventHandler> TYPE =
            new Type<LeaderboardFetchedEventHandler>();

    private Map<String, Integer> leaderboard;

    public LeaderboardFetchedEvent(Map<String, Integer> info) {
        this.leaderboard = info;
    }

    public Map<String, Integer> getLeaderboard() {
        return this.leaderboard;
    }

    @Override
    public Type<LeaderboardFetchedEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(LeaderboardFetchedEventHandler handler) {
        handler.onLeaderboardFetched(this);
    }
}