/*
 * @author <a href="mailto:matgray@cmu.edu">Mat Gray</a>
 * Licensed under a Creative Commons
 * Attribution-NonCommercial-ShareAlike 3.0 Unported License.
 */

package edu.cmu.cs.matgray.client.event;

import com.google.gwt.event.shared.GwtEvent;
import edu.cmu.cs.matgray.shared.interfaces.Game;

/**
 * Event for when a game is fetched from the server.  Allows for UI elements
 * to update
 */
public class GameFetchedEvent extends GwtEvent<GameFetchedEventHandler> {
    public static final GwtEvent.Type<GameFetchedEventHandler> TYPE =
            new GwtEvent.Type<GameFetchedEventHandler>();

    private Game fetchedGame;

    public GameFetchedEvent(Game g) {
        this.fetchedGame = g;
    }

    public Game getGame() {
        return this.fetchedGame;
    }

    @Override
    public GwtEvent.Type<GameFetchedEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(GameFetchedEventHandler handler) {
        handler.onGameFetched(this);
    }
}