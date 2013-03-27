/*
 * @author <a href="mailto:matgray@cmu.edu">Mat Gray</a>
 * Licensed under a Creative Commons
 * Attribution-NonCommercial-ShareAlike 3.0 Unported License.
 */

package edu.cmu.cs.matgray.client.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event for when the UI has to update to the game fetched from the server
 */
public class UpdateUIEvent extends GwtEvent<UpdateUIEventHandler> {

    public static final GwtEvent.Type<UpdateUIEventHandler> TYPE =
            new GwtEvent.Type<UpdateUIEventHandler>();


    @Override
    public Type<UpdateUIEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(UpdateUIEventHandler handler) {
        handler.gameUpdated(this);
    }
}