package chess;

import java.util.Collection;

public interface MovesCalculator {
    public Collection<ChessMove> pieceMoves(ChessBoard chessBoard, ChessPosition chessPosition);
}
