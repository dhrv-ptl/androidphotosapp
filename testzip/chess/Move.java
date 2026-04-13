package chess;

public final class Move {
    private final Square from;
    private final Square to;
    private final Character promotion;
    private final boolean offeredDraw;
    private final boolean isResign;

    public Move(Square from, Square to, Character promotion, boolean offeredDraw, boolean isResign) {
        this.from = from;
        this.to = to;
        this.promotion = promotion;
        this.offeredDraw = offeredDraw;
        this.isResign = isResign;
    }

    public Square from() {
        return from;
    }

    public Square to() {
        return to;
    }

    public Character promotion() {
        return promotion;
    }

    public boolean offeredDraw() {
        return offeredDraw;
    }

    public boolean isResign() {
        return isResign;
    }
}
