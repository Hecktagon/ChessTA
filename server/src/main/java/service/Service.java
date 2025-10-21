package service;

import dataaccess.*;
import dataobjects.*;
import errors.ResponseException;

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

    public AuthData createGame(String authToken, String gameName) throws ResponseException {
        AuthData authData = checkAuth(authToken);
        GameData gameData = new GameData()
    }

    public void clearAll() throws ResponseException {
        authDAO.clearAuths();
        gameDAO.clearGames();
        userDAO.clearUsers();
    }

    private AuthData checkAuth(String authToken) throws ResponseException {
        AuthData authData = authDAO.getAuth(authToken);
        if ((authData) == null){
            throw new ResponseException(ResponseException.Type.UNAUTHORIZED);
        }
        return authData;
    }

    private String generateToken(){
        return UUID.randomUUID().toString();
    }
}
