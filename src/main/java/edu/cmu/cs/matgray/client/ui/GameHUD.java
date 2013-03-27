/*
 * @author <a href="mailto:matgray@cmu.edu">Mat Gray</a>
 * Licensed under a Creative Commons
 * Attribution-NonCommercial-ShareAlike 3.0 Unported License.
 */

package edu.cmu.cs.matgray.client.ui;

import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.view.client.ListDataProvider;
import edu.cmu.cs.matgray.client.Scrabble;
import edu.cmu.cs.matgray.client.event.UpdateUIEvent;
import edu.cmu.cs.matgray.client.event.UpdateUIEventHandler;
import edu.cmu.cs.matgray.client.handler.MoveSubmitClickHandler;
import edu.cmu.cs.matgray.shared.interfaces.Card;
import edu.cmu.cs.matgray.shared.interfaces.Game;
import edu.cmu.cs.matgray.shared.interfaces.Player;
import edu.cmu.cs.matgray.shared.model.CardImpl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * This class is the HUD of the game. Displays to the right of the game board.
 * Shows the current players in the game/their points,
 * shows currently purchased cards, and shows the buttons for submitting
 * moved/buying cards
 */
public class GameHUD extends VerticalPanel implements UpdateUIEventHandler {
    public static final PopupPanel cardChooser = new PopupPanel();

    Button submitMove = new Button("Submit Move");
    Button buyCard = new Button("Purchase Card");
    Label notYourTurn = new Label("It's not your turn.");
    FlexTable cardView = new FlexTable();

    public GameHUD() {
        super();

        /**
         * Build some of the UI
         */
        this.add(new ScoreReadout());
        this.add(new InlineHTML("<br><br><hr><br><br>"));
        this.add(new Label("Available Cards:"));
        this.add(new InlineHTML("<br>"));
        this.add(cardView);
        this.add(new InlineHTML("<br><br><hr><br><br>"));
        cardView.setWidth("100%");
        submitMove.setWidth("100%");
        buyCard.setWidth("100%");

        /**
         * HUD updates itself when the UI needs to be updated
         */
        Scrabble.globalEventBus.addHandler(UpdateUIEvent.TYPE, this);

        /**
         * Can't click on things when purchasing a card
         */
        cardChooser.setGlassEnabled(true);


        submitMove.addClickHandler(new MoveSubmitClickHandler());
        buyCard.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                cardChooser.center();
            }
        });

        /**
         * Initialize the card chooser
         */
        VerticalPanel cardChooseMainPanel = new VerticalPanel();

        /**
         * Cards will be stored in a cell table
         */
        CellTable<Card> cardTable = new CellTable<Card>();

        /**
         * Column containing the card's name
         */
        TextColumn<Card> cardName = new TextColumn<Card>() {
            @Override
            public String getValue(Card card) {
                return card.getCardText();
            }
        };

        /**
         * Column containing the card's cost
         */
        TextColumn<Card> cardCost = new TextColumn<Card>() {
            @Override
            public String getValue(Card card) {
                return card.getCardCost() + " pts";
            }
        };

        /**
         * Cell containing the purchase button
         */
        ButtonCell buttonCell = new ButtonCell() {
            @Override
            public boolean handlesSelection() {
                return false;
            }
        };

        /**
         * Column containing the purchase buttons
         */
        Column purchaseColumn = new Column<Card, String>(buttonCell) {
            @Override
            public String getValue(Card object) {
                // The value to display in the button.
                return "Purchase";
            }
        };

        /**
         * Align the buttons to the right
         */
        purchaseColumn.setHorizontalAlignment(HorizontalPanel.ALIGN_RIGHT);
        purchaseColumn.setFieldUpdater(new FieldUpdater<Card, String>() {
            public void update(int index, Card object, String value) {
                Scrabble.scrabbleService.purchaseCard(Scrabble.gameID,
                        Scrabble.userAndrewID, object.getCardType(), new AsyncCallback<Void>() {
                    @Override
                    public void onFailure(Throwable throwable) {
                        Scrabble.LOGGER.publish(new LogRecord(Level.SEVERE,
                                throwable.getMessage()));
                        Window.alert("Server Error: Could not purchase card");
                    }

                    @Override
                    public void onSuccess(Void aVoid) {
                        cardChooser.hide();
                    }
                });
            }
        });

        /**
         * Set up a dataprovider (will enable sorting of columns)
         */
        ListDataProvider<Card> dataProvider = new ListDataProvider<Card>();

        dataProvider.addDataDisplay(cardTable);

        List<Card> cardList = new ArrayList<Card>(Scrabble.game
                .getAvailableCards());

        List<Card> dataProviderList = dataProvider.getList();

        dataProviderList.addAll(cardList);

        /**
         * Add all of the columns to the table
         */
        cardTable.addColumn(cardName, "Card Name");
        cardTable.addColumn(cardCost, "Card Cost");
        cardTable.addColumn(purchaseColumn);


        ColumnSortEvent.ListHandler<Card> costSortHandler = new
                ColumnSortEvent.ListHandler<Card>(dataProviderList);

        /**
         * Define sorting by cost of card
         */
        costSortHandler.setComparator(cardCost, new Comparator<Card>() {
            @Override
            public int compare(Card o1, Card o2) {
                int x = o1.getCardCost();
                int y = o2.getCardCost();
                return (x < y) ? -1 : ((x == y) ? 0 : 1);
            }
        });

        cardTable.addColumnSortHandler(costSortHandler);

        ColumnSortEvent.ListHandler<Card> nameSortHandler = new
                ColumnSortEvent.ListHandler<Card>(dataProviderList);

        /**
         * Define sorting by name of card
         */
        nameSortHandler.setComparator(cardName, new Comparator<Card>() {
            @Override
            public int compare(Card o1, Card o2) {
                if (o1 == o2) {
                    return 0;
                }
                if (o1 != null) {
                    return (o2 != null) ?
                            o1.getCardText().compareTo(o2.getCardText()) : 1;
                }
                return -1;
            }
        });

        cardTable.addColumnSortHandler(nameSortHandler);


        cardTable.getColumn(0).setSortable(true);
        cardTable.getColumn(1).setSortable(true);
        cardTable.setRowData(0, dataProviderList);

        /**
         * Initially sort by card cost
         */
        cardTable.getColumnSortList().push(cardCost);
        ColumnSortEvent.fire(cardTable, cardTable.getColumnSortList());

        cardChooseMainPanel.add(cardTable);

        /**
         * Add a close option to the bottom of the popup
         */
        HorizontalPanel hp = new HorizontalPanel();
        Button close = new Button("Close");
        close.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                cardChooser.hide();
            }
        });
        hp.setWidth("100%");
        close.setWidth("100%");
        hp.add(close);
        cardChooseMainPanel.add(hp);
        cardChooser.add(cardChooseMainPanel);
    }

    @Override
    public void gameUpdated(UpdateUIEvent event) {
        Player currentPlayer = Scrabble.game.getPlayers().get(Scrabble
                .userAndrewID);

        this.remove(submitMove);
        this.remove(buyCard);
        this.remove(notYourTurn);

        /**
         * Only show buy/submit buttons in it's your turn
         */
        if (Scrabble.game.getCurrentTurn().equals(currentPlayer)) {
            this.add(buyCard);
            this.add(submitMove);

            /**
             * only allow submit move to be clicked if there is at least one
             * piece placed on the board
             */
            buyCard.setEnabled(
                    Scrabble.changes.getChangesToBoard().size() == 0);

            submitMove.setEnabled(Scrabble.game.getPlayers().get(Scrabble.userAndrewID)
                    .getPieces().size() != Game.PIECES_PER_PLAYER);
        } else {
            this.add(notYourTurn);
        }

        /**
         * Redraw cardview
         */
        cardView.removeAllRows();
        Map<Card.CARD_TYPE, Integer> cards = currentPlayer.getCards();
        int row = 0;
        for (Card.CARD_TYPE c : cards.keySet()) {
            cardView.setWidget(row, 0, new Label(CardImpl.getCardName(
                    Scrabble.game.getAvailableCards(), c)));
            cardView.setWidget(row, 1, new Label(String.valueOf(cards.get(c))));
            row++;
        }
    }
}
