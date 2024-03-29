package IS24_LB11.tools;

import IS24_LB11.game.components.*;
import IS24_LB11.game.tools.JsonConverter;
import IS24_LB11.game.utils.JsonStringException;
import IS24_LB11.game.utils.SyntaxException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class JsonConverterTest {
    @Test
    @DisplayName("Converting all types of cards")
    public void cardConversionTest() throws JsonStringException,SyntaxException {
        JsonConverter jsonConverter = new JsonConverter();
        String jsonNormalCard = "{ \"NormalCard\" \"" + "FEF_FF0" + "\" }";
        String jsonGoldenCard = "{ \"GoldenCard\" \"" + "_EEQFF1QFFA__" + "\" }";
        String jsonStarterCard = "{ \"StarterCard\" \"" + "EPIE_F0I__FPIA" + "\" }";
        String jsonGoalSymbol = "{ \"GoalCard\" \"" + "O2FFF" + "\" }";
        String jsonGoalPattern = "{ \"GoalCard\" \"" + "O2FFFD1" + "\" }";

        NormalCard normalCard = new NormalCard("FEF_FF0");
        GoldenCard goldenCard = new GoldenCard("_EEQFF1QFFA__");
        StarterCard starterCard = new StarterCard("EPIE_F0I__FPIA");
        GoalSymbol goalSymbol = new GoalSymbol("O2FFF");
        GoalPattern goalPattern = new GoalPattern("O2FFFD1");

        System.out.println(jsonConverter.objectToJSON(goldenCard));
        //assert(jsonConverter.objectToJSON(normalCard).compareTo(jsonNormalCard)==0);
    }
}
