package chess;

import java.util.ArrayList;
import java.util.List;

public final class Rules {
    private Rules() {
    }

    static List<Move> pseudoLegalMoves(Board b, Square from, GameState s) {
        List<Move> moves = new ArrayList<>();
        Piece piece = b.get(from);
        if (piece == null) {
            return moves;
        }

        switch (piece.kind()) {
            case PAWN -> addPawnMoves(b, from, piece.color(), s, moves);
            case KNIGHT -> addKnightMoves(b, from, piece.color(), moves);
            case BISHOP -> addSlidingMoves(b, from, piece.color(), moves, new int[][]{
                {1, 1}, {1, -1}, {-1, 1}, {-1, -1}
            });
            case ROOK -> addSlidingMoves(b, from, piece.color(), moves, new int[][]{
                {1, 0}, {-1, 0}, {0, 1}, {0, -1}
            });
            case QUEEN -> addSlidingMoves(b, from, piece.color(), moves, new int[][]{
                {1, 1}, {1, -1}, {-1, 1}, {-1, -1}, {1, 0}, {-1, 0}, {0, 1}, {0, -1}
            });
            case KING -> addKingMoves(b, from, piece.color(), s, moves);
        }

        return moves;
    }

    static boolean isMoveLegal(Board b, GameState s, Move m) {
        if (m == null || m.from() == null || m.to() == null) {
            return false;
        }

        Piece movingPiece = b.get(m.from());
        if (movingPiece == null) {
            return false;
        }
        if (movingPiece.color() != s.turn()) {
            return false;
        }

        Piece destination = b.get(m.to());
        if (destination != null && destination.color() == movingPiece.color()) {
            return false;
        }

        List<Move> pseudo = pseudoLegalMoves(b, m.from(), s);
        if (!containsMove(pseudo, m)) {
            return false;
        }

        if (isCastlingAttempt(movingPiece, m)) {
            if (isInCheck(b, movingPiece.color(), s)) {
                return false;
            }
            Color attacker = GameState.opposite(movingPiece.color());
            for (Square sq : castlePathSquares(movingPiece.color(), m.to())) {
                if (Attack.isSquareAttacked(b, sq, attacker)) {
                    return false;
                }
            }
        }

        Board simulated = b.copy();
        GameState simState = s.copy();
        applyMove(simulated, simState, m);
        return !isInCheck(simulated, movingPiece.color(), simState);
    }

    static void applyMove(Board b, GameState s, Move m) {
        Piece movingPiece = b.get(m.from());
        Piece destinationBeforeMove = b.get(m.to());
        Square oldEnPassantTarget = s.enPassantTarget();
        s.setEnPassantTarget(null);

        if (movingPiece != null && movingPiece.kind() == Kind.PAWN) {
            int rankDelta = m.to().rank() - m.from().rank();
            if (m.from().file() == m.to().file() && Math.abs(rankDelta) == 2) {
                int midRank = (m.from().rank() + m.to().rank()) / 2;
                s.setEnPassantTarget(new Square(m.from().file(), midRank));
            }

            boolean diagonalMove = m.from().file() != m.to().file();
            if (diagonalMove && destinationBeforeMove == null && oldEnPassantTarget != null
                    && oldEnPassantTarget.equals(m.to())) {
                int dir = movingPiece.color() == Color.WHITE ? 1 : -1;
                int capturedRank = m.to().rank() - dir;
                Square capturedSq = new Square(m.to().file(), capturedRank);
                b.set(capturedSq, null);
            }
        }

        b.set(m.to(), movingPiece);
        b.set(m.from(), null);

        if (movingPiece != null && movingPiece.kind() == Kind.KING
                && Math.abs(m.to().file() - m.from().file()) == 2) {
            int rank = m.from().rank();
            if (m.to().file() == 6) {
                Square rookFrom = new Square(7, rank);
                Square rookTo = new Square(5, rank);
                Piece rook = b.get(rookFrom);
                b.set(rookTo, rook);
                b.set(rookFrom, null);
            } else if (m.to().file() == 2) {
                Square rookFrom = new Square(0, rank);
                Square rookTo = new Square(3, rank);
                Piece rook = b.get(rookFrom);
                b.set(rookTo, rook);
                b.set(rookFrom, null);
            }
        }

        if (movingPiece != null && movingPiece.kind() == Kind.KING) {
            if (movingPiece.color() == Color.WHITE) {
                s.setWK(false);
                s.setWQ(false);
            } else {
                s.setBK(false);
                s.setBQ(false);
            }
        }

        if (movingPiece != null && movingPiece.kind() == Kind.ROOK) {
            if (m.from().file() == 0 && m.from().rank() == 0) {
                s.setWQ(false);
            } else if (m.from().file() == 7 && m.from().rank() == 0) {
                s.setWK(false);
            } else if (m.from().file() == 0 && m.from().rank() == 7) {
                s.setBQ(false);
            } else if (m.from().file() == 7 && m.from().rank() == 7) {
                s.setBK(false);
            }
        }

        if (destinationBeforeMove != null && destinationBeforeMove.kind() == Kind.ROOK) {
            if (m.to().file() == 0 && m.to().rank() == 0) {
                s.setWQ(false);
            } else if (m.to().file() == 7 && m.to().rank() == 0) {
                s.setWK(false);
            } else if (m.to().file() == 0 && m.to().rank() == 7) {
                s.setBQ(false);
            } else if (m.to().file() == 7 && m.to().rank() == 7) {
                s.setBK(false);
            }
        }

        if (movingPiece != null && movingPiece.kind() == Kind.PAWN) {
            int promotionRank = movingPiece.color() == Color.WHITE ? 7 : 0;
            if (m.to().rank() == promotionRank) {
                b.set(m.to(), promotedPiece(movingPiece.color(), m.promotion()));
            }
        }

        s.setTurn(GameState.opposite(s.turn()));
    }

    static boolean isInCheck(Board b, Color color, GameState s) {
        Square kingSquare = b.findKing(color);
        if (kingSquare == null) {
            return false;
        }
        return Attack.isSquareAttacked(b, kingSquare, GameState.opposite(color));
    }

    static boolean hasAnyLegalMove(Board b, GameState s, Color side) {
        GameState probeBase = s.copy();
        probeBase.setTurn(side);
        for (PieceOnSquare pos : b.allPieces()) {
            if (pos.piece().color() != side) {
                continue;
            }
            List<Move> pseudo = pseudoLegalMoves(b, pos.square(), probeBase);
            for (Move m : pseudo) {
                Move probeMove = new Move(m.from(), m.to(), m.promotion(), false, false);
                if (isMoveLegal(b, probeBase, probeMove)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean containsMove(List<Move> moves, Move target) {
        for (Move move : moves) {
            if (move.from().equals(target.from()) && move.to().equals(target.to())) {
                return true;
            }
        }
        return false;
    }

    private static void addPawnMoves(Board b, Square from, Color color, GameState s, List<Move> out) {
        int dir = color == Color.WHITE ? 1 : -1;
        int startRank = color == Color.WHITE ? 1 : 6;

        Square oneForward = squareOrNull(from.file(), from.rank() + dir);
        if (oneForward != null && b.get(oneForward) == null) {
            out.add(new Move(from, oneForward, null, false, false));

            Square twoForward = squareOrNull(from.file(), from.rank() + (2 * dir));
            if (from.rank() == startRank && twoForward != null && b.get(twoForward) == null) {
                out.add(new Move(from, twoForward, null, false, false));
            }
        }

        Square diagLeft = squareOrNull(from.file() - 1, from.rank() + dir);
        if (diagLeft != null) {
            Piece target = b.get(diagLeft);
            if (target != null && target.color() != color) {
                out.add(new Move(from, diagLeft, null, false, false));
            }
        }

        Square diagRight = squareOrNull(from.file() + 1, from.rank() + dir);
        if (diagRight != null) {
            Piece target = b.get(diagRight);
            if (target != null && target.color() != color) {
                out.add(new Move(from, diagRight, null, false, false));
            }
        }

        Square ep = s.enPassantTarget();
        if (ep != null
                && ep.rank() == from.rank() + dir
                && Math.abs(ep.file() - from.file()) == 1
                && b.get(ep) == null) {
            out.add(new Move(from, ep, null, false, false));
        }
    }

    private static void addKnightMoves(Board b, Square from, Color color, List<Move> out) {
        int[][] deltas = {
            {1, 2}, {2, 1}, {2, -1}, {1, -2},
            {-1, -2}, {-2, -1}, {-2, 1}, {-1, 2}
        };
        for (int[] delta : deltas) {
            addIfAvailable(b, from, color, from.file() + delta[0], from.rank() + delta[1], out);
        }
    }

    private static void addKingMoves(Board b, Square from, Color color, GameState s, List<Move> out) {
        for (int df = -1; df <= 1; df++) {
            for (int dr = -1; dr <= 1; dr++) {
                if (df == 0 && dr == 0) {
                    continue;
                }
                addIfAvailable(b, from, color, from.file() + df, from.rank() + dr, out);
            }
        }

        if (color == Color.WHITE && from.file() == 4 && from.rank() == 0) {
            if (s.wK()
                    && b.get(new Square(5, 0)) == null
                    && b.get(new Square(6, 0)) == null) {
                out.add(new Move(from, new Square(6, 0), null, false, false));
            }
            if (s.wQ()
                    && b.get(new Square(3, 0)) == null
                    && b.get(new Square(2, 0)) == null
                    && b.get(new Square(1, 0)) == null) {
                out.add(new Move(from, new Square(2, 0), null, false, false));
            }
        } else if (color == Color.BLACK && from.file() == 4 && from.rank() == 7) {
            if (s.bK()
                    && b.get(new Square(5, 7)) == null
                    && b.get(new Square(6, 7)) == null) {
                out.add(new Move(from, new Square(6, 7), null, false, false));
            }
            if (s.bQ()
                    && b.get(new Square(3, 7)) == null
                    && b.get(new Square(2, 7)) == null
                    && b.get(new Square(1, 7)) == null) {
                out.add(new Move(from, new Square(2, 7), null, false, false));
            }
        }
    }

    private static void addSlidingMoves(Board b, Square from, Color color, List<Move> out, int[][] directions) {
        for (int[] dir : directions) {
            int file = from.file() + dir[0];
            int rank = from.rank() + dir[1];
            while (true) {
                Square target = squareOrNull(file, rank);
                if (target == null) {
                    break;
                }
                Piece occupant = b.get(target);
                if (occupant == null) {
                    out.add(new Move(from, target, null, false, false));
                } else {
                    if (occupant.color() != color) {
                        out.add(new Move(from, target, null, false, false));
                    }
                    break;
                }
                file += dir[0];
                rank += dir[1];
            }
        }
    }

    private static void addIfAvailable(Board b, Square from, Color color, int file, int rank, List<Move> out) {
        Square target = squareOrNull(file, rank);
        if (target == null) {
            return;
        }
        Piece occupant = b.get(target);
        if (occupant == null || occupant.color() != color) {
            out.add(new Move(from, target, null, false, false));
        }
    }

    private static Square squareOrNull(int file, int rank) {
        if (file < 0 || file > 7 || rank < 0 || rank > 7) {
            return null;
        }
        return new Square(file, rank);
    }

    private static Piece promotedPiece(Color color, Character promotionChar) {
        if (promotionChar == null) {
            return new Queen(color);
        }
        char p = Character.toUpperCase(promotionChar);
        return switch (p) {
            case 'R' -> new Rook(color);
            case 'B' -> new Bishop(color);
            case 'N' -> new Knight(color);
            case 'Q' -> new Queen(color);
            default -> new Queen(color);
        };
    }

    private static boolean isCastlingAttempt(Piece movingPiece, Move m) {
        if (movingPiece.kind() != Kind.KING) {
            return false;
        }
        int fromFile = m.from().file();
        int fromRank = m.from().rank();
        int toFile = m.to().file();
        int toRank = m.to().rank();
        return (fromFile == 4 && fromRank == 0 && ((toFile == 6 && toRank == 0) || (toFile == 2 && toRank == 0)))
                || (fromFile == 4 && fromRank == 7
                && ((toFile == 6 && toRank == 7) || (toFile == 2 && toRank == 7)));
    }

    private static Square[] castlePathSquares(Color color, Square to) {
        if (color == Color.WHITE && to.file() == 6 && to.rank() == 0) {
            return new Square[]{new Square(4, 0), new Square(5, 0), new Square(6, 0)};
        }
        if (color == Color.WHITE && to.file() == 2 && to.rank() == 0) {
            return new Square[]{new Square(4, 0), new Square(3, 0), new Square(2, 0)};
        }
        if (color == Color.BLACK && to.file() == 6 && to.rank() == 7) {
            return new Square[]{new Square(4, 7), new Square(5, 7), new Square(6, 7)};
        }
        return new Square[]{new Square(4, 7), new Square(3, 7), new Square(2, 7)};
    }
}
