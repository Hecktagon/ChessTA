package websocket;

import chess.ChessGame;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.SQLAuth;
import dataaccess.SQLGame;
import dataaccess.SQLUser;
import dataobjects.*;
import errors.ResponseException;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import org.eclipse.jetty.websocket.api.Session;
import websocket.commands.*;
import websocket.messages.*;

import java.io.IOException;
import java.util.Objects;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private final Gson gson = new Gson();
    private SQLAuth sqlAuth;
    private SQLUser sqlUser;
    private SQLGame sqlGame;

    @Override
    public void handleConnect(WsConnectContext wsCtx) {
        System.out.println("Websocket connected");
        wsCtx.enableAutomaticPings();
        try {
            sqlAuth = new SQLAuth();
            sqlUser = new SQLUser();
            sqlGame = new SQLGame();
        } catch (ResponseException e){
            System.out.println("WARNING: SQL failed with error: " + e.toString());
        }
    }

    @Override
    public void handleMessage(WsMessageContext wsCtx) {
        try {
            UserGameCommand command = new Gson().fromJson(wsCtx.message(), UserGameCommand.class);
            switch (command.getCommandType()) {
                case CONNECT -> connect(command, wsCtx.session);
                case MAKE_MOVE -> makeMove(gson.fromJson(wsCtx.message(), MakeMoveCommand.class), wsCtx.session);
                case LEAVE -> leave(command, wsCtx.session);
                case RESIGN -> resign(command, wsCtx.session);
            }
        } catch (ResponseException|IOException e) {
            System.out.println("WARNING: Message failed with error: " + e.toString());
        }
    }

    @Override
    public void handleClose(WsCloseContext wsCtx) {
        System.out.println("Websocket closed");
        sqlAuth = null;
        sqlUser = null;
        sqlGame = null;
    }

    private void connect(UserGameCommand command, Session session) throws ResponseException, IOException {
        Integer gameID = command.getGameID();
        AuthData authData = sqlAuth.getAuth(command.getAuthToken());
        GameData gameData = sqlGame.getGame(gameID);
        connections.add(gameID, session);
        ChessGame game = gameData.game();

        // Send the loadGame to the connecting user:
        sendMessage(session, new LoadGameMessage(game));
        // Send the notification of user joining to other game members:
        connections.broadcast(gameID, new NotificationMessage(authData.username() + " joined the game."), session);
    }

    private void makeMove(MakeMoveCommand command, Session session) throws ResponseException, IOException {
        Integer gameID = command.getGameID();
        AuthData authData = sqlAuth.getAuth(command.getAuthToken());
        GameData gameData = sqlGame.getGame(gameID);
        ChessGame game = gameData.game();

        ChessGame.TeamColor playerColor = authData.username().equals(gameData.whiteUsername()) ?
                ChessGame.TeamColor.WHITE: authData.username().equals(gameData.blackUsername()) ?
                ChessGame.TeamColor.BLACK: null;

        ChessGame.TeamColor enemyColor = playerColor == ChessGame.TeamColor.WHITE ?
                ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE;

        if (playerColor == null){
            sendMessage(session, new ErrorMessage("Observers cannot make moves"));
            return;
        }

        if (game.isGameOver()){
            sendMessage(session, new ErrorMessage("The game is over"));
            return;
        }

        try {
            game.makeMove(command.getMove());
        } catch (InvalidMoveException e) {
            sendMessage(session, new ErrorMessage("Invalid move"));
            return;
        }

        connections.broadcast(gameID, new LoadGameMessage(game), null);

        connections.broadcast(gameID, new NotificationMessage(authData.username() + "made the move" +
                command.getMove()), session);

        if (game.isInCheckmate(enemyColor)) {
            connections.broadcast(gameID, new NotificationMessage("Player " + enemyColor.toString().toLowerCase()
                    + "is in checkmate, " + playerColor.toString().toLowerCase() + " wins!"), null);
        } else if (game.isInCheck(enemyColor)){
            connections.broadcast(gameID, new NotificationMessage("Player " + enemyColor.toString().toLowerCase()
                    + " is in check!"), null);
        } else if (game.isInStalemate(ChessGame.TeamColor.WHITE) || game.isInStalemate(ChessGame.TeamColor.BLACK)){
            connections.broadcast(gameID, new NotificationMessage("Stalemate!"),null);
            game.setGameOver(true);
        }
    }

    private void leave(UserGameCommand command, Session session) throws ResponseException, IOException {
        Integer gameID = command.getGameID();
        AuthData authData = sqlAuth.getAuth(command.getAuthToken());
        connections.remove(gameID, session);

        // Send the notification of user leaving to other game members:
        connections.broadcast(gameID, new NotificationMessage(authData.username() + " left the game."), session);
    }

    private void resign(UserGameCommand command, Session session) throws ResponseException, IOException {
        Integer gameID = command.getGameID();
        AuthData authData = sqlAuth.getAuth(command.getAuthToken());
        GameData gameData = sqlGame.getGame(gameID);
        ChessGame game = gameData.game();

        game.setGameOver(true);
        connections.broadcast(gameID, new NotificationMessage(authData.username() + " resigned!"), session);
    }

    private void sendMessage(Session session, ServerMessage message) throws IOException {
        session.getRemote().sendString(gson.toJson(message));
    }
}