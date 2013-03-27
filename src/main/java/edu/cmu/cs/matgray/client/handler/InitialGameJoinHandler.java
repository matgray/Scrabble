/*
 * @author <a href="mailto:matgray@cmu.edu">Mat Gray</a>
 * Licensed under a Creative Commons
 * Attribution-NonCommercial-ShareAlike 3.0 Unported License.
 */

package edu.cmu.cs.matgray.client.handler;


import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import edu.cmu.cs.matgray.client.Scrabble;
import edu.cmu.cs.matgray.client.event.UpdateUIEvent;
import edu.cmu.cs.matgray.client.ui.BoardUI;
import edu.cmu.cs.matgray.client.ui.GameHUD;
import edu.cmu.cs.matgray.client.ui.Rack;
import edu.cmu.cs.matgray.shared.ChangeVerifier;
import edu.cmu.cs.matgray.shared.interfaces.GameUpdate;
import edu.cmu.cs.matgray.shared.model.ChangeRequestImpl;

import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * Handler for when a game is initially joined
 */
public class InitialGameJoinHandler implements AsyncCallback<GameUpdate> {
    @Override
    public void onFailure(Throwable throwable) {
        /**
         * Log message to console
         */
        Scrabble.LOGGER.publish(new LogRecord(Level.SEVERE,
                throwable.getMessage()));
        Window.alert("There was a problem joining the game");
    }

    @Override
    public void onSuccess(GameUpdate gamePackage) {
        /**
         * Set static variables
         */
        Scrabble.lastUpdateTime = gamePackage.getTimeStamp();
        Scrabble.game = gamePackage.getGame();
        Scrabble.gameID = gamePackage.getID();
        Scrabble.changes = new ChangeRequestImpl(gamePackage.getGame(),
                Scrabble.userAndrewID);
        /**
         * Show the game
         */
        switchToGameUI(Scrabble.game.getName());
        Scrabble.loadingPopup.hide();
        /**
         * Notify other widgets that a new game was
         * fetched
         */
        Scrabble.globalEventBus.fireEvent(new UpdateUIEvent());

        /**
         * Create a new verifier
         */
        Scrabble.scrabbleService.getVerifier(new AsyncCallback<ChangeVerifier>() {
            @Override
            public void onFailure(Throwable throwable) {
                Window.alert("There was a problem getting the move " +
                        "verification object from the server");
                /**
                 * Log message
                 */
                Scrabble.LOGGER.publish(new LogRecord(Level.SEVERE,
                        throwable.getMessage()));
            }

            @Override
            public void onSuccess(ChangeVerifier changeVerifier) {
                Scrabble.verifier = changeVerifier;
            }
        });
    }

    /**
     * Switches the view to the game
     *
     * @param gameName
     */
    private void switchToGameUI(String gameName) {
        VerticalPanel vp = new VerticalPanel();
        Label name = new Label(gameName);
        name.setStyleName("gradLarge");
        vp.add(name);
        HorizontalPanel hp = new HorizontalPanel();
        hp.add(new BoardUI());
        hp.add(new GameHUD());
        vp.add(hp);
        vp.add(new Rack());
        Scrabble.stateHandler.switchToWidget(vp);
    }
}
