package chess;

import java.util.Objects;

public final class Square {
    private final int file;
    private final int rank;

    public Square(int file, int rank) {
        if (file < 0 || file > 7 || rank < 0 || rank > 7) {
            throw new IllegalArgumentException("Square out of bounds: file=" + file + ", rank=" + rank);
        }
        this.file = file;
        this.rank = rank;
    }

    public int file() {
        return file;
    }

    public int rank() {
        return rank;
    }

    public static Square parse(String value) {
        if (value == null || value.length() != 2) {
            throw new IllegalArgumentException("Invalid square: " + value);
        }
        char fileChar = Character.toLowerCase(value.charAt(0));
        char rankChar = value.charAt(1);
        if (fileChar < 'a' || fileChar > 'h' || rankChar < '1' || rankChar > '8') {
            throw new IllegalArgumentException("Invalid square: " + value);
        }
        return new Square(fileChar - 'a', rankChar - '1');
    }

    @Override
    public String toString() {
        return "" + (char) ('a' + file) + (char) ('1' + rank);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Square square)) return false;
        return file == square.file && rank == square.rank;
    }

    @Override
    public int hashCode() {
        return Objects.hash(file, rank);
    }
}
