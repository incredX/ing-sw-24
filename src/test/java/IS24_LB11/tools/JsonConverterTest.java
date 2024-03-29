package IS24_LB11.tools;

import IS24_LB11.game.components.*;
import IS24_LB11.game.tools.JsonConverter;
import IS24_LB11.game.utils.JsonStringException;
import IS24_LB11.game.utils.SyntaxException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class JsonConverterTest {
    @Test
    @DisplayName("Converting all types of cards")
    public void cardConversionTest() throws JsonStringException,SyntaxException {
        JsonConverter jsonConverter = new JsonConverter();
        ArrayList<String> stringCards = new ArrayList<>();
        stringCards.add("{ \"NormalCard\": \"" + "NFEF_FF0" + "\" }");
        stringCards.add("{ \"GoldenCard\": \"" + "GEK_EFF1KFFP__" + "\" }");
        stringCards.add("{ \"StarterCard\": \"" + "SEEEE_F0AI_PIAF" + "\" }");
        stringCards.add("{ \"GoalCard\": \"" + "O2FFF" + "\" }");
        stringCards.add("{ \"GoalCard\": \"" + "O2FFFD1" + "\" }");
        
        CardFactory cardFactory = new CardFactory();
        ArrayList<CardInterface> cardList = new ArrayList<>();
        cardList.add(CardFactory.newPlayableCard("NFEF_FF0"));
        cardList.add(CardFactory.newPlayableCard("GEK_EFF1KFFP__"));
        cardList.add(CardFactory.newPlayableCard("SEEEE_F0AI_PIAF"));
        cardList.add(CardFactory.newSerialCard("O2FFF"));
        cardList.add(CardFactory.newSerialCard("O2FFFD1"));
        
        for (int index = 0; index < cardList.size(); index++) {
            assert(jsonConverter.objectToJSON(cardList.get(index)).equals(stringCards.get(index)));
        }
    }
}
