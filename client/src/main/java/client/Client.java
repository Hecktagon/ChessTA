package client;

import dataobjects.*;
import errors.ResponseException;
import server.ServerFacade;
import static ui.EscapeSequences.*;

public class Client {
    ServerFacade facade;
    private String clientUsername = null;
    private String clientAuthToken = null;

    public Client(ServerFacade serverFacade){
        facade = serverFacade;
    }

    // ###   package-private methods:   ###
    String help(){
        if (clientAuthToken == null) {
            return """
                    'quit' - Exit the program.
                    'register <username> <password> <email>' - Create a new account.
                    'login <username> <password>' - Login to an existing account.
                    """;
        }
        return """
                'logout' - Log out of the current session.
                'create' - Create a new chess game.
                'list' - List all existing chess games.
                'play' - Join a chess game.
                'observe'
                """;
    }

    String login(String[] params) throws ResponseException {
        checkParams(params, 2);
        AuthData auth = facade.login(new UserData(params[0], params[1], null));
        clientAuthToken = auth.authToken();
        return String.format("You logged in as %s", auth.username());
    }

    String register(String[] params) throws ResponseException {
        checkParams(params, 2);
        AuthData auth = facade.register(new UserData(params[0], params[1], params[2]));
        clientAuthToken = auth.authToken();
        return String.format("You registered as %s", auth.username());
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

    private void checkParams(String[] params, int len) throws ResponseException {
        if(params.length != len){
            throw new ResponseException(ResponseException.Type.CLIENT_ERROR, "Incorrect number of parameters.");
        }
    }
}
