package IS24_LB11.game.components;

import IS24_LB11.game.utils.SyntaxException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class GoalCardTest {

    @Test
    void testValidCardCreation () throws SyntaxException {

        String [] validId = new String[] {
                "O2FFF",
                "O2PPP",
                "O2AAA",
                "O2III",
                "O3QKM",
                "O2MM_",
                "O2KK_",
                "O2QQ_",
                "O2FFFD1",
                "O2PPPD0",
                "O2AAAD1",
                "O2IIID0",
                "O3PFFL3",
                "O3IPPL2",
                "O3FAAL1",
                "O3AIIL0"
        };
        for (String id: validId) {
            assertEquals (id, CardFactory.newSerialCard(id).asString(), "id: "+id);
        }
    }

    @Test
    void testInvalidCardCreation () throws  SyntaxException {

        String [] invalidId = new String [] {
                "GTEEQFF1QFFA_________________",
                "Z_EEQFF9_FFA__",
                "GAZK_EFF1KFFP__",
                "DEEE_FF2EFHFP_",
                "EEE_FF",
                "MGEEE_FF9EFF]FP_",
                "EEE_FF2E}FP_",
                "EEE_FF2EP_",
                "GEEE_FF2EFFFP@",
                "EEE_FF2EFFFZ_MNVC",
                "",
                "......................"
        };
        for (String id: invalidId) {
            assertThrows(SyntaxException.class, () -> CardFactory.newSerialCard(id).asString(), "id: "+id);
        }
    }
}
