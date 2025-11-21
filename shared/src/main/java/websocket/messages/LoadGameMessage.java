package websocket.messages;

import chess.ChessGame;

public class LoadGameMessage extends ServerMessage{
    ChessGame chessGame;

    public LoadGameMessage(ChessGame game){
        super(ServerMessageType.LOAD_GAME);
        chessGame = game;
    }

    public ChessGame getChessGame() {
        return chessGame;
    }
}
