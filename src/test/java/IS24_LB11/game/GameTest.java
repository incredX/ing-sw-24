package IS24_LB11.game;

import IS24_LB11.game.components.CardFactory;
import IS24_LB11.game.components.GoalCard;
import IS24_LB11.game.components.PlayableCard;
import IS24_LB11.game.components.StarterCard;
import IS24_LB11.game.tools.JsonConverter;
import IS24_LB11.game.tools.JsonException;
import IS24_LB11.game.utils.Position;
import IS24_LB11.game.utils.SyntaxException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.function.Function;
import java.util.stream.Collectors;

import static IS24_LB11.game.GameMessages.*;
import static junit.framework.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class GameTest {
    @Test
    @DisplayName("Simulating game setup checking the hand size, the inizialized score, the proper shown goal lenght, the porper number of NormalCard and GoldenCard ")
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
            assertEquals(0, player.getSetup().getChosenGoalIndex()); //0 in order to have the cursor inizialized
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
    }


    @Test
    @DisplayName("Test created in order to make some check related to the CLI development that check the drawn card is not equal to teh remaining cards of the deck")
    void testValidDraw() throws SyntaxException, FileNotFoundException, DeckException, JsonException {
        JsonConverter jsonConverter = new JsonConverter();
        int playersNumber = 2;
        Game game = new Game(playersNumber);
        ArrayList<String> playerNames = new ArrayList<>(playersNumber);
        for (int i = 0; i < 2; i++)
            playerNames.add("Player " + (i + 1));
        game.setupGame(playerNames);
        ArrayList<GoalCard> goalCardsChoosen = new ArrayList<>();
        for (int i = 0; i < 2; i++)
            goalCardsChoosen.add(game.getPlayers().get(i).getSetup().getGoals()[i % 2]);
        ArrayList<StarterCard> starterCardsSideChoosen = new ArrayList<>();
        for (int i = 0; i < 2; i++)
            starterCardsSideChoosen.add(game.getPlayers().get(i).getSetup().getStarterCard());
        starterCardsSideChoosen.stream().forEach(x->x.flip());
        game.chooseGoalPhase(goalCardsChoosen,starterCardsSideChoosen);


        game.executeTurn(game.getPlayers().getFirst().name(),game.getPlayers().getFirst().getBoard().getAvailableSpots().getFirst(), game.getPlayers().getFirst().getHand().getFirst(),false,1 );

        PlayableCard drawnCard = game.getPlayers().getFirst().getHand().getLast();
        Deck originalDeck = game.getNormalDeck();

        for (int i=0; i<35; i++){
            //System.out.println(originalDeck.showCard(1).asString());
            assertNotEquals(drawnCard.asString(), originalDeck.drawCard(1).asString());
            //System.out.println(playedCard.asString());
            //System.out.println(originalDeck.size());
        }
        assertEquals(0, originalDeck.size());

    }
//
//    @Test
//    @DisplayName("Testing the throw of Wrong Deck Index")
//    void invalidExecution() throws SyntaxException, FileNotFoundException, DeckException, JsonException {
//        JsonConverter jsonConverter = new JsonConverter();
//        int playersNumber = 2;
//        Game game = new Game(playersNumber);
//        //Receiving players name
//        ArrayList<String> playerNames = new ArrayList<>(playersNumber);
//        for (int i = 0; i < 2; i++)
//            playerNames.add("Player " + (i + 1));
//        game.setupGame(playerNames);
//        //Receiving players Goal
//        ArrayList<GoalCard> goalCardsChoosen = new ArrayList<>();
//        for (int i = 0; i < 2; i++)
//            goalCardsChoosen.add(game.getPlayers().get(i).getSetup().getGoals()[i % 2]);
//        ArrayList<StarterCard> starterCardsSideChoosen = new ArrayList<>();
//        for (int i = 0; i < 2; i++)
//            starterCardsSideChoosen.add(game.getPlayers().get(i).getSetup().getStarterCard());
//        starterCardsSideChoosen.stream().forEach(x->x.flip());
//        game.chooseGoalPhase(goalCardsChoosen,starterCardsSideChoosen);
//
//        Player player = game.getPlayers().getFirst();
//
//        assertEquals(INDEX_DECK_WRONG, game.executeTurn(player.name(),player.getBoard().getAvailableSpots().getFirst(), player.getHand().getFirst(),true,9));
//        assertEquals(INDEX_DECK_WRONG, game.executeTurn(player.name(),player.getBoard().getAvailableSpots().getFirst(), player.getHand().getFirst(),true,8));
//        assertEquals(INDEX_DECK_WRONG, game.executeTurn(player.name(),player.getBoard().getAvailableSpots().getFirst(), player.getHand().getFirst(),true,7));
//        assertEquals(INDEX_DECK_WRONG, game.executeTurn(player.name(),player.getBoard().getAvailableSpots().getFirst(), player.getHand().getFirst(),true,6));
//    }

    @Test
    @DisplayName("Testing the execution of the entire game letting the game finishing by himself with points trigger or the deck's emptiness")
    void testTotalGame() throws DeckException, SyntaxException, FileNotFoundException, JsonException {
        JsonConverter jsonConverter = new JsonConverter();
        int goldCounter = 0;
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
        board2.start(game.getPlayers().get(1).getSetup().getStarterCard());
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
        game.getPlayers().get(1).setBoard(board2);

        Board board3 = new Board();
        board3.start(game.getPlayers().get(2).getSetup().getStarterCard());
        for (int i = 0; i < 5; i++) {
            board3.placeCard(CardFactory.newPlayableCard("NIIE_IB0"), board3.getAvailableSpots().getFirst());
        }
        for (int i = 0; i < 5; i++) {
            board3.placeCard(CardFactory.newPlayableCard("NIIE_AB0"), board3.getAvailableSpots().getFirst());
        }
        for (int i = 0; i < 5; i++) {
            board3.placeCard(CardFactory.newPlayableCard("NIIE_FB0"), board3.getAvailableSpots().getFirst());
        }
        for (int i = 0; i < 5; i++) {
            board3.placeCard(CardFactory.newPlayableCard("NIIE_PB0"), board3.getAvailableSpots().getFirst());
        }
        game.getPlayers().get(2).setBoard(board3);

        Board board4 = new Board();
        board4.start(game.getPlayers().getLast().getSetup().getStarterCard());
        for (int i = 0; i < 5; i++) {
            board4.placeCard(CardFactory.newPlayableCard("NIIE_IB0"), board4.getAvailableSpots().getFirst());
        }
        for (int i = 0; i < 5; i++) {
            board4.placeCard(CardFactory.newPlayableCard("NIIE_AB0"), board4.getAvailableSpots().getFirst());
        }
        for (int i = 0; i < 5; i++) {
            board4.placeCard(CardFactory.newPlayableCard("NIIE_FB0"), board4.getAvailableSpots().getFirst());
        }
        for (int i = 0; i < 5; i++) {
            board4.placeCard(CardFactory.newPlayableCard("NIIE_PB0"), board4.getAvailableSpots().getFirst());
        }
        game.getPlayers().getLast().setBoard(board4);


        for (int i = 0; i < 18; i++) {
            for (Player player: game.getPlayers()){
                int turn = 0;
                if (i<=15){
                    turn = turn+1;

                    player.getHand().getFirst().flip();
                    String faceDown = player.getHand().getFirst().isFaceDown() ? "face-down" : "face-up";

//                    System.out.printf("HO PIAZZATO: %s (%s)\n", player.getHand().getFirst().asString(), faceDown);
//                    System.out.println("E QUESTA E' LA MIA HAND: \nPRIMA CARTA: " +player.getHand().getFirst().asString() +"\nSECONDA CARTA: " +player.getHand().get(1).asString() +"\nTERZA CARTA: " +player.getHand().getLast().asString());
//                    System.out.println("DIMENSIONE DEL GOLDEN DECK: " +game.getGoldenDeck().size());
//                    System.out.println("DIMENSIONE DEL NORMAL DECK: " +game.getNormalDeck().size());

                    String firstCard = player.getHand().getFirst().asString();
//                    System.out.println("QUESTI SONO GLI AVAILABLE SPOTS: " +player.getBoard().getAvailableSpots());
//                    System.out.println("IO STO CERCANDO DI PIAZZARLA QUI: " +player.getBoard().getAvailableSpots().getFirst());
//                    System.out.println("HO ESEGUITO QUESTO NUMERO DI TURNI: " +turn);
                    assertEquals(VALID_TURN,game.executeTurn(player.name(),player.getBoard().getAvailableSpots().getFirst(),player.getHand().getFirst(),game.getNormalDeck().size()==0,1));
                    assertEquals( "num turni: " +turn, turn, 1);

//                    System.out.println("QUESTA E' L'ULTIMA CARTA NELLA MIA BOARD: " +player.getBoard().getPlacedCards().getLast().card().asString());
                    assertEquals("id: "+firstCard, firstCard, player.getBoard().getPlacedCards().getLast().card().asString());

                    assertNotEquals(firstCard, player.getHand().getFirst().asString(), "id: "+firstCard);
                    assertNotEquals(firstCard, player.getHand().get(1).asString(), "id: "+firstCard);
                    assertNotEquals(firstCard, player.getHand().getLast().asString(), "id: "+firstCard);

                }
                else if (game.getFinalTurn()==false)  {
                    turn = turn+1;

                    System.out.println("HO PIAZZATO: " +player.getHand().getFirst().asString());
                    System.out.println("E QUESTA E' LA MIA HAND: \nPRIMA CARTA: " +player.getHand().getFirst().asString() +"\nSECONDA CARTA: " +player.getHand().get(1).asString() +"\nTERZA CARTA: " +player.getHand().getLast().asString());
                    System.out.println("DIMENSIONE DEL GOLDEN DECK: " +game.getGoldenDeck().size());
                    System.out.println("DIMENSIONE DEL NORMAL DECK: " +game.getNormalDeck().size());


                    String firstCard = player.getHand().getFirst().asString();
                    System.out.println("QUESTI SONO GLI AVAILABLE SPOTS: " +player.getBoard().getAvailableSpots());
                    System.out.println("IO STO CERCANDO DI PIAZZARLA QUI: " +player.getBoard().getAvailableSpots().getFirst());
                    System.out.println("HO ESEGUITO QUESTO NUMERO DI TURNI: " +turn);
                    assertEquals(VALID_TURN,game.executeTurn(player.name(), player.getBoard().getAvailableSpots().getFirst(), player.getHand().getFirst(), game.getNormalDeck().size()==0, 1));
                    assertEquals( "num turni: " +turn, turn, 1);


                    System.out.println("QUESTA E' L'ULTIMA CARTA NELLA MIA BOARD: " +player.getBoard().getPlacedCards().getLast().card().asString());

                    assertEquals("id: "+firstCard, firstCard, player.getBoard().getPlacedCards().getLast().card().asString());

                    assertNotEquals(firstCard, player.getHand().getFirst().asString(), "id: "+firstCard);
                    assertNotEquals(firstCard, player.getHand().get(1).asString(), "id: "+firstCard);
                    assertNotEquals(firstCard, player.getHand().getLast().asString(), "id: "+firstCard);


                    }

                else {
                    turn = turn+1;

                    System.out.println("HO PIAZZATO: " +player.getHand().getFirst().asString());
                    System.out.println("E QUESTA E' LA MIA HAND: \nPRIMA CARTA: " +player.getHand().getFirst().asString() +"\nSECONDA CARTA: " +player.getHand().get(1).asString() +"\nTERZA CARTA: " +player.getHand().getLast().asString());
                    System.out.println("DIMENSIONE DEL GOLDEN DECK: " +game.getGoldenDeck().size());
                    System.out.println("DIMENSIONE DEL NORMAL DECK: " +game.getNormalDeck().size());


                    String firstCard = player.getHand().getFirst().asString();
                    System.out.println("QUESTI SONO GLI AVAILABLE SPOTS: " +player.getBoard().getAvailableSpots());
                    System.out.println("IO STO CERCANDO DI PIAZZARLA QUI: " +player.getBoard().getAvailableSpots().getFirst());
                    System.out.println("HO ESEGUITO QUESTO NUMERO DI TURNI: " +turn);
                    assertEquals(VALID_LAST_TURN, game.executeTurn(player.name(),player.getBoard().getAvailableSpots().getFirst(), player.getHand().getFirst(),true,1));
                    assertEquals( "num turni: " +turn, turn, 1);

                    System.out.println("QUESTA E' L'ULTIMA CARTA NELLA MIA BOARD: " +player.getBoard().getPlacedCards().getLast().card().asString());
                    assertEquals("id: "+firstCard, firstCard, player.getBoard().getPlacedCards().getLast().card().asString());

                    assertNotEquals(firstCard, player.getHand().getFirst().asString(), "id: "+firstCard);
                    assertNotEquals(firstCard, player.getHand().get(1).asString(), "id: "+firstCard);
                    assertNotEquals(firstCard, player.getHand().getLast().asString(), "id: "+firstCard);


                }
            }

//            if (game.getFinalTurn()){
//                Player player = game.getPlayers().getFirst().getScore() >= game.getPlayers().get(1).getScore() ? game.getPlayers().getFirst() : game.getPlayers().get(1);
//                assert(player.getScore() >= 20);
//                break;
//            }
        }

//        for(Player player: game.getPlayers()){
//            assertEquals(VALID_LAST_TURN, game.executeTurn(player.name(),player.getBoard().getAvailableSpots().getFirst(), player.getHand().getFirst(),true,1));
//        }
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
