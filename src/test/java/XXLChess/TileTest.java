package XXLChess;

import XXLChess.Piece.Piece;
import XXLChess.Piece.Rook;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TileTest {

    @Test
    void getX() {
        Tile tile = new Tile(null,48*3,48*4,14,true);
        assertEquals(3,tile.getX());
    }

    @Test
    void getY() {
        Tile tile = new Tile(null,48*3,48*4,14,true);
        assertEquals(4,tile.getY());
    }
}