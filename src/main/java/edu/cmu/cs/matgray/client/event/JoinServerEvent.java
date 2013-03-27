/*
 * @author <a href="mailto:matgray@cmu.edu">Mat Gray</a>
 * Licensed under a Creative Commons
 * Attribution-NonCommercial-ShareAlike 3.0 Unported License.
 */

package edu.cmu.cs.matgray.client.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event for when a client joins a pre-existing game
 */
public class JoinServerEvent extends GwtEvent<JoinServerEventHandler> {
    public static final GwtEvent.Type<JoinServerEventHandler> TYPE =
            new GwtEvent.Type<JoinServerEventHandler>();

    int id;

    /**
     * Create a new JoinServerEvent
     *
     * @param id the game id that was deserialized
     */
    public JoinServerEvent(int id) {
        this.id = id;
    }

    /**
     * Get the updated game
     *
     * @return the updated game
     */
    public int getId() {
        return this.id;
    }

    @Override
    public GwtEvent.Type<JoinServerEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(JoinServerEventHandler handler) {
        handler.onJoinServer(this);
    }
}