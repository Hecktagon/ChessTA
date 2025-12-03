package client;

import chess.ChessGame;

public record ClientGameInfo(ChessGame.TeamColor clientColor, ChessGame clientGame, Integer gameID) {
}
