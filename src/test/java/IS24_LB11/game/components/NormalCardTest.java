package IS24_LB11.game.components;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;

import IS24_LB11.game.symbol.Empty;
import IS24_LB11.game.symbol.Item;
import IS24_LB11.game.symbol.Suit;
import IS24_LB11.game.utils.SyntaxException;

import static IS24_LB11.game.utils.Corners.UP_LEFT;
import static IS24_LB11.game.utils.Corners.UP_RIGHT;
import static IS24_LB11.game.utils.Corners.DOWN_LEFT;
import static IS24_LB11.game.utils.Corners.DOWN_RIGHT;

public class NormalCardTest {

    @Test
    void testValidCardCreation() throws SyntaxException {
        String[] validId = new String[]{
                "FEF_FF0",
                "FEF_FB0",
                "_EAAAF0",
                "EEEEAB0",
                "____AB0",
        };
        for (String id: validId) {
            assert (new NormalCard(id).asString().equals("R" + id));
        }
    }

    @Test
    void testInvalidCardCreation() {
        String[] invalidId = new String[]{
                "FEF_",
                "FEF_FF_",
                "EE__F[0",
                "EE__fB0",
                "____XB0"
        };

        for (String id: invalidId) {
            assertThrows(SyntaxException.class, () -> new NormalCard(id));
        }
    }

    @Test
    void testCorners() throws SyntaxException {
        String id = "FQE_FF0";
        NormalCard nc = new NormalCard(id);
        assert (!nc.hasCorner(DOWN_RIGHT));
        assert (nc.hasCorner(UP_LEFT));
        assert (nc.getCorner(UP_LEFT) == Suit.MUSHROOM);
        assert (nc.getCorner(UP_RIGHT) == Item.QUILL);
        assert (nc.getCorner(DOWN_LEFT) == Empty.symbol());
    }
}
