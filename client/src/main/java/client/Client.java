package client;

import chess.ChessBoard;
import chess.ChessGame;
import dataobjects.*;
import errors.ResponseException;
import server.ServerFacade;
import ui.GameUI;

import java.util.ArrayList;
import java.util.Collection;

import static ui.EscapeSequences.*;

public class Client {
    ServerFacade facade;
    private String clientUsername = null;
    private String clientAuthToken = null;
    private ArrayList<Integer> gameIDs = new ArrayList<>();
    private GameUI gameUI;

    public Client(ServerFacade serverFacade){
        facade = serverFacade;
        gameUI = new GameUI();
    }

    // ###   package-private methods:   ###
    String help(){
        if (clientAuthToken == null) {
            return """
                    'help' - Lists command options.
                    'quit' - Exit the program.
                    'register <username> <password> <email>' - Create a new account.
                    'login <username> <password>' - Login to an existing account.""";
        }
        return """
                'help' - Lists command options.
                'logout' - Log out of the current session.
                'create <game name>' - Create a new chess game.
                'list' - List all existing chess games.
                'play' <game number> <white/black> - Join a chess game.
                'observe <game number> - Watch a game.'""";
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
        if(gameList.isEmpty()){return "No ongoing games.";}
        for(GameData game : gameList){
            i++;
            gameIDs.add(game.gameID());
            result.append(String.format("%d: Game: %s, White: %s, Black: %s \n",
                    i, game.gameName(), colorOpen(game.whiteUsername()), colorOpen(game.blackUsername())));
        }
        return result.toString();
    }

    String playGame(String[] params) throws ResponseException {
        checkLoggedIn();
        checkParams(params, 2);
        int gameID = gameNumToGameID(params[0]);
        ChessGame.TeamColor color = strToColor(params[1]);
        facade.joinGame(clientAuthToken, new JoinGameRequest(gameID, color));
        // Temporary code for printing a default board:
        ChessBoard board = new ChessBoard();
        board.resetBoard();
        return String.format("You joined game %s as %s.", params[0], params[1]) + "\n" + gameUI.gameToUi(board, color);
    }

    String observeGame(String[] params) throws ResponseException {
        checkLoggedIn();
        checkParams(params, 1);
        int gameID = gameNumToGameID(params[0]);
        // Temporary code for printing a default board:
        ChessBoard board = new ChessBoard();
        board.resetBoard();
        return String.format("You are observing game %s.", params[0]) + "\n" +
                gameUI.gameToUi(board, ChessGame.TeamColor.WHITE);
    }

    // gracefully handles a non integer input from user.
    private int gameNumToGameID(String stringNum) throws ResponseException {
        int gameNum;
        try {
            gameNum = Integer.parseInt(stringNum);
        } catch (NumberFormatException e) {
            throw new ResponseException(ResponseException.Type.CLIENT_ERROR, "Game number must be numerical.");
        }
        if (gameIDs.isEmpty()){listGames();} // populate the game list if it is empty
        if (gameNum <= 0 || gameNum > gameIDs.size()){
            throw new ResponseException(ResponseException.Type.CLIENT_ERROR,
                    String.format("No game with number %d", gameNum));
        }
        return gameIDs.get(gameNum - 1);
    }

    private void checkParams(String[] params, int len) throws ResponseException {
        if(params.length != len){
            throw new ResponseException(ResponseException.Type.CLIENT_ERROR, "Incorrect number of parameters. " +
                    "Try 'help' for a list of valid commands");
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

    private ChessGame.TeamColor strToColor(String colorStr) throws ResponseException {
        if (colorStr.equalsIgnoreCase("white")){
            return ChessGame.TeamColor.WHITE;
        } else if (colorStr.equalsIgnoreCase("black")) {
            return ChessGame.TeamColor.BLACK;
        }
        throw new ResponseException(ResponseException.Type.CLIENT_ERROR, "Team color must be 'black' or 'white'.");
    }
}
