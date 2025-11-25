package websocket.commands;

import chess.ChessGame;
import chess.ChessMove;

public class MakeMoveCommand extends UserGameCommand {
    ChessMove move;
    public MakeMoveCommand(ChessMove move, String authToken, Integer gameID, ChessGame.TeamColor color){
        super(CommandType.MAKE_MOVE, authToken, gameID, color);
        this.move = move;
    }

    public ChessMove getMove() {
        return move;
    }
}
