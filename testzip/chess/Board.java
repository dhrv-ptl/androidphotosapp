package chess;

import java.util.ArrayList;
import java.util.List;

public final class Board {
    private final Piece[][] grid;

    public Board() {
        this.grid = new Piece[8][8];
    }

    private Board(Piece[][] grid) {
        this.grid = grid;
    }

    public Piece get(Square square) {
        return grid[square.rank()][square.file()];
    }

    public void set(Square square, Piece piece) {
        grid[square.rank()][square.file()] = piece;
    }

    public boolean inBounds(Square square) {
        return square.file() >= 0 && square.file() < 8 && square.rank() >= 0 && square.rank() < 8;
    }

    public Board copy() {
        Piece[][] cloned = new Piece[8][8];
        for (int r = 0; r < 8; r++) {
            System.arraycopy(grid[r], 0, cloned[r], 0, 8);
        }
        return new Board(cloned);
    }

    public List<PieceOnSquare> allPieces() {
        List<PieceOnSquare> pieces = new ArrayList<>();
        for (int rank = 0; rank < 8; rank++) {
            for (int file = 0; file < 8; file++) {
                Piece piece = grid[rank][file];
                if (piece != null) {
                    pieces.add(new PieceOnSquare(new Square(file, rank), piece));
                }
            }
        }
        return pieces;
    }

    public Square findKing(Color color) {
        for (int rank = 0; rank < 8; rank++) {
            for (int file = 0; file < 8; file++) {
                Piece piece = grid[rank][file];
                if (piece != null && piece.color() == color && piece.kind() == Kind.KING) {
                    return new Square(file, rank);
                }
            }
        }
        return null;
    }
}

record PieceOnSquare(Square square, Piece piece) {
}
