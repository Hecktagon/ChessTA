package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

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
        if(myPiece == null){
            return new HashSet<>();
        }

        Collection<ChessMove> moves = myPiece.pieceMoves(chessBoard, startPosition);
        Collection<ChessMove> valids = new HashSet<>();

        for(ChessMove move : moves){
            // if the move puts king in danger, remove it
            if(tryMove(move)){
                valids.add(move);
            }
        }
        return valids;
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
        ChessPiece myPiece =  chessBoard.getPiece(myPos);

        // Error if move is invalid
        if(!valids.contains(move) || myPiece.getTeamColor() != currentTeam){
            throw new InvalidMoveException(move + " is not a valid move.");
        }

        ChessPosition newPos = move.getEndPosition();

        // Execute the move
        chessBoard.addPiece(myPos, null);
        if (move.getPromotionPiece() == null){
            chessBoard.addPiece(newPos, myPiece);
        } else {
            chessBoard.addPiece(newPos, new ChessPiece(myPiece.getTeamColor(), move.getPromotionPiece()));
        }

        currentTeam = currentTeam == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE;
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
        Collection<ChessPosition> dangerSpaces = getEndPositions(teamMoves(enemyTeam));
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

        // if you have no moves and are in check, it is checkmate
        return teamCantMove(teamColor);
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
        System.out.print(chessBoard);
        // if you have no moves and aren't in check, it is stalemate
        return teamCantMove(teamColor);
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


    private Collection<ChessMove> teamMoves(TeamColor teamColor){
        HashSet<ChessMove> moves =  new HashSet<>();
        for(int row = 1; row < 9; row++){
           for(int col = 1; col < 9; col++){
               ChessPosition curPos = new ChessPosition(row, col);
               ChessPiece curPiece = chessBoard.getPiece(curPos);
               if(curPiece != null && curPiece.getTeamColor() == teamColor){
                   moves.addAll(curPiece.pieceMoves(chessBoard, curPos));
               }
           }
        }
        return moves;
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

        // undo the move
        chessBoard.addPiece(endPos, targetPiece);
        chessBoard.addPiece(startPos, myPiece);

        return canMove;
    }


    private TeamColor getEnemyTeam(TeamColor myTeam){
        return myTeam == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE;
    }

    private boolean teamCantMove(TeamColor team){
        // if any move succeeds, return false
        Collection<ChessMove> myTeamMoves = teamMoves(team);
        for(ChessMove move : myTeamMoves){
            if(tryMove(move)){
                return false;
            }
        }
        // if all moves fail return true
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return Objects.equals(chessBoard, chessGame.chessBoard) && currentTeam == chessGame.currentTeam;
    }

    @Override
    public int hashCode() {
        return Objects.hash(chessBoard, currentTeam);
    }
}
