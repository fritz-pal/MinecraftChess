package me.fritzpal.minecraftChess.gameLogic;

import java.util.Objects;

public final class Move {
    private final Vec2 from;
    private final Vec2 to;
    private final Piece piece;
    private final String notation;
    private final PieceType promotionType;
    private final String fen;

    public Move(Vec2 from, Vec2 to, Piece piece, String notation, PieceType promotionType, String fen) {
        this.from = from;
        this.to = to;
        this.piece = piece;
        this.notation = notation;
        this.promotionType = promotionType;
        this.fen = fen;
    }

    public boolean isCapture() {
        return notation.contains("x");
    }

    public boolean isCheck() {
        return notation.contains("+") || notation.contains("#");
    }

    public boolean isPromotion() {
        return notation.contains("=");
    }

    public boolean isCastling() {
        return notation.contains("O-O");
    }

    public String getEngineNotation() {
        if (!isPromotion()) return from.getName() + to.getName();
        return from.getName() + to.getName() + Character.toLowerCase(promotionType.getNotation());
    }

    public Vec2 from() {
        return from;
    }

    public Vec2 to() {
        return to;
    }

    public Piece piece() {
        return piece;
    }

    public String notation() {
        return notation;
    }

    public PieceType promotionType() {
        return promotionType;
    }

    public String fen() {
        return fen;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        Move that = (Move) obj;
        return Objects.equals(this.from, that.from) &&
                Objects.equals(this.to, that.to) &&
                Objects.equals(this.piece, that.piece) &&
                Objects.equals(this.notation, that.notation) &&
                Objects.equals(this.promotionType, that.promotionType) &&
                Objects.equals(this.fen, that.fen);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to, piece, notation, promotionType, fen);
    }

    @Override
    public String toString() {
        return "Move[" +
                "from=" + from + ", " +
                "to=" + to + ", " +
                "piece=" + piece + ", " +
                "notation=" + notation + ", " +
                "promotionType=" + promotionType + ", " +
                "fen=" + fen + ']';
    }
}