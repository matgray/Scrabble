/*
 * @author <a href="mailto:matgray@cmu.edu">Mat Gray</a>
 * Licensed under a Creative Commons
 * Attribution-NonCommercial-ShareAlike 3.0 Unported License.
 */

package edu.cmu.cs.matgray.client.ui;

import com.google.gwt.user.client.ui.*;

public class LoadingView extends PopupPanel {
    /**
     * This widget pops up when the client is synchronizing it's data with
     * the server.
     */
    public LoadingView() {
        super();
        VerticalPanel vp = new VerticalPanel();
        vp.add(new Label("Synchronizing"));
        HorizontalPanel hp = new HorizontalPanel();
        hp.setWidth("100%");
        hp.setHorizontalAlignment(HorizontalPanel.ALIGN_CENTER);
        hp.add(new Image("images/ajax-loader.gif"));
        vp.add(hp);
        this.add(vp);
        this.setGlassEnabled(true);
    }
}
