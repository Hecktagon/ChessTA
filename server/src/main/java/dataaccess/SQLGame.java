package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import dataobjects.AuthData;
import dataobjects.GameData;
import errors.ResponseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class SQLGame implements GameDAO{
    public SQLGame() throws ResponseException {
        String createStatement =
                """
                CREATE TABLE IF NOT EXISTS game (
                  `gameID` int NOT NULL,
                  `whiteUsername` varchar(256) DEFAULT NULL,
                  `blackUsername` varchar(256) DEFAULT NULL,
                  `gameName` varchar(256) NOT NULL,
                  `chessGame` TEXT DEFAULT NULL,
                  PRIMARY KEY (`gameID`)
                )
                """;
        DatabaseManager.configureDatabase(createStatement);
    }


    @Override
    public GameData createGame(GameData gameData) throws ResponseException {
        String statement = "INSERT INTO game (gameID, gameName, chessGame) VALUES (?, ?, ?)";
        String gameJson = gameToJson(gameData.game());
        DatabaseManager.executeUpdate(statement, gameData.gameID(), gameData.gameName(), gameJson);
        return gameData;
    }

    @Override
    public Collection<GameData> readGames() throws ResponseException {
        String statement = "SELECT * FROM game";
        return executeSelect(statement);
    }

    @Override
    public GameData getGame(Integer gameID) throws ResponseException {
        String statement = "SELECT * FROM game WHERE gameID=?";
        ArrayList<GameData> game = executeSelect(statement, gameID);
        if (game.isEmpty()){
            return null;
        }
        return game.getFirst();
    }

    @Override
    public GameData updateGame(GameData gameData) throws ResponseException {
        String statement = """
        UPDATE game
        SET whiteUsername = ?, blackUsername = ?, chessGame = ?
        WHERE gameID = ?
        """;
        DatabaseManager.executeUpdate(statement, gameData.whiteUsername(),
                gameData.blackUsername(), gameToJson(gameData.game()), gameData.gameID());
        return gameData;
    }

    @Override
    public void clearGames() throws ResponseException {
        String statement = "TRUNCATE game";
        DatabaseManager.executeUpdate(statement);
    }

    private String gameToJson(ChessGame chessGame){
        return new Gson().toJson(chessGame, ChessGame.class);
    }

    private GameData readChessGame(ResultSet rs) throws SQLException{
        ChessGame chessGame = new Gson().fromJson(rs.getString("chessGame"), ChessGame.class);
        return(new GameData(rs.getInt("gameID"), rs.getString("whiteUsername"),
                rs.getString("blackUsername"), rs.getString("gameName"),
                chessGame));
    }

    private ArrayList<GameData> executeSelect(String statement, Object... params) throws ResponseException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement preparedStatement = conn.prepareStatement(statement)) {
                for (int i = 0; i < params.length; i++){
                    // for each of our params, we set the ?'s to those params
                    preparedStatement.setObject(i + 1, params[i]);
                }
                ResultSet rs = preparedStatement.executeQuery();
                ArrayList<GameData> games = new ArrayList<>();
                while(rs.next()){
                    games.add(readChessGame(rs));
                }
                return games;
            }
        }catch (DataAccessException | SQLException dataEx) {
            throw new ResponseException(ResponseException.Type.DATA_ACCESS_ERROR);
        }
    }
}
