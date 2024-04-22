package IS24_LB11.game;

import IS24_LB11.game.components.CardFactory;
import IS24_LB11.game.components.GoalCard;
import IS24_LB11.game.components.PlayableCard;
import IS24_LB11.game.components.StarterCard;
import IS24_LB11.game.tools.JsonConverter;
import IS24_LB11.game.tools.JsonException;
import IS24_LB11.game.utils.Color;
import IS24_LB11.game.utils.Position;
import IS24_LB11.game.utils.SyntaxException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Comparator;

public class GameTest {
    @Test
    @DisplayName("Simulating game setup")
    public void gameStart() throws SyntaxException, FileNotFoundException, DeckException {
        int playersNumber = 4;
        Game game = new Game(playersNumber);
        //Receiving players name
        ArrayList<String> playerNames = new ArrayList<>(playersNumber);
        for (int i = 0; i < 4; i++)
            playerNames.add("Player " + (i + 1));

        game.setupGame(playerNames);

        //Receiving players Goal and StartedCardFace
        ArrayList<GoalCard> goalCardsChoosen = new ArrayList<>();
        for (int i = 0; i < 4; i++)
            goalCardsChoosen.add(game.getPlayers().get(i).getSetup().getGoals()[i % 2]);
        ArrayList<StarterCard> starterCardsSideChoosen = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            if (i % 2 == 0)
                game.getPlayers().get(i).getSetup().flipStarterCard();
            starterCardsSideChoosen.add(game.getPlayers().get(i).getSetup().getStarterCard());
        }
        for (Player player: game.getPlayers())
            System.out.println(player.getSetup());

        game.chooseGoalPhase(goalCardsChoosen,starterCardsSideChoosen);

        for (Player player: game.getPlayers())
            System.out.println(player);
    }

    @Test
    @DisplayName("Simulating normal turn")
    public void normalTurnSample() throws SyntaxException, FileNotFoundException, DeckException, JsonException {
        JsonConverter jsonConverter = new JsonConverter();
        int playersNumber = 4;
        Game game = new Game(playersNumber);
        //Receiving players name
        ArrayList<String> playerNames = new ArrayList<>(playersNumber);
        for (int i = 0; i < 4; i++)
            playerNames.add("Player " + (i + 1));
        game.setupGame(playerNames);
        //Receiving players Goal
        ArrayList<GoalCard> goalCardsChoosen = new ArrayList<>();
        for (int i = 0; i < 4; i++)
            goalCardsChoosen.add(game.getPlayers().get(i).getSetup().getGoals()[i % 2]);
        ArrayList<StarterCard> starterCardsSideChoosen = new ArrayList<>();
        for (int i = 0; i < 4; i++)
            starterCardsSideChoosen.add(game.getPlayers().get(i).getSetup().getStarterCard());
        game.chooseGoalPhase(goalCardsChoosen,starterCardsSideChoosen);
        //symulate receiving error beacuse it's not player turn
        String mex1=game.executeTurn(playerNames.get(1),new Position(1,1),(PlayableCard) CardFactory.newSerialCard("NEA_AAF0"),false,2);
        System.out.println("mex1 " + mex1+ "\n");

        //symultae correct turn
        System.out.println("Before " + game.getPlayers().getFirst());
        System.out.println("card to be playerd: " + game.getPlayers().getFirst().getHand().getFirst().asString());
        String mex2 = game.executeTurn(playerNames.getFirst(),new Position(1,1),game.getPlayers().getFirst().getHand().getFirst(),true,2);
        System.out.println("After " + game.getPlayers().getFirst());
        System.out.println("mex2 " + mex2+ "\n");

        String mex3 = game.executeTurn(playerNames.get(1),new Position(1,-1),(PlayableCard) CardFactory.newSerialCard("NEA_AAF1"),false,2);
        System.out.println(game.getPlayers().get(1));
        System.out.println("mex3 " + mex3);
    }
    @Test
    @DisplayName("Simulating drawing all cards and not exectuing final round")
    public void fourTurns() throws SyntaxException, FileNotFoundException, DeckException, JsonException {
        JsonConverter jsonConverter = new JsonConverter();
        int playersNumber = 4;
        Game game = new Game(playersNumber);
        //Receiving players name
        ArrayList<String> playerNames = new ArrayList<>(playersNumber);
        for (int i = 0; i < 4; i++)
            playerNames.add("Player " + (i + 1));
        game.setupGame(playerNames);
        //Receiving players Goal
        ArrayList<GoalCard> goalCardsChoosen = new ArrayList<>();
        for (int i = 0; i < 4; i++)
            goalCardsChoosen.add(game.getPlayers().get(i).getSetup().getGoals()[i % 2]);
        ArrayList<StarterCard> starterCardsSideChoosen = new ArrayList<>();
        for (int i = 0; i < 4; i++)
            starterCardsSideChoosen.add(game.getPlayers().get(i).getSetup().getStarterCard());
        game.chooseGoalPhase(goalCardsChoosen,starterCardsSideChoosen);
        //symulate receiving error beacuse it's not player turn

        for (int i = 0; i < 20; i++) {
            for (Player player: game.getPlayers())
                System.out.println(player);
            for (Player player: game.getPlayers()){
                if (player.getHand().getFirst().asString().charAt(0)=='G'){
                    player.getHand().getFirst().flip();
                    System.out.printf(game.executeTurn(player.name(),player.getBoard().getAvailableSpots().getFirst(),player.getHand().getFirst(),i>=10,1));
                }
                else
                    System.out.printf(game.executeTurn(player.name(),player.getBoard().getAvailableSpots().getFirst(), player.getHand().getFirst(),i>=10,1));
                System.out.println(" " + game.getTurn() + ") FinalTurn: " + game.getFinalTurn()+ " " + player.getBoard().getAvailableSpots().toString());
                System.out.println(i+" "+game.getNormalDeck().isEmpty() +" "+game.getNormalDeck().size() +" "+game.getGoldenDeck().size() +" "+game.getGoldenDeck().isEmpty());

            }
            if (game.getFinalTurn()){
                System.out.println("FINISHED");
                break;
            }
        }
        ArrayList<Player> ranking = new ArrayList<>();
        ranking = game.finalGamePhase();
        for (Player player: ranking)
            System.out.println(player.name());
    }
}
