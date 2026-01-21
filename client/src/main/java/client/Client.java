package client;

import chess.*;
import com.google.gson.Gson;
import dataobjects.*;
import errors.ResponseException;
import server.ServerFacade;
import ui.GameUI;
import websocket.NotificationHandler;
import websocket.WebSocketFacade;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Client implements NotificationHandler {
    private final ServerFacade facade;
    private WebSocketFacade wsFacade;
    private String clientUsername = null;
    private String clientAuthToken = null;
    private ClientGameInfo clientGame = null;
    private ArrayList<Integer> gameIDs = new ArrayList<>();
    private GameUI gameUI;
    private ChessBoard currentBoard;
    private Gson gson = new Gson();

    public Client(String url){
        facade = new ServerFacade(url);
        try {
            wsFacade = new WebSocketFacade(url, this);
        } catch (ResponseException e){
            System.out.println("Websocket failed with error: " + e);
        }
        gameUI = new GameUI();
    }

    @Override
    public void notify(NotificationMessage message) {
        System.out.println(message.getMessage());
    }

    @Override
    public void error(ErrorMessage message) {
        System.out.println(SET_TEXT_COLOR_RED + message.getMessage() + RESET_TEXT_COLOR);
    }

    @Override
    public void loadGame(LoadGameMessage gameMessage){
        ChessBoard board = gameMessage.getGame().getBoard();
        if(clientGame.color() != null) {
            System.out.print(gameUI.gameToUi(board, clientGame.color()));
        } else {
            System.out.print(gameUI.gameToUi(board, ChessGame.TeamColor.WHITE));
        }
        currentBoard = board;
    }

    // ###   package-private methods:   ###
    String help(){
        // not logged in
        if (clientAuthToken == null) {
            return """
                    'help' - Lists command options.
                    'quit' - Exit the program.
                    'register <username> <password> <email>' - Create a new account.
                    'login <username> <password>' - Login to an existing account.""";
        // not in game
        } if (clientGame == null) {
            return """
                    'help' - Lists command options.
                    'logout' - Log out of the current session.
                    'create <game name>' - Create a new chess game.
                    'list' - List all existing chess games.
                    'play' <game number> <white/black> - Join a chess game.
                    'observe <game number> - Watch a game.'""";
        // not a player
        } if (clientGame.color() == null) {
            return """
                    'help' - Lists command options.
                    'redraw' - Redraws the chess board.
                    'leave' - Leaves the game.
                    'highlight <position>' - Given a position in proper syntax (Ex. e4) shows the moves for a piece
                    """;
        }
        // player
        return """
                'help' - Lists command options.
                'redraw' - Redraws the chess board.
                'leave' - Leaves the game.
                'move <start position> <end position>' - Makes a chess move given moves in proper syntax (Ex. e2 e4).
                'resign' - Forfeits the game.
                'highlight <position>' - Given a position in proper syntax (Ex. e4) shows the moves for that piece.
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

        // connect to the game via websocket and show that the user is playing a game
        wsFacade.sendCommand(new UserGameCommand(UserGameCommand.CommandType.CONNECT, clientAuthToken, gameID));
        clientGame = new ClientGameInfo(gameID, color);

        return String.format("\nYou joined game %s as %s.", params[0], params[1]);
    }

    String observeGame(String[] params) throws ResponseException {
        checkLoggedIn();
        checkParams(params, 1);
        int gameID = gameNumToGameID(params[0]);

        wsFacade.sendCommand(new UserGameCommand(UserGameCommand.CommandType.CONNECT, clientAuthToken, gameID));
        clientGame = new ClientGameInfo(gameID, null);

        return String.format("You are observing game %s.", params[0]);
    }

    String redrawBoard() throws ResponseException {
        checkInGame();
        return gameUI.gameToUi(currentBoard, clientGame.color());
    }

    String leaveGame() throws ResponseException {
        checkInGame();
        wsFacade.sendCommand(new UserGameCommand(UserGameCommand.CommandType.LEAVE,
                clientAuthToken, clientGame.gameID()));
        clientGame = null;
        return "You left the game.";
    }

    String makeMove(String[] params) throws ResponseException {
        checkIsPlayer();
        checkParams(params, 2);

        ChessPosition startPos = strToPos(params[0]);
        ChessPosition endPos = strToPos(params[1]);
        ChessPiece.PieceType promo = getPromoPiece(startPos, endPos);

        wsFacade.sendCommand(new MakeMoveCommand(clientAuthToken, clientGame.gameID(),
                new ChessMove(startPos, endPos, promo)));

        return null;
    }

    String resign() throws ResponseException {
        checkIsPlayer();
        Scanner scanner = new Scanner(System.in);
        System.out.print(SET_TEXT_COLOR_YELLOW + "Are you sure you want to resign? (y/n): " + RESET_TEXT_COLOR);
        String response = scanner.nextLine();

        if(response.equalsIgnoreCase("y")){
            wsFacade.sendCommand(new UserGameCommand(UserGameCommand.CommandType.RESIGN,
                    clientAuthToken, clientGame.gameID()));
            return null;
        }
        return "Resign aborted.";
    }

    String highlightMoves(String[] params) throws ResponseException {
        checkInGame();
        checkParams(params, 1);
        ChessPosition highlightPos = strToPos(params[0]);
        GameUI highlightUI = new GameUI(highlightPos);
        return highlightUI.gameToUi(currentBoard, clientGame.color());
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

    private void checkInGame() throws ResponseException {
        checkLoggedIn();
        if (clientGame == null){
            throw new ResponseException(ResponseException.Type.CLIENT_ERROR, "Please join a game first.");
        }
    }

    private void checkIsPlayer() throws ResponseException{
        checkInGame();
        if (clientGame.color() == null){
            throw new ResponseException(ResponseException.Type.CLIENT_ERROR, "Must be a player.");
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

    private ChessPosition strToPos(String strPos) throws ResponseException {
        ResponseException badFormat = new ResponseException(ResponseException.Type.CLIENT_ERROR, "Invalid input for chess position. " +
                "Must follow chess syntax like 'E4'.");
        if (strPos.length() != 2){
            throw badFormat;
        }

        int col = switch(strPos.substring(0, 1).toLowerCase()){
            case "a" -> 1;
            case "b" -> 2;
            case "c" -> 3;
            case "d" -> 4;
            case "e" -> 5;
            case "f" -> 6;
            case "g" -> 7;
            case "h" -> 8;
            default -> throw badFormat;
        };

        int row;
        try {
            row = Integer.parseInt(strPos.substring(1, 2));
        } catch(NumberFormatException e){
            throw badFormat;
        }

        return new ChessPosition(row, col);
    }

    private ChessPiece.PieceType getPromoPiece(ChessPosition start, ChessPosition end){
        ChessPiece currPiece = currentBoard.getPiece(start);
        if(currPiece != null && currPiece.getPieceType().equals(ChessPiece.PieceType.PAWN)){
            if ((currPiece.getTeamColor().equals(ChessGame.TeamColor.WHITE) && end.getRow() == 8)
            || (currPiece.getTeamColor().equals(ChessGame.TeamColor.BLACK) && end.getRow() == 1)){
                return strToPromo();
                }
            }
        return null;
    }

    private ChessPiece.PieceType strToPromo(){
        while (true) {
            System.out.print("Enter promotion piece 'Q', 'R', 'B', or 'N': ");
            Scanner scanner = new Scanner(System.in);
            String userInput = scanner.nextLine();
            switch (userInput.toLowerCase()){
                case "q" -> {return ChessPiece.PieceType.QUEEN;}
                case "r" -> {return ChessPiece.PieceType.ROOK;}
                case "b" -> {return ChessPiece.PieceType.BISHOP;}
                case "n" -> {return ChessPiece.PieceType.KNIGHT;}
                default -> System.out.println(SET_TEXT_COLOR_RED +
                        "Invalid promotion piece, must be 'Q', 'R', 'B', or 'N'." + RESET_TEXT_COLOR);
            }
        }
    }
    }

