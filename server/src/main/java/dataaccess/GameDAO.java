package dataaccess;

import dataobjects.GameData;
import errors.ResponseException;

import java.util.Collection;

public interface GameDAO {
    GameData createGame(GameData gameData) throws ResponseException;

    Collection<GameData> readGames() throws ResponseException;

    GameData getGame(Integer gameID) throws ResponseException;

    GameData updateGame(GameData gameData) throws ResponseException;

    void clearGames() throws ResponseException;

}