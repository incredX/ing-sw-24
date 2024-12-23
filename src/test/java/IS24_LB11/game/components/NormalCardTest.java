package IS24_LB11.game.components;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
    @DisplayName("Testing the valid creation of the NormalCard")
    void testValidCardCreation() throws SyntaxException {
        String[] validId = new String[]{
                "NFEF_FF0",
                "NFEF_FB0",
                "N_EAAAF0",
                "NEEEEAB0",
                "N____AB0",
        };
        for (String id: validId) {
            assertEquals (id, CardFactory.newPlayableCard(id).asString(), "id: "+id);
        }
    }

    @Test
    @DisplayName("Testing the invalid creation of the NormalCard")
    void testInvalidCardCreation() {
        String[] invalidId = new String[]{
                "FEF_",
                "___________FEF_FF_",
                "EE__F[0",
                "EE__fB0",
                "____XB0",
                ""
        };

        for (String id: invalidId) {
            assertThrows(SyntaxException.class, () -> CardFactory.newPlayableCard(id).asString().equals(id), "id: "+id);
        }
    }

    @Test
    @DisplayName("Testing the proper functioning of the methods getCorner and hasCorner applied to NormalCard")
    void testCorners() throws SyntaxException {
        String id = "NFQE_FF0";
        NormalCard nc = (NormalCard) CardFactory.newPlayableCard(id);
        assert (nc.hasCorner(UP_LEFT));
        assert (nc.hasCorner(UP_RIGHT));
        assert (nc.hasCorner(DOWN_LEFT));
        assert (!nc.hasCorner(DOWN_RIGHT));
        assert (nc.getCorner(UP_LEFT) == Suit.MUSHROOM);
        assert (nc.getCorner(UP_RIGHT) == Item.QUILL);
        assert (nc.getCorner(DOWN_LEFT) == Empty.symbol());
    }

    @Test
    @DisplayName("Testing the proper functioning of the symbol counter applied to some invented NormalCard")
    void testCountersUpdate() throws SyntaxException {
        String[] ids = {"NFQQQFF0", "NKFAAFF0", "NFIMPFF0", "NEI_IIB0"};
        HashMap<Symbol, Integer> counters = new HashMap<>();

        for (Suit suit: Suit.values()) counters.put(suit, 0);
        for (Item item: Item.values()) counters.put(item, 0);

        for (String id: ids) {
            NormalCard card = (NormalCard) CardFactory.newPlayableCard(id);
            card.updateCounters(counters);
        }
        assert (counters.get(Suit.MUSHROOM) == 3);
        assert (counters.get(Suit.ANIMAL) == 2);
        assert (counters.get(Suit.INSECT) == 2);
        assert (counters.get(Suit.PLANT) == 1);
        assert (counters.get(Item.QUILL) == 3);
        assert (counters.get(Item.INKWELL) == 1);
        assert (counters.get(Item.MANUSCRIPT) == 1);
    }
}
