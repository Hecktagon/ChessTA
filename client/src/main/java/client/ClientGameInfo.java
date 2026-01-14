package client;

import chess.ChessGame;

public record ClientGameInfo(ChessGame game, ChessGame.TeamColor color) {
}
