package XXLChess.Piece;

import XXLChess.*;

import java.util.ArrayList;
import java.util.List;

/**
 * The Chancellor class represents the Chancellor piece in the game.
 * It extends the Piece class.
 */
public class Chancellor extends Piece {
    /**
     * Creates a new instance of the Chancellor piece.
     * @param app The application instance.
     * @param color The color of the piece (WHITE or BLACK).
     * @param x The initial x-coordinate of the piece on the board.
     * @param y The initial y-coordinate of the piece on the board.
     */
    public Chancellor(App app, PieceColor color, int x, int y) {
        super(app, color, PieceType.CHANCELLOR.getImagePath(color), x, y,8.5);
    }

    /**
     * Returns the type of the piece.
     * @return The type of the piece (CHANCELLOR).
     */
    @Override
    public PieceType getType() {
        return PieceType.CHANCELLOR;
    }

    /**
     * Returns a list of legal moves for the Chancellor piece at the given position on the board.
     * @param x The x-coordinate of the piece.
     * @param y The y-coordinate of the piece.
     * @param board The game board.
     * @return A list of legal moves as int arrays [startX, startY, endX, endY].
     */
    @Override
    public List<int[]> getLegalMoves(int x, int y, Board board) {

            List<int[]> legalMoves = new ArrayList<>();

            // Add knight moves
            legalMoves.addAll(getKnightMoves(x, y, board));

            // Add rook moves
            legalMoves.addAll(getMoveHorizontalAndVerticalLines(x, y, board));

            return legalMoves;

    }
}

