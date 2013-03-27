/*
 * @author <a href="mailto:matgray@cmu.edu">Mat Gray</a>
 * Licensed under a Creative Commons
 * Attribution-NonCommercial-ShareAlike 3.0 Unported License.
 */

package edu.cmu.cs.matgray.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.impl.SchedulerImpl;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.google.web.bindery.event.shared.SimpleEventBus;
import edu.cmu.cs.matgray.client.event.*;
import edu.cmu.cs.matgray.client.handler.InitialGameJoinHandler;
import edu.cmu.cs.matgray.client.ui.GameBrowser;
import edu.cmu.cs.matgray.client.ui.Leaderboard;
import edu.cmu.cs.matgray.client.ui.LoadingView;
import edu.cmu.cs.matgray.client.ui.StateHandler;
import edu.cmu.cs.matgray.shared.ChangeVerifier;
import edu.cmu.cs.matgray.shared.interfaces.ChangeRequest;
import edu.cmu.cs.matgray.shared.interfaces.Game;
import edu.cmu.cs.matgray.shared.interfaces.GameUpdate;
import edu.cmu.cs.matgray.shared.interfaces.UserInfo;
import edu.cmu.cs.matgray.shared.model.ChangeRequestImpl;

import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class Scrabble implements EntryPoint, UpdateNecessaryEventHandler,
        GameFetchedEventHandler, JoinServerEventHandler,
        CreateGameRequestEventHandler, UserInfoFetchedEventHandler {

    /**
     * Refresh Rate in ms
     */
    public final static int REFRESH_RATE = 5000;

    /**
     * Create a remote service proxy to talk to the server-side game service.
     */
    public static final GameServiceAsync scrabbleService = GWT.create
            (GameService
                    .class);

    /**
     * stateHandler handles what is being shown to the user
     */
    public static StateHandler stateHandler = null;

    /**
     * where events to be seen globaly are fired
     */
    public static SimpleEventBus globalEventBus = new SimpleEventBus();

    /**
     * Name and email of user
     */
    public static String userAndrewID;
    public static String userName = "";
    public static String userDept;
    public static Game game;
    public static ChangeRequest changes = null;
    public static Timestamp lastUpdateTime = null;
    public static int gameID;
    public static final ConsoleLogHandler LOGGER = new ConsoleLogHandler();
    public static LoadingView loadingPopup = new LoadingView();
    public static ChangeVerifier verifier;

    /**
     * Implement stateHandler using a DeckPanel
     */
    private DeckPanel deckPanel = new DeckPanel();
    private TextBox nameBox;
    private Button dismissInfoPrompt;
    private PopupPanel andrewIdPrompt;
    private TextBox newGameName;
    private Button createGame;
    private Scheduler scheduler;
    private PopupPanel gameCreationPrompt;

    public void onModuleLoad() {
        deckPanel.setWidth("100%");
        stateHandler = new StateHandler(deckPanel);
        RootPanel.get().add(deckPanel);

        /**
         * Add necessary event handlers
         */
        Scrabble.globalEventBus.addHandler(UpdateNecessaryEvent.TYPE, this);
        Scrabble.globalEventBus.addHandler(JoinServerEvent.TYPE, this);
        Scrabble.globalEventBus.addHandler(GameFetchedEvent.TYPE, this);
        Scrabble.globalEventBus.addHandler(CreateGameRequestEvent.TYPE, this);
        Scrabble.globalEventBus.addHandler(UserInfoFetchedEvent.TYPE, this);

        /**
         * Render webapp content
         */
        nameBox = new TextBox();
        nameBox.setStyleName("tbCenter");
        Label namePrompt = new Label("Andrew ID: ");
        namePrompt.setStyleName("gradLarge");
        dismissInfoPrompt = new Button("Login");
        dismissInfoPrompt.setWidth("100%");
        /**
         * For creating new games
         */
        newGameName = new TextBox();
        createGame = new Button("Create Game");
        nameBox.addKeyPressHandler(new AndrewIDSubmitHandler());

        VerticalPanel infoPrompt = new VerticalPanel();
        andrewIdPrompt = new PopupPanel();
        andrewIdPrompt.setGlassEnabled(true);
        infoPrompt.add(namePrompt);
        infoPrompt.add(nameBox);
        infoPrompt.add(dismissInfoPrompt);
        andrewIdPrompt.add(infoPrompt);
        andrewIdPrompt.center();

        /**
         * Initialize scheduler
         */
        scheduler = new SchedulerImpl();

        /**
         * Save information when user inputs information and close popup
         */
        dismissInfoPrompt.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                Scrabble.userAndrewID = nameBox.getName();
                nameBox.setEnabled(false);
                dismissInfoPrompt.setEnabled(false);
                andrewIdPrompt.hide();
                userAndrewID = nameBox.getText();
                Scrabble.loadingPopup.center();

                Scrabble.scrabbleService.getUserInfo(userAndrewID, new AsyncCallback<UserInfo>() {
                    @Override
                    public void onFailure(Throwable throwable) {
                        Scrabble.loadingPopup.hide();
                        Window.alert("Invalid Andrew ID!  Please input a " +
                                "valid Andrew ID.");
                        nameBox.setEnabled(true);
                        dismissInfoPrompt.setEnabled(true);
                        andrewIdPrompt.show();
                    }

                    @Override
                    public void onSuccess(UserInfo userInfo) {
                        Scrabble.loadingPopup.hide();
                        Scrabble.globalEventBus.fireEvent(new
                                UserInfoFetchedEvent(userInfo));
                    }
                });

            }
        });

        /**
         * If the user wants to create a new game
         */
        createGame.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                Scrabble.globalEventBus.fireEvent(new CreateGameRequestEvent());
            }
        });

        scheduler.scheduleFixedDelay(new Scheduler.RepeatingCommand() {
            @Override
            public boolean execute() {
                /**
                 * Only attempt update if board is loaded (need a board id)
                 */
                if (lastUpdateTime != null) {
                    /**
                     * See if there's a new version of the board
                     */
                    Scrabble.scrabbleService.updateRequired(lastUpdateTime,
                            gameID,
                            new AsyncCallback<Boolean>() {
                                @Override
                                public void onFailure(Throwable throwable) {
                                    Window.alert("Could not fetch update " +
                                            "time from the server");
                                    Scrabble.LOGGER.publish(new LogRecord(Level.SEVERE,
                                            throwable.getMessage()));
                                }

                                @Override
                                public void onSuccess(Boolean aBoolean) {
                                    /**
                                     * Only update the board if there's a newer version
                                     */
                                    if (aBoolean) {
                                        LOGGER.publish(new LogRecord(Level.INFO,
                                                "Update Necessary"));
                                        Scrabble.globalEventBus.fireEvent(new UpdateNecessaryEvent());
                                    }
                                    LOGGER.publish(new LogRecord(Level.FINEST,
                                            "Update Not Necessary"));
                                }
                            });

                } else {
                    LOGGER.publish(new LogRecord(Level.FINEST,
                            "Last Update time is NULL, not updating..."));
                }
                // Invoke refresh again
                return true;
            }
        }, Scrabble.REFRESH_RATE);

        nameBox.setFocus(true);
    }

    private class AndrewIDSubmitHandler implements KeyPressHandler {

        @Override
        public void onKeyPress(KeyPressEvent keyPressEvent) {
            /**
             * Place the piece on enter
             */
            if (keyPressEvent.getCharCode() == KeyCodes.KEY_ENTER) {
                dismissInfoPrompt.click();
            }
        }
    }

    @Override
    public void onUpdateNeeded(UpdateNecessaryEvent event) {
        Scrabble.loadingPopup.center();
        Scrabble.scrabbleService.getGame(gameID, new
                AsyncCallback<GameUpdate>() {
                    @Override
                    public void onFailure(Throwable throwable) {
                        Window.alert("There was a problem fetching the game " +
                                "from the server");
                        Scrabble.LOGGER.publish(new LogRecord(Level.SEVERE,
                                throwable.getMessage()));
                        Scrabble.loadingPopup.hide();
                    }

                    @Override
                    public void onSuccess(GameUpdate updatedGame) {
                        Scrabble.lastUpdateTime = updatedGame.getTimeStamp();
                        Scrabble.globalEventBus.fireEvent(new GameFetchedEvent
                                (updatedGame.getGame()));
                    }
                });
    }

    @Override
    public void onGameFetched(GameFetchedEvent event) {
        Scrabble.loadingPopup.hide();

        Scrabble.game = event.getGame();
        Scrabble.changes = new ChangeRequestImpl(event.getGame(), userAndrewID);
        LOGGER.publish(new LogRecord(Level.INFO,
                "New Game Fetched"));
        Scrabble.globalEventBus.fireEvent(new UpdateUIEvent());
    }

    @Override
    public void onJoinServer(JoinServerEvent event) {
        Scrabble.loadingPopup.center();
        Scrabble.scrabbleService.joinGame(Scrabble.userName,
                Scrabble.userAndrewID,
                Scrabble.userDept,
                event.getId(),
                new InitialGameJoinHandler());
    }

    @Override
    public void onCreateGameRequest(CreateGameRequestEvent event) {
        gameCreationPrompt.hide();
        Scrabble.loadingPopup.center();
        scrabbleService.createGame(userName, userAndrewID, userDept,
                newGameName.getText(),
                new InitialGameJoinHandler());
    }

    @Override
    public void onUserInfoRetrieved(UserInfoFetchedEvent event) {
        Scrabble.userName = event.getUserInfo().getName();
        VerticalPanel vp = new VerticalPanel();
        Label welcomeLabel = new Label(event.getUserInfo()
                .getName());
        welcomeLabel.setStyleName("gradLarge");
        Label rep = new Label(event.getUserInfo()
                .getDepartment());
        Scrabble.userDept = event.getUserInfo().getDepartment();
        rep.setStyleName("gradRegular");
        vp.add(welcomeLabel);
        vp.add(rep);
        vp.add(new InlineHTML("<br><br>"));

        Button b = new Button("Create a New Game");
        b.setWidth("100%");
        b.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                gameCreationPrompt = new PopupPanel();
                gameCreationPrompt.setGlassEnabled(true);
                VerticalPanel gameOptions = new VerticalPanel();
                gameOptions.add(new InlineHTML("<br>"));

                HorizontalPanel h1 = new HorizontalPanel();
                h1.setWidth("100%");
                h1.setHorizontalAlignment(HorizontalPanel.ALIGN_CENTER);
                h1.add(new Label("Choose a name for your game: "));
                gameOptions.add(h1);
                gameOptions.add(new InlineHTML("<br>"));
                HorizontalPanel gameCreator = new HorizontalPanel();
                gameCreator.add(newGameName);
                gameCreator.add(createGame);

                Button exit = new Button("Close");

                exit.addClickHandler(new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent clickEvent) {
                        gameCreationPrompt.hide();
                    }
                });
                gameOptions.add(gameCreator);
                gameOptions.add(new InlineHTML("<br>"));
                HorizontalPanel h = new HorizontalPanel();
                h.setWidth("100%");
                h.setHorizontalAlignment(HorizontalPanel.ALIGN_CENTER);
                h.add(exit);
                gameOptions.add(h);
                gameOptions.add(new InlineHTML("<br>"));
                gameCreationPrompt.add(gameOptions);
                gameCreationPrompt.center();
            }
        });
        vp.add(new InlineHTML("<br><br>"));
        vp.add(b);
        vp.add(new InlineHTML("<br><br>"));

        GameBrowser browser = new GameBrowser();
        browser.setWidth("100%");
        vp.add(browser);

        vp.add(new InlineHTML("<br><br><hr><br>"));

        vp.add(new Label("Department Leaderboard:"));
        vp.add(new InlineHTML("<br>"));
        Leaderboard leaderboard = new Leaderboard();
        leaderboard.setWidth("100%");
        vp.add(leaderboard);

        stateHandler.switchToWidget(vp);
        deckPanel.showWidget(0);
    }
}