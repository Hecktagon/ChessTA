package chess;

import java.util.Collection;
import java.util.HashSet;

public class CalculatorKnight implements MovesCalculator{
    public Collection<ChessMove> pieceMoves(ChessBoard chessBoard, ChessPosition chessPosition){
        HashSet<ChessMove> moves = new HashSet<>();

        int[][] directions = {
                {1,0},
                {-1,0},
                {0,-1},
                {0,1}
        };

        for(int[] direction : directions) {
            moves.addAll(CalculatorHelper.checkDirection(direction, 8,
                    chessPosition, chessBoard, false, true));
        }
        return moves;
    }
}
