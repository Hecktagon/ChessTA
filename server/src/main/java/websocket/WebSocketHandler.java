package websocket;

import com.google.gson.Gson;
import errors.ResponseException;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import org.eclipse.jetty.websocket.api.Session;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;

import java.io.IOException;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private final Gson gson = new Gson();

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

    private void connect(UserGameCommand command, Session session){}

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