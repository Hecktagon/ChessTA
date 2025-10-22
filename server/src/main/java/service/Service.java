package service;

import chess.ChessGame;
import dataaccess.*;
import dataobjects.*;
import errors.ResponseException;
import records.JoinGameRequest;

import java.util.HashSet;
import java.util.UUID;

public class Service {
    AuthDAO authDAO;
    GameDAO gameDAO;
    UserDAO userDAO;
    HashSet<Integer> gameIDs;

    public Service(){
        authDAO = new LocalAuth();
        userDAO = new LocalUser();
        gameDAO = new LocalGame();
        gameIDs = new HashSet<>();
    }

//  ### PUBLIC SERVICE METHODS ###
    public AuthData register(UserData registerRequest) throws ResponseException {
        boolean userTaken = userDAO.getUser(registerRequest.username()) != null;
        if (userTaken){
            throw new ResponseException(ResponseException.Type.ALREADY_TAKEN);
        }

        userDAO.createUser(registerRequest);

        AuthData authData = new AuthData(generateToken(), registerRequest.username());
        authDAO.createAuth(authData);

        return authData;
    }

    public AuthData login(UserData loginRequest) throws ResponseException {
        UserData userData = userDAO.getUser(loginRequest.username());
        if (userData == null || !userData.password().equals(loginRequest.password())){
            throw new ResponseException(ResponseException.Type.UNAUTHORIZED);
        }

        AuthData authData = new AuthData(generateToken(), loginRequest.username());
        authDAO.createAuth(authData);

        return authData;
    }

    public void logout(String authToken) throws ResponseException {
        checkAuth(authToken);
        authDAO.deleteAuth(authToken);
    }

    public GameData createGame(String authToken, String gameName) throws ResponseException {
        AuthData authData = checkAuth(authToken);
        GameData gameData = new GameData(generateGameID(), null, null, gameName, null);
        gameDAO.createGame(gameData);
        return new GameData(generateGameID(), null, null, null, null);
    }

    public void joinGame(String authToken, JoinGameRequest joinGameRequest) throws ResponseException {
        AuthData authData = checkAuth(authToken);
        GameData gameData = gameDAO.getGame(joinGameRequest.gameID());

        GameData updatedGameData;
        // Joining as BLACK:
        if (joinGameRequest.playerColor().equals(ChessGame.TeamColor.BLACK)){
            if(gameData.blackUsername() != null){
                throw new ResponseException(ResponseException.Type.ALREADY_TAKEN);
            }
            updatedGameData = new GameData(gameData.gameID(), gameData.whiteUsername(), authData.username(),
                    gameData.gameName(), gameData.game());
        }
        // Joining as WHITE:
        else {
            if(gameData.whiteUsername() != null){
                throw new ResponseException(ResponseException.Type.ALREADY_TAKEN);
            }
            updatedGameData = new GameData(gameData.gameID(), authData.username(), gameData.blackUsername(),
                    gameData.gameName(), gameData.game());
        }

        gameDAO.updateGame(updatedGameData);
    }

    public void clearAll() throws ResponseException {
        authDAO.clearAuths();
        gameDAO.clearGames();
        userDAO.clearUsers();
    }

//  ### PRIVATE HELPER METHODS ###
    private AuthData checkAuth(String authToken) throws ResponseException {
        AuthData authData = authDAO.getAuth(authToken);
        if ((authData) == null){
            throw new ResponseException(ResponseException.Type.UNAUTHORIZED);
        }
        return authData;
    }

    private Integer generateGameID(){
        int i = 0;
        while(true){
            i++;
            if(!gameIDs.contains(i)) {
                return i;
            }
        }
    }

    private String generateToken(){
        return UUID.randomUUID().toString();
    }
}
