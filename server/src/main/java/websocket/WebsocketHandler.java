package websocket;

import com.google.gson.Gson;
import io.javalin.websocket.*;
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;
import websocket.commands.UserGameCommand;

import java.io.IOException;

public class WebsocketHandler  implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final WsConnectionsManager connections = new WsConnectionsManager();

    @Override
    public void handleConnect(@NotNull WsConnectContext wsCtx) throws Exception {
        System.out.println("Websocket connected");
        wsCtx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(@NotNull WsMessageContext wsCtx) throws Exception {
        try {
            UserGameCommand userGameCommand = new Gson().fromJson(wsCtx.message(), UserGameCommand.class);
            switch (userGameCommand.getCommandType()) {
                case CONNECT -> playerJoinsGame(wsCtx.session, userGameCommand);
                case MAKE_MOVE -> playerMakesMove(wsCtx.session, userGameCommand);
                case LEAVE -> playerLeaves(wsCtx.session, userGameCommand);
                case RESIGN -> playerResigns(wsCtx.session, userGameCommand);
            }
        } catch (IOException ex) {
            System.out.println("WARNING: " + ex.toString());
        }
    }

    @Override
    public void handleClose(@NotNull WsCloseContext wsCtx) throws Exception {

    }

    private void playerJoinsGame(Session session, UserGameCommand command) throws IOException{

    }

    private void playerMakesMove(Session session, UserGameCommand command) throws IOException{

    }

    private void playerLeaves(Session session, UserGameCommand command) throws IOException{

    }

    private void playerResigns(Session session, UserGameCommand command) throws IOException{

    }
}
