/*
 * @author <a href="mailto:matgray@cmu.edu">Mat Gray</a>
 * Licensed under a Creative Commons
 * Attribution-NonCommercial-ShareAlike 3.0 Unported License.
 */

package edu.cmu.cs.matgray.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import edu.cmu.cs.matgray.shared.ChangeVerifier;
import edu.cmu.cs.matgray.shared.interfaces.*;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("play")
public interface GameService extends RemoteService {

    /**
     * Fetch the department leaderboard from the server
     *
     * @return a map that maps department names to scores
     */
    Map<String, Integer> getLeaderboard();

    /**
     * Get a game from the server
     *
     * @param gameId the id of the game
     * @return a GameUpdate object describing the game
     */
    GameUpdate getGame(int gameId);

    /**
     * Create a new game
     *
     * @param fullName the name of the creator
     * @param andrewID the andrewID of the creator
     * @param gameName the name of game
     * @return a GameUpdate object describing the game
     */
    GameUpdate createGame(String fullName, String andrewID, String department,
                          String gameName);

    /**
     * Get a list of all the currently running games
     *
     * @return a list of GameInfos, describing the different games
     */
    List<GameInfo> getGameList();

    /**
     * See if an update is required
     *
     * @param t      the timestamp of the client-side game
     * @param gameID the id of the game
     * @return if there is a new update to the game, false otherwise
     */
    boolean updateRequired(Timestamp t, int gameID);

    /**
     * Submit changes for review and persitance to the server
     *
     * @param p             the player making the change
     * @param changeRequest the changes made
     * @param gameID        the id of the game
     * @return true if persisted (ie. passed server-side verification),
     *         false otherwise
     */
    boolean submitChanges(Player p, ChangeRequest changeRequest, int gameID);

    /**
     * Join a game
     *
     * @param fullname the name of the person joining
     * @param andrewID the andrewID of the person joining
     * @param gameID   the id of the game to join
     * @return a GameUpdate object describing the game
     */
    GameUpdate joinGame(String fullname, String andrewID,
                        String department, int gameID);

    /**
     * Get info about a andrewID
     *
     * @param andrewID the andrewID to lookup in the CMU LDAP directory
     * @return a UserInfo object describing the andrewID
     * @throws IllegalArgumentException if andrewID is invalid
     */
    UserInfo getUserInfo(String andrewID) throws IllegalArgumentException;

    /**
     * Get a changeVerifier for storing changes to a game
     *
     * @return a changeverifier for use
     */
    ChangeVerifier getVerifier();

    /**
     * Purchase a card
     *
     * @param gameID   the id of the game
     * @param andrewID the andrewID of the purchaser
     * @param type     the type of card to purchase
     */
    void purchaseCard(int gameID, String andrewID, Card.CARD_TYPE type);
}
