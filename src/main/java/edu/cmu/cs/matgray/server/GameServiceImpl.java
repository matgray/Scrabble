/*
 * @author <a href="mailto:matgray@cmu.edu">Mat Gray</a>
 * Licensed under a Creative Commons
 * Attribution-NonCommercial-ShareAlike 3.0 Unported License.
 */

package edu.cmu.cs.matgray.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.unboundid.ldap.sdk.LDAPException;
import edu.cmu.cs.matgray.client.GameService;
import edu.cmu.cs.matgray.server.model.GameDatabaseInfo;
import edu.cmu.cs.matgray.shared.ChangeVerifier;
import edu.cmu.cs.matgray.shared.interfaces.*;
import edu.cmu.cs.matgray.shared.model.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class GameServiceImpl extends RemoteServiceServlet implements
        GameService {

    private static PersistenceService persistenceService = new
            PersistenceService();

    private static ChangeVerifier verifier = null;

    private static final String SERVER = "http://127.0.0.1:8888/";

    /**
     * Initialize a change verifier to verify changes to boards
     */
    private void initChangeVerifier() {
        try {
            verifier = new ChangeVerifier();
            /**
             * Get the list of valid words
             */
            URL u = new URL(SERVER + "words.txt");
            InputStream in = u.openStream();
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(in));
            String line;
            while ((line = br.readLine()) != null) {
                verifier.addToDictionary(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public ChangeVerifier getVerifier() {
        if (verifier == null) {
            initChangeVerifier();
        }
        return verifier;
    }

    @Override
    public Map<String, Integer> getLeaderboard() {
        try {
            return persistenceService.getLeaderboard();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Create a new game
     *
     * @param andrewID the name of the client who created the game
     * @param gameName the name of the server
     * @return the created game (with the creator already added)
     */
    @Override
    public GameUpdate createGame(String fullName, String andrewID,
                                 String department,
                                 String gameName) {
        if (verifier == null) {
            initChangeVerifier();
        }
        GameImpl g = null;
        Timestamp t = null;
        int gameID = -1;
        try {
            GameDatabaseInfo info = persistenceService.createGame(gameName);
            gameID = info.getGameID();
            g = info.getGame();
            g.addPlayer(fullName, andrewID, department);
            g.setAvailablePieces(generateBoardPieces());
            g.setAvailableCards(getDefaultCardSet());
            generateBoard(g.getGameBoard());
            saveGameState(g, gameID);
            g = persistenceService.readGame(gameID);
            t = persistenceService.getGameUpdateTimestamp(gameID);
        } catch (Exception e) {
            /**
             * Log to catalina.out
             */
            e.printStackTrace();
        }
        return new GameUpdateImpl(g, t, gameID);
    }

    /**
     * Get the default set of cards
     *
     * @return a set of default cards
     * @throws IOException
     */
    private Set<Card> getDefaultCardSet() throws IOException {
        URL u = new URL(SERVER + "default.cset");
        InputStream in = u.openStream();
        BufferedReader br = new BufferedReader(
                new InputStreamReader(in));
        Set<Card> cards = new HashSet<Card>(4);
        String line;
        while ((line = br.readLine()) != null) {
            String[] data = line.split(",");
            String typeName = data[0];
            int typeCost = Integer.parseInt(data[1]);
            Card.CARD_TYPE type = Card.CARD_TYPE.valueOf(typeName);
            cards.add(new CardImpl(type, data[2], typeCost));
        }
        return cards;
    }

    @Override
    public UserInfo getUserInfo(String andrewID) throws IllegalArgumentException {
        if (verifier == null) {
            initChangeVerifier();
        }

        try {
            List<String> attributes = DirectoryService.getAttributes(andrewID);
            if (attributes.size() == 0) {
                throw new IllegalArgumentException();
            }
            return new UserInfoImpl(attributes.get(0), attributes.get(1));
        } catch (LDAPException e) {
            /**
             * Log to catalina.out
             */
            e.printStackTrace();
        }
        return null;
    }


    /**
     * Generate a board from the board file
     *
     * @param b the board to fill
     * @throws IOException
     */
    private void generateBoard(Board b) throws IOException {
        URL u = new URL(SERVER + "default.board");
        InputStream in = u.openStream();
        BufferedReader br = new BufferedReader(
                new InputStreamReader(in));
        int row = 0;
        String line;
        while ((line = br.readLine()) != null) {
            String[] data = line.split(",");
            int col = 0;
            for (String cell : data) {
                Location location = new LocationImpl(row, col);
                if (cell.equals("_")) {
                    b.setSlotAtLocation(location, new SlotImpl());
                } else if (cell.equals("S")) {
                    b.setSlotAtLocation(location,
                            new SlotImpl(Slot.SLOT_TYPE.ROOT, 1));
                } else {
                    String[] s = cell.split("-");
                    int factor = Integer.parseInt(s[0]);
                    String type = s[1];
                    if (type.equals("l")) {
                        Slot slot = new SlotImpl(Slot.SLOT_TYPE
                                .FACTOR_LETTER, factor);
                        b.setSlotAtLocation(location, slot);
                    } else if (type.equals("w")) {
                        Slot slot = new SlotImpl(Slot.SLOT_TYPE.FACTOR_WORD,
                                factor);
                        b.setSlotAtLocation(location, slot);
                    }
                }
                col++;
            }
            row++;
        }
    }

    /**
     * Generate the set of pieces that a game should use
     *
     * @return a set of all of the pieces in a game
     * @throws IOException if distribution file cannot be read
     */
    private Set<ScrabblePiece> generateBoardPieces() throws IOException {
        URL u = new URL(SERVER + "english.dist");
        InputStream in = u.openStream();
        BufferedReader br = new BufferedReader(
                new InputStreamReader(in));
        Set<ScrabblePiece> pieces = new HashSet<ScrabblePiece>(100);
        String line;
        while ((line = br.readLine()) != null) {
            String[] data = line.split(",");
            String letter = data[0];
            int count = Integer.valueOf(data[1]);
            int value = Integer.valueOf(data[2]);
            for (int i = 0; i < count; i++) {
                ScrabblePiece piece;
                if (letter.equals("_")) {
                    piece = new WildScrabblePiece(value);
                } else {
                    piece = new ScrabblePieceImpl(letter, value);
                }
                pieces.add(piece);
            }
        }
        return pieces;
    }

    /**
     * Save the game state
     *
     * @param game the game to save
     */
    private GameUpdate saveGameState(Game game, int gameID) {
        try {

            GameModificationService.refillPieces(game);
            return new GameUpdateImpl(game,
                    persistenceService.saveGame(game, gameID),
                    gameID);

        } catch (Exception e) {
            /**
             * Log to catalina.out
             */
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean updateRequired(Timestamp t, int gameID) {
        try {
            return t.before(persistenceService.getGameUpdateTimestamp
                    (gameID));
        } catch (Exception e) {
            /**
             * Log to catalina.out
             */
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean submitChanges(Player p, ChangeRequest changeRequest,
                                 int gameID) {
        try {
            Game g = persistenceService.readGame(gameID);

            ChangeRequestImpl request = new ChangeRequestImpl(g, p.getAndrewID());
            request.getChangesToBoard().putAll(changeRequest.getChangesToBoard());

            request.getAddedCards().putAll(changeRequest.getAddedCards());

            if (verifier.areValidChanges(
                    request, p, g.getGameBoard())) {

                if (verifier.getInvalidWords(g.getGameBoard(),
                        changeRequest).size() == 0) {

                    int additionalPoints = GameModificationService
                            .additionalPoints(g.getGameBoard(),
                                    request, p.getAndrewID());

                    g.getCurrentTurn().setScore(
                            g.getCurrentTurn().getScore() + additionalPoints);

                    persistenceService.addToLeaderboard(p.getDepartmentName(), additionalPoints);


                    GameModificationService.placePieces(g, request, p.getAndrewID());
                    this.saveGameState(g, gameID);
                    return true;
                }
            }
        } catch (Exception e) {
            /**
             * Log to catalina.out
             */
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Get a list of all games currently in session
     *
     * @return a list of all games currently in session
     */
    @Override
    public List<GameInfo> getGameList() {
        try {
            return persistenceService.getGameList();
        } catch (Exception e) {
            /**
             * Log to catalina.out
             */
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get a specific game
     *
     * @param gameId the id of the game to fetch
     * @return the fetched game, or null if not found
     */
    @Override
    public GameUpdate getGame(int gameId) {
        if (verifier == null) {
            initChangeVerifier();
        }

        Game g = null;
        Timestamp t = null;
        try {
            g = persistenceService.readGame(gameId);
            t = persistenceService.getGameUpdateTimestamp(gameId);
        } catch (Exception e) {
            /**
             * Log to catalina.out
             */
            e.printStackTrace();
        }
        return new GameUpdateImpl(g, t, gameId);
    }

    @Override
    public GameUpdate joinGame(String fullname, String andrewID,
                               String department, int gameID) {
        Game g = null;
        Timestamp t = null;
        try {
            g = persistenceService.readGame(gameID);
            if (g.getPlayers().get(andrewID) == null) {
                g.addPlayer(fullname, andrewID, department);
                return saveGameState(g, gameID);
            } else {
                return new GameUpdateImpl(g, persistenceService
                        .getGameUpdateTimestamp(gameID), gameID);
            }
        } catch (ClassNotFoundException e) {
            /**
             * This exception means the database has an older version of the
             * game in it.
             *
             * Log to catalina.out
             */
            e.printStackTrace();
        } catch (Exception e) {
            /**
             * Log to catalina.out
             */
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void purchaseCard(int gameID, String andrewID, Card.CARD_TYPE type) {
        try {
            Game g = persistenceService.readGame(gameID);
            GameModificationService.purchaseCard(g, andrewID, type);
            persistenceService.saveGame(g, gameID);
        } catch (Exception e) {
            /**
             * Log to catalina.out
             */
            e.printStackTrace();
        }
    }

}
