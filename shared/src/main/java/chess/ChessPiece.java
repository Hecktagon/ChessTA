package chess;

import java.util.Collection;
import java.util.List;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    private final ChessGame.TeamColor teamColor;
    private final ChessPiece.PieceType pieceType;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        teamColor = pieceColor;
        pieceType = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return teamColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return pieceType;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        MovesCalculator calculator = calculatorTyper(board, myPosition);
        return calculator.pieceMoves(board, myPosition);
    }

//  returns the correct move calculator for the given piece
    private MovesCalculator calculatorTyper(ChessBoard board, ChessPosition myPosition){
        PieceType myType = board.getPiece(myPosition).pieceType;
        return switch (myType) {
            case KING -> new CalculatorKing();
            case QUEEN -> new CalculatorQueen();
            case ROOK ->  new CalculatorRook();
            case KNIGHT -> new CalculatorKnight();
            case BISHOP -> new CalculatorBishop();
            case PAWN -> new CalculatorPawn();
        };
    }
}
