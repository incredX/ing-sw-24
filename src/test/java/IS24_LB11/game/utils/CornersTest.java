package IS24_LB11.game.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;

import IS24_LB11.game.symbol.Empty;
import IS24_LB11.game.symbol.Item;
import IS24_LB11.game.symbol.Suit;
import IS24_LB11.game.symbol.Symbol;

import static IS24_LB11.game.utils.Direction.UP_LEFT;
import static IS24_LB11.game.utils.Direction.UP_RIGHT;
import static IS24_LB11.game.utils.Direction.DOWN_LEFT;
import static IS24_LB11.game.utils.Direction.DOWN_RIGHT;

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
    }

    @Test
    void testGetCorner() throws SyntaxException {
        Corners corners = new Corners("FQ_E");
        Symbol[] symbols = new Symbol[] {Suit.MUSHROOM, Item.QUILL, null, Empty.symbol()};
        for (int i=0; i<4; i++) assert(symbols[i] == corners.getCorner(Direction.parse(i)));
    }
}
