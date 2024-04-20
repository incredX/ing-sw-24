package IS24_LB11.game.components;

import IS24_LB11.game.utils.SyntaxException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CardFactoryTest {
    @Test

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
