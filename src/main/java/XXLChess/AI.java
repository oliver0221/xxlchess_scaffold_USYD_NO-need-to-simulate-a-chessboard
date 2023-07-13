//package XXLChess;
//import XXLChess.*;
//import XXLChess.Piece.Piece;
//
//import java.util.Comparator;
//import java.util.List;
//
//public class AI{
//    private static final int MAX_DEPTH = 3;
//    private PieceColor aiColor;
//    private Board board;
//
//    public AI(PieceColor aiColor, Board board) {
//        this.aiColor = aiColor;
//        this.board = board;
//    }
//
//
//    public static int[] getBestMove(Board board, PieceColor aiColor) {
//        double bestScore = -1;
//        int[] bestMove = new int[4];
//        PieceColor opponentColor = (aiColor == PieceColor.WHITE) ? PieceColor.BLACK : PieceColor.WHITE;
//
//        List<Piece> pieces = board.getPiecesByColor(aiColor);
//        pieces.sort(Comparator.comparing(Piece::getValue));  // Sort pieces by their values in ascending order
//
//        for (Piece piece : pieces) {
//            int startX = piece.getX();
//            int startY = piece.getY();
//            for (int[] move : piece.safeMove(piece.getLegalMoves(startX, startY,board),startX,startY, board))
//            {
//                int endX = move[0];
//                int endY = move[1];
//
//                // Perform the move
//                Piece originalPiece = board.getTileAt(endX, endY).getPiece();
//                board.movePieceNow(startX, startY, endX, endY);
//
//                // If the move captures an opponent piece of higher value, update bestMove and bestScore
//                if (isCapturingOpponent(piece, move, board)) {
//                    double score = originalPiece.getValue();
//                    if (score > bestScore) {
//                        bestScore = score;
//                        bestMove = new int[] {startX, startY, endX, endY};
//                    }
//                }
//                // If the move is safe and bestScore is still -1 (which means no capture move has been found),
//                // update bestMove to the current safe move
//
//
//                // Undo the move
//                board.movePieceNow(endX, endY, startX, startY);
//                if (originalPiece != null) {
//                    board.getTileAt(endX, endY).setPiece(originalPiece);
//                }
//            }
//        }
//
//        // If bestScore is still -1 after checking all pieces and their moves, no capture or safe move has been found.
//        // In this case, let the piece of the lowest value make a move (which may not be safe)
//        if (bestScore == -1) {
//            Piece lowestValuePiece = pieces.get(0);
//            int startX = lowestValuePiece.getX();
//            int startY = lowestValuePiece.getY();
//            int[] move = lowestValuePiece.getLegalMoves(startX, startY, board).get(0);
//            bestMove = new int[] {startX, startY, move[0], move[1]};
//        }
//
//        return bestMove;
//    }
//
//    private static boolean isCapturingOpponent(Piece piece, int[] move, Board board) {
//        int endX = move[0];
//        int endY = move[1];
//        Piece targetPiece = board.getTileAt(endX, endY).getPiece();
//        if (targetPiece != null && targetPiece.getColor() != piece.getColor()) {
//            return piece.getValue() < targetPiece.getValue();
//        }
//        return false;
//    }
////    private static boolean isMoveSafe(Piece piece, int[] move, Board board, PieceColor opponentColor) {
////        int endX = move[0];
////        int endY = move[1];
////        Piece originalPiece = board.getTileAt(endX, endY).getPiece();
////        // Perform the move
////        board.movePieceNow(piece.getX(), piece.getY(), endX, endY);
////        for (Piece opponentPiece : board.getPiecesByColor(opponentColor)) {
////            for (int[] opponentMove : opponentPiece.getLegalMoves(opponentPiece.getX(), opponentPiece.getY(), board)) {
////                if (opponentMove[0] == endX && opponentMove[1] == endY) {
////                    // Undo the move
////                    board.movePieceNow(endX, endY, piece.getX(), piece.getY());
////                    if (originalPiece != null) {
////                        board.getTileAt(endX, endY).setPiece(originalPiece);
////                    }
////                    return false;
////                }
////            }
////        }
////        // Undo the move
////        board.movePieceNow(endX, endY, piece.getX(), piece.getY());
////        if (originalPiece != null) {
////            board.getTileAt(endX, endY).setPiece(originalPiece);
////        }
////        return true;
////    }
//
//
//
//
//}

package XXLChess;

import XXLChess.Board;
import XXLChess.Piece.Piece;

import java.util.*;

/**
 * The AI class represents an artificial intelligence player in the XXLChess game.
 * It is responsible for making intelligent moves for the computer player.
 */
public class AI {
    private PieceColor color;
    private PieceColor opponentColor;

    /**
     * Constructs an AI object with the specified color.
     * @param color The color of the AI player.
     */
    public AI(PieceColor color) {
        this.color = color;
        this.opponentColor = (color == PieceColor.WHITE) ? PieceColor.BLACK : PieceColor.WHITE;
    }

    /**
     * Generates and returns a move for the AI player based on the current board state.
     * The AI evaluates available moves and selects the best move to play against the opponent.
     * The move is determined by considering safe moves, capture moves, and any legal move if no safe or capture moves are available.
     * @param board The current game board.
     * @return An array representing the move [startX, startY, endX, endY].
     */
    public int[] getMove(Board board) {
        List<Piece> pieces = board.getPiecesByColor(color);
        List<int[]> safeMoves = new ArrayList<>();
        List<int[]> captureMoves = new ArrayList<>();
        List<int[]> allMoves = new ArrayList<>();

        for (Piece piece : pieces) {
            List<int[]> legalMoves = piece.getLegalMoves(piece.getX(), piece.getY(), board);
            for (int[] move : legalMoves) {
                Piece targetPiece = board.getTileAt(move[0], move[1]).getPiece();
                if (targetPiece != null && targetPiece.getColor() == opponentColor) {
                    captureMoves.add(new int[]{piece.getX(), piece.getY(), move[0], move[1]});
                }
                allMoves.add(new int[]{piece.getX(), piece.getY(), move[0], move[1]});
            }
        }

        for (int[] move : allMoves) {
            Piece piece = board.getTileAt(move[0], move[1]).getPiece();
            List<int[]> moves = piece.getLegalMoves(piece.getX(), piece.getY(), board);
            List<int[]> safeMoveList = piece.safeMove(moves, move[0], move[1], board);
            for (int[] safeMove : safeMoveList) {
                safeMoves.add(new int[]{piece.getX(), piece.getY(), safeMove[0], safeMove[1]});
            }
        }

        if (!safeMoves.isEmpty()) {
            int[] bestMove = null;
            double bestScore = -1;// Prioritize capture moves that are also safe
            for (int[] move : captureMoves) {
                for (int[] safeMove : safeMoves) {
                    if (Arrays.equals(move, safeMove)) {
                        //return move;
                        Piece targetPiece = board.getTileAt(move[2], move[3]).getPiece();
                        if (targetPiece != null && targetPiece.getColor() != color && targetPiece.getValue() > bestScore) {
                            bestMove = move;
                            bestScore = targetPiece.getValue();

                        }
                    }
                }
            }
            // If no safe capture moves, make a safe move
            if (bestMove != null) {
                return bestMove;
            }
            return selectRandomMove(safeMoves);
        }

        // If no safe moves, make a capture move
        if (!captureMoves.isEmpty()) {
            return selectRandomMove(captureMoves);
        }

        // If no capture moves, make any legal move
        return selectRandomMove(allMoves);
    }

    /**
     * Selects a random move from the given list of moves.
     * @param moves The list of moves to choose from.
     * @return An array representing the selected move [startX, startY, endX, endY].
     */
    private int[] selectRandomMove(List<int[]> moves) {
        Random random = new Random();
        int index = random.nextInt(moves.size());
        return moves.get(index);
    }
}

