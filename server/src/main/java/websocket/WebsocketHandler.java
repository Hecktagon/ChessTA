package websocket;

import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.SQLAuth;
import dataaccess.SQLGame;
import dataobjects.GameData;
import dataobjects.UserData;
import errors.ResponseException;
import io.javalin.websocket.*;
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;

public class WebsocketHandler  implements WsConnectHandler, WsMessageHandler, WsCloseHandler {
    private AuthDAO authDAO;
    private GameDAO gameDAO;
    private final WsConnectionsManager connections = new WsConnectionsManager();
    private final Gson gson = new Gson();

    @Override
    public void handleConnect(@NotNull WsConnectContext wsCtx) throws Exception {
        System.out.println("Websocket connected");
        wsCtx.enableAutomaticPings();
        authDAO = new SQLAuth();
        gameDAO = new SQLGame();
    }

    @Override
    public void handleMessage(@NotNull WsMessageContext wsCtx) throws Exception {
        UserGameCommand userGameCommand = gson.fromJson(wsCtx.message(), UserGameCommand.class);
        switch (userGameCommand.getCommandType()) {
            case CONNECT -> playerJoinsGame(wsCtx.session, userGameCommand);
            case MAKE_MOVE -> playerMakesMove(wsCtx.session, gson.fromJson(wsCtx.message(), MakeMoveCommand.class));
            case LEAVE -> playerLeaves(wsCtx.session, userGameCommand);
            case RESIGN -> playerResigns(wsCtx.session, userGameCommand);
        }
    }

    @Override
    public void handleClose(@NotNull WsCloseContext wsCtx) throws Exception {
        System.out.println("Websocket closed");
    }

    private void playerJoinsGame(Session session, UserGameCommand command) throws ResponseException, IOException{
        String username = usernameFromAuth(command.getAuthToken());
        String playerType = command.getTeamColor() == null ? "observer" :
                command.getTeamColor().toString().toLowerCase();
        connections.addSessionToGame(session, command.getGameID());
        connections.gameBroadcast(
                command.getGameID(), new NotificationMessage(username + "joined as " + playerType), session);
    }

    private void playerMakesMove(Session session, MakeMoveCommand command) throws ResponseException, IOException{
        String username = usernameFromAuth(command.getAuthToken());
        GameData gameData = gameDAO.getGame(command.getGameID());
        try {
            gameData.game().makeMove(command.getMove());
        } catch (InvalidMoveException e){
            throw new ResponseException(ResponseException.Type.CLIENT_ERROR, "Invalid move.");
        }
        gameDAO.updateGame(gameData);
        connections.gameBroadcast(command.getGameID(), new LoadGameMessage(gameData.game()), null);
        connections.gameBroadcast(command.getGameID(), new NotificationMessage(
                username + " made move " + command.getMove().toString()), session);
    }

    private void playerLeaves(Session session, UserGameCommand command) throws ResponseException, IOException{
        String username = usernameFromAuth(command.getAuthToken());
        connections.removeSessionFromGame(session, command.getGameID());
        connections.gameBroadcast(command.getGameID(), new NotificationMessage(username + "left the game."), session);
    }

    private void playerResigns(Session session, UserGameCommand command) throws ResponseException, IOException{
        String username = usernameFromAuth(command.getAuthToken());
        GameData gameData = gameDAO.getGame(command.getGameID());
        gameData.game().setGameOver(true);
        gameDAO.updateGame(gameData);
        connections.gameBroadcast(command.getGameID(), new NotificationMessage(username + "resigned."), session);
    }

    private String usernameFromAuth(String authToken) throws ResponseException {
        return authDAO.getAuth(authToken).username();
    }
}
