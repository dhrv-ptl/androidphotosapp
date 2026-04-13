package chess;

public final class GameState {
    private Color turn;
    private boolean wK;
    private boolean wQ;
    private boolean bK;
    private boolean bQ;
    private Square enPassantTarget;

    public GameState(Color turn, boolean wK, boolean wQ, boolean bK, boolean bQ, Square enPassantTarget) {
        this.turn = turn;
        this.wK = wK;
        this.wQ = wQ;
        this.bK = bK;
        this.bQ = bQ;
        this.enPassantTarget = enPassantTarget;
    }

    public Color turn() {
        return turn;
    }

    public void setTurn(Color turn) {
        this.turn = turn;
    }

    public boolean wK() {
        return wK;
    }

    public void setWK(boolean wK) {
        this.wK = wK;
    }

    public boolean wQ() {
        return wQ;
    }

    public void setWQ(boolean wQ) {
        this.wQ = wQ;
    }

    public boolean bK() {
        return bK;
    }

    public void setBK(boolean bK) {
        this.bK = bK;
    }

    public boolean bQ() {
        return bQ;
    }

    public void setBQ(boolean bQ) {
        this.bQ = bQ;
    }

    public Square enPassantTarget() {
        return enPassantTarget;
    }

    public void setEnPassantTarget(Square enPassantTarget) {
        this.enPassantTarget = enPassantTarget;
    }

    public GameState copy() {
        return new GameState(turn, wK, wQ, bK, bQ, enPassantTarget);
    }

    public static Color opposite(Color color) {
        return color == Color.WHITE ? Color.BLACK : Color.WHITE;
    }
}
