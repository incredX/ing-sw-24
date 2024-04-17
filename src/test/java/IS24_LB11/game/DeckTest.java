package IS24_LB11.game.components;
import IS24_LB11.game.Deck;
import IS24_LB11.game.utils.SyntaxException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static junit.framework.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DeckTest {
    public ArrayList<CardInterface> cards;
    @BeforeAll

    void init () throws SyntaxException {
        String[] GoldenIds = new String[]{
               // "G_EEQFF1QFFA__", "GEK_EFF1KFFP__", "GMEE_FF1MFFI__", "GEE_EFF2EFFFA_", "GEEE_FF2EFFFP_", "GE_EEFF2EFFFI_", "GE_K_FF3_FFF__", "GQE__FF3_FFF__", "G_M_EFF3_FFF__", "GE_E_FF5_FFFFF",
                "GQEE_PF1QPPI__", "GEM_EPF1MPPF__", "GE_KEPF1KPPA__", "G_EEEPF2EPPPI_", "GEEE_PF2EPPPA_", "GE_EEPF2EPPPF_", "GE_Q_PF3_PPP__", "GME__PF3_PPP__", "G_K_EPF3_PPP__", "GEE__PF5_PPPPP",
                "GKEE_AF1KAAI__", "G_EEMAF1MAAP__", "GE_QEAF1QAAF__", "GEE_EAF2EAAAI_", "GE_EEAF2EAAAF_", "G_EEEAF2EAAAP_", "GE_M_AF3_AAA__", "GEK__AF3_AAA__", "G_E_QAF3_AAA__", "G_E_EAF5_AAAAA",
                "GEQ_EIF1QIIP__", "GE_MEIF1MIIA__", "G_EEKIF1KIIF__", "GEE_EIF2EIIIA_", "GEEE_IF2EIIIP_", "GE_EEIF2EIIIF_", "GK_E_IF3_III__", "GEM__IF3_III__", "G__QEIF3_III__", "GEE__IF5_IIIII"
        };

        cards = new ArrayList<>();

        for(String id: GoldenIds) {
            cards.add(new GoldenCard(id.substring(1)));
        }
    }

    @Test
    void shuffleShouldRandomlySortCards() {

        Deck OriginalDeck = new Deck(cards);
        OriginalDeck.shuffle();

        Deck ShuffledDeck = new Deck(OriginalDeck.getCards());

        assertNotEquals(OriginalDeck,ShuffledDeck, "Cards should be shuffled");

        assertTrue("Shuffled cards should contains the same elements", containsSameElements(OriginalDeck, ShuffledDeck));

    }

    private boolean containsSameElements(Deck deck1, Deck deck2) {
        if (deck1.size() != deck2.size()) {
            return false;
        }

        for(CardInterface card: cards) {
            if (!deck2.contains(card)){
                return false;
            }
        }
        return true;
    }

}