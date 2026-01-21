package ui;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import static ui.EscapeSequences.*;

public class GameUI {
    private final String[] columns = {"a", "b", "c", "d", "e", "f", "g", "h"};
    private final String[] rows = {"1", "2", "3", "4", "5", "6", "7", "8"};
    private static final String RESET = RESET_BG_COLOR + RESET_TEXT_COLOR + RESET_TEXT_BOLD_FAINT;
    private final String[] whitePieces = {WHITE_BISHOP, WHITE_KING, WHITE_KNIGHT, WHITE_PAWN, WHITE_QUEEN, WHITE_ROOK};
    private final String[] blackPieces = {BLACK_BISHOP, BLACK_KING, BLACK_KNIGHT, BLACK_PAWN, BLACK_QUEEN, BLACK_ROOK};
    private ChessPosition highlightPos = null;

    public GameUI () {}

    public GameUI (ChessPosition highlightPos){
        this.highlightPos = highlightPos;
    }

    // takes in a chessboard and team color, returns a pretty UI string of the board from that color's perspective.
    public String gameToUi(ChessBoard board, ChessGame.TeamColor team){
        List<String> squares = boardToSquares(board);
        StringBuilder boardUI = new StringBuilder();

        // if BLACK, print the board backwards.
        if (team == ChessGame.TeamColor.BLACK){
            squares = squares.reversed();
        }
        boardUI.append(RESET);
        int rowCounter = 0;
        for (String square : squares) {
            rowCounter++;
            boardUI.append(square);
            // adds the newline to separate the rows.
            if (rowCounter == 10) {
                rowCounter = 0;
                boardUI.append("\n");
            }
        }
        boardUI.append(RESET);
        return boardUI.toString();
    }

    // generates a string array from a chess board where each element is one tile of the board UI
    private ArrayList<String> boardToSquares(ChessBoard board){
        ArrayList<String> boardSquares = new ArrayList<>();
        boolean whiteTile = true;

        HashSet<ChessPosition> highlightSquares =  highlightPos == null ?
                new HashSet<>() : getEndPositions(highlightPos, board);

        // first do the top column labels:
        addColumnLabel(boardSquares);
        for (int row = 8; row > 0; row--){
            boardSquares.add(SET_BG_COLOR_NAVY + SET_TEXT_COLOR_VERY_LIGHT_GREY + " " + rows[row-1] + " " + RESET);
            for (int col = 1; col < 9; col++){
                ChessPosition currPos = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(currPos);
                String pieceUI = pieceToUI(piece);
                String bgColor;
                if(highlightSquares.contains(currPos)){
                    bgColor = whiteTile ? SET_BG_COLOR_GREEN : SET_BG_COLOR_DARK_GREEN;
                } else {
                    bgColor = whiteTile ? SET_BG_COLOR_LIGHT_GREY : SET_BG_COLOR_DARK_GREY;
                }
                boardSquares.add(bgColor + SET_TEXT_COLOR_BLACK +  pieceUI);
                whiteTile = !whiteTile;
            }
            whiteTile = !whiteTile;
            boardSquares.add(SET_BG_COLOR_NAVY + SET_TEXT_COLOR_VERY_LIGHT_GREY +" " + rows[row-1] + " " + RESET);
        }
        // finish by adding bottom column labels:
        addColumnLabel(boardSquares);

        return boardSquares;
    }

    private void addColumnLabel(ArrayList<String> boardSquares) {
        boardSquares.add(SET_BG_COLOR_NAVY +  EMPTY + RESET);
        for (String column : columns) {
            boardSquares.add(SET_BG_COLOR_NAVY + SET_TEXT_COLOR_VERY_LIGHT_GREY + " " + column + " " + RESET);
        }
        boardSquares.add(SET_BG_COLOR_NAVY + EMPTY + RESET);
    }

    private HashSet<ChessPosition> getEndPositions(ChessPosition myPosition, ChessBoard board){
        ChessGame tempGame = new ChessGame();
        tempGame.setBoard(board);
        Collection<ChessMove> moves = tempGame.validMoves(myPosition);
        HashSet<ChessPosition> positions = new HashSet<>();

        for(ChessMove move : moves) {
            positions.add(move.getEndPosition());
        }

        return positions;
    }

    // converts a ChessPiece into its corresponding UI element
    private String pieceToUI(ChessPiece piece){
        if(piece == null){
            return EMPTY;
        }

        String[] pieceUIs = piece.getTeamColor() == ChessGame.TeamColor.WHITE ?
                whitePieces : blackPieces;
        return switch(piece.getPieceType()){
            case KING -> pieceUIs[1];
            case QUEEN -> pieceUIs[4];
            case BISHOP -> pieceUIs[0];
            case KNIGHT -> pieceUIs[2];
            case ROOK -> pieceUIs[5];
            case PAWN -> pieceUIs[3];
        };
    }
}
