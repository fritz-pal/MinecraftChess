package me.fritzpal.minecraftChess.stateMachine;

import me.fritzpal.minecraftChess.ActiveGame;
import me.fritzpal.minecraftChess.gameLogic.Board;
import me.fritzpal.minecraftChess.gameLogic.Vec2;

public class TurnState implements State {

    private final ActiveGame game;

    public TurnState(ActiveGame game) {
        this.game = game;
    }

    @Override
    public void onTileClick(Vec2 pos) {
        Board board = game.getMainBoard();
        if (board.occupied(pos)) {
            if (board.getPiece(pos).isWhite() == board.isWhiteTurn()) {
                game.setSelectedTile(pos);
                game.changeState(new PieceSelectedState(game));
            }
        }
    }
}