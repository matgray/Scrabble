/*
 * @author <a href="mailto:matgray@cmu.edu">Mat Gray</a>
 * Licensed under a Creative Commons
 * Attribution-NonCommercial-ShareAlike 3.0 Unported License.
 */

package edu.cmu.cs.matgray.client.ui;

import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import edu.cmu.cs.matgray.client.Scrabble;
import edu.cmu.cs.matgray.client.event.UpdateUIEvent;
import edu.cmu.cs.matgray.shared.exception.IllegalMoveException;
import edu.cmu.cs.matgray.shared.interfaces.Location;
import edu.cmu.cs.matgray.shared.interfaces.ScrabblePiece;
import edu.cmu.cs.matgray.shared.model.WildScrabblePiece;

/**
 * This class is the GUI for selecting the character for a wildcard to be
 */
public class WildSelector extends PopupPanel {

    final TextBox tb = new TextBox();
    WildScrabblePiece piece;
    Location location;

    public WildSelector(Location location, ScrabblePiece piece) {
        super();
        this.setGlassEnabled(true);
        this.piece = (WildScrabblePiece) piece;
        this.location = location;
        VerticalPanel mainPanel = new VerticalPanel();

        mainPanel.add(new Label("Please Type A Character:"));
        mainPanel.add(new InlineHTML("<br>"));
        HorizontalPanel hp1 = new HorizontalPanel();
        hp1.setWidth("100%");
        hp1.setHorizontalAlignment(HorizontalPanel.ALIGN_CENTER);
        hp1.add(tb);
        hp1.add(new InlineHTML("<br>"));
        Button insert = new Button("Insert");
        insert.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                placePiece();
            }
        });
        HorizontalPanel hp2 = new HorizontalPanel();
        hp2.setHorizontalAlignment(HorizontalPanel.ALIGN_CENTER);
        hp2.setWidth("100%");
        hp2.add(insert);

        final WildSelector ws = this;
        final ScrabblePiece p = piece;
        Button close = new Button("Close");
        close.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                ws.hide();
                ((WildScrabblePiece) p).setLetter(null);
            }
        });
        hp2.add(new InlineHTML("  "));
        hp2.add(close);

        tb.addKeyPressHandler(new ValidPieceKeyPressHandler());

        /**
         * WildCards can only hold one character
         */
        tb.setMaxLength(1);
        /**
         * Center text
         */
        tb.setStyleName("tbCenter");
        mainPanel.add(hp1);
        mainPanel.add(hp2);
        this.add(mainPanel);
        this.center();
        tb.setFocus(true);
    }

    /**
     * This class handles the kepresses to set the wildcard
     */
    private class ValidPieceKeyPressHandler implements KeyPressHandler {
        @Override
        public void onKeyPress(KeyPressEvent event) {
            /**
             * Place the piece on enter
             */
            if (event.getCharCode() == KeyCodes.KEY_ENTER) {
                placePiece();
            }
            /**
             * On other inputs, update the wildcard if possible,
             * and highlight valid/false
             */
            else {
                tb.setText("");
                tb.removeStyleName("validTextBox");
                tb.removeStyleName("invalidTextBox");
                if (piece.setLetter(String.valueOf(event.getCharCode()).toLowerCase())) {
                    tb.setText(piece.getLetter());
                    tb.addStyleName("validTextBox");
                } else {
                    event.stopPropagation();
                    tb.addStyleName("invalidTextBox");
                }
            }
        }
    }

    /**
     * Places a wildcard on the board if possible, alerts on fail.
     */
    private void placePiece() {
        if (piece.isSet()) {
            try {
                Scrabble.changes.addToBoard(location, piece);
            } catch (IllegalMoveException e) {
                Window.alert("Illegal Move!");
            }
            Scrabble.globalEventBus.fireEvent(new UpdateUIEvent());
            this.hide();
        } else {
            Window.alert("Invalid Piece");
        }
    }
}