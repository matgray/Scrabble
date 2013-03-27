/*
 * @author <a href="mailto:matgray@cmu.edu">Mat Gray</a>
 * Licensed under a Creative Commons
 * Attribution-NonCommercial-ShareAlike 3.0 Unported License.
 */

package edu.cmu.cs.matgray.client.handler;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import edu.cmu.cs.matgray.client.Scrabble;
import edu.cmu.cs.matgray.shared.interfaces.Player;

import java.util.Set;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * Handler for when the "submit move" button is pressed
 */
public class MoveSubmitClickHandler implements ClickHandler {
    @Override
    public void onClick(ClickEvent clickEvent) {
        /**
         * Take the changes off of the board
         */
        Scrabble.changes.revert();

        Player currentPlayer = Scrabble.game.getPlayers().
                get(Scrabble.userAndrewID);

        /**
         * Make sure changes are valid
         */
        if (Scrabble.verifier.areValidChanges(Scrabble.changes, currentPlayer,
                Scrabble.game.getGameBoard())) {

            Set<String> invalidWords = Scrabble.verifier.getInvalidWords(
                    Scrabble.game.getGameBoard(),
                    Scrabble.changes);

            /**
             * Show the invalid words in a popup
             */
            if (invalidWords.size() > 0) {
                final PopupPanel p = new PopupPanel();
                p.setGlassEnabled(true);
                VerticalPanel vp = new VerticalPanel();
                vp.add(new Label("Invalid Words:"));
                vp.add(new InlineHTML("<br>"));
                for (String word : invalidWords) {
                    vp.add(new Label(word));
                }
                Button b = new Button("Close");
                b.addClickHandler(new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent clickEvent) {
                        p.hide();
                    }
                });
                vp.add(new InlineHTML("<br>"));
                HorizontalPanel hp = new HorizontalPanel();
                hp.setWidth("100%");
                hp.setHorizontalAlignment(HorizontalPanel.ALIGN_CENTER);
                hp.add(b);
                vp.add(hp);
                p.add(vp);
                p.center();
                /**
                 * Put the invalid changes back onto the board
                 */
                Scrabble.changes.refill();
                return;
            }

            /**
             * If the changes are valid, send them to the server.  The server
             * will then re-verify the changes, update the database,
             * and the other clients will update themselves
             */
            Scrabble.scrabbleService.submitChanges(
                    currentPlayer,
                    Scrabble.changes,
                    Scrabble.gameID,
                    new AsyncCallback<Boolean>() {
                        @Override
                        public void onFailure(Throwable throwable) {
                            Window.alert("There was a problem submitting the " +
                                    "changes to the server");
                            Scrabble.LOGGER.publish(new LogRecord(Level.SEVERE,
                                    throwable.getMessage()));
                        }

                        @Override
                        public void onSuccess(Boolean aBoolean) {
                            if (aBoolean) {
                                Scrabble.LOGGER.publish(
                                        new LogRecord(Level.INFO,
                                                "Changes successfully made to server"));
                            } else {
                                Scrabble.LOGGER.publish(
                                        new LogRecord(Level.SEVERE,
                                                "Changes not written to the server. " +
                                                        "Server-side verification failed"));

                                Window.alert("Your move is invalid");

                            }
                        }
                    }
            );
        }
        /**
         * If the player submitted invalid moves (eg. not connected to the
         * root), then log and notify
         */
        else {
            Scrabble.LOGGER.publish(new LogRecord(Level.SEVERE,
                    "Failed Client-side verification"));
            Scrabble.changes.refill();
            Window.alert("Your move is invalid");
        }
    }
}
