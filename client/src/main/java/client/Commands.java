package client;

import errors.ResponseException;
import server.ServerFacade;

public class Commands {
    ServerFacade facade;

    public Commands(ServerFacade serverFacade){
        facade = serverFacade;
    }

    // ###   package-private methods:   ###
    String help(){
        return "";
    }

    String login(String[] params) throws ResponseException {
        return "";
    }

    String register(String[] params) throws ResponseException {
        return "";
    }

    String logout() throws ResponseException {
        return "";
    }

    String createGame(String[] params) throws ResponseException {
        return "";
    }

    String listGames() throws ResponseException {
        return "";
    }

    String playGame(String[] params) throws ResponseException {
        return"";
    }

    String observeGame(String[] params) throws ResponseException {
        return"";
    }
}
