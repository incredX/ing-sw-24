package IS24_LB11.game.components;

import IS24_LB11.game.utils.SyntaxException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CardFactoryTest {

    @Test
    @DisplayName( "Testing the correct execution of the methods newPlayableCard and newSerialCard")
    void testNewCard () throws SyntaxException {
        assertThrows(SyntaxException.class, () -> CardFactory.newPlayableCard("").asString()); //empty string
        assertThrows(SyntaxException.class, () -> CardFactory.newPlayableCard("K_EEQFF1QFFA__").asString()); //invalid first character
        assertThrows(SyntaxException.class, () -> CardFactory.newPlayableCard("P_EEQFF1QFFA__").asString());
        assertThrows(SyntaxException.class, () -> CardFactory.newPlayableCard("L_EEQFF1QFFA__").asString());
        assertThrows(SyntaxException.class, () -> CardFactory.newPlayableCard("G_EEQjajshFF1QFFA__").asString()); //catching some invalid character inside the string
        assertThrows(SyntaxException.class, () -> CardFactory.newPlayableCard("Z_EEQFF1QFFA__").asString());
        assertEquals("G_EEQFF1QFFA__", CardFactory.newPlayableCard("G_EEQFF1QFFA__").asString()); //some valid string
        assertEquals("NFEF_FF0", CardFactory.newPlayableCard("NFEF_FF0").asString());
        assertEquals("SEPIE_F0I__FPIA", CardFactory.newPlayableCard("SEPIE_F0I__FPIA").asString());
        assertThrows(SyntaxException.class, () -> CardFactory.newSerialCard("").asString());
        assertThrows(SyntaxException.class, () -> CardFactory.newSerialCard("G_EEghasuQFF1QFFA__").asString());
        assertThrows(SyntaxException.class, () -> CardFactory.newSerialCard("K_EEQFF1QFFA__").asString());
        assertThrows(SyntaxException.class, () -> CardFactory.newSerialCard("K_EEQFF1QFFA__").asString());
        assertThrows(SyntaxException.class, () -> CardFactory.newSerialCard("G_EEsedrftgyhQFF1QFFA__").asString());
        assertThrows(SyntaxException.class, () -> CardFactory.newSerialCard("G_EE}@najQFF1QFFA__").asString());
        assertThrows(SyntaxException.class, () -> CardFactory.newSerialCard("Z_EEQFF1QFFA__").asString());
        assertEquals("G_EEQFF1QFFA__", CardFactory.newSerialCard("G_EEQFF1QFFA__").asString());
        assertEquals("NFEF_FF0", CardFactory.newSerialCard("NFEF_FF0").asString());
        assertEquals("SEPIE_F0I__FPIA", CardFactory.newSerialCard("SEPIE_F0I__FPIA").asString());
        assertEquals("O2PPPD0", CardFactory.newSerialCard("O2PPPD0").asString());
        assertEquals("O3PFFL3", CardFactory.newSerialCard("O3PFFL3").asString());
    }

    @Test
    @DisplayName( "Testing the valid PlayableCard creation")
    void testValidPlayableCardCreation () throws SyntaxException {

        String [] validId = new String[] {
                "G_EEQFF1QFFA__",
                "GEK_EFF1KFFP__",
                "SEPIE_F0I__FPIA",
                "SAEEF_F0F__PAFI",
                "NFEF_FB0",
                "N_EAAAF0"
        };
        for (String id: validId) {
            assertEquals (id, CardFactory.newPlayableCard(id).asString(), "id: "+id);
        }
    }

    @Test
    @DisplayName( "Testing the invalid PlayableCard creation")
    void testInvalidPlayableCardCreation () throws  SyntaxException {

        String [] invalidId = new String [] {
                "dTEEQFF",
                "_EEQFF__",
                "ùZK_FFP__",
                "§_FF2EFHFP_",
                "EEF",
                "G9EFF]FP_",
                "GEE2E}FP_",
                "_FF2EP_",
                "@EEEFFFP@",
                "çEEVC",
                "",
                "XFEF_FB0",
                "M_EAAAF0",
                "O2FFF",
                "O2PPP"
        };
        for (String id: invalidId) {
            assertThrows(SyntaxException.class, () -> CardFactory.newPlayableCard(id).asString(), "id: "+id);
        }
    }


    @Test
    @DisplayName("Same as PlayableCard but using newSerialCard")
    void testValidSerialCardCreation () throws SyntaxException {
        String [] validId = new String []{
                "O2FFF",
                "O2PPP",
                "O2FFFD1",
                "O2PPPD0",
                "O3FAAL1",
                "O3AIIL0",
                "GEM_EPF1MPPF__",
                "GE_KEPF1KPPA__",
                "N_EAAAF0",
                "NA_AEAF0",
                "SEEEE_F0PF_IAFP",
                "SEEEE_F0AI_PIAF"
        };
        for (String id: validId) {
            assertEquals (id, CardFactory.newSerialCard(id).asString(), "id: "+id);
        }
    }

    @Test
    @DisplayName("Same as PlayableCard but using newSerialCard")
    void testInvalidSerialCardCreation () throws  SyntaxException {

        String [] invalidId = new String [] {
                "dTEEQFF",
                "_EEQFF__",
                "ùZK_FFP__",
                "§_FF2EFHFP_",
                "EEF",
                "G9EFF]FP_",
                "GEE2E}FP_",
                "_FF2EP_",
                "@EEEFF@",
                "ç___",
                "",
                "XFEF_FB0",
                "M_EAAAF0"
        };
        for (String id: invalidId) {
            assertThrows(SyntaxException.class, () -> CardFactory.newSerialCard(id).asString(), "id: "+id);
        }
    }
}
