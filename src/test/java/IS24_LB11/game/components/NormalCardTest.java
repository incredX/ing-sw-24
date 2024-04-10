package IS24_LB11.game.components;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;

import IS24_LB11.game.symbol.Symbol;
import IS24_LB11.game.symbol.Empty;
import IS24_LB11.game.symbol.Item;
import IS24_LB11.game.symbol.Suit;
import IS24_LB11.game.utils.SyntaxException;

import java.util.HashMap;

import static IS24_LB11.game.utils.Direction.UP_LEFT;
import static IS24_LB11.game.utils.Direction.UP_RIGHT;
import static IS24_LB11.game.utils.Direction.DOWN_LEFT;
import static IS24_LB11.game.utils.Direction.DOWN_RIGHT;

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

    @Test
    void testCountersUpdate() throws SyntaxException {
        String[] ids = {"FQE_FF0", "KF_AFF0", "FIMEFF0", "EI_IIB0"};
        HashMap<Symbol, Integer> counters = new HashMap<>();

        for (Suit suit: Suit.values()) counters.put(suit, 0);
        for (Item item: Item.values()) counters.put(item, 0);

        for (String id: ids) {
            NormalCard card = new NormalCard(id);
            card.updateCounters(counters);
        }
        assert (counters.get(Suit.MUSHROOM) == 3);
        assert (counters.get(Suit.ANIMAL) == 1);
        assert (counters.get(Suit.INSECT) == 2);
        assert (counters.get(Suit.PLANT) == 0);
        assert (counters.get(Item.QUILL) == 1);
        assert (counters.get(Item.INKWELL) == 1);
        assert (counters.get(Item.MANUSCRIPT) == 1);
    }
}
