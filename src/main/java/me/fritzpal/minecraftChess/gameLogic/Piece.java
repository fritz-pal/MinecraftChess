package me.fritzpal.minecraftChess.gameLogic;

import java.util.Objects;

public final class Piece {
    private final PieceType type;
    private final boolean isWhite;

    public Piece(PieceType type, boolean isWhite) {
        this.type = type;
        this.isWhite = isWhite;
    }

    @Override
    public String toString() {
        return (isWhite ? "White " : "Black ") + type.toString();
    }

    public PieceType type() {
        return type;
    }

    public boolean isWhite() {
        return isWhite;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        Piece that = (Piece) obj;
        return Objects.equals(this.type, that.type) &&
                this.isWhite == that.isWhite;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, isWhite);
    }

}