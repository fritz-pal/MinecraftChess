package me.fritzpal.minecraftChess.stateMachine;

import me.fritzpal.minecraftChess.gameLogic.Vec2;

public interface State {
    void onTileClick(Vec2 pos);
}