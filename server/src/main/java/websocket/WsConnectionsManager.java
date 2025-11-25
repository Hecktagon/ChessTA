package websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

public class WsConnectionsManager {
    // TODO: HashSet prevents duplicate sessions in a game, do we want that?
    private final ConcurrentHashMap<Integer, HashSet<Session>> gameConnections = new ConcurrentHashMap<>();

    public void addSessionToGame(Session session, Integer gameID){
        if(!gameConnections.containsKey(gameID)){
            gameConnections.put(gameID, new HashSet<>());
        }
        gameConnections.get(gameID).add(session);
    }

    public void removeSessionFromGame(Session session, Integer gameID) throws IOException {
        tryGetGameSessions(gameID).remove(session);
    }

    // leave "excludedSession" as null to not exclude any sessions.
    public void gameBroadcast(Integer gameID, ServerMessage message, Session excludedSession) throws IOException {
        HashSet<Session> gameSessions = tryGetGameSessions(gameID);
        for(Session session : gameSessions){
            if(!session.equals(excludedSession)) {
                sendServerMessage(session, message);
            }
        }
    }

    private void sendServerMessage(Session session, ServerMessage message) throws IOException {
        if (session.isOpen()) {
            String messageJson = new Gson().toJson(message);
            session.getRemote().sendString(messageJson);
        }
    }

    private HashSet<Session> tryGetGameSessions(Integer gameID) throws IOException {
        if (!gameConnections.containsKey(gameID)){
            throw new IOException("No Such Game in Websocket.");
        }
        return gameConnections.get(gameID);
    }
}
