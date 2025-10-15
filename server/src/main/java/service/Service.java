package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import dataobjects.AuthData;
import dataobjects.UserData;
import errors.ResponseException;
import java.util.UUID;

public class Service {
    AuthDAO authDAO;
    GameDAO gameDAO;
    UserDAO userDAO;

    public Service(AuthDAO authDataAccess, UserDAO userDataAccess, GameDAO gameDataAccess){
        authDAO = authDataAccess;
        gameDAO = gameDataAccess;
        userDAO = userDataAccess;
    }

    public AuthData register(UserData registerRequest) throws ResponseException {
        boolean userTaken = userDAO.getUser(registerRequest.username()) != null;
        if (userTaken){
            throw new ResponseException("Username already taken", ResponseException.Type.ALREADY_TAKEN);
        }

        userDAO.createUser(registerRequest);

        AuthData authData = new AuthData(generateToken(), registerRequest.username());
        authDAO.createAuth(authData);

        return authData;
    }

    private String generateToken(){
        return UUID.randomUUID().toString();
    }
}
