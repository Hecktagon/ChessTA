package websocket;

public class WebsocketFacade {
    private final ServerMessageObserver clientWs;

    public WebsocketFacade(String serverUrl, ServerMessageObserver serverMessageObserver){
        clientWs = serverMessageObserver;
    }

    public void connect(){

    }

    public void makeMove(){

    }

    public void leave(){

    }

    public void resign(){

    }
}
