package me.fritzpal.minecraftChess.stateMachine;

import me.fritzpal.minecraftChess.ActiveGame;
import me.fritzpal.minecraftChess.gameLogic.Board;
import me.fritzpal.minecraftChess.gameLogic.Result;
import me.fritzpal.minecraftChess.gameLogic.Vec2;

public class PieceSelectedState implements State {

    private final ActiveGame game;

    public PieceSelectedState(ActiveGame game) {
        this.game = game;
    }

    @Override
    public void onTileClick(Vec2 pos) {
        Board board = game.getMainBoard();
        if (game.getSelectedTile() != null && game.getSelectedTile().equals(pos)) {
            game.setSelectedTile(null);
            game.changeState(new TurnState(game));
            return;
        }
        if (board.occupied(pos) && board.getPiece(pos).isWhite() == board.isWhiteTurn()) {
            game.setSelectedTile(pos);
            game.update();
            return;
        }
        if (!board.occupied(pos) || board.getPiece(pos).isWhite() != board.isWhiteTurn()) {
            if (board.isLegalMove(game.getSelectedTile(), pos)) {
                if (board.isPromotingMove(game.getSelectedTile(), pos)) {
                    game.sendPromotionOption(pos);
                    return;
                }
                board.move(game.getSelectedTile(), pos, true, null);
                if (board.getResult() != Result.NOTFINISHED) {
                    game.endGame();
                    return;
                }
            }
            game.setSelectedTile(null);
            game.changeState(new TurnState(game));
        }
    }
}