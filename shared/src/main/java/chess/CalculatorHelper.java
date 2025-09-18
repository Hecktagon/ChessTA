package chess;

import java.util.Collection;
import java.util.HashSet;

public class CalculatorHelper {

    private static boolean canTake(ChessPiece myPiece, ChessPiece targetPiece){
        return !myPiece.getTeamColor().equals(targetPiece.getTeamColor());
    }

    public static boolean inBounds(ChessPosition myPos){
        return myPos.getRow() <= 8 && myPos.getRow() > 0 && myPos.getColumn() <= 8 && myPos.getColumn() > 0;
    }

    // generates all the promotion moves for a promoting pawn
    private static Collection<ChessMove> promoMoves(ChessPosition startPos, ChessPosition endPos){
        HashSet<ChessMove> promotions = new HashSet<>();
        ChessPiece.PieceType[] promoTypes = {ChessPiece.PieceType.QUEEN, ChessPiece.PieceType.BISHOP,
                ChessPiece.PieceType.KNIGHT, ChessPiece.PieceType.ROOK};

        for(ChessPiece.PieceType type : promoTypes){
            promotions.add(new ChessMove(startPos, endPos, type));
        }
        return promotions;
    }

    // takes in a direction [x, y]/[col increment,row increment], and returns all possible moves in that direction.
    public static Collection<ChessMove> checkDirection(int[] direction, int numMoves, ChessPosition myPos, ChessBoard board, boolean promotes, boolean takes){
        if (numMoves > 2){
            numMoves = 8;
        }
        HashSet<ChessMove> moves = new HashSet<>();
        int myCol = myPos.getColumn();
        int myRow = myPos.getRow();
        ChessPiece myPiece;

        int newCol = myCol;
        int newRow = myRow;

        for(int i = 0; i < numMoves; i++){
            newCol += direction[0];
            newRow += direction[1];

            ChessPosition newPos = new ChessPosition(newRow, newCol);
            if(!inBounds(newPos)){
                break;
            }
            ChessPiece targetPiece = board.getPiece(newPos);

            // for pawn moves, allows the adding of all promotional moves if needed.
            HashSet<ChessMove> currentMove = new HashSet<>();
            if(promotes){
                currentMove.addAll(promoMoves(myPos, newPos));
            }else {
                currentMove.add(new ChessMove(myPos, newPos, null));
            }

            // if there is no piece, it is a valid move. if there is a piece, only valid if can take.
            if (targetPiece == null){
                moves.addAll(currentMove);
            } else {
                myPiece = board.getPiece(myPos);
                if (takes && canTake(myPiece, targetPiece)) {
                    moves.addAll(currentMove);
                    break;
                } else {
                    break;
                }
            }
        }
        return moves;
    }


}
