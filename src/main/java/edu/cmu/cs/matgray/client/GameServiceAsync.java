/*
 * @author <a href="mailto:matgray@cmu.edu">Mat Gray</a>
 * Licensed under a Creative Commons
 * Attribution-NonCommercial-ShareAlike 3.0 Unported License.
 */

package edu.cmu.cs.matgray.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import edu.cmu.cs.matgray.shared.ChangeVerifier;
import edu.cmu.cs.matgray.shared.interfaces.*;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

/**
 * The async counterpart of <code>GameService</code>.
 */
public interface GameServiceAsync {

    void getGame(int serverID, AsyncCallback<GameUpdate> async);


    void getGameList(AsyncCallback<List<GameInfo>> async);


    void createGame(String fullName, String creatorName,
                    String department, String serverName,
                    AsyncCallback<GameUpdate> async);

    void updateRequired(Timestamp t, int gameID, AsyncCallback<Boolean>
            async);

    void submitChanges(Player p, ChangeRequest changeRequest, int gameID,
                       AsyncCallback<Boolean>
                               async);

    void joinGame(String fullname, String andrewID, String department,
                  int serverID, AsyncCallback<GameUpdate> async);

    void getUserInfo(String andrewID, AsyncCallback<UserInfo> async) throws IllegalArgumentException;

    void getVerifier(AsyncCallback<ChangeVerifier> async);

    void purchaseCard(int gameID, String andrewID, Card.CARD_TYPE type,
                      AsyncCallback<Void> async);

    void getLeaderboard(AsyncCallback<Map<String, Integer>> async);
}
