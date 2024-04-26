package IS24_LB11.tools;

import IS24_LB11.game.*;
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
import java.util.Arrays;
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

        stringCards.add("{\"normalCard\":\"" + "NFEF_FF0" + "\"}");
        stringCards.add("{\"goldenCard\":\"" + "GEK_EFF1KFFP__" + "\"}");
        stringCards.add("{\"starterCard\":\"" + "SEEEE_F0AI_PIAF" + "\"}");
        stringCards.add("{\"goalCard\":\"" + "O2FFF" + "\"}");
        stringCards.add("{\"goalCard\":\"" + "O2FFFD1" + "\"}");
        
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
    @DisplayName("Converting placedCard to JSON")
    public void placedCardConversionTest() throws SyntaxException, JsonException {
        PlacedCard placedCard = new PlacedCard(CardFactory.newPlayableCard("NFEF_FF0"),new Position(1,1));
        String str = new JsonConverter().objectToJSON(placedCard);
        System.out.println(str);
    }
    @Test
    @DisplayName("Converting json to PlacedCar")
    public  void jsonPlacedCardConversion() throws JsonException, SyntaxException {
        String str="{\"PlacedCard\":{\"normalCard\":\"NFEF_FF0\",\"Position\":\"X1Y1\"}}";
        PlacedCard placedCard= (PlacedCard) new JsonConverter().JSONToObject(str);
        System.out.println(new JsonConverter().objectToJSON(placedCard));
    }

    @Test
    @DisplayName("Converting object board to json")
    public void boardConversionTest() throws JsonException, SyntaxException{
        String str = "{\"Board\":{\"placedCards\":[{\"PlacedCard\":{\"starterCard\":\"SEEEE_F0AI_PIAF\",\"Position\":\"X0Y0\"}},{\"PlacedCard\":{\"normalCard\":\"NFEF_FF0\",\"Position\":\"X1Y1\"}},{\"PlacedCard\":{\"goldenCard\":\"GEK_EFB1KFFP__\",\"Position\":\"X-1Y1\"}}]}}";
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

        stringCards.add("{\"normalCard\":\"" + "NFEF_FF0" + "\"}");
        stringCards.add("{\"goldenCard\":\"" + "GEK_EFF1KFFP__" + "\"}");
        stringCards.add("{\"starterCard\":\"" + "SEEEE_F0AI_PIAF" + "\"}");
        stringCards.add("{\"goalCard\":\"" + "O2FFF" + "\"}");
        stringCards.add("{\"goalCard\":\"" + "O2FFFD1" + "\"}");

        for (int index = 0; index < cardListGenerated.size(); index++) {
            cardListConverted.add((CardInterface) jsonConverter.JSONToObject(stringCards.get(index)));
            assert(cardListConverted.get(index).asString().compareTo(cardListGenerated.get(index).asString())==0);
        }

    }
    @Test
    @DisplayName("Converting JSON to Board")
    public void jsonBoardConversionTest() throws JsonException,SyntaxException {
        JsonConverter jsonConverter = new JsonConverter();
        String str = "{\"Board\":{\"placedCards\":[{\"PlacedCard\":{\"starterCard\":\"SEEEE_F0AI_PIAF\",\"Position\":\"X0Y0\"}},{\"PlacedCard\":{\"normalCard\":\"NFEF_FF0\",\"Position\":\"X1Y1\"}},{\"PlacedCard\":{\"goldenCard\":\"GEK_EFB1KFFP__\",\"Position\":\"X-1Y1\"}}]}}";
        Board board = (Board) jsonConverter.JSONToObject(str);
        assert(jsonConverter.objectToJSON(board).compareTo(str)==0);
    }

    @Test
    @DisplayName("Converting JSON to player")
    public void jsonPlayerConversionTest() throws JsonException, SyntaxException {
        JsonConverter jsonConverter = new JsonConverter();
        String str= "{\"Player\":{\"name\":\"Test\",\"Color\":\"GREEN\",\"Hand\":[{\"normalCard\":\"NFEF_FF0\"},{\"normalCard\":\"N_FEFFF0\"},{\"goldenCard\":\"GEK_EFB1KFFP__\"}],\"PersonalGoal\":{\"goalCard\":\"O2PPP\"},\"Score\":\"0\",\"PlayerSetup\":{\"StarterCard\":{\"starterCard\":\"SEEEE_F0AI_PIAF\"},\"Goals\":[{\"goalCard\":\"O2FFF\"},{\"goalCard\":\"O2PPP\"}],\"Color\":\"GREEN\",\"Hand\":[{\"normalCard\":\"NFEF_FF0\"},{\"normalCard\":\"N_FEFFF0\"},{\"goldenCard\":\"GEK_EFB1KFFP__\"}],\"chosenGoalIndex\":\"1\"},\"Board\":{\"placedCards\":[{\"PlacedCard\":{\"starterCard\":\"SEEEE_F0AI_PIAF\",\"Position\":\"X0Y0\"}}]}}}";
        Player player = (Player) jsonConverter.JSONToObject(str);
        assert(str.compareTo(jsonConverter.objectToJSON(player))==0);
    }
    @Test
    @DisplayName("Converting JSON to playerSetup")
    public void jsonPlayerSetupConversionTest() throws JsonException, SyntaxException {
        JsonConverter jsonConverter = new JsonConverter();
        String str= "{\"PlayerSetup\":{\"StarterCard\":{\"starterCard\":\"SEEEE_F0AI_PIAF\"},\"Goals\":[{\"goalCard\":\"O2FFF\"},{\"goalCard\":\"O2PPP\"}],\"Color\":\"GREEN\",\"Hand\":[{\"normalCard\":\"NFEF_FF0\"},{\"normalCard\":\"N_FEFFF0\"},{\"goldenCard\":\"GEK_EFF1KFFP__\"}],\"chosenGoalIndex\":\"1\"}}";
        PlayerSetup playerSetup = (PlayerSetup) jsonConverter.JSONToObject(str);
        assert (str.compareTo(jsonConverter.objectToJSON(playerSetup))==0);
    }

    @Test
    @DisplayName("Deck initialiazing")
    public void jsonDeck() throws FileNotFoundException, SyntaxException {
        JsonConverter jsonConverter = new JsonConverter();
        // normal deck checking
        ArrayList<String> deckNormalCardString = new ArrayList<>(Arrays.asList("NFEF_FF0", "NFF_EFF0", "NE_FFFF0", "N_FEFFF0", "N_QPFFF0", "NKF_AFF0", "NFIMEFF0", "NEFE_FF1", "NF_EEFF1", "N_EFEFF1", "NPEP_PF0", "NPP_EPF0", "NE_PPPF0", "N_PEPPF0", "N_IQPPF0", "NFP_KPF0",
                "NM_PAPF0", "NEEP_PF1", "NEE_PPF1", "N_PEEPF1", "NAAE_AF0", "N_EAAAF0", "NA_AEAF0", "NEA_AAF0", "N_IKAAF0", "NPA_MAF0", "NQ_AFAF0", "N_EAEAF1", "NE_EAAF1", "NEAE_AF1", "NIIE_IF0", "N_EIIIF0", "NI_IEIF0", "NEI_IIF0",
                "N_QAIIF0", "NMI_FIF0", "NIPK_IF0", "NI_EEIF1", "NEE_IIF1", "N_IEEIF1"));
        Deck deckNormal = jsonConverter.JSONToDeck('N');
        int checkNormalCard = (int) deckNormal.getCards()
                .stream()
                .filter(x->deckNormalCardString.indexOf(x.asString())==-1)
                .count();
        assertEquals(0,checkNormalCard);
        assertEquals(40,deckNormal.size());

        ArrayList<String> deckGoldenCardString = new ArrayList<>(Arrays.asList("G_EEQFF1QFFA__", "GEK_EFF1KFFP__", "GMEE_FF1MFFI__", "GEE_EFF2EFFFA_", "GEEE_FF2EFFFP_", "GE_EEFF2EFFFI_", "GE_K_FF3_FFF__", "GQE__FF3_FFF__", "G_M_EFF3_FFF__", "GE_E_FF5_FFFFF",
                "GQEE_PF1QPPI__", "GEM_EPF1MPPF__", "GE_KEPF1KPPA__", "G_EEEPF2EPPPI_", "GEEE_PF2EPPPA_", "GE_EEPF2EPPPF_", "GE_Q_PF3_PPP__", "GME__PF3_PPP__", "G_K_EPF3_PPP__", "GEE__PF5_PPPPP",
                "GKEE_AF1KAAI__", "G_EEMAF1MAAP__", "GE_QEAF1QAAF__", "GEE_EAF2EAAAI_", "GE_EEAF2EAAAF_", "G_EEEAF2EAAAP_", "GE_M_AF3_AAA__", "GEK__AF3_AAA__", "G_E_QAF3_AAA__", "G_E_EAF5_AAAAA",
                "GEQ_EIF1QIIP__", "GE_MEIF1MIIA__", "G_EEKIF1KIIF__", "GEE_EIF2EIIIA_", "GEEE_IF2EIIIP_", "GE_EEIF2EIIIF_", "GK_E_IF3_III__", "GEM__IF3_III__", "G__QEIF3_III__", "GEE__IF5_IIIII"));
        Deck deckGold = jsonConverter.JSONToDeck('G');
        int checkGoldenCard = (int) deckGold.getCards()
                .stream()
                .filter(x->deckGoldenCardString.indexOf(x.asString())==-1)
                .count();
        assertEquals(0,checkGoldenCard);
        assertEquals(40,deckGold.size());

        ArrayList<String> deckStarterCardString = new ArrayList<>(Arrays.asList("SEPIE_F0I__FPIA", "SAEEF_F0F__PAFI", "SEEEE_F0PF_IAFP", "SEEEE_F0AI_PIAF", "SEE___F0AIPIFPA", "SEE___F0PAFFAPI"));
        Deck deckStarter = jsonConverter.JSONToDeck('S');
        int checkStarterCard = (int) deckStarter.getCards()
                .stream()
                .filter(x->deckStarterCardString.indexOf(x.asString())==-1)
                .count();
        assertEquals(0,checkStarterCard);
        assertEquals(6,deckStarter.size());

        ArrayList<String> deckGoalCardString = new ArrayList<>(Arrays.asList("O2FFF", "O2PPP", "O2AAA", "O2III", "O3QKM", "O2MM_", "O2KK_", "O2QQ_", "O2FFFD1", "O2PPPD0", "O2AAAD1", "O2IIID0", "O3PFFL3", "O3IPPL2", "O3FAAL1", "O3AIIL0"));
        Deck deckGoal = jsonConverter.JSONToDeck('O');
        int checkGoalCard = (int) deckGoal.getCards()
                .stream()
                .filter(x->deckGoalCardString.indexOf(x.asString())==-1)
                .count();
        assertEquals(0,checkGoalCard);
        assertEquals(16,deckGoal.size());
    }
}
