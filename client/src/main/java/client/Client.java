package client;

import errors.ResponseException;
import server.ServerFacade;
import static ui.EscapeSequences.*;

public class Client {
    ServerFacade facade;
    private String username = null;
    private String authToken = null;

    public Client(ServerFacade serverFacade){
        facade = serverFacade;
    }

    // ###   package-private methods:   ###
    String help(){
        if (authToken == null) {
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

    private void checkParams(String[] params, int len) throws ResponseException {
        if(params.length != len){
            throw new ResponseException(ResponseException.Type.CLIENT_ERROR, "Incorrect number of parameters.");
        }
    }
}
