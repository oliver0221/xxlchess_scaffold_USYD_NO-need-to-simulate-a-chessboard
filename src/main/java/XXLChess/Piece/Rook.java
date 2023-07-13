package XXLChess.Piece;

import XXLChess.*;

import java.util.List;

/**
 * The Rook class represents a rook chess piece.
 * It extends the Piece class and provides methods for retrieving the type of the piece and its legal moves.
 */
public class Rook extends Piece {
    /**
     * Constructs a Rook object with the specified attributes.
     * @param app the main application instance
     * @param color the color of the piece (PieceColor.WHITE or PieceColor.BLACK)
     * @param x the initial x-coordinate of the piece on the chessboard
     * @param y the initial y-coordinate of the piece on the chessboard
     */
    public Rook(App app, PieceColor color, int x, int y) {
        super(app, color, PieceType.ROOK.getImagePath(color), x, y,5.25);
    }

    /**
     * Retrieves the type of the rook piece.
     * @return the type of the piece (PieceType.ROOK)
     */
    @Override
    public PieceType getType() {
        return PieceType.ROOK;
    }

    /**
     * Retrieves a list of legal moves for the rook piece at the specified position on the chessboard.
     * The legal moves include horizontal and vertical moves.
     * @param x the x-coordinate of the piece's position
     * @param y the y-coordinate of the piece's position
     * @param board the chessboard on which the piece is located
     * @return a list of integer arrays representing the legal moves for the rook
     */
    @Override
    public List<int[]> getLegalMoves(int x, int y, Board board) {
        return getMoveHorizontalAndVerticalLines(x, y, board);
    }
}