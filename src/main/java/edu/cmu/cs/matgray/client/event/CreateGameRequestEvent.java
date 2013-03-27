/*
 * @author <a href="mailto:matgray@cmu.edu">Mat Gray</a>
 * Licensed under a Creative Commons
 * Attribution-NonCommercial-ShareAlike 3.0 Unported License.
 */

package edu.cmu.cs.matgray.client.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event for when a client creates a game
 */
public class CreateGameRequestEvent extends GwtEvent<CreateGameRequestEventHandler> {

    public static final Type<CreateGameRequestEventHandler> TYPE =
            new Type<CreateGameRequestEventHandler>();

    @Override
    public Type<CreateGameRequestEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(CreateGameRequestEventHandler handler) {
        handler.onCreateGameRequest(this);
    }
}