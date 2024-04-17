package IS24_LB11.tools;

import IS24_LB11.game.Board;
import IS24_LB11.game.Player;
import IS24_LB11.game.components.*;
import IS24_LB11.game.tools.JsonConverter;
import IS24_LB11.game.utils.JsonException;
import IS24_LB11.game.utils.Position;
import IS24_LB11.game.utils.SyntaxException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class JsonConverterTest {
    @Test
    @DisplayName("Converting all types of cards")
    public void cardConversionTest() throws JsonException,SyntaxException {
        JsonConverter jsonConverter = new JsonConverter();
        ArrayList<String> stringCards = new ArrayList<>();
        CardFactory cardFactory = new CardFactory();
        ArrayList<CardInterface> cardList = new ArrayList<>();

        stringCards.add("{ \"Card\": \"" + "NFEF_FF0" + "\" }");
        stringCards.add("{ \"Card\": \"" + "GEK_EFF1KFFP__" + "\" }");
        stringCards.add("{ \"Card\": \"" + "SEEEE_F0AI_PIAF" + "\" }");
        stringCards.add("{ \"Card\": \"" + "O2FFF" + "\" }");
        stringCards.add("{ \"Card\": \"" + "O2FFFD1" + "\" }");
        
        cardList.add(cardFactory.newSerialCard("NFEF_FF0"));
        cardList.add(cardFactory.newSerialCard("GEK_EFF1KFFP__"));
        cardList.add(cardFactory.newSerialCard("SEEEE_F0AI_PIAF"));
        cardList.add(cardFactory.newSerialCard("O2FFF"));
        cardList.add(cardFactory.newSerialCard("O2FFFD1"));
        
        for (int index = 0; index < cardList.size(); index++) {
            assert(jsonConverter.objectToJSON(cardList.get(index)).compareTo(stringCards.get(index))==0);
        }
    }

    @Test
    @DisplayName("Converting object board to json")
    public void boardConversionTest() throws JsonException,SyntaxException {
        String str = "{ \"Board\": { \"placedCards\": [ { \"Card\": \"SEEEE_F0AI_PIAF\", X0Y0}, { \"Card\": \"NFEF_FF0\", X1Y1}, { \"Card\": \"GEK_EFF1KFFP__\", X-1Y1} ] }";
        JsonConverter jsonConverter = new JsonConverter();
        Board board = new Board();
        CardFactory cardFactory = new CardFactory();
        StarterCard starterCard = (StarterCard) cardFactory.newSerialCard("SEEEE_F0AI_PIAF");
        NormalCard normalCard = (NormalCard) cardFactory.newSerialCard("NFEF_FF0");
        GoldenCard goldenCard = (GoldenCard) cardFactory.newSerialCard("GEK_EFF1KFFP__");
        board.start(starterCard);
        board.placeCard(normalCard, new Position(1,1));
        board.placeCard(goldenCard, new Position(-1,1));
        System.out.println(str);
        System.out.println(jsonConverter.objectToJSON(board));
        assert(jsonConverter.objectToJSON(board).compareTo(str)==0);
    }

    @Test
    @DisplayName("Converting object player to json")
    public void playerConversionTest() throws JsonException,SyntaxException{
        String str = "{ \"Player\": { \"Status\": false, \"Name\": NameTest, \"PersonalGoal\": { \"Card\": \"O2FFF\" }, \"OnHandCard\": {{ \"Card\": \"NKF_AFF0\" }, { \"Card\": \"NFEF_FF0\" }, { \"Card\": \"GEK_EFF1KFFP__\" }, }, \"Color\": java.awt.Color[r=1,g=2,b=3], \"Score\": 10} }";
        JsonConverter jsonConverter = new JsonConverter();
        ArrayList<PlayableCard> cardList = new ArrayList<>();
        CardFactory cardFactory = new CardFactory();
        Player player = new Player();

        PlayableCard normalCard1 = (NormalCard) cardFactory.newSerialCard("NFEF_FF0");
        PlayableCard normalCard2 = (NormalCard) cardFactory.newSerialCard("NKF_AFF0");
        PlayableCard goldenCard = (GoldenCard) cardFactory.newSerialCard("GEK_EFF1KFFP__");
        GoalCard goalCard = (GoalCard) cardFactory.newSerialCard("O2FFF");
        cardList.add(normalCard2);
        cardList.add(normalCard1);
        cardList.add(goldenCard);

        player.setName("NameTest");
        player.setColor(new Color(16, 103, 1));
        player.setScore(10);
        player.setOnHandCard(cardList);
        player.setStatus(false);
        player.setPersonalGoal(goalCard);
        System.out.println(jsonConverter.objectToJSON(player));
        assert(jsonConverter.objectToJSON(player).compareTo(str)==0);
    }

    @Test
    @DisplayName("Converting JSON to all types of card")
    public void jsonCardConversionTest() throws JsonException,SyntaxException{
        JsonConverter jsonConverter = new JsonConverter();
        CardFactory cardFactory = new CardFactory();
        ArrayList<CardInterface> cardListGenerated = new ArrayList<>();
        ArrayList<CardInterface> cardListConverted = new ArrayList<>();

        ArrayList<String> stringCards = new ArrayList<>();

        cardListGenerated.add(cardFactory.newSerialCard("NFEF_FF0"));
        cardListGenerated.add(cardFactory.newSerialCard("GEK_EFF1KFFP__"));
        cardListGenerated.add(cardFactory.newSerialCard("SEEEE_F0AI_PIAF"));
        cardListGenerated.add(cardFactory.newSerialCard("O2FFF"));
        cardListGenerated.add(cardFactory.newSerialCard("O2FFFD1"));

        stringCards.add("{ \"Card\": \"" + "NFEF_FF0" + "\" }");
        stringCards.add("{ \"Card\": \"" + "GEK_EFF1KFFP__" + "\" }");
        stringCards.add("{ \"Card\": \"" + "SEEEE_F0AI_PIAF" + "\" }");
        stringCards.add("{ \"Card\": \"" + "O2FFF" + "\" }");
        stringCards.add("{ \"Card\": \"" + "O2FFFD1" + "\" }");

        for (int index = 0; index < cardListGenerated.size(); index++) {
            cardListConverted.add((CardInterface) jsonConverter.JSONToObject(stringCards.get(index)));
            assert(cardListConverted.get(index).asString().compareTo(cardListGenerated.get(index).asString())==0);
        }

    }
    @Test
    @DisplayName("Converting JSON to all types of card")
    public void jsonBoardConversionTest() throws JsonException,SyntaxException {
        JsonConverter jsonConverter = new JsonConverter();
        String str = "{ \"Board\": { \"placedCards\": [ { \"Card\": \"SEEEE_F0AI_PIAF\", X0Y0}, { \"Card\": \"NFEF_FF0\", X1Y1}, { \"Card\": \"GEK_EFF1KFFP__\", X-1Y1} ] }";
        Board board = (Board) jsonConverter.JSONToObject(str);
        System.out.println(str);
        System.out.println(jsonConverter.objectToJSON(board));
        assert(jsonConverter.objectToJSON(board).compareTo(str)==0);
    }

    @Test
    @DisplayName("Deck initialiazing")
    public void jsonDeck() throws FileNotFoundException, SyntaxException {
        JsonConverter jsonConverter = new JsonConverter();
        Scanner sc = new Scanner(new File("resources/Cards.json"));
        String text = "";
        while (sc.hasNextLine())
            text = text.concat(sc.nextLine());
        jsonConverter.JSONToDeck(text,'N');


    }
}
