package websocket;

import com.google.gson.Gson;
import errors.ResponseException;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;


import java.io.IOException;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<Integer, HashSet<Session>> connections = new ConcurrentHashMap<>();

    public void add(Integer gameID, Session session) {
        if(!connections.containsKey(gameID)){
            connections.put(gameID, new HashSet<>());
        }
        connections.get(gameID).add(session);
    }

    public void remove(Integer gameID, Session session) throws ResponseException {
        if(!connections.containsKey(gameID)){
            throw new ResponseException(ResponseException.Type.SERVER_ERROR, "No such game ID.");
        }
        HashSet<Session> sessions = connections.get(gameID);
        if(!sessions.contains(session)){
            throw new ResponseException(ResponseException.Type.SERVER_ERROR, "Invalid session.");
        }
        sessions.remove(session);
    }

    public void broadcast(Integer gameID, ServerMessage message, Session excludeSession) throws IOException {
        for (Session session : connections.get(gameID)) {
            if (session.isOpen()) {
                if (!session.equals(excludeSession)) {
                    session.getRemote().sendString(new Gson().toJson(message));
                }
            }
        }
    }
}