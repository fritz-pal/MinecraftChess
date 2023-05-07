package me.fritzpal.minecraftChess.gameLogic;

public enum PieceType {
    QUEEN('Q'),
    ROOK('R'),
    BISHOP('B'),
    KNIGHT('N'),
    PAWN('P'),
    KING('K');

    private final char notation;

    PieceType(char notation) {
        this.notation = notation;
    }

    public static PieceType fromNotation(char notation) {
        for (PieceType pieceType : values()) {
            if (pieceType.getNotation() == Character.toUpperCase(notation)) {
                return pieceType;
            }
        }
        return null;
    }

    public static PieceType fromString(String s) {
        switch (s.toLowerCase()) {
            case "queen":
                return QUEEN;
            case "rook":
                return ROOK;
            case "bishop":
                return BISHOP;
            case "knight":
                return KNIGHT;
            case "pawn":
                return PAWN;
            case "king":
                return KING;
            default:
                return null;
        }
    }

    public char getNotation() {
        return notation;
    }

    public Integer getCustomModelData(boolean white) {
        return ordinal() + 1 + (white ? 0 : 6);
    }
}