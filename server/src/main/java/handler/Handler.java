package handler;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import service.Service;

public class Handler {
    AuthDAO authDAO;
    GameDAO gameDAO;
    UserDAO userDAO;

    Service service;

    public Handler(AuthDAO authDataAccess, UserDAO userDataAccess, GameDAO gameDataAccess){
        authDAO = authDataAccess;
        userDAO = userDataAccess;
        gameDAO = gameDataAccess;

        service = new Service(authDAO, userDAO, gameDAO);
    }
}
