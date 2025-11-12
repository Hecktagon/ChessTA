package ui;

import chess.ChessBoard;
import chess.ChessGame;

import static ui.EscapeSequences.*;

public class GameUI {
    private static final String[] columns = {"a", "b", "c", "d", "e", "f", "g", "h"};
    private static final String[] rows = {"1", "2", "3", "4", "5", "6", "7", "8"};
    private static final String BLACKBG = SET_BG_COLOR_LIGHT_GREY;
    private static final String WHITEBG = SET_BG_COLOR_WHITE;
    private static final String NAVYBG = SET_BG_COLOR_NAVY;

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
//        for (int row = 1; row < 9; row++){
//            for (int col = 1; col < 9; col++){
//
//            }
//        }
        return boardSquares;
    }
}
