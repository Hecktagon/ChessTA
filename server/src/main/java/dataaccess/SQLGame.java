package dataaccess;

import dataobjects.GameData;
import errors.ResponseException;

import java.util.Collection;
import java.util.List;

public class SQLGame implements GameDAO{
    private final String createStatement =
        """
        CREATE TABLE IF NOT EXISTS  pet (
          `gameID` int NOT NULL AUTO_INCREMENT,
          `whiteUsername` varchar(256) NOT NULL,
          `blackUsername` varchar(256) NOT NULL,
          `gameName` varchar(256) NOT NULL,
          `game` TEXT DEFAULT NULL,
          PRIMARY KEY (`gameID`),
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
        """;

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
}
