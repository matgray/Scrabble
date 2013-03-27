/*
 * @author <a href="mailto:matgray@cmu.edu">Mat Gray</a>
 * Licensed under a Creative Commons
 * Attribution-NonCommercial-ShareAlike 3.0 Unported License.
 */

package edu.cmu.cs.matgray.client.handler;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import edu.cmu.cs.matgray.client.Scrabble;
import edu.cmu.cs.matgray.client.event.UpdateUIEvent;
import edu.cmu.cs.matgray.client.ui.BoardUI;
import edu.cmu.cs.matgray.client.ui.WildSelector;
import edu.cmu.cs.matgray.shared.exception.IllegalMoveException;
import edu.cmu.cs.matgray.shared.interfaces.*;
import edu.cmu.cs.matgray.shared.model.CardImpl;
import edu.cmu.cs.matgray.shared.model.WildScrabblePiece;

/**
 * Handler for when a cell is clicked on the board
 */
public class BoardCellClickHandler implements ClickHandler {

    /**
     * Location of the cell
     */
    Location location;

    /**
     * Initialize a new handler
     *
     * @param location the location on the board of the click handler
     */
    public BoardCellClickHandler(Location location) {
        this.location = location;
    }

    /**
     * Initialize the card panel (shows up when the user has cards in his/her
     * inventory
     *
     * @param currentPlayer the current player (who owns the cards)
     * @param cardPanel     the panel to add the cards to
     * @param popupPanel    the popup that the cardPanel is on
     * @return true if the user has cards, false otherwise
     */
    private boolean initCardPanel(Player currentPlayer, Panel cardPanel,
                                  final PopupPanel popupPanel) {

        boolean hasCards = false;

        for (Card.CARD_TYPE cardType : currentPlayer.getCards().keySet()) {
            final Card.CARD_TYPE ct = cardType;

            if (currentPlayer.getCards().get(cardType) > 0) {
                /**
                 * If we get here, the player has > 0 cards
                 */
                hasCards = true;
                Button b = new Button(CardImpl.getCardName(Scrabble.game
                        .getAvailableCards(), cardType));

                /**
                 * Add the card to the board on click
                 */
                b.addClickHandler(new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent clickEvent) {
                        Card card = CardImpl.getCardByType(Scrabble.game
                                .getAvailableCards(), ct);
                        if (card != null) {
                            Scrabble.changes.addCardToBoard(location, card);
                            Scrabble.globalEventBus.fireEvent(new UpdateUIEvent());
                            popupPanel.hide();
                        }
                    }
                });
                /**
                 * Add the card to the panel
                 */
                cardPanel.add(b);
            }
        }
        return hasCards;
    }

    /**
     * Checks to see if a card is located at the current location,
     * and gives the option to remove it
     *
     * @param location   the location of the slot in question
     * @param popupPanel the PopupPanel that the panel is located in
     * @param panel      the panel to draw the remove card button to
     */
    private void checkForLocalCard(final Location location,
                                   final PopupPanel popupPanel,
                                   Panel panel) {

        if (Scrabble.game.getGameBoard().getSlotAtLocation(location).getCard
                () != null && Scrabble.changes.getAddedCards().containsKey(location)
                ) {
            Button removeButton = new Button("Remove Card");
            removeButton.setWidth("100%");
            removeButton.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent clickEvent) {
                    Scrabble.changes.removeCardFromBoard(location);
                    Scrabble.globalEventBus.fireEvent(new UpdateUIEvent());
                    popupPanel.hide();
                }
            });
            panel.add(new InlineHTML("<br>"));
            panel.add(removeButton);
            panel.add(new InlineHTML("<br>"));
        }
    }

    @Override
    public void onClick(ClickEvent clickEvent) {
        Player currentPlayer = Scrabble.game.getPlayers().get(Scrabble
                .userAndrewID);

        /**
         * Initialize panels
         */
        final PopupPanel popupPanel = new PopupPanel();
        popupPanel.setGlassEnabled(true);
        VerticalPanel mainPanel = new VerticalPanel();
        HorizontalPanel topInnerPanel = new HorizontalPanel();
        topInnerPanel.setHorizontalAlignment(HorizontalPanel.ALIGN_CENTER);
        HorizontalPanel middleInnerPanel = new HorizontalPanel();

        Slot clickedSlot = Scrabble.game.getGameBoard().getSlotAtLocation
                (location);
        boolean hasCards = false;
        boolean hasPieces = false;

        /**
         * Case 1: the slot is empty
         */
        if (clickedSlot.getOwner() == null) {
            /**
             * SHow the type of the slot
             */
            Label slotType = new Label("Slot Type: " + clickedSlot
                    .getDescription());
            mainPanel.add(slotType);
            mainPanel.add(new InlineHTML("<br>"));

            /**
             * Read out all of the pieces that the player can put here
             */
            FlexTable flexTable = new FlexTable();
            int i = 0;
            for (final ScrabblePiece piece : currentPlayer.getPieces()) {
                hasPieces = true;
                Button b = new Button();
                b.setSize(BoardUI.TILE_DIMENSION, BoardUI.TILE_DIMENSION);
                b.setHTML(piece.getHTML());
                b.setStyleName("scrabbleRegular");
                b.addClickHandler(new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent clickEvent) {
                        /**
                         * If the user chooses a wild card,
                         * allow them to select a letter
                         */
                        if (piece instanceof WildScrabblePiece) {
                            new WildSelector(location, piece);
                        }
                        /**
                         * If it's not a wild card, then just add the piece
                         * to the board
                         */
                        else {
                            try {
                                Scrabble.changes.addToBoard(location, piece);
                                Scrabble.globalEventBus.fireEvent(new UpdateUIEvent());
                            } catch (IllegalMoveException e) {
                                Window.alert("Illegal Move!");
                            }
                        }
                        popupPanel.hide();
                    }
                });
                /**
                 * Add each piece to the panel
                 */
                flexTable.setWidget(0, i, b);
                i++;
            }
            topInnerPanel.add(flexTable);

            /**
             * If the slot does not already have a card on it,
             * allow the player to place a card.
             */
            if (clickedSlot.getCard() == null) {
                hasCards = initCardPanel(currentPlayer, middleInnerPanel,
                        popupPanel);
            }
        }
        /**
         * Case 2: The user has placed a piece on this slot during this turn,
         * so he/she is able to remove it
         */
        else if (clickedSlot.getOwner() == currentPlayer &&
                Scrabble.changes.getChangesToBoard().get(location) != null) {

            Button removeButton = new Button("Remove Piece");
            removeButton.setWidth("100%");
            /**
             * Remove the piece on click
             */
            removeButton.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent clickEvent) {
                    Scrabble.changes.removeFromBoard(location);
                    Scrabble.globalEventBus.fireEvent(new UpdateUIEvent());
                    popupPanel.hide();
                }
            });
            topInnerPanel.add(removeButton);
            /**
             * Alternatively, if there is no card on the slot,
             * the user can put a card on the slot
             */
            if (clickedSlot.getCard() == null) {
                hasCards = initCardPanel(currentPlayer, middleInnerPanel,
                        popupPanel);
            }
        }
        /**
         * Case 3: The piece on the board has already been placed
         */
        else {
            /**
             * Show the owner of the piece
             */
            String ownerName = clickedSlot.getOwner().getName();
            Label label = new Label("Owner: " + ownerName);
            topInnerPanel.add(label);

        }
        /**
         * Render labels if necessary
         */
        if (hasPieces) {
            mainPanel.add(new InlineHTML("<br><hr><br>"));
            mainPanel.add(new Label("Available Pieces:"));
        }
        mainPanel.add(topInnerPanel);
        if (hasCards) {
            mainPanel.add(new InlineHTML("<br><hr><br>"));
            mainPanel.add(new Label("Available Cards:"));
            mainPanel.add(middleInnerPanel);
        }

        /**
         * Allow for a newly placed card to be removed
         */
        checkForLocalCard(location, popupPanel, mainPanel);

        Button close = new Button("Close");
        close.setWidth("100%");
        close.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                popupPanel.hide();
            }
        });
        mainPanel.add(new InlineHTML("<br>"));
        mainPanel.add(close);
        popupPanel.add(mainPanel);
        popupPanel.center();
    }
}
