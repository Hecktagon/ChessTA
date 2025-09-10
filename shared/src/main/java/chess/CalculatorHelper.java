package chess;

import java.util.Collection;

public class CalculatorHelper {

    // takes in a direction [x, y]/[col increment,row increment], and returns a ChessMove if move is possible
    static ChessMove checkMove(int[] direction, ChessPosition myPos, ChessBoard board, ChessPiece.PieceType promo){
        ChessMove move = null;
        int myCol = myPos.getColumn();
        int myRow = myPos.getRow();

        int newCol = myCol + direction[0];
        int newRow = myRow + direction[1];

        ChessPosition newPos = new ChessPosition(newRow, newCol);
        ChessPiece targetPiece = board.getPiece(newPos);

        // if there is no piece, it is a valid move. if there is a piece, only valid if can take.
        if (targetPiece == null){
            return new ChessMove(myPos, newPos, promo);
        } else {

        }

        return move;
    }
}
