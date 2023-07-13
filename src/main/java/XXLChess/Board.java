package XXLChess;

import XXLChess.Piece.King;
import XXLChess.Piece.Pawn;
import XXLChess.Piece.Piece;
import XXLChess.Piece.Queen;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static XXLChess.App.*;

/**
 * The Board class represents the chessboard in the XXLChess game.
 * It maintains the state of the chessboard and provides methods for manipulating the tiles and pieces on the board.
 */
public class Board {
    private final int tileSize = CELLSIZE;
    private final int boardSize = BOARD_WIDTH ;
    private final int sidebarWidth = SIDEBAR;
    private final int windowWidth = tileSize * boardSize + sidebarWidth;
    private final int windowHeight = tileSize * boardSize;
    private int[] lastMovedFrom = null;
    private int[] lastMovedTo = null;

    private List<Tile> highlightedTiles = new ArrayList<>();

    private Tile[][] tiles;
    private App app;
    private Piece movingPiece;
    private int[] startPosition;
    private int[] targetPosition;
    private double progress;
    private double pieceMovementSpeed;
    private double maxMovementTime;
    private Piece movingRook = null;
    private int[] rookStartPosition = null;
    private int[] rookTargetPosition = null;
    private double rookProgress = 0;
    //public boolean checkState = true;
    /**
     * Constructs a new Board object with the specified App instance.
     * The Board represents the game board consisting of tiles.
     * @param app The App instance associated with the Board.
     * @see App
     * @see Tile
     * @see #initializeTiles()
     */
    public Board(App app) {
        this.app = app;
        this.tiles = new Tile[boardSize][boardSize];
        initializeTiles();
    }

    /**
     * Sets the movement parameters for the chess pieces on the board.
     * This method allows specifying the piece movement speed and the maximum movement time.
     * @param pieceMovementSpeed The speed at which the chess pieces can move.
     * @param maxMovementTime The maximum time allowed for a piece to complete its movement.
     * @see #pieceMovementSpeed
     * @see #maxMovementTime
     */
    public void setMovementParameters(double pieceMovementSpeed, double maxMovementTime) {
        this.pieceMovementSpeed = pieceMovementSpeed;
        this.maxMovementTime = maxMovementTime;
    }

    /**
     * Initializes the tiles on the board.
     * This method creates and assigns Tile objects to each position on the board based on the board size.
     * Each tile is positioned at the appropriate coordinates and is assigned the correct color (black or white).
     * @see Tile
     * @see #boardSize
     * @see #tiles
     * @return None
     */
    private void initializeTiles() {
        for (int x = 0; x < boardSize; x++) {
            for (int y = 0; y < boardSize; y++) {
                boolean isBlack = (x + y) % 2 == 1;
                tiles[x][y] = new Tile(app, x * tileSize, y * tileSize, tileSize, isBlack);
            }
        }
    }

    /**
     * Retrieves the size of the board.
     * This method returns the number of tiles in each row/column of the board.
     * @see #boardSize
     * @return The size of the board.
     */
    public int getBoardSize() {
        return boardSize;
    }

    /**
     * Draws the game board and its components on the screen.
     * This method is responsible for rendering the tiles, pieces, and highlighting specific elements on the board.
     * It iterates over each tile on the board and performs the following actions:
     * Calls the draw() method of each tile to render it on the screen.
     * Retrieves the piece on the current tile.
     * If the flashState is true and the piece is an instance of King and is in check,
     * it draws a red highlight on the tile to indicate the king is in check.
     * If the lastMovedFrom and lastMovedTo coordinates are not null,
     * it draws a highlight on the tiles indicating the last moved piece's source and destination.
     * @see Tile#draw()
     * @see Tile#getPiece()
     * @see Piece
     * @see King
     * @see Tile#drawHighlight(int, int, int)
     * @see App#flashState
     * @see App#checkState
     * @see #lastMovedFrom
     * @see #lastMovedTo
     */
    public void draw() {
        app.checkState = false; // Reset checkState at the start of each frame
        for (int x = 0; x < boardSize; x++) {
            for (int y = 0; y < boardSize; y++) {
                tiles[x][y].draw();
                Piece piece = tiles[x][y].getPiece();
                if (app.flashState && piece instanceof King && ((King) piece).isInCheck(this)) {
             // Draw highlight in red if the king is in check
                    tiles[x][y].drawHighlight(255, 0, 0);
                    app.checkState = true; // Set checkState to true if a king is in check
                }
            }
        }
        if (lastMovedFrom != null && lastMovedTo != null) {
            tiles[lastMovedFrom[0]][lastMovedFrom[1]].drawHighlight(238, 229, 109);
            tiles[lastMovedTo[0]][lastMovedTo[1]].drawHighlight(238, 229, 109);
        }
    }

    /**
     * Retrieves the tile at the specified coordinates on the game board.
     * This method allows accessing a specific tile on the board based on its x and y coordinates.
     * @param x The x-coordinate of the tile.
     * @param y The y-coordinate of the tile.
     * @return The Tile object at the specified coordinates if they are within the valid range, or null otherwise.
     * @see Tile
     * @see #boardSize
     */
    public Tile getTileAt(int x, int y) {
        if (x >= 0 && x < boardSize && y >= 0 && y < boardSize) {
            return tiles[x][y];
        }
        return null;
    }

    /**
     * Checks if the specified tile on the game board is empty.
     * This method determines whether a tile at the given coordinates does not contain a piece.
     * @param x The x-coordinate of the tile.
     * @param y The y-coordinate of the tile.
     * @return true if the tile is empty (contains no piece), false otherwise.
     * @see Tile
     * @see Tile#getPiece()
     * @see #getTileAt(int, int)
     */
    public boolean isTileEmpty(int x, int y) {
        Tile tile = getTileAt(x, y);
        return tile != null && tile.getPiece() == null;
    }

    /**
     * Moves a chess piece from one tile to another on the game board.
     * This method facilitates the movement of a chess piece by updating its position and managing related actions.
     * It takes the starting coordinates (fromX, fromY) and the target coordinates (toX, toY) as parameters.
     * The method performs the following actions:
     * Retrieves the piece on the starting tile and assigns it to the movingPiece variable.
     * If a valid piece is found:
     * Removes the piece from the starting tile by setting it to null.
     * Updates the piece's position to the target coordinates.
     * Tracks the last moved from and to coordinates for highlighting purposes.
     * Sets the moved flag of the piece to true.
     * If the movingPiece is an instance of King and the move involves castling:
     * Determines the positions of the rook before and after the castling move based on the target coordinates.
     * Retrieves the rook piece and sets its moved flag to true.
     * Stores the starting and target positions of the rook for animation.
     * Removes the rook from the original position by setting it to null.
     * Updates the rook's position to the target coordinates.
     * Calculates the starting and target pixel positions for animation based on the tile coordinates and tile size.
     * Initializes the progress and rookProgress variables for animation.
     * Adjusts the piece movement speed based on the distance and the maximum movement time allowed.
     * @param fromX The x-coordinate of the starting tile.
     * @param fromY The y-coordinate of the starting tile.
     * @param toX The x-coordinate of the target tile.
     * @param toY The y-coordinate of the target tile.
     * @see Tile#getPiece()
     * @see Tile#setPiece(Piece)
     * @see Piece#setX(int)
     * @see Piece#setY(int)
     * @see Piece#setMoved(boolean)
     * @see #movingPiece
     * @see #movingRook
     * @see #lastMovedFrom
     * @see #lastMovedTo
     * @see #rookStartPosition
     * @see #rookTargetPosition
     * @see #startPosition
     * @see #targetPosition
     * @see #progress
     * @see #rookProgress
     * @see #pieceMovementSpeed
     * @see #maxMovementTime
     */
    public void movePiece(int fromX, int fromY, int toX, int toY) {
        movingPiece = tiles[fromX][fromY].getPiece();
        if (movingPiece != null) {
            tiles[fromX][fromY].setPiece(null);
            movingPiece.setX(toX);
            movingPiece.setY(toY);
            lastMovedFrom = new int[]{fromX, fromY};
            lastMovedTo = new int[]{toX, toY};
            movingPiece.setMoved(true);
        }
        if (movingPiece instanceof King && Math.abs(toX - fromX) == 4) {
            int rookOldX, rookNewX;
            if (toX > fromX) {
                // Kingside castling
                rookOldX = 13;
                rookNewX = 10;
            } else {
                // Queenside castling
                rookOldX = 0;
                rookNewX = 4;
            }
            movingRook = getTileAt(rookOldX, toY).getPiece();
            movingRook.setMoved(true);
            rookStartPosition = new int[]{rookOldX * tileSize, toY * tileSize};
            rookTargetPosition = new int[]{rookNewX * tileSize, toY * tileSize};
            rookProgress = 0;
            tiles[rookOldX][toY].setPiece(null);
            movingRook.setX(rookNewX);
            movingRook.setY(toY);
        }
        int fromXPixel = fromX * tileSize;
        int fromYPixel = fromY * tileSize;
        int toXPixel = toX * tileSize;
        int toYPixel = toY * tileSize;
        startPosition = new int[]{fromXPixel, fromYPixel};
        targetPosition = new int[]{toXPixel, toYPixel};
        progress = 0;
        double distance = Math.sqrt(Math.pow(toXPixel - fromXPixel, 2) + Math.pow(toYPixel - fromYPixel, 2));
        double speed = pieceMovementSpeed;
        if (distance / speed > maxMovementTime * app.frameRate) {
            speed = distance / (maxMovementTime * app.frameRate);
        }
        pieceMovementSpeed = speed;

    }

    /**
     * Immediately moves a chess piece from one tile to another on the game board without any animation or checks.
     * This method facilitates the instantaneous movement of a chess piece by updating its position on the board.
     * It takes the starting coordinates (fromX, fromY) and the target coordinates (toX, toY) as parameters.
     * The method performs the following actions:
     * Retrieves the piece on the starting tile and assigns it to the newpiece variable.
     * If a valid piece is found:
     * Sets the newpiece on the target tile.
     * Removes the piece from the starting tile by setting it to null.
     * Updates the piece's position to the target coordinates.
     * Note: This method does not handle any animations, legality checks, or additional actions related to the movement.
     * It is primarily used for immediate piece relocation without any intermediate steps.
     * @param fromX The x-coordinate of the starting tile.
     * @param fromY The y-coordinate of the starting tile.
     * @param toX The x-coordinate of the target tile.
     * @param toY The y-coordinate of the target tile.
     * @see Tile#getPiece()
     * @see Tile#setPiece(Piece)
     * @see Piece#setX(int)
     * @see Piece#setY(int)
     */
    public void movePieceNow(int fromX,int fromY,int toX,int toY){
        Piece newpiece = tiles[fromX][fromY].getPiece();
        if(newpiece != null) {
            tiles[toX][toY].setPiece(newpiece);
            tiles[fromX][fromY].setPiece(null);
            newpiece.setX(toX);
            newpiece.setY(toY);
        }
    }

    /**
     * Updates the movement of the currently moving chess piece on the game board.
     * This method is responsible for updating the position and progress of the moving piece during animation.
     * It adjusts the position of the moving piece based on the piece movement speed and handles the completion of movement.
     * The method performs the following actions:
     * If a piece is currently in the process of moving:
     * Updates the progress of the movement by incrementing it with the piece movement speed.
     * Calculates the distance between the start position and the target position of the movement.
     * If the progress exceeds or equals the distance:
     * Sets the moving piece on the target tile.
     * Removes the moving piece from the start tile.
     * If the moving piece is a Pawn and it has crossed the middle row of the board, upgrades it to a Queen.
     * Resets the moving piece-related variables.
     * If a rook is currently in the process of moving:
     * Updates the progress of the rook movement by incrementing it with the piece movement speed.
     * Calculates the distance between the starting and target positions of the rook.
     * If the rook progress exceeds or equals the rook distance:
     * Sets the moving rook on the target tile.
     * Resets the moving rook-related variables.
     * @see Tile#setPiece(Piece)
     * @see Tile#getPiece()
     * @see Pawn
     * @see #pieceMovementSpeed
     * @see #progress
     * @see #targetPosition
     * @see #startPosition
     * @see #movingPiece
     * @see #movingRook
     * @see #rookStartPosition
     * @see #rookTargetPosition
     * @see #rookProgress
     * @see #upgradePawnToQueen(App, int, int, PieceColor)
     */
    public void updateMovingPiece() {
        if (movingPiece != null) {
            progress += pieceMovementSpeed;
            double distance = Math.sqrt(Math.pow(targetPosition[0] - startPosition[0], 2) + Math.pow(targetPosition[1] - startPosition[1], 2));
            if (progress >= distance) {
                tiles[targetPosition[0]/tileSize][targetPosition[1]/tileSize].setPiece(movingPiece);
                tiles[startPosition[0]/tileSize][startPosition[1]/tileSize].setPiece(null);
                if (movingPiece instanceof Pawn) {
                    // Calculate the middle row of the board
                    int middleRow = boardSize / 2;
                    // Check if the Pawn has crossed the middle row, depending on its color
                    if ((movingPiece.getColor() == PieceColor.WHITE && targetPosition[1] / tileSize < middleRow) ||
                            (movingPiece.getColor() == PieceColor.BLACK && targetPosition[1] / tileSize >= middleRow)) {
                        // If the Pawn has crossed the middle row, upgrade it to a Queen
                        upgradePawnToQueen(app, targetPosition[0] / tileSize, targetPosition[1] / tileSize, movingPiece.getColor());
                    }
                }
                    movingPiece = null;
                    startPosition = null;
                    targetPosition = null;
                    progress = 0;
            }
        }
        // Rook movement logic moved here
        if (movingRook != null) {
            rookProgress += pieceMovementSpeed;
            double rookDistance = Math.sqrt(Math.pow(rookTargetPosition[0] - rookStartPosition[0], 2) + Math.pow(rookTargetPosition[1] - rookStartPosition[1], 2));
            if (rookProgress >= rookDistance) {
                tiles[rookTargetPosition[0]/tileSize][rookTargetPosition[1]/tileSize].setPiece(movingRook);
                movingRook = null;
                rookStartPosition = null;
                rookTargetPosition = null;
                rookProgress = 0;
            }
        }
    }

    /**
     * Draws the currently moving chess piece on the game board during animation.
     * This method is responsible for rendering the moving piece at its intermediate positions during the animation.
     * It calculates the ratio of the progress and updates the position of the moving piece accordingly.
     * If a piece is currently in the process of moving, the method performs the following actions:
     * If the moving piece is also a rook, it calls the calculateRatio() method to calculate the ratio of the rook's progress.
     * It calls the calculateRatio() method to calculate the ratio of the progress of the moving piece.
     * @see #calculateRatio(double, int[], int[], Piece)
     * @see #movingPiece
     * @see #movingRook
     * @see #progress
     * @see #targetPosition
     * @see #startPosition
     */
    public void drawMovingPiece() {
        if (movingPiece != null) {
            if (movingRook != null) {
                calculateRatio(rookProgress, rookTargetPosition, rookStartPosition, movingRook);
            }
            calculateRatio(progress, targetPosition, startPosition, movingPiece);
        }
    }

    /**
     * Calculates the ratio of the progress for a moving rook piece and updates its position accordingly.
     * This method is used in the drawMovingPiece() method to calculate the intermediate position of the moving rook during animation.
     * It uses the ratio to determine the x and y coordinates of the rook's current position based on its progress and the target and start positions.
     * Finally, it calls the draw() method of the moving rook to render it at the calculated position on the game board.
     * @param rookProgress The progress of the rook movement.
     * @param rookTargetPosition The target position of the rook movement.
     * @param rookStartPosition The start position of the rook movement.
     * @param movingRook The rook piece that is currently moving.
     * @see Piece#draw(int, int, int)
     * @see #tileSize
     * @return None
     */
    private void calculateRatio(double rookProgress, int[] rookTargetPosition, int[] rookStartPosition, Piece movingRook) {
        double ratio = rookProgress / Math.sqrt(Math.pow(rookTargetPosition[0] - rookStartPosition[0], 2) + Math.pow(rookTargetPosition[1] - rookStartPosition[1], 2));
        int x = (int) (rookStartPosition[0] + (rookTargetPosition[0] - rookStartPosition[0]) * ratio);
        int y = (int) (rookStartPosition[1] + (rookTargetPosition[1] - rookStartPosition[1]) * ratio);
        movingRook.draw(x, y, tileSize);
    }

    /**
     * Highlights the legal moves of a given chess piece on the game board.
     * This method is responsible for drawing visual highlights on the tiles that represent the legal moves of the piece.
     * It takes the piece and its list of legal moves as parameters and applies the appropriate highlight color based on the type of move.
     * The method performs the following actions:
     * If the list of legal moves is not null:
     * Iterates over each move in the list.
     * Retrieves the target tile of the move.
     * If the target tile does not have a piece:
     * Draws a blue highlight on the target tile to indicate a valid move.
     * If the target tile has a piece of a different color:
     * Draws a red highlight on the target tile to indicate a capture move.
     * @param piece The chess piece for which to highlight the legal moves.
     * @param legalMoves The list of legal moves for the given piece.
     * @see Tile#drawHighlight(int, int, int)
     * @see Piece#getColor()
     */
    public void highlightLegalMoves(Piece piece, List<int[]> legalMoves) {
        if (legalMoves != null) {
            for (int[] move : legalMoves) {
            Tile targetTile = tiles[move[0]][move[1]];
                if (targetTile.getPiece() == null) {
                    targetTile.drawHighlight(196,224,232);
                } else if (targetTile.getPiece().getColor() != piece.getColor()) {
                    targetTile.drawHighlight(255, 164, 102);
                }
            }
        }
    }

    /**
     * Removes the highlights from all the tiles on the game board.
     * This method is responsible for resetting the highlight state of all tiles to false, effectively removing any existing highlights.
     * The method performs the following actions:
     * Iterates over each row of tiles on the game board.
     * For each tile in the row, sets the highlight state to false.
     * @see Tile#isHighlight
     */
    public void deHighlightTiles()
    {
        for(Tile[] row: tiles)
        {
            for(Tile tile :row)
            {
                tile.isHighlight = false;
            }
        }
    }

    /**
     * Sets up the chessboard based on the layout specified in a layout file.
     * This method reads the layout file line by line and assigns the appropriate chess pieces to the corresponding tiles on the game board.
     * The method performs the following actions:
     * Attempts to open and read the layout file specified by the filename.
     * Reads each line of the layout file and processes it.
     * For each character in the line:
     * Checks if the character represents a chess piece.
     * Determines the color and type of the piece based on the character's case and symbol.
     * Creates a new piece object using the specified color, type, column, and row.
     * Retrieves the corresponding tile at the specified column and row.
     * Sets the created piece on the tile.
     * @param layoutFilename The filename of the layout file containing the chessboard configuration.
     * @see BufferedReader
     * @see FileReader
     * @see PieceColor
     * @see PieceType
     * @see Piece#createPiece(App, PieceType, PieceColor, int, int)
     * @see Tile#setPiece(Piece)
     */
    public void setupBoard(String layoutFilename) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(layoutFilename));
            String line;
            int row = 0;

            while ((line = reader.readLine()) != null) {
                for (int col = 0; col < line.length(); col++) {
                    char pieceChar = line.charAt(col);
                    PieceColor color;
                    PieceType type;
                    if(pieceChar == ' ')
                        continue;
                    if (Character.isUpperCase(pieceChar)) {
                        color = PieceColor.BLACK;
                    } else {
                        color = PieceColor.WHITE;
                        pieceChar = Character.toUpperCase(pieceChar);
                    }

                    type = PieceType.fromSymbol(String.valueOf(pieceChar));
                    if (type != null) {
                        Piece piece = Piece.createPiece(app, type, color, col, row);
                        Tile tile = getTileAt(col, row);
                        tile.setPiece(piece);
                    }
                }
                row++;
            }

            reader.close();
        } catch (FileNotFoundException e) {
            System.err.println("Layout file not found: " + layoutFilename);
        } catch (IOException e) {
            System.err.println("Error reading layout file: " + layoutFilename);
        }
    }

    /**
     * Checks if a specific tile on the game board is occupied by an opponent's piece.
     * This method determines if the specified tile contains a piece and if that piece belongs to the opponent of the current player.
     * The method performs the following actions:
     * Retrieves the tile at the specified coordinates (x, y).
     * Retrieves the piece on the tile.
     * Returns true if the piece exists and its color is not the same as the current player's color, indicating it belongs to the opponent.
     * Returns false otherwise.
     * @param x The x-coordinate of the tile on the game board.
     * @param y The y-coordinate of the tile on the game board.
     * @param currentPlayerColor The color of the current player.
     * @see Tile
     * @see Piece
     * @see PieceColor
     * @return true if the tile is occupied by an opponent's piece, false otherwise.
     */
    public boolean isTileOccupiedByOpponent(int x, int y, PieceColor currentPlayerColor) {
        Tile tile = getTileAt(x, y);
        Piece piece = tile.getPiece();
        return piece != null && piece.getColor() != currentPlayerColor;
    }

    /**
     * Upgrades a pawn to a queen piece on the game board.
     * This method replaces a pawn piece with a queen piece at the specified tile coordinates (x, y), effectively upgrading the pawn.
     * The method performs the following actions:
     * Creates a new queen piece object using the provided application instance, color, and tile coordinates.
     * Retrieves the tile at the specified coordinates (x, y).
     * Sets the new queen piece on the tile, replacing the existing pawn piece.
     * @param app The application instance.
     * @param x The x-coordinate of the tile on the game board.
     * @param y The y-coordinate of the tile on the game board.
     * @param color The color of the new queen piece.
     * @see Queen
     * @see PieceColor
     * @see Tile
     * @see Tile#setPiece(Piece)
     */
    public void upgradePawnToQueen(App app, int x, int y, PieceColor color) {
        Queen newQueen = new Queen(app, color, x, y);
        tiles[x][y].setPiece(newQueen);
    }

    /**
     * Retrieves a list of pieces on the game board that belong to a specific color.
     * This method returns all the pieces on the board that have the specified color.
     * The method performs the following actions:
     * Creates a new ArrayList to store the pieces of the specified color.
     * Iterates over each tile on the game board.
     * Retrieves the piece on the current tile.
     * If the piece exists and its color matches the specified color, adds the piece to the list.
     * Returns the list of pieces of the specified color.
     * @param color The color of the pieces to retrieve.
     * @see List
     * @see Piece
     * @see PieceColor
     * @see Tile
     * @see Tile#getPiece()
     * @return A list of pieces that belong to the specified color.
     */
    public List<Piece> getPiecesByColor(PieceColor color) {
        List<Piece> piecesByColor = new ArrayList<>();
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                Piece piece = getTileAt(i, j).getPiece();
                if (piece != null && piece.getColor() == color) {
                    piecesByColor.add(piece);
                }
            }
        }
        return piecesByColor;
    }

    /**
     * Retrieves a list of pieces on the game board that belong to a specific type and color.
     * This method returns all the pieces on the board that have the specified type and color.
     * The method performs the following actions:
     * Creates a new ArrayList to store the pieces of the specified type and color.
     * Retrieves the list of pieces that belong to the specified color using the getPiecesByColor() method.
     * Iterates over each piece in the list.
     * If the piece's type matches the specified type, adds the piece to the result list.
     * Returns the list of pieces that match the specified type and color.
     * @param type The type of the pieces to retrieve.
     * @param color The color of the pieces to retrieve.
     * @see List
     * @see Piece
     * @see PieceType
     * @see PieceColor
     * @see #getPiecesByColor(PieceColor)
     * @return A list of pieces that belong to the specified type and color.
     */
    public List<Piece> getPieceByType(PieceType type, PieceColor color) {
        List<Piece> pieces = new ArrayList<>();
        List<Piece> piecesByColor = getPiecesByColor(color);
        for (Piece piece : piecesByColor) {
            if (piece.getType() == type) {
                pieces.add(piece);
            }
        }
        return pieces;
    }

    /**
     * Checks if the specified color is in checkmate on the given board.
     * This method determines if the specified color is in a checkmate position, where their king is in check and there are no legal moves to escape the check.
     * The method performs the following actions:
     * Retrieves the king of the specified color from the list of pieces on the board.
     * If no king is found, returns false.
     * If the king is not in check, returns false.
     * Iterates over each piece of the specified color.
     * Retrieves the legal moves for the piece.
     * Checks if any of the legal moves can result in a safe move (i.e., the king is not in check after the move).
     * If a safe move is found, returns false.
     * If no piece can make a safe move, returns true (indicating checkmate).
     * @param board The game board.
     * @param color The color to check for checkmate.
     * @return true if the specified color is in checkmate, false otherwise.
     * @see Board
     * @see PieceColor
     * @see Piece
     * @see King
     * @see King#isInCheck(Board)
     * @see Piece#safeMove(List, int, int, Board)
     * @see List
     * @see int[]
     */
    public boolean checkmate(Board board, PieceColor color) {
        // Get the king
        King king = null;
        List<Piece> pieces = board.getPiecesByColor(color);
        for (Piece piece : pieces) {
            if (piece instanceof King) {
                king = (King) piece;
                break;
            }
        }

        // If no king is found, return false
        if (king == null) {
            return false;
        }

        // If king is not in check, return false
        if (!king.isInCheck(board)) {
            return false;
        }

        // Check if any piece can make a safe move
        for (Piece piece : pieces) {
            List<int[]> legalMoves = piece.safeMove(piece.getLegalMoves(piece.getX(), piece.getY(), board), piece.getX(), piece.getY(), board);
            if (legalMoves.size() > 0) {
                return false;
            }
        }
        // If no piece can make a safe move, return true (checkmate)
        return true;
    }

}

