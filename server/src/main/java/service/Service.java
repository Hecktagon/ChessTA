package service;

import dataaccess.*;
import dataobjects.AuthData;
import dataobjects.UserData;
import errors.ResponseException;
import java.util.UUID;

public class Service {
    AuthDAO authDAO;
    GameDAO gameDAO;
    UserDAO userDAO;

    public Service(){
        authDAO = new LocalAuth();
        userDAO = new LocalUser();
        gameDAO = new LocalGame();
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
        boolean noSuchUser = userDAO.getUser(loginRequest.username()) == null;
        if (noSuchUser){
            throw new ResponseException(ResponseException.Type.UNAUTHORIZED);
        }

        AuthData authData = new AuthData(generateToken(), loginRequest.username());
        authDAO.createAuth(authData);

        return authData;
    }


    public void logout(String authToken) throws ResponseException {
        if (authDAO.getAuth(authToken) == null){
            throw new ResponseException(ResponseException.Type.UNAUTHORIZED);
        }

        authDAO.deleteAuth(authToken);
    }

    private String generateToken(){
        return UUID.randomUUID().toString();
    }
}
