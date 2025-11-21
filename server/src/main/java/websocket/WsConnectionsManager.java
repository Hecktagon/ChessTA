package websocket;

import org.eclipse.jetty.websocket.api.Session;

import java.util.concurrent.ConcurrentHashMap;

public class WsConnectionsManager {
    private final ConcurrentHashMap<Session, Session> connections = new ConcurrentHashMap<>();

    //TODO: will have to keep track of connections per game

    public void add(Session session){

    }
}
