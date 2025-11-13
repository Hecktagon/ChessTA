package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;

import static ui.EscapeSequences.*;

public class GameUI {
    private final String[] columns = {"a", "b", "c", "d", "e", "f", "g", "h"};
    private final String[] rows = {"1", "2", "3", "4", "5", "6", "7", "8"};
    private final String BLACKBG = SET_BG_COLOR_LIGHT_GREY;
    private final String WHITEBG = SET_BG_COLOR_WHITE;
    private final String NAVYBG = SET_BG_COLOR_NAVY;
    private final String[] whitePieces = {WHITE_BISHOP, WHITE_KING, WHITE_KNIGHT, WHITE_PAWN, WHITE_QUEEN, WHITE_ROOK};
    private final String[] blackPieces = {BLACK_BISHOP, BLACK_KING, BLACK_KNIGHT, BLACK_PAWN, BLACK_QUEEN, BLACK_ROOK};


    public String gameToUi(ChessBoard board, ChessGame.TeamColor team){
        String[] squares = boardToSquares(board);
        StringBuilder boardUI = new StringBuilder();

        // if BLACK, print the board backwards.
        if(team == ChessGame.TeamColor.BLACK){
            int rowCounter = 0;
            for(int i = squares.length; i > 0; i--){
                rowCounter++;
                boardUI.append(squares[i-1]);
                // adds the newline to separate the rows.
                if(rowCounter == 8){
                    rowCounter = 0;
                    boardUI.append("\n");
                }
            }
            return boardUI.toString();
        }

        // else if WHITE, print the board forward:
        int rowCounter = 0;
        for (String square : squares) {
            rowCounter++;
            boardUI.append(square);
            // adds the newline to separate the rows.
            if(rowCounter == 8){
                rowCounter = 0;
                boardUI.append("\n");
            }
        }
        return boardUI.toString();
    }

    private String[] boardToSquares(ChessBoard board){
        String[] boardSquares = new String[100];
        // TODO: break the board up into individual strings for each square (including the row/column labels) 
//        for (int row = 1; row < 9; row++){
//            for (int col = 1; col < 9; col++){
//
//            }
//        }
        return boardSquares;
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
