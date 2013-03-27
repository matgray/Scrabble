/*
 * @author <a href="mailto:matgray@cmu.edu">Mat Gray</a>
 * Licensed under a Creative Commons
 * Attribution-NonCommercial-ShareAlike 3.0 Unported License.
 */

package edu.cmu.cs.matgray.client.event;

import com.google.gwt.event.shared.EventHandler;

/**
 * Handler class for GameFetchedEvent
 */
public interface GameFetchedEventHandler extends EventHandler {
    void onGameFetched(GameFetchedEvent event);
}
