package me.fritzpal.minecraftChess.gameLogic;

public enum Result {
    WHITEWONBYCHECKMATE("1-0"),
    BLACKWONBYCHECKMATE("0-1"),
    BLACKWONBYRESIGNATION("0-1"),
    WHITEWONBYRESIGNATION("1-0"),
    DRAWBYSTALEMATE("1/2-1/2"),
    DRAWBYFIFTYMOVESRULE("1/2-1/2"),
    DRAWBYTHREEFOLDREPETITION("1/2-1/2"),
    DRAWBYINSUFFICIENTMATERIAL("1/2-1/2"),
    DRAWBYAGREEMENT("1/2-1/2"),
    NOTFINISHED("*");

    private final String notation;

    Result(String notation) {
        this.notation = notation;
    }

    @Override
    public String toString() {
        switch (this) {
            case WHITEWONBYCHECKMATE:
                return "White won by checkmate!";
            case BLACKWONBYCHECKMATE:
                return "Black won by checkmate!";
            case BLACKWONBYRESIGNATION:
                return "Black won by resignation!";
            case WHITEWONBYRESIGNATION:
                return "White won by resignation!";
            case DRAWBYSTALEMATE:
                return "Draw by stalemate!";
            case DRAWBYFIFTYMOVESRULE:
                return "Draw by fifty moves rule!";
            case DRAWBYTHREEFOLDREPETITION:
                return "Draw by threefold repetition!";
            case DRAWBYINSUFFICIENTMATERIAL:
                return "Draw by insufficient material!";
            case DRAWBYAGREEMENT:
                return "Draw by agreement!";
            case NOTFINISHED:
                return "Game not finished!";
            default:
                throw new IllegalArgumentException();
        }
    }

    public boolean whiteWon() {
        return this == WHITEWONBYCHECKMATE || this == WHITEWONBYRESIGNATION;
    }

    public boolean blackWon() {
        return this == BLACKWONBYCHECKMATE || this == BLACKWONBYRESIGNATION;
    }

    public boolean isDraw() {
        return this == DRAWBYSTALEMATE || this == DRAWBYFIFTYMOVESRULE || this == DRAWBYTHREEFOLDREPETITION || this == DRAWBYINSUFFICIENTMATERIAL || this == DRAWBYAGREEMENT;
    }

    public String getNotation() {
        return notation;
    }
}