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
import java.util.Collection;
import java.util.List;

public class SQLGame implements GameDAO{
    public SQLGame() throws ResponseException {
        String createStatement =
                """
                CREATE TABLE IF NOT EXISTS  game (
                  `gameID` int NOT NULL AUTO_INCREMENT,
                  `whiteUsername` varchar(256) NOT NULL,
                  `blackUsername` varchar(256) NOT NULL,
                  `gameName` varchar(256) NOT NULL,
                  `game` TEXT DEFAULT NULL,
                  PRIMARY KEY (`gameID`),
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
                """;
        DatabaseManager.configureDatabase(createStatement);
    }


    @Override
    public GameData createGame(GameData gameData) throws ResponseException {
        return null;
    }

    @Override
    public Collection<GameData> readGames() throws ResponseException {
        return List.of();
    }

    @Override
    public GameData getGame(Integer gameID) throws ResponseException {
        return null;
    }

    @Override
    public GameData updateGame(GameData gameData) throws ResponseException {
        return null;
    }

    @Override
    public void clearGames() throws ResponseException {

    }

    private GameData readChessGame(ResultSet rs) throws SQLException{
        ChessGame chessGame = new Gson().fromJson(rs.getString("game"), ChessGame.class);
        return(new GameData(rs.getInt("gameID"), rs.getString("whiteUsername"),
                rs.getString("blackUsername"), rs.getString("gameName"),
                chessGame));
    }

    private GameData executeSelect(String statement, Object... params) throws ResponseException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement preparedStatement = conn.prepareStatement(statement)) {
                for (int i = 0; i < params.length; i++){
                    // for each of our params, we set the ?'s to those params
                    preparedStatement.setObject(i + 1, params[i]);
                }
                ResultSet rs = preparedStatement.executeQuery();
                if(rs.next()){
                    return readChessGame(rs);
                }
                return null;
            }
        }catch (DataAccessException | SQLException dataEx) {
            throw new ResponseException(ResponseException.Type.DATA_ACCESS_ERROR);
        }
    }
}
