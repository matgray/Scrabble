/*
 * @author <a href="mailto:matgray@cmu.edu">Mat Gray</a>
 * Licensed under a Creative Commons
 * Attribution-NonCommercial-ShareAlike 3.0 Unported License.
 */

package edu.cmu.cs.matgray.client.ui;

import com.google.gwt.user.client.ui.*;
import edu.cmu.cs.matgray.client.Scrabble;
import edu.cmu.cs.matgray.client.event.UpdateUIEvent;
import edu.cmu.cs.matgray.client.event.UpdateUIEventHandler;
import edu.cmu.cs.matgray.shared.interfaces.Player;
import edu.cmu.cs.matgray.shared.interfaces.ScrabblePiece;

import java.util.List;

/**
 * The rack is the widget under the board
 */
public class Rack extends VerticalPanel implements UpdateUIEventHandler {
    FlexTable piecesView = new FlexTable();
    HorizontalPanel hp = new HorizontalPanel();

    public Rack() {
        super();
        Scrabble.globalEventBus.addHandler(UpdateUIEvent.TYPE, this);
        this.add(new InlineHTML("<br><br>"));
        VerticalPanel leftVp = new VerticalPanel();

        /**
         * Show the pieces that the current player has
         */
        leftVp.add(new Label("Available Pieces:"));
        leftVp.add(piecesView);
        hp.add(leftVp);
        this.add(hp);
    }

    @Override
    public void gameUpdated(UpdateUIEvent event) {
        /**
         * Redraw the rack
         */
        piecesView.removeAllRows();

        Player currentPlayer = Scrabble.game.getPlayers().get(Scrabble
                .userAndrewID);

        List<ScrabblePiece> pieces = currentPlayer.getPieces();

        for (int i = 0; i < pieces.size(); i++) {
            Button b = new Button();
            b.setHTML(pieces.get(i).getHTML());
            b.setSize(BoardUI.TILE_DIMENSION, BoardUI.TILE_DIMENSION);
            b.setStyleName("scrabbleRegular");
            b.setEnabled(false);
            piecesView.setWidget(0, i, b);
        }
    }
}
