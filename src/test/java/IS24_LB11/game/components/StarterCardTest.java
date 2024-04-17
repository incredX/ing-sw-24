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


public class StarterCardTest {

    @Test

    void testValidCardCreation () throws SyntaxException {

        String [] validId = new String[] {
                "EPIE_F0I__FPIA",
                "AEEF_F0F__PAFI",
                "EEEE_F0PF_IAFP",
                "EEEE_F0AI_PIAF",
                "EE___F0AIPIFPA",
                "EE___F0PAFFAPI"
        };
        for (String id: validId) {
            assert (new StarterCard(id).asString().equals("S" + id));
        }
    }

    @Test
    void testInvalidCardCreation () throws  SyntaxException {

        String [] invalidId = new String [] {
                "SEPIE_F0I__FPIA",
                "LAEEF_F0F__PAFI",
                "0EEEE_F0PF_IAFP",
                "SEEEE_F9AI_PIAF",
                "_EE___F0AIPIFPA",
                "JEE___F0PAFFAPI",
                "_F0I__FPIA",
                "",
                "               ",
                "111111111111111",
                "_______________",
                "ee___f0paffapi"
        };
        for (String id: invalidId) {
            assertThrows(SyntaxException.class, () -> new StarterCard(id));
        }
    }


    @Test
    void testCorners() throws SyntaxException {
        String id = "EPIE_F0I__FPIA" ;
        GoldenCard gc = new GoldenCard(id);
        assert (gc.getCorner(UP_LEFT) == Empty.symbol());
        assert (gc.getCorner(UP_RIGHT) == Suit.PLANT);
        assert (gc.getCorner(DOWN_LEFT) == Suit.INSECT);
        assert (gc.getCorner(DOWN_RIGHT) == Empty.symbol());
    }

    @Test
    void testCountersUpdate() throws SyntaxException { //da modificare e adattare alla specifica funzione
        String[] ids = {"AEEF_B0F__PAFI"};//, "AEEF_F0F__PAFI", "EEEE_F0PF_IAFP", "EEEE_F0AI_PIAF", "EE___F0AIPIFPA", "EE___F0PAFFAPI"};
        HashMap<Symbol, Integer> counters = new HashMap<>();

        for (Suit suit: Suit.values()) counters.put(suit, 0);
        for (Item item: Item.values()) counters.put(item, 0);

        for (String id: ids) {
            StarterCard card = new StarterCard(id);
            card.updateCounters(counters);
        }
        assert (counters.get(Suit.MUSHROOM) == 1);
        assert (counters.get(Suit.ANIMAL) == 1);
        assert (counters.get(Suit.INSECT) == 1);
        assert (counters.get(Suit.PLANT) == 1);
        assert (counters.get(Item.QUILL) == 0);
        assert (counters.get(Item.INKWELL) == 0);
        assert (counters.get(Item.MANUSCRIPT) == 0);
    }
}