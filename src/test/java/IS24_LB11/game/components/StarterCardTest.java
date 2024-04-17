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
                "SEPIE_F0I__FPIA",
                "SAEEF_F0F__PAFI",
                "SEEEE_F0PF_IAFP",
                "SEEEE_F0AI_PIAF",
                "SEE___F0AIPIFPA",
                "SEE___F0PAFFAPI"
        };
        for (String id: validId) {
            assert (CardFactory.newPlayableCard(id).asString().equals(id));
        }
    }

    @Test
    void testInvalidCardCreation () throws  SyntaxException {

        String [] invalidId = new String [] {
                "EPIE_F0I__FPIA",
                "LAEEF_F0F__PAFI",
                "0EEEE_F0PF_IAFP",
                "SEEEEAI_PIAF",
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
            assertThrows(SyntaxException.class, () -> CardFactory.newPlayableCard(id).asString().equals(id), "id: "+id);
        }
    }


    @Test
    void testCorners() throws SyntaxException {
        String id = "SEPIE_F0I__FPIA" ;
        StarterCard sc = (StarterCard) CardFactory.newPlayableCard(id);
        assert (sc.hasCorner(UP_LEFT));
        assert (sc.hasCorner(UP_RIGHT));
        assert (sc.hasCorner(DOWN_LEFT));
        assert (sc.hasCorner(DOWN_RIGHT));
        assert (sc.getCorner(UP_LEFT) == Empty.symbol());
        assert (sc.getCorner(UP_RIGHT) == Suit.PLANT);
        assert (sc.getCorner(DOWN_LEFT) == Suit.INSECT);
        assert (sc.getCorner(DOWN_RIGHT) == Empty.symbol());
    }

    @Test
    void testCountersUpdate() throws SyntaxException { //da modificare e adattare alla specifica funzione
        String[] ids = {"SAEEF_F0F__PAFI", "SAEEF_F0F__PAFI", "SEEEE_F0PF_IAFP", "SEEEE_F0AI_PIAF", "SEE___F0AIPIFPA", "SEE___F0PAFFAPI","SAEEF_B0F__PAFI", "SAEEF_B0F__PAFI", "SEEEE_B0PF_IAFP", "SEEEE_B0AI_PIAF", "SEE___B0AIPIFPA", "SEE___B0PAFFAPI"};
        HashMap<Symbol, Integer> counters = new HashMap<>();

        for (Suit suit: Suit.values()) counters.put(suit, 0);
        for (Item item: Item.values()) counters.put(item, 0);

        for (String id: ids) {
            StarterCard card = (StarterCard) CardFactory.newPlayableCard(id);
            card.updateCounters(counters);
        }
        assert (counters.get(Suit.MUSHROOM) == 12);
        assert (counters.get(Suit.ANIMAL) == 11);
        assert (counters.get(Suit.INSECT) == 8);
        assert (counters.get(Suit.PLANT) == 9);
        assert (counters.get(Item.QUILL) == 0);
        assert (counters.get(Item.INKWELL) == 0);
        assert (counters.get(Item.MANUSCRIPT) == 0);
    }
}