package IS24_LB11.tools;

import IS24_LB11.game.Board;
import IS24_LB11.game.components.*;
import IS24_LB11.game.tools.JsonConverter;
import IS24_LB11.game.utils.JsonException;
import IS24_LB11.game.utils.Position;
import IS24_LB11.game.utils.SyntaxException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class JsonConverterTest {
    @Test
    @DisplayName("Converting all types of cards")
    public void cardConversionTest() throws JsonException,SyntaxException {
        JsonConverter jsonConverter = new JsonConverter();
        ArrayList<String> stringCards = new ArrayList<>();
        stringCards.add("{ \"NormalCard\": \"" + "NFEF_FF0" + "\" }");
        stringCards.add("{ \"GoldenCard\": \"" + "GEK_EFF1KFFP__" + "\" }");
        stringCards.add("{ \"StarterCard\": \"" + "SEEEE_F0AI_PIAF" + "\" }");
        stringCards.add("{ \"GoalCard\": \"" + "O2FFF" + "\" }");
        stringCards.add("{ \"GoalCard\": \"" + "O2FFFD1" + "\" }");
        
        CardFactory cardFactory = new CardFactory();
        ArrayList<CardInterface> cardList = new ArrayList<>();
        cardList.add(cardFactory.newPlayableCard("NFEF_FF0"));
        cardList.add(cardFactory.newPlayableCard("GEK_EFF1KFFP__"));
        cardList.add(cardFactory.newPlayableCard("SEEEE_F0AI_PIAF"));
        cardList.add(cardFactory.newSerialCard("O2FFF"));
        cardList.add(cardFactory.newSerialCard("O2FFFD1"));
        
        for (int index = 0; index < cardList.size(); index++) {
            assert(jsonConverter.objectToJSON(cardList.get(index)).equals(stringCards.get(index)));
        }
    }

    @Test
    @DisplayName("Converting object board to json")
    public void boardConversionTest() throws JsonException, SyntaxException {
        JsonConverter jsonConverter = new JsonConverter();
        Board board = new Board();
        CardFactory cardFactory = new CardFactory();
        StarterCard starterCard = (StarterCard) cardFactory.newPlayableCard("SEEEE_F0AI_PIAF");
        NormalCard normalCard = (NormalCard) cardFactory.newPlayableCard("NFEF_FF0");
        GoldenCard goldenCard = (GoldenCard) cardFactory.newPlayableCard("GEK_EFF1KFFP__");
        board.start(starterCard);
        board.placeCard(normalCard, new Position(1,1));
        board.placeCard(goldenCard, new Position(-1,1));
        System.out.println(jsonConverter.objectToJSON(board));
    }
}
