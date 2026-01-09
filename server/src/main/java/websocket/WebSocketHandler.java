package websocket;

import chess.ChessGame;
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
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void handleClose(WsCloseContext wsCtx) {
        System.out.println("Websocket closed");
    }

    private void connect(UserGameCommand command, Session session) throws ResponseException, IOException {
        String auth = command.getAuthToken();
        Integer gameID = command.getGameID();
        AuthData authData = sqlAuth.getAuth(auth);
        GameData gameData = sqlGame.getGame(gameID);
        connections.add(gameID, session);

        ChessGame game = gameData.game();

        // Send the loadGame to the connecting user:
        session.getRemote().sendString(new Gson().toJson(new LoadGameMessage(game)));
        // Send the notification of user joining to other game members:
        connections.broadcast(gameID, new NotificationMessage(authData.username() + " joined the game."), session);
    }

    private void makeMove(UserGameCommand command, Session session){}

    private void leave(UserGameCommand command, Session session){}

    private void resign(UserGameCommand command, Session session){}

//    private void exit(String visitorName, Session session) throws IOException {
//        var message = String.format("%s left the shop", visitorName);
//        var notification = new Notification(Notification.Type.DEPARTURE, message);
//        connections.broadcast(session, notification);
//        connections.remove(session);
//    }

}