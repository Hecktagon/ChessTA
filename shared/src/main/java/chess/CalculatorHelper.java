package chess;

import java.util.Collection;

public class CalculatorHelper {

    private boolean canTake(ChessPiece myPiece, ChessPiece targetPiece){

    }

    // takes in a direction [x, y]/[col increment,row increment], and returns all possible moves in that direction.
    static ChessMove checkMove(int[] direction, int numMoves, ChessPosition myPos, ChessBoard board, ChessPiece.PieceType promo){
        if (numMoves > 2){
            numMoves = 8;
        }

        int myCol = myPos.getColumn();
        int myRow = myPos.getRow();
        ChessPiece myPiece;

        int newCol = myCol + direction[0];
        int newRow = myRow + direction[1];

        ChessPosition newPos = new ChessPosition(newRow, newCol);
        ChessPiece targetPiece = board.getPiece(newPos);

        // if there is no piece, it is a valid move. if there is a piece, only valid if can take.
        if (targetPiece == null){
            return new ChessMove(myPos, newPos, promo);
        } else {
            myPiece = board.getPiece(myPos);
            return (canTake(myPiece, targetPiece)) ? new ChessMove(myPos, newPos, promo) : null;
        }

    }


}
