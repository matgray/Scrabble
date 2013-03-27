/*
 * @author <a href="mailto:matgray@cmu.edu">Mat Gray</a>
 * Licensed under a Creative Commons
 * Attribution-NonCommercial-ShareAlike 3.0 Unported License.
 */

package edu.cmu.cs.matgray.client.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event for when a new update to the game is available from the server
 */
public class UpdateNecessaryEvent extends GwtEvent<UpdateNecessaryEventHandler> {
    public static final Type<UpdateNecessaryEventHandler> TYPE =
            new Type<UpdateNecessaryEventHandler>();


    @Override
    public Type<UpdateNecessaryEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(UpdateNecessaryEventHandler handler) {
        handler.onUpdateNeeded(this);
    }
}