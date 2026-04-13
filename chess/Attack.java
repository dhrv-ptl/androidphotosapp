package chess;

public final class Attack {
    private Attack() {
    }

    static boolean isSquareAttacked(Board b, Square target, Color byColor) {
        if (isPawnAttacking(b, target, byColor)) {
            return true;
        }
        if (isKnightAttacking(b, target, byColor)) {
            return true;
        }
        if (isKingAttacking(b, target, byColor)) {
            return true;
        }
        if (isSlidingAttacking(b, target, byColor, new int[][]{
            {1, 1}, {1, -1}, {-1, 1}, {-1, -1}
        }, Kind.BISHOP, Kind.QUEEN)) {
            return true;
        }
        return isSlidingAttacking(b, target, byColor, new int[][]{
            {1, 0}, {-1, 0}, {0, 1}, {0, -1}
        }, Kind.ROOK, Kind.QUEEN);
    }

    private static boolean isPawnAttacking(Board b, Square target, Color byColor) {
        int pawnRank = byColor == Color.WHITE ? target.rank() - 1 : target.rank() + 1;
        int leftFile = target.file() - 1;
        int rightFile = target.file() + 1;

        Square left = squareOrNull(leftFile, pawnRank);
        if (left != null) {
            Piece p = b.get(left);
            if (p != null && p.color() == byColor && p.kind() == Kind.PAWN) {
                return true;
            }
        }

        Square right = squareOrNull(rightFile, pawnRank);
        if (right != null) {
            Piece p = b.get(right);
            if (p != null && p.color() == byColor && p.kind() == Kind.PAWN) {
                return true;
            }
        }
        return false;
    }

    private static boolean isKnightAttacking(Board b, Square target, Color byColor) {
        int[][] deltas = {
            {1, 2}, {2, 1}, {2, -1}, {1, -2},
            {-1, -2}, {-2, -1}, {-2, 1}, {-1, 2}
        };
        for (int[] delta : deltas) {
            Square from = squareOrNull(target.file() + delta[0], target.rank() + delta[1]);
            if (from == null) {
                continue;
            }
            Piece p = b.get(from);
            if (p != null && p.color() == byColor && p.kind() == Kind.KNIGHT) {
                return true;
            }
        }
        return false;
    }

    private static boolean isKingAttacking(Board b, Square target, Color byColor) {
        for (int df = -1; df <= 1; df++) {
            for (int dr = -1; dr <= 1; dr++) {
                if (df == 0 && dr == 0) {
                    continue;
                }
                Square from = squareOrNull(target.file() + df, target.rank() + dr);
                if (from == null) {
                    continue;
                }
                Piece p = b.get(from);
                if (p != null && p.color() == byColor && p.kind() == Kind.KING) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean isSlidingAttacking(
            Board b, Square target, Color byColor, int[][] directions, Kind kindA, Kind kindB) {
        for (int[] dir : directions) {
            int file = target.file() + dir[0];
            int rank = target.rank() + dir[1];
            while (true) {
                Square sq = squareOrNull(file, rank);
                if (sq == null) {
                    break;
                }
                Piece p = b.get(sq);
                if (p != null) {
                    if (p.color() == byColor && (p.kind() == kindA || p.kind() == kindB)) {
                        return true;
                    }
                    break;
                }
                file += dir[0];
                rank += dir[1];
            }
        }
        return false;
    }

    private static Square squareOrNull(int file, int rank) {
        if (file < 0 || file > 7 || rank < 0 || rank > 7) {
            return null;
        }
        return new Square(file, rank);
    }
}
