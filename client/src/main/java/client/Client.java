package client;

import dataobjects.*;
import errors.ResponseException;
import server.ServerFacade;

import java.util.ArrayList;
import java.util.Collection;

import static ui.EscapeSequences.*;

public class Client {
    ServerFacade facade;
    private String clientUsername = null;
    private String clientAuthToken = null;
    private ArrayList<Integer> gameIDs = new ArrayList<>();

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
                'create <game name>' - Create a new chess game.
                'list' - List all existing chess games.
                'play' - Join a chess game.
                'observe'
                """;
    }

    String login(String[] params) throws ResponseException {
        checkParams(params, 2);
        AuthData auth = facade.login(new UserData(params[0], params[1], null));
        clientAuthToken = auth.authToken();
        clientUsername = auth.username();
        return String.format("You logged in as %s.", clientUsername);
    }

    String register(String[] params) throws ResponseException {
        checkParams(params, 3);
        AuthData auth = facade.register(new UserData(params[0], params[1], params[2]));
        clientAuthToken = auth.authToken();
        clientUsername = auth.username();
        return String.format("You registered as %s.", clientUsername);
    }

    String logout() throws ResponseException {
        checkLoggedIn();
        facade.logout(clientAuthToken);
        clientAuthToken = null;
        return "You logged out.";
    }

    String createGame(String[] params) throws ResponseException {
        checkLoggedIn();
        checkParams(params, 1);
        facade.createGame(clientAuthToken, params[0]); // [0] == gameName
        return String.format("You created the game %s.", params[0]);
    }

    // serves to both display the games to the user and populate the gameIDs list when necessary.
    String listGames() throws ResponseException {
        checkLoggedIn();
        Collection<GameData> gameList = facade.listGames(clientAuthToken);
        gameIDs = new ArrayList<>();
        StringBuilder result = new StringBuilder();
        int i = 0;
        for(GameData game : gameList){
            i++;
            gameIDs.add(game.gameID());
            result.append(String.format("%d: Game: %s, White: %s, Black: %s \n",
                    i, game.gameName(), colorOpen(game.whiteUsername()), colorOpen(game.blackUsername())));
        }
        return result.toString();
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

    private void checkLoggedIn() throws ResponseException {
        if (clientAuthToken == null) {
            throw new ResponseException(ResponseException.Type.CLIENT_ERROR, "Please log in first.");
        }
    }

    private String colorOpen(String username){
        if (username != null){
            return username;
        }
        return SET_BG_COLOR_GREEN + SET_TEXT_COLOR_BLACK + " Available " + RESET_TEXT_COLOR + RESET_BG_COLOR;
    }
}
