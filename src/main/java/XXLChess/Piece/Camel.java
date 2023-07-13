package XXLChess.Piece;

import XXLChess.*;

import java.util.List;

/**
 * The Camel class represents the Camel piece in the game.
 * It extends the Piece class.
 */
public class Camel extends Piece {
    /**
     * Creates a new instance of the Camel piece.
     * @param app The application instance.
     * @param color The color of the piece (WHITE or BLACK).
     * @param x The initial x-coordinate of the piece on the board.
     * @param y The initial y-coordinate of the piece on the board.
     */
    public Camel(App app, PieceColor color, int x, int y) {
        super(app, color, PieceType.CAMEL.getImagePath(color), x, y,2);
    }

    /**
     * Returns the type of the piece.
     * @return The type of the piece (CAMEL).
     */
    @Override
    public PieceType getType() {
        return PieceType.CAMEL;
    }
    /**
     * Returns a list of legal moves for the Camel piece at the given position on the board.
     * @param x The x-coordinate of the piece.
     * @param y The y-coordinate of the piece.
     * @param board The game board.
     * @return A list of legal moves as int arrays [startX, startY, endX, endY].
     */
    @Override
    public List<int[]> getLegalMoves(int x, int y, Board board) {
        return getCamelMoves(x, y, board);
    }
}

