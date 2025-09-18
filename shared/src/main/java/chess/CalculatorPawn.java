package chess;

import java.util.Collection;
import java.util.HashSet;

public class CalculatorPawn implements MovesCalculator{
    public Collection<ChessMove> pieceMoves(ChessBoard chessBoard, ChessPosition chessPosition){
        HashSet<ChessMove> moves = new HashSet<>();

        ChessPiece myPawn = chessBoard.getPiece(chessPosition);
        // tells the pawn which direction to go
        int colorMultiplier = myPawn.getTeamColor() == ChessGame.TeamColor.WHITE ? 1 : -1;

        // checks whether the pawn is in its starting position, can move 2 if it is.
        int numAdvance = ((colorMultiplier == 1 && chessPosition.getRow() == 2) ||
                (colorMultiplier == -1 && chessPosition.getRow() == 7)) ? 2 : 1;

        // checks whether the pawn will promote
        boolean promotes = (colorMultiplier == 1 && chessPosition.getRow() == 7) ||
                (colorMultiplier == -1 && chessPosition.getRow() == 2);

        int[] advance = {0, colorMultiplier};

        int myRow = chessPosition.getRow();
        int myCol = chessPosition.getColumn();

        int[] leftTake = {-1, colorMultiplier};
        ChessPosition leftPos = new ChessPosition(myRow + leftTake[1], myCol + leftTake[0]);
        ChessPiece leftTarget = CalculatorHelper.inBounds(leftPos) ? chessBoard.getPiece(leftPos) : null;

        int[] rightTake = {1, colorMultiplier};
        ChessPosition rightPos = new ChessPosition(myRow + rightTake[1], myCol + rightTake[0]);
        ChessPiece rightTarget = CalculatorHelper.inBounds(rightPos) ? chessBoard.getPiece(rightPos) : null;

        // check forward move
        moves.addAll(CalculatorHelper.checkDirection(advance, numAdvance, chessPosition, chessBoard, promotes, false));

        // check left take if there is a piece there
        if (leftTarget != null){
            moves.addAll(CalculatorHelper.checkDirection(leftTake, 1, chessPosition, chessBoard, promotes, true));
        }

        // check right take if there is a piece there
        if (rightTarget != null){
            moves.addAll(CalculatorHelper.checkDirection(rightTake, 1, chessPosition, chessBoard, promotes, true));
        }

        return moves;
    }

}
