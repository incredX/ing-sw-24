package IS24_LB11.game.components;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
                "G_EEQFF1QFFA__",
                "GEK_EFF1KFFP__",
                "GMEE_FF1MFFI__",
                "GEE_EFF2EFFFA_",
                "GEEE_FF2EFFFP_"
        };
        for (String id: validId) {
            assertEquals (id, CardFactory.newPlayableCard(id).asString(), "id: "+id);
        }
    }

    @Test
    void testInvalidCardCreation () throws  SyntaxException {

        String [] invalidId = new String [] {
                "GTEEQFF1QFFA_________________",
                "G_EEQFF9_FFA__",
                "GZK_EFF1KFFP__",
                "GEEE_FF2EFHFP_",
                "EEE_FF",
                "GEEE_FF9EFF]FP_",
                "GEEE_FF2E}FP_",
                "EEE_FF2EP_",
                "GEEE_FF2EFFFP@",
                "EEE_FF2EFFFZ_MNVC",
                ""
        };
        for (String id: invalidId) {
            assertThrows(SyntaxException.class, () -> CardFactory.newPlayableCard(id).asString(), "id: "+id);
        }
    }


    @Test
    void testCorners() throws SyntaxException {
        String id = "G_EEQFF1QFFA__" ;
        GoldenCard gc = (GoldenCard) CardFactory.newPlayableCard(id);
        assert (!gc.hasCorner(UP_LEFT));
        assert (gc.hasCorner(UP_RIGHT));
        assert (gc.hasCorner(DOWN_LEFT));
        assert (gc.hasCorner(DOWN_RIGHT));
        assert (gc.getCorner(UP_RIGHT) == Empty.symbol());
        assert (gc.getCorner(DOWN_LEFT) == Empty.symbol());
        assert (gc.getCorner(DOWN_RIGHT) == Item.QUILL);
    }

    @Test
    void testCountersUpdate() throws SyntaxException {
        String[] ids = {"G_EEQFF1QFFA__", "GEK_EFF1KFFP__", "GMEE_FF1MFFI__", "GEE_EFF2EFFFA_", "GEEE_FF2EFFFP_", "GEEE_IB2EIIIP_"};
        HashMap<Symbol, Integer> counters = new HashMap<>();

        for (Suit suit: Suit.values()) counters.put(suit, 0);
        for (Item item: Item.values()) counters.put(item, 0);

        for (String id: ids) {
            GoldenCard card = (GoldenCard) CardFactory.newPlayableCard(id);
            card.updateCounters(counters);
        }
        assert (counters.get(Suit.MUSHROOM) == 0);
        assert (counters.get(Suit.ANIMAL) == 0);
        assert (counters.get(Suit.INSECT) == 1);
        assert (counters.get(Suit.PLANT) == 0);
        assert (counters.get(Item.QUILL) == 1);
        assert (counters.get(Item.INKWELL) == 1);
        assert (counters.get(Item.MANUSCRIPT) == 1);
    }
}
