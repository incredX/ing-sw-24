package IS24_LB11.tools;

import IS24_LB11.game.Board;
import IS24_LB11.game.Deck;
import IS24_LB11.game.Player;
import IS24_LB11.game.PlayerSetup;
import IS24_LB11.game.components.*;
import IS24_LB11.game.tools.JsonConverter;
import IS24_LB11.game.tools.JsonException;
import IS24_LB11.game.utils.Color;
import IS24_LB11.game.utils.Position;
import IS24_LB11.game.utils.SyntaxException;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class JsonConverterTest {
    @Test
    @DisplayName("Converting all types of cards")
    public void cardConversionTest() throws JsonException, SyntaxException{
        JsonConverter jsonConverter = new JsonConverter();
        ArrayList<String> stringCards = new ArrayList<>();
        CardFactory cardFactory = new CardFactory();
        ArrayList<CardInterface> cardList = new ArrayList<>();

        stringCards.add("{\"Card\":\"" + "NFEF_FF0" + "\"}");
        stringCards.add("{\"Card\":\"" + "GEK_EFF1KFFP__" + "\"}");
        stringCards.add("{\"Card\":\"" + "SEEEE_F0AI_PIAF" + "\"}");
        stringCards.add("{\"Card\":\"" + "O2FFF" + "\"}");
        stringCards.add("{\"Card\":\"" + "O2FFFD1" + "\"}");
        
        cardList.add(cardFactory.newSerialCard("NFEF_FF0"));
        cardList.add(cardFactory.newSerialCard("GEK_EFF1KFFP__"));
        cardList.add(cardFactory.newSerialCard("SEEEE_F0AI_PIAF"));
        cardList.add(cardFactory.newSerialCard("O2FFF"));
        cardList.add(cardFactory.newSerialCard("O2FFFD1"));
        
        for (int index = 0; index < cardList.size(); index++) {
            System.out.println(jsonConverter.objectToJSON(cardList.get(index)));
            assert(jsonConverter.objectToJSON(cardList.get(index)).compareTo(stringCards.get(index))==0);
        }
    }

    @Test
    @DisplayName("Converting object board to json")
    public void boardConversionTest() throws JsonException, SyntaxException{
        String str = "{\"Board\":{\"placedCards\":[{\"Card\":\"SEEEE_F0AI_PIAF\",\"Position\":\"X0Y0\"},{\"Card\":\"NFEF_FF0\",\"Position\":\"X1Y1\"},{\"Card\":\"GEK_EFB1KFFP__\",\"Position\":\"X-1Y1\"}]}}";
        JsonConverter jsonConverter = new JsonConverter();
        Board board = new Board();
        CardFactory cardFactory = new CardFactory();
        StarterCard starterCard = (StarterCard) cardFactory.newSerialCard("SEEEE_F0AI_PIAF");
        NormalCard normalCard = (NormalCard) cardFactory.newSerialCard("NFEF_FF0");
        GoldenCard goldenCard = (GoldenCard) cardFactory.newSerialCard("GEK_EFB1KFFP__");
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
        JsonConverter jsonConverter = new JsonConverter();
        CardFactory cardFactory = new CardFactory();
        GoalCard[] goalCards = new GoalCard[2];
        goalCards[0] = (GoalCard) cardFactory.newSerialCard("O2FFF");
        goalCards[1] = (GoalCard) cardFactory.newSerialCard("O2PPP");
        ArrayList<PlayableCard> playerHand = new ArrayList<>();
        playerHand.add((PlayableCard) cardFactory.newSerialCard("NFEF_FF0"));
        playerHand.add((PlayableCard) cardFactory.newSerialCard("N_FEFFF0"));
        playerHand.add((PlayableCard) cardFactory.newSerialCard("GEK_EFB1KFFP__"));

        PlayerSetup playerSetup = new PlayerSetup((StarterCard) cardFactory.newSerialCard("SEEEE_F0AI_PIAF"),goalCards,playerHand,Color.fromInt(1));
        Player player = new Player("Test",playerSetup);
        player.getSetup().selectGoal(goalCards[1]);
        player.applySetup();
        System.out.println(jsonConverter.objectToJSON(player));
        }

    @Test
    @DisplayName("Converting object playerSetup to json")
    public void playerSetupConversionTest() throws JsonException,SyntaxException{
        JsonConverter jsonConverter = new JsonConverter();
        CardFactory cardFactory = new CardFactory();
        GoalCard[] goalCards = new GoalCard[2];
        goalCards[0] = (GoalCard) cardFactory.newSerialCard("O2FFF");
        goalCards[1] = (GoalCard) cardFactory.newSerialCard("O2PPP");
        ArrayList<PlayableCard> playerHand = new ArrayList<>();
        playerHand.add((PlayableCard) cardFactory.newSerialCard("NFEF_FF0"));
        playerHand.add((PlayableCard) cardFactory.newSerialCard("N_FEFFF0"));
        playerHand.add((PlayableCard) cardFactory.newSerialCard("GEK_EFF1KFFP__"));

        PlayerSetup playerSetup = new PlayerSetup((StarterCard) cardFactory.newSerialCard("SEEEE_F0AI_PIAF"),goalCards,playerHand,Color.fromInt(1));
        playerSetup.selectGoal(goalCards[1]);
        System.out.println(jsonConverter.objectToJSON(playerSetup));
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

        stringCards.add("{\"Card\":\"" + "NFEF_FF0" + "\"}");
        stringCards.add("{\"Card\":\"" + "GEK_EFF1KFFP__" + "\"}");
        stringCards.add("{\"Card\":\"" + "SEEEE_F0AI_PIAF" + "\"}");
        stringCards.add("{\"Card\":\"" + "O2FFF" + "\"}");
        stringCards.add("{\"Card\":\"" + "O2FFFD1" + "\"}");

        for (int index = 0; index < cardListGenerated.size(); index++) {
            cardListConverted.add((CardInterface) jsonConverter.JSONToObject(stringCards.get(index)));
            assert(cardListConverted.get(index).asString().compareTo(cardListGenerated.get(index).asString())==0);
        }

    }
    @Test
    @DisplayName("Converting JSON to Board")
    public void jsonBoardConversionTest() throws JsonException,SyntaxException {
        JsonConverter jsonConverter = new JsonConverter();
        //String str = "{\"Board\":{\"placedCards\":[{\"Card\":\"SEEEE_F0AI_PIAF\",\"Position\":\"X0Y0\"},{\"Card\":\"NFEF_FF0\",\"Position\":\"X1Y1\"},{\"Card\":\"GEK_EFF1KFFP__\",\"Position\":\"X-1Y1\"}]}}";
        String str = "{\"Board\":{\"placedCards\":[{\"Card\":\"SEEEE_F0AI_PIAF\",\"Position\":\"X0Y0\"}]}}";
        Board board = (Board) jsonConverter.JSONToObject(str);
        System.out.println(str);
        System.out.println(jsonConverter.objectToJSON(board));
        assert(jsonConverter.objectToJSON(board).compareTo(str)==0);
    }

    @Test
    @DisplayName("Converting JSON to player")
    public void jsonPlayerConversionTest() throws JsonException, SyntaxException {
        JsonConverter jsonConverter = new JsonConverter();
        String str= "{\"Player\":{\"name\":\"Test\",\"Color\":\"GREEN\",\"Hand\":[{\"Card\":\"NFEF_FF0\"},{\"Card\":\"N_FEFFF0\"},{\"Card\":\"GEK_EFB1KFFP__\"}],\"PersonalGoal\":{\"Card\":\"O2PPP\"},\"Score\":\"0\",\"PlayerSetup\":{\"StarterCard\":{\"Card\":\"SEEEE_F0AI_PIAF\"},\"Goals\":[{\"Card\":\"O2FFF\"},{\"Card\":\"O2PPP\"}],\"Hand\":[{\"Card\":\"NFEF_FF0\"},{\"Card\":\"N_FEFFF0\"},{\"Card\":\"GEK_EFF1KFFP__\"}],\"chosenGoalIndex\":\"1\"},\"Board\":{\"placedCards\":[{\"Card\":\"SEEEE_F0AI_PIAF\",\"Position\":\"X0Y0\"}]}}}";
        Player player = (Player) jsonConverter.JSONToObject(str);
        System.out.println(str);
        System.out.println(jsonConverter.objectToJSON(player));
        assert(str.compareTo(jsonConverter.objectToJSON(player))==0);
    }
    @Test
    @DisplayName("Converting JSON to playerSetup")
    public void jsonPlayerSetupConversionTest() throws JsonException, SyntaxException {
        JsonConverter jsonConverter = new JsonConverter();
        String str= "{\"PlayerSetup\":{\"StarterCard\":{\"Card\":\"SEEEE_F0AI_PIAF\"},\"Goals\":[{\"Card\":\"O2FFF\"},{\"Card\":\"O2PPP\"}],\"Hand\":[{\"Card\":\"NFEF_FF0\"},{\"Card\":\"N_FEFFF0\"},{\"Card\":\"GEK_EFF1KFFP__\"}],\"chosenGoalIndex\":\"0\"}}";
        PlayerSetup playerSetup = (PlayerSetup) jsonConverter.JSONToObject(str);
        System.out.println(str);
        System.out.println(jsonConverter.objectToJSON(playerSetup));
    }

    @Test
    @DisplayName("Deck initialiazing")
    public void jsonDeck() throws FileNotFoundException, SyntaxException {
        JsonConverter jsonConverter = new JsonConverter();

        Deck deckNormal = jsonConverter.JSONToDeck('N');
        for (CardInterface cardInterface: deckNormal.getCards())
            System.out.println(cardInterface.asString());
        System.out.println(deckNormal.getCards().size());

        Deck deckGold = jsonConverter.JSONToDeck('G');
        for (CardInterface cardInterface: deckGold.getCards())
            System.out.println(cardInterface.asString());
        System.out.println(deckGold.getCards().size());

        Deck deckStarter = jsonConverter.JSONToDeck('S');
        for (CardInterface cardInterface: deckStarter.getCards())
            System.out.println(cardInterface.asString());
        System.out.println(deckStarter.getCards().size());

        Deck goalDeck = jsonConverter.JSONToDeck('O');
        for (CardInterface cardInterface: goalDeck.getCards())
            System.out.println(cardInterface.asString());
        System.out.println(goalDeck.getCards().size());


    }

    @Test
    @DisplayName("json server")
    public void jsonServer() throws SyntaxException, JsonException {
        Gson gson = new Gson();
        JsonConverter jsonConverter = new JsonConverter();
        Board board = new Board();
        CardFactory cardFactory = new CardFactory();
        StarterCard starterCard = (StarterCard) cardFactory.newSerialCard("SEEEE_F0AI_PIAF");
        NormalCard normalCard = (NormalCard) cardFactory.newSerialCard("NFEF_FF0");
        GoldenCard goldenCard = (GoldenCard) cardFactory.newSerialCard("GEK_EFF1KFFP__");
        board.start(starterCard);
        board.placeCard(normalCard, new Position(1,1));
        board.placeCard(goldenCard, new Position(-1,1));
        String string = jsonConverter.objectToJSON(board);
        System.out.println(string);
        JsonObject jsonObject = gson.fromJson(string,JsonObject.class);
        System.out.println(jsonObject.get("Board"));
        }
}
