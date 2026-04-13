package chess;

public abstract class Piece {
    private final Color color;
    private final Kind kind;

    protected Piece(Color color, Kind kind) {
        this.color = color;
        this.kind = kind;
    }

    public Color color() {
        return color;
    }

    public Kind kind() {
        return kind;
    }
}

enum Color {
    WHITE,
    BLACK
}

enum Kind {
    KING,
    QUEEN,
    ROOK,
    BISHOP,
    KNIGHT,
    PAWN
}

final class King extends Piece {
    King(Color color) {
        super(color, Kind.KING);
    }
}

final class Queen extends Piece {
    Queen(Color color) {
        super(color, Kind.QUEEN);
    }
}

final class Rook extends Piece {
    Rook(Color color) {
        super(color, Kind.ROOK);
    }
}

final class Bishop extends Piece {
    Bishop(Color color) {
        super(color, Kind.BISHOP);
    }
}

final class Knight extends Piece {
    Knight(Color color) {
        super(color, Kind.KNIGHT);
    }
}

final class Pawn extends Piece {
    Pawn(Color color) {
        super(color, Kind.PAWN);
    }
}
