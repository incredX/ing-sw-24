package IS24_LB11.game.components;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;

import IS24_LB11.game.symbol.Symbol;
import IS24_LB11.game.symbol.Empty;
import IS24_LB11.game.symbol.Item;
import IS24_LB11.game.symbol.Suit;
import IS24_LB11.game.utils.SyntaxException;

import java.util.HashMap;

import static IS24_LB11.game.utils.Corners.UP_LEFT;
import static IS24_LB11.game.utils.Corners.UP_RIGHT;
import static IS24_LB11.game.utils.Corners.DOWN_LEFT;
import static IS24_LB11.game.utils.Corners.DOWN_RIGHT;


public class GoldenCardTest {

    @Test

    void testValidCardCreation () throws SyntaxException {

        String [] validId = new String[] {
                "_EEQFF1QFFA__",
                "EK_EFF1KFFP__",
                "MEE_FF1MFFI__",
                "EE_EFF2EFFFA_",
                "EEE_FF2EFFFP_"
        };
        for (String id: validId) {
            assert (new GoldenCard(id).asString().equals("G" + id));
        }
    }

    @Test
    void testInvalidCardCreation () throws  SyntaxException {

        String [] invalidId = new String [] {
                "TEEQFF1QFFA__",
                "ZK_EFF1KFFP__",
                "EEE_FF2EFHFP_",
                "EEE_FF",
                "EEE_FF2EFF]FP_",
                "EEE_FF2E}FP_",
                "EEE_FF2EP_",
                "EEE_FF2EFFFP@",
                "EEE_FF2EFFFZ_"
        };
        for (String id: invalidId) {
            assertThrows(SyntaxException.class, () -> new GoldenCard(id));
        }
    }


    @Test
    void testCorners() throws SyntaxException {
        String id = "_EEQFF1QFFA__" ;
        GoldenCard gc = new GoldenCard(id);
        assert (!gc.hasCorner(UP_LEFT));
        assert (gc.getCorner(UP_RIGHT) == Empty.symbol());
        assert (gc.getCorner(DOWN_LEFT) == Empty.symbol());
        assert (gc.getCorner(DOWN_RIGHT) == Item.QUILL);
    }

    @Test
    void testCountersUpdate() throws SyntaxException {
        String[] ids = {"_EEQFF1QFFA__", "EK_EFF1KFFP__", "MEE_FF1MFFI__", "EE_EFF2EFFFA_", "EEE_FF2EFFFP_"};
        HashMap<Symbol, Integer> counters = new HashMap<>();

        for (Suit suit: Suit.values()) counters.put(suit, 0);
        for (Item item: Item.values()) counters.put(item, 0);

        for (String id: ids) {
            NormalCard card = new NormalCard(id);
            card.updateCounters(counters);
        }
        assert (counters.get(Suit.MUSHROOM) == 0);
        assert (counters.get(Suit.ANIMAL) == 0);
        assert (counters.get(Suit.INSECT) == 0);
        assert (counters.get(Suit.PLANT) == 0);
        assert (counters.get(Item.QUILL) == 1);
        assert (counters.get(Item.INKWELL) == 1);
        assert (counters.get(Item.MANUSCRIPT) == 1);
    }
}
