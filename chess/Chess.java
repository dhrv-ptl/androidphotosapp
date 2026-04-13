// Dhruv Patel
// Rushi Patel
package chess;

import java.util.ArrayList;

public class Chess {

        enum Player { white, black }
	private static Board board;
	private static GameState state;
    
	/**
	 * Plays the next move for whichever player has the turn.
	 * 
	 * @param move String for next move, e.g. "a2 a3"
	 * 
	 * @return A ReturnPlay instance that contains the result of the move.
	 *         See the section "The Chess class" in the assignment description for details of
	 *         the contents of the returned ReturnPlay instance.
	 */
	public static ReturnPlay play(String move) {
		if (board == null || state == null) {
			start();
		}

		String trimmed = move.trim();
		if (trimmed.equals("resign")) {
			ReturnPlay.Message resignMessage =
					state.turn() == Color.WHITE
							? ReturnPlay.Message.RESIGN_BLACK_WINS
							: ReturnPlay.Message.RESIGN_WHITE_WINS;
			return makeReturnPlay(resignMessage);
		}

		String[] tokens = trimmed.split("\\s+");
		Square from = Square.parse(tokens[0]);
		Square to = Square.parse(tokens[1]);
		Character promotion = null;
		boolean offeredDraw = false;
		for (int i = 2; i < tokens.length; i++) {
			if ("draw?".equals(tokens[i])) {
				offeredDraw = true;
			} else if (!tokens[i].isEmpty()) {
				promotion = tokens[i].charAt(0);
			}
		}

		Move moveObj = new Move(from, to, promotion, offeredDraw, false);
		if (!Rules.isMoveLegal(board, state, moveObj)) {
			return makeReturnPlay(ReturnPlay.Message.ILLEGAL_MOVE);
		}

		Rules.applyMove(board, state, moveObj);
		if (offeredDraw) {
			return makeReturnPlay(ReturnPlay.Message.DRAW);
		}
		Color opponent = state.turn();
		boolean oppInCheck = Rules.isInCheck(board, opponent, state);
		if (oppInCheck) {
			if (!Rules.hasAnyLegalMove(board, state, opponent)) {
				ReturnPlay.Message mate =
						(opponent == Color.WHITE)
								? ReturnPlay.Message.CHECKMATE_BLACK_WINS
								: ReturnPlay.Message.CHECKMATE_WHITE_WINS;
				return makeReturnPlay(mate);
			}
			return makeReturnPlay(ReturnPlay.Message.CHECK);
		}
		return makeReturnPlay(null);
	}
	
	
	/**
	 * This method should reset the game, and start from scratch.
	 */
	public static void start() {
		board = new Board();

		board.set(Square.parse("a1"), new Rook(Color.WHITE));
		board.set(Square.parse("b1"), new Knight(Color.WHITE));
		board.set(Square.parse("c1"), new Bishop(Color.WHITE));
		board.set(Square.parse("d1"), new Queen(Color.WHITE));
		board.set(Square.parse("e1"), new King(Color.WHITE));
		board.set(Square.parse("f1"), new Bishop(Color.WHITE));
		board.set(Square.parse("g1"), new Knight(Color.WHITE));
		board.set(Square.parse("h1"), new Rook(Color.WHITE));
		for (char file = 'a'; file <= 'h'; file++) {
			board.set(Square.parse(file + "2"), new Pawn(Color.WHITE));
		}

		board.set(Square.parse("a8"), new Rook(Color.BLACK));
		board.set(Square.parse("b8"), new Knight(Color.BLACK));
		board.set(Square.parse("c8"), new Bishop(Color.BLACK));
		board.set(Square.parse("d8"), new Queen(Color.BLACK));
		board.set(Square.parse("e8"), new King(Color.BLACK));
		board.set(Square.parse("f8"), new Bishop(Color.BLACK));
		board.set(Square.parse("g8"), new Knight(Color.BLACK));
		board.set(Square.parse("h8"), new Rook(Color.BLACK));
		for (char file = 'a'; file <= 'h'; file++) {
			board.set(Square.parse(file + "7"), new Pawn(Color.BLACK));
		}

		state = new GameState(Color.WHITE, true, true, true, true, null);
	}

	private static ReturnPlay makeReturnPlay(ReturnPlay.Message message) {
		ReturnPlay result = new ReturnPlay();
		result.piecesOnBoard = exportPieces(board);
		result.message = message;
		return result;
	}

	private static ArrayList<ReturnPiece> exportPieces(Board b) {
		ArrayList<ReturnPiece> out = new ArrayList<>();
		for (PieceOnSquare pos : b.allPieces()) {
			ReturnPiece rp = new ReturnPiece();
			rp.pieceFile = ReturnPiece.PieceFile.valueOf(
					String.valueOf((char) ('a' + pos.square().file())));
			rp.pieceRank = pos.square().rank() + 1;
			rp.pieceType = mapPieceType(pos.piece());
			out.add(rp);
		}
		return out;
	}

	private static ReturnPiece.PieceType mapPieceType(Piece piece) {
		if (piece.color() == Color.WHITE) {
			return switch (piece.kind()) {
				case PAWN -> ReturnPiece.PieceType.WP;
				case ROOK -> ReturnPiece.PieceType.WR;
				case KNIGHT -> ReturnPiece.PieceType.WN;
				case BISHOP -> ReturnPiece.PieceType.WB;
				case QUEEN -> ReturnPiece.PieceType.WQ;
				case KING -> ReturnPiece.PieceType.WK;
			};
		}
		return switch (piece.kind()) {
			case PAWN -> ReturnPiece.PieceType.BP;
			case ROOK -> ReturnPiece.PieceType.BR;
			case KNIGHT -> ReturnPiece.PieceType.BN;
			case BISHOP -> ReturnPiece.PieceType.BB;
			case QUEEN -> ReturnPiece.PieceType.BQ;
			case KING -> ReturnPiece.PieceType.BK;
		};
	}
}
