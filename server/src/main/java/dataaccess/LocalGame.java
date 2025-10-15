package dataaccess;

import dataobjects.AuthData;
import dataobjects.GameData;
import errors.ResponseException;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class LocalGame implements GameDAO{
    private final HashMap<Integer, GameData> gameTable = new HashMap<>();

    @Override
    public GameData createGame(GameData gameData) throws ResponseException {
        gameTable.put(gameData.gameID(), gameData);
        return gameData;
    }

    @Override
    public Collection<GameData> readGames() throws ResponseException {
        return gameTable.values();
    }

    @Override
    public GameData getGame(Integer gameID) throws ResponseException {
        if (gameTable.containsKey(gameID)){
            return gameTable.get(gameID);
        }
        return null;
    }

    @Override
    public GameData updateGame(GameData gameData) throws ResponseException {
        gameTable.put(gameData.gameID(), gameData);
        return gameData;
    }

    @Override
    public void clearGames() throws ResponseException {
        gameTable.clear();
    }
}
