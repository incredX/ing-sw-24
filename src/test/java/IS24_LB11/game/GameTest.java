package IS24_LB11.game;

import IS24_LB11.game.components.*;
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
import java.util.function.Function;
import java.util.stream.Collectors;

import static IS24_LB11.game.GameMessages.*;
import static junit.framework.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
        long numNormalCard = game.getPlayers().getFirst().getHand().stream().filter(card -> card.asString().startsWith("N")).count();

        long numGoldenCard = game.getPlayers().getFirst().getHand().stream().filter(card -> card.asString().startsWith("G")).count();

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
        for (Player player: game.getPlayers()){
            assertEquals(3, player.getHand().size());
            assertEquals(0, player.getScore());
            assertEquals(-1, player.getSetup().getChosenGoalIndex());
            assertEquals(2, player.setup().getGoals().length);
            assertEquals(2, numNormalCard);
            assertEquals(1, numGoldenCard);
        }
    }

    @Test
    @DisplayName("Simulating normal turn")

    public void normalTurnSample() throws SyntaxException, FileNotFoundException, DeckException, JsonException {
        JsonConverter jsonConverter = new JsonConverter();
        int playersNumber = 4;
        long numNormalCard;
        long numGoldenCard;
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
        assertEquals(NOT_PLAYER_TURN, mex1);
        //symulate correct turn

        ArrayList<String> originalHand = (ArrayList<String>) game.getPlayers().getFirst().getHand()
                .stream().map(card -> card.asString()).collect(Collectors.toList());

        String mex2 = game.executeTurn(game.getPlayers().getFirst().name(), game.getPlayers().getFirst().getBoard().getAvailableSpots().getFirst(), game.getPlayers().getFirst().getHand().getFirst(), true, 1);

        assertEquals(VALID_TURN, mex2);

        numNormalCard = game.getPlayers().getFirst().getHand().stream().filter(card -> card.asString().startsWith("N")).count();

        numGoldenCard = game.getPlayers().getFirst().getHand().stream().filter(card -> card.asString().startsWith("G")).count();

        ArrayList<String> nextHand = (ArrayList<String>) game.getPlayers().getFirst().getHand()
                .stream().map(card -> card.asString()).collect(Collectors.toList());

        assertNotEquals(nextHand,originalHand);

        assertEquals(1, numNormalCard);
        assertEquals(2, numGoldenCard);

        String mex3 = game.executeTurn(game.getPlayers().getFirst().name(), game.getPlayers().getFirst().getBoard().getAvailableSpots().getFirst(), game.getPlayers().getFirst().getHand().getFirst(), true, 1);

        assertEquals(NOT_PLAYER_TURN, mex3);
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
            for (Player player: game.getPlayers()){
                if (game.getNormalDeck().isEmpty()) {
                    break;
                }
                else if (player.getHand().getFirst().asString().charAt(0)=='G'){
                    player.getHand().getFirst().flip();
                    assertEquals(VALID_TURN,game.executeTurn(player.name(),player.getBoard().getAvailableSpots().getFirst(),player.getHand().getFirst(),i>=10,1));
                } else
                    assertEquals(VALID_TURN, game.executeTurn(player.name(),player.getBoard().getAvailableSpots().getFirst(), player.getHand().getFirst(),i>=10,1));
            }
            if (game.getFinalTurn()){
                break;
            }
        }

//        ArrayList<Player> sortedScore = (ArrayList<Player>) game.getPlayers().clone();
//        sortedScore.sort((a,b)->Integer.compare(b.getScore(), a.getScore()));
//        //printArray(sortedScore, Player -> Player.name() + ": " + Player.getScore());

        ArrayList<Player> ranking = new ArrayList<>();
        ranking = game.getFinalRanking();
        assertEquals(null, ranking);
    }

    private <T> void printArray(ArrayList<T> array, Function<T, String> function) {
        System.out.print("{ ");
        for(T ele: array) System.out.print(function.apply(ele)+", ");
        System.out.print(" }\n");
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
            for (Player player: game.getPlayers()){
                if (player.getHand().getFirst().asString().charAt(0)=='G'){
                    assertEquals(VALID_TURN, game.executeTurn(player.name(),player.getBoard().getAvailableSpots().getFirst(),player.getHand().getFirst(),i>=10,1));
                }
                else
                    assertEquals(VALID_TURN, game.executeTurn(player.name(),player.getBoard().getAvailableSpots().getFirst(), player.getHand().getFirst(),i>=0,1));
            }
            if (game.getFinalTurn()){
                break;
            }
        }
        for(Player player: game.getPlayers()){
            assertEquals(VALID_LAST_TURN, game.executeTurn(player.name(),player.getBoard().getAvailableSpots().getFirst(), player.getHand().getFirst(),true,1));
        }

        assertEquals(GAME_ENDED, game.executeTurn(game.getPlayers().getFirst().name(),game.getPlayers().getFirst().getBoard().getAvailableSpots().getFirst(), game.getPlayers().getFirst().getHand().getFirst(),true,1));

        ArrayList<Player> sortedScore = (ArrayList<Player>) game.getPlayers().clone();
        sortedScore.sort((a,b)->Integer.compare(b.getScore(), a.getScore()));

        ArrayList<Player> ranking = new ArrayList<>();
        ranking = game.getFinalRanking();

        assertEquals(sortedScore, ranking);
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

        assertEquals(INDEX_DECK_WRONG, game.executeTurn(player.name(),player.getBoard().getAvailableSpots().getFirst(), player.getHand().getFirst(),true,9));
        assertEquals(INDEX_DECK_WRONG, game.executeTurn(player.name(),player.getBoard().getAvailableSpots().getFirst(), player.getHand().getFirst(),true,8));
        assertEquals(INDEX_DECK_WRONG, game.executeTurn(player.name(),player.getBoard().getAvailableSpots().getFirst(), player.getHand().getFirst(),true,7));
        assertEquals(INDEX_DECK_WRONG, game.executeTurn(player.name(),player.getBoard().getAvailableSpots().getFirst(), player.getHand().getFirst(),true,6));
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
                if (player.getHand().getFirst().asString().charAt(0)=='G' && i<=30){
                    player.getHand().getFirst().flip();
                    assertEquals(VALID_TURN,game.executeTurn(player.name(),player.getBoard().getAvailableSpots().getFirst(),player.getHand().getFirst(),game.getNormalDeck().size()==0,1));
                    }
                else {
                    assertEquals(VALID_TURN,game.executeTurn(player.name(), player.getBoard().getAvailableSpots().getFirst(), player.getHand().getFirst(), game.getNormalDeck().size()==0, 1));
                    }
            }
            if (game.getFinalTurn()){
                Player player = game.getPlayers().getFirst().getScore() >= game.getPlayers().get(1).getScore() ? game.getPlayers().getFirst() : game.getPlayers().get(1);
                assert(player.getScore() >= 20);
                break;
            }
        }

        for(Player player: game.getPlayers()){
            assertEquals(VALID_LAST_TURN, game.executeTurn(player.name(),player.getBoard().getAvailableSpots().getFirst(), player.getHand().getFirst(),true,1));
        }
    }
    @Test
    @DisplayName("Print player in order to understand the course of the game")
    public void pointsOnePlayer() throws SyntaxException, FileNotFoundException, DeckException, JsonException {
        int goldCounter = 0;
        int playersNumber = 1;
        Game game = new Game(playersNumber);
        //Receiving players name
        ArrayList<String> playerNames = new ArrayList<>(playersNumber);
        for (int i = 0; i < 1; i++)
            playerNames.add("Player " + (i + 1));
        game.setupGame(playerNames);
        //Receiving players Goal
        ArrayList<GoalCard> goalCardsChoosen = new ArrayList<>();
        for (int i = 0; i < 1; i++)
            goalCardsChoosen.add(game.getPlayers().getFirst().getSetup().getGoals()[i % 2]);
        ArrayList<StarterCard> starterCardsSideChoosen = new ArrayList<>();
        for (int i = 0; i < 1; i++)
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
        for (int k = 0; k < 40; k++) {
            String str=(game.executeTurn(game.getPlayers().getFirst().name(),game.getPlayers().getFirst().getBoard().getAvailableSpots().getFirst(), game.getPlayers().getFirst().getHand().getFirst(),true,1));
            System.out.println(str);
            if (str.equals("GAME ENDED"))
                break;
            System.out.println(game.getPlayers().getFirst());
        }
        System.out.println(game.getPublicGoals().getFirst().asString());
        System.out.println(game.getPublicGoals().getLast().asString());
        System.out.println(game.getPlayers());

    }
}
