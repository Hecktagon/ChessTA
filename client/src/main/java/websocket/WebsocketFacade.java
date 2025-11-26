package websocket;

import com.google.gson.Gson;
import errors.ResponseException;
import jakarta.websocket.*;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;
import static ui.EscapeSequences.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebsocketFacade extends Endpoint{
    private final ServerMessageObserver clientWs;
    private final Session session;
    private final Gson gson = new Gson();

    public WebsocketFacade(String serverUrl, ServerMessageObserver serverMessageObserver) throws ResponseException {
        clientWs = serverMessageObserver;
        try {
            serverUrl = serverUrl.replace("http", "ws");
            URI socketURI = new URI(serverUrl + "/ws");
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            session = container.connectToServer(this, socketURI);

            // This processes ServerMessages and sends them to the client
            session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage serverMessage = gson.fromJson(message, ServerMessage.class);
                    if(serverMessage.getServerMessageType().equals(ServerMessage.ServerMessageType.NOTIFICATION)){
                        NotificationMessage notification = gson.fromJson(message, NotificationMessage.class);
                        clientWs.notify(notification.getMessage());
                    } else if (serverMessage.getServerMessageType().equals(ServerMessage.ServerMessageType.ERROR)){
                        ErrorMessage errorMessage = gson.fromJson(message, ErrorMessage.class);
                        clientWs.notify(SET_TEXT_COLOR_RED + errorMessage.getMessage() + RESET_TEXT_COLOR);
                    } else {
                        LoadGameMessage loadGameMessage = gson.fromJson(message, LoadGameMessage.class);
                        clientWs.loadGame(loadGameMessage.getChessGame());
                    }
                }
            });
        } catch (URISyntaxException | DeploymentException | IOException e) {
            throw new ResponseException(ResponseException.Type.SERVER_ERROR);
        }
    }

    public void userCommand(UserGameCommand userGameCommand) throws ResponseException {
        try {
            this.session.getBasicRemote().sendText(gson.toJson(userGameCommand));
        } catch (IOException ex) {
            throw new ResponseException(ResponseException.Type.SERVER_ERROR);
        }
    }

    public void makeMove(MakeMoveCommand makeMoveCommand) throws ResponseException {
        try {
            this.session.getBasicRemote().sendText(gson.toJson(makeMoveCommand));
        } catch (IOException ex) {
            throw new ResponseException(ResponseException.Type.SERVER_ERROR);
        }
    }

    // just to make the jakarta Endpoint extension happy:
    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }
}
