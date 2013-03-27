/*
 * @author <a href="mailto:matgray@cmu.edu">Mat Gray</a>
 * Licensed under a Creative Commons
 * Attribution-NonCommercial-ShareAlike 3.0 Unported License.
 */

package edu.cmu.cs.matgray.server;


import edu.cmu.cs.matgray.server.model.GameDatabaseInfo;
import edu.cmu.cs.matgray.shared.interfaces.Game;
import edu.cmu.cs.matgray.shared.interfaces.GameInfo;
import edu.cmu.cs.matgray.shared.model.GameImpl;
import edu.cmu.cs.matgray.shared.model.GameInfoImpl;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PersistenceService {

    private Connection connect = null;
    private Statement statement = null;
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;

    /**
     * For deploying to OpenShift
     */

    /**
     * Initialize a connection to the database
     *
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    private void init() throws ClassNotFoundException, SQLException {
        /**
         * Load the driver for the database
         */
        Class.forName("com.mysql.jdbc.Driver");

        connect = DriverManager
                .getConnection("jdbc:mysql://localhost/SCRABBLE?"
                        + "user=app&password=apppw");

        /**
         * For querying into the database
         */
        statement = connect.createStatement();
    }

    /**
     * Get a list of the currently active games
     *
     * @return a list of currently active games
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public List<GameInfo> getGameList() throws ClassNotFoundException, SQLException {
        List<GameInfo> gameInfoList = null;
        try {
            init();
            resultSet = statement
                    .executeQuery("select id,name from SCRABBLE.GAME");
            gameInfoList = parseGameInfo(resultSet);
        } finally {
            close();
        }
        return gameInfoList;
    }

    /**
     * Parses important information about the status of currently active games
     *
     * @param resultSet the result from the query
     * @return a list of game details
     * @throws SQLException
     */
    private List<GameInfo> parseGameInfo(ResultSet resultSet) throws SQLException {
        List<GameInfo> games = new ArrayList<GameInfo>();
        while (resultSet.next()) {
            GameInfo g = new GameInfoImpl(resultSet.getString("name"),
                    resultSet.getInt("id"));
            games.add(g);
        }
        return games;
    }

    /**
     * Create an object into a file output stream.  Useful for seralizing
     * objects into the database
     *
     * @param o the object to serialize
     * @return the file input stream
     * @throws IOException if reading/writing of object fails
     */
    private FileInputStream objectToFIS(Object o) throws IOException {
        // Write to disk with FileOutputStream
        FileOutputStream f_out = new FileOutputStream("game.data");
        // Write object with ObjectOutputStream
        ObjectOutputStream obj_out = new ObjectOutputStream(f_out);
        // Write object out to disk
        obj_out.writeObject(o);
        File fBlob = new File("game.data");
        return new FileInputStream(fBlob);
    }

    /**
     * save the state of the game (overwriting previous saves)
     *
     * @param g      the game to save
     * @param gameID the id of the game to save to
     * @return true if save successful, false otherwise
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws IOException
     */
    public Timestamp saveGame(Game g, int gameID) throws ClassNotFoundException,
            SQLException, IOException {
        try {
            init();
            FileInputStream serializedObject = objectToFIS(g);
            preparedStatement = connect
                    .prepareStatement("Update SCRABBLE.GAME SET DATA = ? WHERE id= ?");
            preparedStatement.setBlob(1, serializedObject);
            preparedStatement.setInt(2, gameID);
            preparedStatement.executeUpdate();
        } finally {
            close();
        }
        return this.getGameUpdateTimestamp(gameID);
    }

    /**
     * Create a new game
     *
     * @param gameName the name of the game
     * @return the new game object
     * @throws SQLException
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public GameDatabaseInfo createGame(String gameName) throws SQLException,
            IOException,
            ClassNotFoundException {
        GameImpl g = null;
        int id = -1;
        try {
            init();
            g = new GameImpl();

            FileInputStream serializedObject = objectToFIS(g);

            // PreparedStatements can use variables and are more efficient
            preparedStatement = connect
                    .prepareStatement("insert into  SCRABBLE.GAME values (default, ?, ?, default)");
            preparedStatement.setString(1, gameName + 0);
            preparedStatement.setBlob(2, serializedObject);
            preparedStatement.executeUpdate();

            resultSet = statement
                    .executeQuery("select id from SCRABBLE.GAME " +
                            "where name =\'" + gameName + 0 + "\'");

            while (resultSet.next()) {
                id = resultSet.getInt("id");
            }

            preparedStatement = connect
                    .prepareStatement("Update SCRABBLE.GAME SET NAME = ? WHERE id= ?");
            preparedStatement.setString(1, gameName);
            preparedStatement.setInt(2, id);
            preparedStatement.executeUpdate();

        } finally {
            close();
        }
        return new GameDatabaseInfo(g, id);
    }

    /**
     * Retrieve a game from the server
     *
     * @param gameID the id of the game
     * @return the game read from the database, or null on error
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws IOException
     */
    public GameImpl readGame(int gameID) throws ClassNotFoundException,
            SQLException, IOException {
        GameImpl game = null;
        try {
            init();
            resultSet = statement
                    .executeQuery("select DATA from SCRABBLE.GAME where id="
                            + gameID);
            game = parseGame(resultSet);
        } finally {
            close();
        }
        return game;
    }

    /**
     * Parse a game from BLOB to GameImpl
     *
     * @param resultSet the result of the query
     * @return the GameImpl of the found BLOB
     * @throws SQLException
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private GameImpl parseGame(ResultSet resultSet) throws SQLException,
            IOException, ClassNotFoundException {

        ObjectInputStream out = null;
        while (resultSet.next()) {
            ByteArrayInputStream bos = new
                    ByteArrayInputStream(resultSet.getBytes("DATA"));
            out = new ObjectInputStream(bos);
        }
        return (GameImpl) out.readObject();
    }

    /**
     * Get the last time that a game was updates
     *
     * @param gameID the id of the game
     * @return a timestamp detailing the last update to the game
     * @throws ClassNotFoundException if timestamp objects are incompatable
     * @throws SQLException           if there is a sql problem
     */
    public Timestamp getGameUpdateTimestamp(int gameID) throws ClassNotFoundException, SQLException {
        Timestamp ts = null;
        try {
            init();
            resultSet = statement
                    .executeQuery("select LAST_UPDATE from SCRABBLE.GAME where id="
                            + gameID);
            ts = parseDate(resultSet);
        } finally {
            close();
        }
        return ts;
    }

    /**
     * Pareses the date from a given resultset
     *
     * @param resultSet the resultset to parse
     * @return the timestamp
     * @throws SQLException
     */
    private Timestamp parseDate(ResultSet resultSet) throws SQLException {
        Timestamp ts = null;
        while (resultSet.next()) {
            ts = resultSet.getTimestamp("LAST_UPDATE");
        }
        return ts;
    }

    /**
     * Add to a department's score on the leaderboard.  Adds the department
     * if not already in DB
     *
     * @param deptName         the name of the department
     * @param additionalPoints the value to add
     * @throws ClassNotFoundException if incompatable objects
     * @throws SQLException           on sql error
     */
    public void addToLeaderboard(String deptName, int additionalPoints)
            throws ClassNotFoundException, SQLException {
        try {
            init();
            resultSet = statement
                    .executeQuery("select DEPT from SCRABBLE.LEADERBOARD");
            if (inTable(resultSet, deptName)) {
                preparedStatement = connect
                        .prepareStatement("Update SCRABBLE.LEADERBOARD SET SCORE = SCORE + ? WHERE DEPT= ?");
                preparedStatement.setInt(1, additionalPoints);
                preparedStatement.setString(2, deptName);
                preparedStatement.executeUpdate();
            } else {
                preparedStatement = connect
                        .prepareStatement("insert into  SCRABBLE.LEADERBOARD values (?, ?)");
                preparedStatement.setString(1, deptName);
                preparedStatement.setInt(2, additionalPoints);
                preparedStatement.executeUpdate();
            }
        } finally {
            close();
        }
    }

    /**
     * Returns whether or not a department is in the DB
     *
     * @param resultSet  the result set to search
     * @param deptSearch the name of the department to find
     * @return true if exists, false otherwise
     * @throws SQLException on sql error
     */
    private boolean inTable(ResultSet resultSet, String deptSearch) throws SQLException {
        while (resultSet.next()) {
            if (resultSet.getString("DEPT").equals(deptSearch)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get the leaderboard from the DB
     *
     * @return a map from department name to score
     * @throws SQLException           on sql error
     * @throws ClassNotFoundException if object's are incompatable
     */
    public Map<String, Integer> getLeaderboard() throws SQLException, ClassNotFoundException {
        Map<String, Integer> lb = null;
        try {
            init();
            resultSet = statement
                    .executeQuery("select * from SCRABBLE.LEADERBOARD");
            lb = parseLeaderBoard(resultSet);
        } finally {
            close();
        }
        return lb;
    }

    /**
     * Parses the leaderboard into a map
     *
     * @param resultSet the resultset to parse
     * @return a map from department name to score
     * @throws SQLException on sql error
     */
    private Map<String, Integer> parseLeaderBoard(ResultSet resultSet) throws SQLException {
        Map<String, Integer> map = new HashMap<String, Integer>();
        while (resultSet.next()) {
            String deptName = resultSet.getString("DEPT");
            int deptScore = resultSet.getInt("SCORE");
            map.put(deptName, deptScore);
        }
        return map;
    }

    /**
     * Need to close the resultSet
     */
    private void close() {
        try {
            if (resultSet != null) {
                resultSet.close();
            }

            if (statement != null) {
                statement.close();
            }

            if (connect != null) {
                connect.close();
            }
        } catch (Exception e) {

        }
    }
}
