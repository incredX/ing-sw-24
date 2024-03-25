package IS24_LB11.game;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;

import IS24_LB11.game.symbol.Empty;
import IS24_LB11.game.symbol.Item;
import IS24_LB11.game.symbol.Suit;
import IS24_LB11.game.symbol.Symbol;
import IS24_LB11.game.utils.Corners;
import IS24_LB11.game.utils.SyntaxException;

import static IS24_LB11.game.utils.Corners.UP_LEFT;
import static IS24_LB11.game.utils.Corners.UP_RIGHT;
import static IS24_LB11.game.utils.Corners.DOWN_LEFT;
import static IS24_LB11.game.utils.Corners.DOWN_RIGHT;

public class CornersTest {

    @Test
    @DisplayName("Corners to/from valid strings")
    void testValidStrings() throws SyntaxException {
        String[] valid_ids = new String[] { "FAPI", "EE__", "____", "FEP_", "EQKM" };
        for (String id: valid_ids) {
            assert (id.equals(new Corners(id).asString()));
        }
    }

    @Test
    @DisplayName("Corners to/from invalid strings")
    void testInvalidStrings() {
        String[] valid_ids = new String[] { "fapi", " 1api", "api", "EE_", "_qkm", "fapiEE" };
        for (String id: valid_ids) {
            assertThrows(SyntaxException.class, () -> new Corners(id));
        }
    }

    @Test
    void testHasCorner() throws SyntaxException {
        Corners corners = new Corners("AEQ_");
        assert ( corners.hasCorner(UP_LEFT));
        assert ( corners.hasCorner(UP_RIGHT));
        assert ( corners.hasCorner(DOWN_LEFT));
        assert (!corners.hasCorner(DOWN_RIGHT));
        assert (!corners.hasCorner(4));
        assert (!corners.hasCorner(-1));
    }

    @Test
    void testGetCorner() throws SyntaxException {
        Corners corners = new Corners("FQ_E");
        Symbol[] symbols = new Symbol[] {Suit.MUSHROOM, Item.QUILL, null, Empty.symbol()};
        for (int i=0; i<4; i++) assert(symbols[i] == corners.getCorner(i));
    }

    @Test
    void testOpposite() {
        assert (DOWN_RIGHT == Corners.opposite(UP_LEFT));
        assert (DOWN_LEFT == Corners.opposite(UP_RIGHT));
        assert (UP_RIGHT == Corners.opposite(DOWN_LEFT));
        assert (UP_LEFT == Corners.opposite(DOWN_RIGHT));
    }

    @Test
    void testIsRight() {
        assert (Corners.isRight(UP_RIGHT));
        assert (Corners.isRight(DOWN_RIGHT));
        assert (!Corners.isRight(UP_LEFT));
        assert (!Corners.isRight(DOWN_LEFT));
    }

    @Test
    void testIsUp() {
        assert (Corners.isUp(UP_LEFT));
        assert (Corners.isUp(UP_RIGHT));
        assert (!Corners.isUp(DOWN_LEFT));
        assert (!Corners.isUp(DOWN_RIGHT));
    }

}
