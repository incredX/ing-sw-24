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
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class GameTest {
    @Test
    @DisplayName("Simulating game setup")
    public void gameStart() throws SyntaxException, FileNotFoundException, DeckException {
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

    @Test
    public void stringComparison() throws SyntaxException, FileNotFoundException {
        Game game = new Game(1);
        String s1 = "SEPIE_F0I__FPIA";
        String s2 = "SEPIE_B0I__FPIA";
        String s3 = "SEPIEAF0I__FPIA";
        String s4 = "SEPIE_B1I__FPIA";

        System.out.println(game.numberCharNotEqualInSamePosition(s1,s2));
        System.out.println(game.numberCharNotEqualInSamePosition(s1,s3));
        System.out.println(game.numberCharNotEqualInSamePosition(s1,s4));
        ArrayList<Integer> ranking = new ArrayList<>();
        ranking.add(9);
        ranking.add(3);
        ranking.add(12);
        ranking.stream().sorted(Comparator.comparingInt(x -> x));
        System.out.println(ranking);
    }

    @Test
    @DisplayName("Simulating drawing cards and reaching final round with points trigger")
    public void variousTurns() throws SyntaxException, FileNotFoundException, DeckException, JsonException {
        JsonConverter jsonConverter = new JsonConverter();
        int playersNumber = 2;
        Game game = new Game(playersNumber);
        //Receiving players name
        ArrayList<String> playerNames = new ArrayList<>(playersNumber);
        for (int i = 0; i < 2; i++)
            playerNames.add("Player " + (i + 1));
        game.setupGame(playerNames);
        //Receiving players Goal
        ArrayList<GoalCard> goalCardsChoosen = new ArrayList<>();
        for (int i = 0; i < 2; i++)
            goalCardsChoosen.add(game.getPlayers().get(i).getSetup().getGoals()[i % 2]);
        ArrayList<StarterCard> starterCardsSideChoosen = new ArrayList<>();
        for (int i = 0; i < 2; i++)
            starterCardsSideChoosen.add(game.getPlayers().get(i).getSetup().getStarterCard());
        starterCardsSideChoosen.stream().forEach(x->x.flip());
        game.chooseGoalPhase(goalCardsChoosen,starterCardsSideChoosen);
        //symulate receiving error beacuse it's not player turn

        Board board1 = new Board();
        board1.start(game.getPlayers().getFirst().getSetup().getStarterCard());
        for (int i = 0; i < 5; i++) {
            board1.placeCard(CardFactory.newPlayableCard("NIIE_IB0"), board1.getAvailableSpots().getFirst());
        }
        for (int i = 0; i < 5; i++) {
            board1.placeCard(CardFactory.newPlayableCard("NIIE_AB0"), board1.getAvailableSpots().getFirst());
        }
        for (int i = 0; i < 5; i++) {
            board1.placeCard(CardFactory.newPlayableCard("NIIE_FB0"), board1.getAvailableSpots().getFirst());
        }
        for (int i = 0; i < 5; i++) {
            board1.placeCard(CardFactory.newPlayableCard("NIIE_PB0"), board1.getAvailableSpots().getFirst());
        }

        game.getPlayers().getFirst().setBoard(board1);

        Board board2 = new Board();
        board2.start(game.getPlayers().getFirst().getSetup().getStarterCard());
        for (int i = 0; i < 5; i++) {
            board2.placeCard(CardFactory.newPlayableCard("NIIE_IB0"), board2.getAvailableSpots().getFirst());
        }
        for (int i = 0; i < 5; i++) {
            board2.placeCard(CardFactory.newPlayableCard("NIIE_AB0"), board2.getAvailableSpots().getFirst());
        }
        for (int i = 0; i < 5; i++) {
            board2.placeCard(CardFactory.newPlayableCard("NIIE_FB0"), board2.getAvailableSpots().getFirst());
        }
        for (int i = 0; i < 5; i++) {
            board2.placeCard(CardFactory.newPlayableCard("NIIE_PB0"), board2.getAvailableSpots().getFirst());
        }
        game.getPlayers().getLast().setBoard(board2);

        for (int i = 0; i < 20; i++) {
            for (Player player: game.getPlayers())
                System.out.println(player);
            for (Player player: game.getPlayers()){
                if (player.getHand().getFirst().asString().charAt(0)=='G'){
                    System.out.printf(game.executeTurn(player.name(),player.getBoard().getAvailableSpots().getFirst(),player.getHand().getFirst(),i>=0,1));
                }
                else
                    System.out.printf(game.executeTurn(player.name(),player.getBoard().getAvailableSpots().getFirst(), player.getHand().getFirst(),i>=0,1));
                System.out.println(" " + game.getTurn() + ") FinalTurn: " + game.getFinalTurn()+ " " + player.getBoard().getAvailableSpots().toString());
                System.out.println(i+" "+game.getNormalDeck().isEmpty() +" "+game.getNormalDeck().size() +" "+game.getGoldenDeck().size() +" "+game.getGoldenDeck().isEmpty());
            }
            if (game.getFinalTurn()){
                System.out.println("FINISHED");
                break;
            }
        }
        for(Player player: game.getPlayers()){
            System.out.println(game.executeTurn(player.name(),player.getBoard().getAvailableSpots().getFirst(), player.getHand().getFirst(),true,1));
            System.out.println(player);
        }

        System.out.println(game.finalGamePhase());
        System.out.println(game.getPlayers());
    }

    @Test
    void invalidExecution() throws SyntaxException, FileNotFoundException, DeckException, JsonException {
        JsonConverter jsonConverter = new JsonConverter();
        int playersNumber = 2;
        Game game = new Game(playersNumber);
        //Receiving players name
        ArrayList<String> playerNames = new ArrayList<>(playersNumber);
        for (int i = 0; i < 2; i++)
            playerNames.add("Player " + (i + 1));
        game.setupGame(playerNames);
        System.out.println(playerNames);
        //Receiving players Goal
        ArrayList<GoalCard> goalCardsChoosen = new ArrayList<>();
        for (int i = 0; i < 2; i++)
            goalCardsChoosen.add(game.getPlayers().get(i).getSetup().getGoals()[i % 2]);
        ArrayList<StarterCard> starterCardsSideChoosen = new ArrayList<>();
        for (int i = 0; i < 2; i++)
            starterCardsSideChoosen.add(game.getPlayers().get(i).getSetup().getStarterCard());
        starterCardsSideChoosen.stream().forEach(x->x.flip());
        game.chooseGoalPhase(goalCardsChoosen,starterCardsSideChoosen);

        Player player = game.getPlayers().getFirst();

        System.out.println(player);
        System.out.println(game.executeTurn(player.name(),player.getBoard().getAvailableSpots().getFirst(), player.getHand().getFirst(),true,8));
        System.out.println(player);
        System.out.println(game.executeTurn(player.name(),player.getBoard().getAvailableSpots().getFirst(), player.getHand().getFirst(),true,9));
        System.out.println(player);
        System.out.println(game.executeTurn(player.name(),player.getBoard().getAvailableSpots().getFirst(), player.getHand().getFirst(),true,7));
        System.out.println(player);
        System.out.println(game.executeTurn(player.name(),player.getBoard().getAvailableSpots().getFirst(), player.getHand().getFirst(),true,6));
        System.out.println(player);
    }

    @Test
    void testTotalGame() throws DeckException, SyntaxException, FileNotFoundException, JsonException {
        JsonConverter jsonConverter = new JsonConverter();
        int goldCounter = 0;
        int playersNumber = 2;
        Game game = new Game(playersNumber);
        //Receiving players name
        ArrayList<String> playerNames = new ArrayList<>(playersNumber);
        for (int i = 0; i < 2; i++)
            playerNames.add("Player " + (i + 1));
        game.setupGame(playerNames);
        //Receiving players Goal
        ArrayList<GoalCard> goalCardsChoosen = new ArrayList<>();
        for (int i = 0; i < 2; i++)
            goalCardsChoosen.add(game.getPlayers().get(i).getSetup().getGoals()[i % 2]);
        ArrayList<StarterCard> starterCardsSideChoosen = new ArrayList<>();
        for (int i = 0; i < 2; i++)
            starterCardsSideChoosen.add(game.getPlayers().get(i).getSetup().getStarterCard());
        starterCardsSideChoosen.stream().forEach(x->x.flip());
        game.chooseGoalPhase(goalCardsChoosen,starterCardsSideChoosen);

        Board board1 = new Board();
        board1.start(game.getPlayers().getFirst().getSetup().getStarterCard());
        for (int i = 0; i < 5; i++) {
            board1.placeCard(CardFactory.newPlayableCard("NIIE_IB0"), board1.getAvailableSpots().getFirst());
        }
        for (int i = 0; i < 5; i++) {
            board1.placeCard(CardFactory.newPlayableCard("NIIE_AB0"), board1.getAvailableSpots().getFirst());
        }
        for (int i = 0; i < 5; i++) {
            board1.placeCard(CardFactory.newPlayableCard("NIIE_FB0"), board1.getAvailableSpots().getFirst());
        }
        for (int i = 0; i < 5; i++) {
            board1.placeCard(CardFactory.newPlayableCard("NIIE_PB0"), board1.getAvailableSpots().getFirst());
        }

        game.getPlayers().getFirst().setBoard(board1);

        Board board2 = new Board();
        board2.start(game.getPlayers().getFirst().getSetup().getStarterCard());
        for (int i = 0; i < 5; i++) {
            board2.placeCard(CardFactory.newPlayableCard("NIIE_IB0"), board2.getAvailableSpots().getFirst());
        }
        for (int i = 0; i < 5; i++) {
            board2.placeCard(CardFactory.newPlayableCard("NIIE_AB0"), board2.getAvailableSpots().getFirst());
        }
        for (int i = 0; i < 5; i++) {
            board2.placeCard(CardFactory.newPlayableCard("NIIE_FB0"), board2.getAvailableSpots().getFirst());
        }
        for (int i = 0; i < 5; i++) {
            board2.placeCard(CardFactory.newPlayableCard("NIIE_PB0"), board2.getAvailableSpots().getFirst());
        }
        game.getPlayers().getLast().setBoard(board2);


        for (int i = 0; i < 40; i++) {
            for (Player player: game.getPlayers()){
                if (player.getHand().getFirst().asString().charAt(0)=='G' && i<=32){
                    player.getHand().getFirst().flip();
                    System.out.printf(game.executeTurn(player.name(),player.getBoard().getAvailableSpots().getFirst(),player.getHand().getFirst(),i>=20,1));
                    System.out.println(" " + game.getTurn() + ") FinalTurn: " + game.getFinalTurn());
                    System.out.println(i + " " + game.getNormalDeck().isEmpty() + " " + game.getNormalDeck().size() + " " + game.getGoldenDeck().size() + " " + game.getGoldenDeck().isEmpty());
                }
                else {
                    System.out.printf(game.executeTurn(player.name(), player.getBoard().getAvailableSpots().getFirst(), player.getHand().getFirst(), i >= 20, 1));
                    System.out.println(" " + game.getTurn() + ") FinalTurn: " + game.getFinalTurn());
                    System.out.println(i + " " + game.getNormalDeck().isEmpty() + " " + game.getNormalDeck().size() + " " + game.getGoldenDeck().size() + " " + game.getGoldenDeck().isEmpty());
                }
            }
            if (game.getFinalTurn()){
                System.out.println("FINISHED");
                break;
            }
        }
        for(Player player: game.getPlayers()){
            System.out.println(game.executeTurn(player.name(),player.getBoard().getAvailableSpots().getFirst(), player.getHand().getFirst(),true,1));
            System.out.println(" " + game.getTurn() + ") FinalTurn: " + game.getFinalTurn());
            System.out.println(player);
        }
        System.out.println(game.getNormalDeck().getCards().stream().map(card -> card.asString()).collect(Collectors.toList()));
        System.out.println(game.getGoldenDeck().getCards().stream().map(card -> card.asString()).collect(Collectors.toList()));
        System.out.println(game.getNormalDeck().size());
        System.out.println(game.getGoldenDeck().size());

    }
}
