/*
 * @author <a href="mailto:matgray@cmu.edu">Mat Gray</a>
 * Licensed under a Creative Commons
 * Attribution-NonCommercial-ShareAlike 3.0 Unported License.
 */

package edu.cmu.cs.matgray.client.ui;
/**
 * @author Mathew Gray
 * */

import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Controls what is being displayed inside of the main div, adding widgets on a stack of displayed widgets.
 * widgets not on top of the stack will be hidden.
 */
public class StateHandler {
    private DeckPanel clientView;
    private int currentIndex = -1;

    public StateHandler(DeckPanel clientView) {
        this.clientView = clientView;
    }

    /**
     * Push a new widget onto the widget stack
     *
     * @param newView the new widget
     */
    public void switchToWidget(final Widget newView) {
        /**
         * Panel we will be adding to the DeckPanel
         */
        newView.setStyleName("cent");
        VerticalPanel vClientInspectPanel = new VerticalPanel();
        vClientInspectPanel.add(newView);
        clientView.add(vClientInspectPanel);
        currentIndex++;
        /**
         * Show the inspector
         */
        clientView.showWidget(currentIndex);
    }

    /**
     * Pop the topmost widget
     */
    public void back() {
        clientView.remove(currentIndex);
        currentIndex--;
        clientView.showWidget(currentIndex);
    }
}