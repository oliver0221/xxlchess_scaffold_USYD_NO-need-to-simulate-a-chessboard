package XXLChess;

/**
 * The PieceType enum represents the types of chess pieces in the XXLChess game.
 * Each enum constant represents a specific piece type and has a corresponding symbol and image name.
 * The symbol is used for display and representation, while the image name is used to retrieve the image file for the piece.
 */
public enum PieceType {
    PAWN("P", "pawn"),
    ROOK("R", "rook"),
    KNIGHT("N", "knight"),
    BISHOP("B", "bishop"),
    KING("K", "king"),
    QUEEN("Q", "queen"),
    ARCHBISHOP("H", "archbishop"),
    CAMEL("C", "camel"),
    GENERAL("G", "general"),
    AMAZON("A", "amazon"),
    CHANCELLOR("E", "chancellor");
    private final String symbol;
    private final String imageName;

    /**
     * Constructs a PieceType object with the specified symbol and image name.
     * The PieceType class represents the type of a chess piece, such as Pawn, Knight, Bishop, etc.
     * Each piece type is associated with a symbol and an image name.
     * @param symbol The symbol representing the piece type.
     * @param imageName The name of the image file associated with the piece type.
     */
    PieceType(String symbol, String imageName) {
        this.symbol = symbol;
        this.imageName = imageName;
    }

    /**
     * Retrieves the image path for a chess piece based on its color.
     * This method returns the file path to the image representing the specified chess piece color.
     * The method performs the following actions:
     * Determines the color prefix based on the specified color.
     * If the color is WHITE, the prefix is set to "w".
     * If the color is BLACK, the prefix is set to "b".
     * Formats the image path string using the color prefix and the imageName variable.
     * The imageName variable represents the name of the specific chess piece image.
     * @param color The color of the chess piece.
     * @return The image path for the specified chess piece color.
     * @see PieceColor
     */
    public String getImagePath(PieceColor color) {
        String colorPrefix = color == PieceColor.WHITE ? "w" : "b";
        return String.format("src/main/resources/XXLChess/%s-%s.png", colorPrefix, imageName);
    }

    /**
     * Retrieves the PieceType based on the specified symbol.
     * The fromSymbol() method allows you to obtain the PieceType corresponding to a given symbol.
     * It iterates through all available PieceType values and compares the symbol of each type with the specified symbol.
     * If a match is found, the corresponding PieceType is returned.
     * @param symbol The symbol representing the piece type.
     * @return The PieceType associated with the specified symbol.
     * @throws IllegalArgumentException if the symbol does not match any valid PieceType.
     */
    public static PieceType fromSymbol(String symbol) {
        for (PieceType type : values()) {
            if (type.symbol.equalsIgnoreCase(symbol)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid piece symbol: " + symbol);
    }
}
