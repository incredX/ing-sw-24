package IS24_LB11.game;

import IS24_LB11.game.components.GoalCard;
import IS24_LB11.game.components.JsonConvertable;
import IS24_LB11.game.components.PlayableCard;
import IS24_LB11.game.components.StarterCard;
import IS24_LB11.game.symbol.Symbol;
import IS24_LB11.game.tools.JsonConverter;
import IS24_LB11.game.tools.JsonException;
import IS24_LB11.game.utils.Color;
import IS24_LB11.game.utils.Position;
import IS24_LB11.game.symbol.*;
import IS24_LB11.game.utils.SyntaxException;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.stream.Collectors;

public class Game {
    private String NOT_NORMAL_TURN = "Not normal turn";
    private String NOT_FINAL_TURN = "Not final turn";

    private String NOT_PLAYER_TURN = "Not this player turn";
    private String INVALID_POSITION_CARD_OR_NOT_IN_HAND = "Invalid placement of card or card is not in player's hand";

    private String VALID_TURN = "Valid turn executed";
    private String VALID_TURN_AND_TRIGGERED_FINAL_TURN = "Valid turn executed and score of 20 exceeded";

    private String SETUP_COMPLETE = "Setup completed, the game is starting...";

    private boolean finalTurn;
    private int turn;
    private final int numPlayers;
    private final Deck goalDeck;
    private final Deck goldenDeck;
    private final Deck normalDeck;
    private final Deck starterDeck;
    private final ArrayList<Player> players;

    public Game(int numPlayers) throws SyntaxException, FileNotFoundException {
        JsonConverter jsonConverter = new JsonConverter();
        this.turn = 0;
        this.numPlayers = numPlayers;
        this.goalDeck = jsonConverter.JSONToDeck('O'); // <- here we load the deck from json
        this.goldenDeck = jsonConverter.JSONToDeck('G'); // <- here we load deck from json
        this.normalDeck = jsonConverter.JSONToDeck('N'); // <- here we load deck from json
        this.starterDeck = jsonConverter.JSONToDeck('S'); // <- here we load deck from json
        this.players = new ArrayList<>(numPlayers);
        this.finalTurn = false;
    }


    private Player currentPlayer() {
        return players.get(turn % players.size());
    }

    private void setupPlayer(String name) throws DeckException {
        GoalCard[] goalCards = new GoalCard[]{
                (GoalCard) goalDeck.drawCard(),
                (GoalCard) goalDeck.drawCard()
        };
        ArrayList<PlayableCard> playerHand = new ArrayList<>();
        playerHand.add((PlayableCard) normalDeck.drawCard());
        playerHand.add((PlayableCard) normalDeck.drawCard());
        playerHand.add((PlayableCard) goldenDeck.drawCard());
        PlayerSetup playerSetup = new PlayerSetup((StarterCard) starterDeck.drawCard(), goalCards, playerHand);
        players.add(new Player(name, Color.fromInt(players.size()), playerSetup));
    }

    public String setupGame(ArrayList<String> playerNames) throws DeckException {
        if (playerNames.size() != numPlayers) return "ERROR_TOO_MUCH_NAMES";
        goalDeck.shuffle();
        goldenDeck.shuffle();
        normalDeck.shuffle();
        starterDeck.shuffle();
        for (String name : playerNames)
            setupPlayer(name);
        return SETUP_COMPLETE;
    }

    public String chooseGoalPhase(ArrayList<GoalCard> playersGoalCardChoosen) {
        //not cheking if goal card not present in player hand
        for (Player player : players) {
            for (GoalCard goalCard : playersGoalCardChoosen) {
                if (player.getSetup().selectGoal(goalCard))
                    break;
            }
            player.applySetup();
        }
        return "CHOOSE GOAL PHASE COMPLETED, READY TO GO";
    }

    public String executeTurn(String playerName, Position position, PlayableCard playableCard, boolean deckType, int indexDeck) throws JsonException, DeckException, SyntaxException {
        if (playerName.compareTo(players.get(turn % players.size()).name()) != 0) return NOT_PLAYER_TURN;
        return finalTurn ? executeFinalTurn() : executeNormalTurn(position, playableCard, deckType, indexDeck);
    }

    public String executeNormalTurn(Position position, PlayableCard playableCard, boolean deckType, int indexDeck) throws DeckException, JsonException, SyntaxException {
        Player player = players.get(turn % players.size());
        if (player.placeCard(playableCard, position) == false)
            return INVALID_POSITION_CARD_OR_NOT_IN_HAND;
        else {
            if (playableCard.asString().charAt(7) != 0)
                player.incrementScore(scoreTurnPlayer(player, playableCard));
        }
        //0 for standard deck, 1 for gold deck
        //deck empty o provo a pescare una carta non esistente
        if (deckType && !normalDeck.isEmpty())
            normalDeck.drawCard(indexDeck);
        else if (!deckType && !goldenDeck.isEmpty())
            goldenDeck.drawCard(indexDeck);
        turn++;

        //controllo se turno finale lo faccio solo sull'ultima persona controllando tutti i punteggi
        if (!finalTurn)
            isFinalTurn();
        return VALID_TURN;
    }

    public String executeFinalTurn() {
        return null;
    }
    //remind to check if front or back
    private int scoreTurnPlayer(Player player, PlayableCard playableCard) {
        int score = Integer.valueOf(playableCard.asString().charAt(7));
        HashMap<Symbol, Integer> symbolCounter = player.getBoard().getSymbolCounter();
        switch (playableCard.asString().charAt(0)) {
            case 'N':
                return score;
            case 'G':
                switch (playableCard.asString().charAt(8)) {
                    case 'A':
                        return symbolCounter.get(Suit.ANIMAL);
                    case 'I':
                        return symbolCounter.get(Suit.INSECT);
                    case 'F':
                        return symbolCounter.get(Suit.MUSHROOM);
                    case 'P':
                        return symbolCounter.get(Suit.PLANT);
                    case 'Q':
                        return symbolCounter.get(Item.QUILL);
                    case 'K':
                        return symbolCounter.get(Item.INKWELL);
                    case 'M':
                        return symbolCounter.get(Item.MANUSCRIPT);
                    case 'E':
                        return score;
                    default:
                        return 0;
                }
            default:
                return 0;
        }
    }

    private void isFinalTurn() {
        if (normalDeck.isEmpty() && goldenDeck.isEmpty())
            for (Player player : players)
                if (player.getScore() >= 20)
                    finalTurn = true;
    }

    //ONLY FOR TESTS
    public ArrayList<Player> getPlayers() {
        return players;
    }
}