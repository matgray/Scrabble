/*
 * @author <a href="mailto:matgray@cmu.edu">Mat Gray</a>
 * Licensed under a Creative Commons
 * Attribution-NonCommercial-ShareAlike 3.0 Unported License.
 */

package edu.cmu.cs.matgray.client.ui;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import edu.cmu.cs.matgray.client.Scrabble;
import edu.cmu.cs.matgray.client.event.UpdateUIEvent;
import edu.cmu.cs.matgray.client.event.UpdateUIEventHandler;
import edu.cmu.cs.matgray.client.handler.BoardCellClickHandler;
import edu.cmu.cs.matgray.shared.interfaces.Board;
import edu.cmu.cs.matgray.shared.interfaces.Location;
import edu.cmu.cs.matgray.shared.interfaces.Slot;
import edu.cmu.cs.matgray.shared.model.LocationImpl;
import edu.cmu.cs.matgray.shared.model.WildScrabblePiece;

import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * Displays the scrabble board on the screen
 */
public class BoardUI extends FlexTable implements UpdateUIEventHandler {

    public final static String TILE_DIMENSION = "50px";

    public BoardUI() {
        /**
         * Initialize FlexTable
         */
        super();
        /**
         * Register to know when games are fetched from server
         */
        Scrabble.globalEventBus.addHandler(UpdateUIEvent.TYPE, this);

    }

    @Override
    public void gameUpdated(UpdateUIEvent event) {
        /**
         * Render board
         */
        Scrabble.LOGGER.publish(new LogRecord(Level.INFO,
                "Board UI Updated"));
        for (int i = 0; i < Board.DIMENSION; i++) {
            for (int j = 0; j < Board.DIMENSION; j++) {
                Location location = new LocationImpl(j, i);
                Button cell = new Button();
                Slot s = Scrabble.game.getGameBoard().getSlotAtLocation(
                        location);

                /**
                 * If the owner of a slot is non-null, then there is a piece
                 * on it.
                 */
                if (s.getOwner() != null) {
                    cell.setHTML(s.getPiece().getHTML());
                    if (s.getPiece() instanceof WildScrabblePiece) {
                        cell.addStyleName("underline");
                    }
                    cell.setStyleName("scrabbleRegular");
                }

                /**
                 * Add handler to board cell
                 */
                cell.addClickHandler(new BoardCellClickHandler(location));
                cell.setSize(TILE_DIMENSION, TILE_DIMENSION);

                /**
                 * Color the slot according to it's type
                 */
                if (s.getType() != Slot.SLOT_TYPE.REGULAR) {
                    if (s.getType() == Slot.SLOT_TYPE.FACTOR_LETTER) {
                        if (s.getFactor() == 2) {
                            cell.setStylePrimaryName("scrabbleDoubleLetter");
                        } else if (s.getFactor() == 3) {
                            cell.setStylePrimaryName("scrabbleTripleLetter");
                        } else {
                            cell.setStyleName("scrabbleNLetter", true);
                        }
                    } else if (s.getType() == Slot.SLOT_TYPE.FACTOR_WORD) {
                        if (s.getFactor() == 2) {
                            cell.setStyleName("scrabbleDoubleWord");
                        } else if (s.getFactor() == 3) {
                            cell.setStyleName("scrabbleTripleWord");
                        } else {
                            cell.setStyleName("scrabbleNWord", true);
                        }
                    } else if (s.getType() == Slot.SLOT_TYPE.ROOT) {
                        cell.setStyleName("scrabbleRoot");
                    }
                }
                /**
                 * Color the slot a special color if it has a card on it
                 */
                if (s.getCard() != null) {
                    cell.setStyleName("orange");
                }
                this.setWidget(i, j, cell);
            }
        }
    }
}
