package XXLChess.Piece;

import XXLChess.*;

import java.util.List;

/**
 * Represents a Knight chess piece.
 * This class extends the abstract Piece class and implements the getLegalMoves method to retrieve the legal moves
 * for the Knight at a given position on the board.
 */
public class Knight extends Piece {
    /**
     * Constructs a Knight chess piece.
     * @param app The application object.
     * @param color The color of the piece (WHITE or BLACK).
     * @param x The x-coordinate of the piece on the board.
     * @param y The y-coordinate of the piece on the board.
     */
    public Knight(App app, PieceColor color, int x, int y) {
        super(app, color, PieceType.KNIGHT.getImagePath(color), x, y,2);
    }

    /**
     * Returns the type of the piece, which is PieceType.KNIGHT.
     * @return The type of the piece.
     */
    @Override
    public PieceType getType() {
        return PieceType.KNIGHT;
    }
    /**
     * Retrieves the legal moves for the Knight at the specified position.
     * @param x The x-coordinate of the Knight's position.
     * @param y The y-coordinate of the Knight's position.
     * @param board The game board.
     * @return A list of legal moves as int arrays, where each array contains the x and y coordinates of the target position.
     */
    @Override
    public List<int[]> getLegalMoves(int x, int y, Board board) {
        return getKnightMoves(x, y, board);
    }
}

