package XXLChess;

import XXLChess.Piece.Piece;

import static XXLChess.App.CELLSIZE;

/**
 * The Tile class represents a single tile on the chessboard in the XXLChess game.
 * It contains information about its position, size, color, and the piece located on it.
 */
public class Tile {
    private int x;
    private int y;
    private int size;
    private boolean isBlack;
    private Piece piece;
    private App app;

    private int offsetX = 0;
    private int offsetY = 0;


    public boolean isHighlight;

    private int highlightedR;
    private int highlightedG;
    private int highlightedB;

    /**
     * Creates a tile object.
     * The constructor initializes the tile with the specified parameters, including the application instance,
     * position (x, y), size, and whether it is a black tile.
     * The isHighlight property is set to false by default.
     * @param app The application instance.
     * @param x The x-coordinate of the tile.
     * @param y The y-coordinate of the tile.
     * @param size The size of the tile.
     * @param isBlack A boolean indicating whether the tile is black or not.
     */
    public Tile(App app, int x, int y, int size, boolean isBlack) {
        this.app = app;
        this.x = x;
        this.y = y;
        this.size = size;
        this.isBlack = isBlack;
        this.isHighlight = false;
    }

    /**
     * Draws the tile on the chessboard.
     * The draw() method is responsible for rendering the appearance of the tile on the chessboard.
     * It sets the stroke weight to 0 to remove any border around the tile.
     * The fill color is determined based on the tile's properties: highlighted state or color (black or white).
     * The tile is then drawn as a rectangle using the specified position (x, y) and size.
     * If the tile contains a piece, the piece is also drawn within the tile.
     * @see Piece#draw(int, int, int) for drawing the piece within the tile.
     */
    public void draw() {
        app.pushStyle();
        app.strokeWeight(0);
        //app.stroke(0);
        if(isHighlight)
        {
            app.fill(highlightedR,highlightedG,highlightedB);
        } else if (isBlack) {
            app.fill(180, 135, 100);
        } else {
            app.fill(240, 220, 180);
        }
        app.rect(x, y, size, size);

        if (piece != null) {
            piece.draw(x, y, size);
        }

        app.popStyle();
    }

    /**
     * Retrieves the piece located on the tile.
     * @return The piece located on the tile. Returns null if there is no piece on the tile.
     */
    public Piece getPiece() {
        return piece;
    }

    /**
     * Sets the piece on the tile.
     * @param piece The piece to be set on the tile. Set to null to remove the piece from the tile.
     */
    public void setPiece(Piece piece) {
        this.piece = piece;
    }

    /**
     * Draws a highlight on the tile with the specified RGB color values.
     * @param R The red component of the highlight color (0-255).
     * @param G The green component of the highlight color (0-255).
     * @param B The blue component of the highlight color (0-255).
     */
    public void drawHighlight(int R, int G, int B) {
        this.highlightedR = R;
        this.highlightedG = G;
        this.highlightedB = B;
        isHighlight = true;
    }

    /**
     * Returns the x-coordinate of the tile in terms of board cells.
     * @return The x-coordinate of the tile.
     */
    public int getX() {
        return x/CELLSIZE;
    }

    /**
     * Returns the y-coordinate of the tile in terms of board cells.
     * @return The y-coordinate of the tile.
     */
    public int getY() {
        return y/CELLSIZE;
    }
}
