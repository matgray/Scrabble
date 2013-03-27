/*
 * @author <a href="mailto:matgray@cmu.edu">Mat Gray</a>
 * Licensed under a Creative Commons
 * Attribution-NonCommercial-ShareAlike 3.0 Unported License.
 */

package edu.cmu.cs.matgray.client.event;

import com.google.gwt.event.shared.EventHandler;

/**
 * The handler for the CreateGameRequestEvent
 */
public interface CreateGameRequestEventHandler extends EventHandler {
    void onCreateGameRequest(CreateGameRequestEvent event);
}
