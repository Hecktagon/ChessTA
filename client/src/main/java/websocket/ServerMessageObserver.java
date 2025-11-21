package websocket;

import chess.ChessGame;
import websocket.messages.ServerMessage;

public interface ServerMessageObserver {
    public void loadGame(ChessGame game);
    public void notify(String serverMessage);
}
