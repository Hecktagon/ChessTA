import chess.*;
import client.Repl;
import ui.GameUI;
import ui.GameUI.*;

public class ClientMain {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);
        if(args.length > 100){
            testUI();
        }
        Repl repl = new Repl("http://localhost:8080");
        repl.run();
    }

    private static void testUI() {
        GameUI gameUI = new GameUI();
        ChessGame game = new ChessGame();
        try {
            game.makeMove(new ChessMove(new ChessPosition(2, 5), new ChessPosition(4, 5), null));
            game.makeMove(new ChessMove(new ChessPosition(8,7), new ChessPosition(6,6), null));
        } catch (InvalidMoveException e) {
            System.out.println("Invalid Move Exception!");
            return;
        }
        ChessBoard board = game.getBoard();
        String uiBoard = gameUI.gameToUi(board, ChessGame.TeamColor.WHITE);
        System.out.println(uiBoard);
    }
}