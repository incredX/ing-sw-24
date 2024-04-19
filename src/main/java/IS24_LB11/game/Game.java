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
        this.finalTurn=false;
    }


    private Player currentPlayer() {
        return players.get(turn%players.size());
    }

    private void setupPlayer(String name) throws DeckException {
        GoalCard[] goalCards = new GoalCard[] {
                (GoalCard) goalDeck.drawCard(),
                (GoalCard) goalDeck.drawCard()
        };
        ArrayList<PlayableCard> playerHand = new ArrayList<>();
        playerHand.add((PlayableCard) normalDeck.drawCard());
        playerHand.add((PlayableCard) normalDeck.drawCard());
        playerHand.add((PlayableCard) goldenDeck.drawCard());
        PlayerSetup playerSetup = new PlayerSetup((StarterCard) starterDeck.drawCard(),goalCards,playerHand);
        players.add(new Player(name,Color.fromInt(players.size()),playerSetup));
    }

    public String setupGame(ArrayList<String> playerNames) throws DeckException {
        if (playerNames.size()!=numPlayers) return "ERROR_TOO_MUCH_NAMES";
        goalDeck.shuffle();
        goldenDeck.shuffle();
        normalDeck.shuffle();
        starterDeck.shuffle();
        for (String name: playerNames)
            setupPlayer(name);
        return SETUP_COMPLETE;
    }

    public String chooseGoalPhase(ArrayList<GoalCard> playersGoalCardChoosen){
        //not cheking if goal card not present in player hand
        for (Player player: players) {
            for (GoalCard goalCard :playersGoalCardChoosen){
                if (player.getSetup().selectGoal(goalCard))
                    break;
            }
            player.applySetup();
        }
        return "CHOOSE GOAL PHASE COMPLETED, READY TO GO";
    }

    public String executeTurn(Player player, PlayableCard playableCard, Position position){
     return null;
    }
    //ONLY FOR TESTS
    public ArrayList<Player> getPlayers() {
        return players;
    }
}