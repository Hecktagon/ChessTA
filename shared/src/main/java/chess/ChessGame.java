package chess;

import java.util.Collection;
import java.util.HashSet;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private ChessBoard chessBoard;
    private TeamColor currentTeam;
    public ChessGame() {
        chessBoard = new ChessBoard();
        chessBoard.resetBoard();
        currentTeam = TeamColor.WHITE;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return currentTeam;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        currentTeam = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece myPiece = chessBoard.getPiece(startPosition);
        Collection<ChessMove> moves = myPiece.pieceMoves(chessBoard, startPosition);




        return moves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition myPos = move.getStartPosition();
        Collection<ChessMove> valids = validMoves(myPos);

        // Error if move is invalid
        if(!valids.contains(move)){
            throw new InvalidMoveException(move.toString() + " is not a valid move.");
        }

        ChessPosition newPos = move.getEndPosition();

        // Execute the move
        ChessPiece myPiece =  chessBoard.getPiece(myPos);
        chessBoard.addPiece(myPos, null);
        chessBoard.addPiece(newPos, myPiece);
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPos = findKing(teamColor);
        TeamColor enemyTeam = getEnemyTeam(teamColor);
        Collection<ChessPosition> dangerSpaces = attackedSpaces(enemyTeam);
        return dangerSpaces.contains(kingPos);
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        // have to be in check to be in checkmate
        if(!isInCheck(teamColor)){
            return false;
        }

        ChessPosition kingPos = findKing(teamColor);
        TeamColor enemyTeam = getEnemyTeam(teamColor);
        Collection<ChessPosition> dangerSpaces = attackedSpaces(enemyTeam);

        ChessPiece myKing = chessBoard.getPiece(kingPos);
        Collection<ChessMove> kingMoves = myKing.pieceMoves(chessBoard, kingPos);
        for (ChessMove kingMove : kingMoves){
            // if there is a safe move the king can make, then it isn't checkmate.
            if(!dangerSpaces.contains(kingMove.getEndPosition())){
                 return false;
            }
        }
        // if no safe moves were found, it is checkmate
        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        // can't be in check if you are in stalemate
        if(isInCheck(teamColor)){
            return false;
        }

        // all pieces have to be unable to move for stalemate
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        chessBoard = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return chessBoard;
    }

    private Collection<ChessPosition> getEndPositions(Collection<ChessMove> moves){
        HashSet<ChessPosition> endPoses = new HashSet<>();
        for(ChessMove move : moves){
            endPoses.add(move.getEndPosition());
        }
        return endPoses;
    }

    private Collection<ChessPosition> attackedSpaces(TeamColor teamColor){
        HashSet<ChessPosition> dangerSpaces =  new HashSet<>();
        for(int row = 1; row < 9; row++){
           for(int col = 1; col < 9; col++){
               ChessPosition curPos = new ChessPosition(row, col);
               ChessPiece curPiece = chessBoard.getPiece(curPos);
               if(curPiece != null && curPiece.getTeamColor() == teamColor){
                   dangerSpaces.addAll(getEndPositions(curPiece.pieceMoves(chessBoard, curPos)));
               }
           }
        }
        return dangerSpaces;
    }

    private ChessPosition findKing(TeamColor kingColor) {
        for (int row = 1; row < 9; row++) {
            for (int col = 1; col < 9; col++) {
                ChessPosition curPos = new ChessPosition(row, col);
                ChessPiece curPiece = chessBoard.getPiece(curPos);
                if (curPiece != null && curPiece.getPieceType() == ChessPiece.PieceType.KING
                        && curPiece.getTeamColor() == kingColor) {
                    return curPos;
                }
            }
        }
        System.out.print(kingColor.toString() + " is missing their king!\n");
        return null;
    }


    private boolean tryMove(ChessMove move){
        ChessPosition startPos = move.getStartPosition();
        ChessPosition endPos = move.getEndPosition();
        ChessPiece myPiece = chessBoard.getPiece(startPos);
        TeamColor myTeam = myPiece.getTeamColor();
        ChessPiece targetPiece = chessBoard.getPiece(endPos);

        // make the move, then check if it puts your king in danger
        chessBoard.addPiece(endPos, myPiece);
        boolean canMove = !isInCheck(myTeam);

        // if the move is invalid, undo it
        if(!canMove){
            chessBoard.addPiece(endPos, targetPiece);
            chessBoard.addPiece(startPos, myPiece);
        }

        return canMove;
    }

    private TeamColor getEnemyTeam(TeamColor myTeam){
        return myTeam == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE;
    }

}
