package client;

import chess.ChessGame;

public record ClientGameInfo(Integer gameID, ChessGame.TeamColor color) {
}
