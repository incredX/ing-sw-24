package IS24_LB11.game;

import IS24_LB11.game.components.GoalCard;
import IS24_LB11.game.components.PlayableCard;
import IS24_LB11.game.components.StarterCard;
import IS24_LB11.game.tools.JsonConverter;
import IS24_LB11.game.tools.JsonException;
import IS24_LB11.game.utils.Color;
import IS24_LB11.game.utils.Position;
import IS24_LB11.game.utils.SyntaxException;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import static IS24_LB11.game.GameMessages.*;

/**
 * Represents the main game logic and state.
 */
public class Game {
    private boolean finalTurn;
    private boolean gameEnded;
    private int turn;
    private int lastTurn;
    private final int numPlayers;
    private final Deck goalDeck;
    private final Deck goldenDeck;
    private final Deck normalDeck;
    private final Deck starterDeck;
    private final ArrayList<Player> players;
    private ArrayList<Player> finalRanking;
    private ArrayList<GoalCard> publicGoals;

    /**
     * Constructs a new Game with the specified number of players.
     *
     * @param numPlayers the number of players
     * @throws SyntaxException       if there is a syntax error in the game setup
     * @throws FileNotFoundException if a required file is not found
     * @throws DeckException         if there is an error with the deck
     */
    public Game(int numPlayers) throws SyntaxException, FileNotFoundException, DeckException {
        JsonConverter jsonConverter = new JsonConverter();
        this.turn = -1;
        this.numPlayers = numPlayers;
        this.goalDeck = jsonConverter.JSONToDeck('O'); // <- here we load the deck from json
        this.goldenDeck = jsonConverter.JSONToDeck('G'); // <- here we load deck from json
        this.normalDeck = jsonConverter.JSONToDeck('N'); // <- here we load deck from json
        this.starterDeck = jsonConverter.JSONToDeck('S'); // <- here we load deck from json
        publicGoals = new ArrayList<>();
        goalDeck.shuffle();
        publicGoals.add((GoalCard) goalDeck.drawCard());
        publicGoals.add((GoalCard) goalDeck.drawCard());
        this.players = new ArrayList<>(numPlayers);
        this.finalTurn = false;
    }

    /**
     * Returns the current player.
     *
     * @return the current player
     */
    public Player currentPlayer() {
        if (turn == -1)
            return players.getFirst();
        return players.get(turn % players.size());
    }

    /**
     * Sets up the game with the specified player names and set up game items.
     *
     * @param playerNames the list of player names
     * @return a message indicating the result of the setup
     * @throws DeckException if there is an error with the deck
     */
    public String setupGame(ArrayList<String> playerNames) throws DeckException {
        if (playerNames.size() != numPlayers) return NAMES_OUT_OF_BOUND;
        goalDeck.shuffle();
        goldenDeck.shuffle();
        normalDeck.shuffle();
        starterDeck.shuffle();
        for (String name : playerNames)
            setupPlayer(name);
        return GameMessages.SETUP_COMPLETE;
    }

    /**
     * Sets up a player with the specified name, add card to hand, starter card and goals to the playerSetup.
     *
     * @param name the name of the player
     * @throws DeckException if there is an error with the deck
     */
    private void setupPlayer(String name) throws DeckException {
        GoalCard[] goalCards = new GoalCard[]{
                (GoalCard) goalDeck.drawCard(),
                (GoalCard) goalDeck.drawCard()
        };
        ArrayList<PlayableCard> playerHand = new ArrayList<>();
        playerHand.add((PlayableCard) normalDeck.drawCard());
        playerHand.add((PlayableCard) normalDeck.drawCard());
        playerHand.add((PlayableCard) goldenDeck.drawCard());
        PlayerSetup playerSetup = new PlayerSetup((StarterCard) starterDeck.drawCard(), goalCards, playerHand, Color.fromInt(players.size()));
        players.add(new Player(name, playerSetup));
    }

    /**
     * Executes the goal selection phase for the players, set private goal and starter card for the player and apply player setup.
     *
     * @param playersGoalCardChoosen  the list of goal cards chosen by the players
     * @param starterCardFacePosition the list of starter card face positions
     * @return a message indicating the result of the goal phase
     */
    public String chooseGoalPhase(ArrayList<GoalCard> playersGoalCardChoosen, ArrayList<StarterCard> starterCardFacePosition) {
        for (Player player : players) {
            for (GoalCard goalCard : playersGoalCardChoosen) {
                if (player.getSetup().selectGoal(goalCard))
                    break;
            }
            for (StarterCard starterCard : starterCardFacePosition)
                if (numberCharNotEqualInSamePosition(player.getSetup().getStarterCard().asString(), starterCard.asString()))
                    if (player.getSetup().getStarterCard().asString().charAt(6) != starterCard.asString().charAt(6))
                        player.getSetup().flipStarterCard();
            player.applySetup();
        }
        turn = 0;
        return GOAL_PHASE_COMPLETED;
    }

    /**
     * Executes a turn for the specified player and decide it self if is player final turn or not.
     *
     * @param playerName   the name of the player
     * @param position     the position to place the card
     * @param playableCard the card to be played
     * @param deckType     the type of deck (true for golden, false for normal)
     * @param indexDeck    the index of the card in the deck
     * @return a message indicating the result of the turn
     * @throws JsonException   if there is a JSON error
     * @throws DeckException   if there is an error with the deck
     * @throws SyntaxException if there is a syntax error
     */
    public String executeTurn(String playerName, Position position, PlayableCard playableCard, boolean deckType, int indexDeck) throws JsonException, DeckException, SyntaxException {
        if (!playerName.equals(currentPlayer().name())) return NOT_PLAYER_TURN;
        if (hasGameEnded()) return GAME_ENDED;
        return finalTurn ? executeFinalTurn(position, playableCard) : executeNormalTurn(position, playableCard, deckType, indexDeck);
    }

    /**
     * Executes a normal turn for the current player.
     *
     * @param position     the position to place the card
     * @param playableCard the card to be played
     * @param deckType     the type of deck (true for golden, false for normal)
     * @param indexDeck    the index of the card in the deck
     * @return a message indicating the result of the turn
     * @throws DeckException   if there is an error with the deck
     * @throws JsonException   if there is a JSON error
     * @throws SyntaxException if there is a syntax error
     */
    private String executeNormalTurn(Position position, PlayableCard playableCard, boolean deckType, int indexDeck) throws DeckException, JsonException, SyntaxException {
        System.out.printf("executing turn of %s (turn %d)\n", currentPlayer().name(), turn);
        Player player = currentPlayer();
        if (normalDeck.isEmpty() && !deckType)
            return CANT_DRAW_FROM_NORMAL_DECK_IS_EMPTY;
        if (goldenDeck.isEmpty() && deckType)
            return CANT_DRAW_FROM_GOLDEN_DECK_IS_EMPTY;
        if ((!deckType && normalDeck.getCards().size() - indexDeck < 0) || (deckType && goldenDeck.getCards().size() - indexDeck < 0) || indexDeck < 1 || indexDeck > 3)
            return INDEX_DECK_WRONG;
        if (!player.placeCard(playableCard, position))
            return INVALID_POSITION_CARD_OR_NOT_IN_HAND;
        else {
            player.incrementScoreLastCardPlaced();
        }

        if (!deckType) {
            player.addCardToHand((PlayableCard) normalDeck.drawCard(indexDeck));
        } else if (deckType) {
            player.addCardToHand((PlayableCard) goldenDeck.drawCard(indexDeck));
        }

        turn++;
        if (!finalTurn)
            isFinalTurn();
        return VALID_TURN;
    }

    /**
     * Executes the final turn for the current player.
     *
     * @param position     the position to place the card
     * @param playableCard the card to be played
     * @return a message indicating the result of the final turn
     * @throws JsonException   if there is a JSON error
     * @throws SyntaxException if there is a syntax error
     */
    private String executeFinalTurn(Position position, PlayableCard playableCard) throws JsonException, SyntaxException {
        if ((turn + 1) > lastTurn) {
            return GAME_ENDED;
        }
        Player player = players.get(turn % players.size());
        if (!player.placeCard(playableCard, position))
            return INVALID_POSITION_CARD_OR_NOT_IN_HAND;
        else {
            player.incrementScoreLastCardPlaced();
        }

        turn++;

        if (player.name().equals(players.getLast().name()) && finalTurn) {
            gameEnded = true;
            finalGamePhase();
        }

        return VALID_LAST_TURN;
    }

    /**
     * When executed it sets finalTurn flag and sets the final turn.
     */
    private void isFinalTurn() {
        if (turn % players.size() == 0 && !finalTurn) {
            if (normalDeck.isEmpty() && goldenDeck.isEmpty()) {
                finalTurn = true;
                //lastTurn = turn + players.size();
            }
            for (Player player : players)
                if (player.getScore() >= 20) {
                    finalTurn = true;
                    //lastTurn = turn + players.size();
                }
        }
    }

    /**
     * Executes the final game phase.
     *
     * @throws SyntaxException if there is a syntax error
     */
    private void finalGamePhase() throws SyntaxException {
        for (Player player : players) {
            player.personalGoalScore();
            player.publicGoalScore(publicGoals);
        }
    }

    /**
     * Checks if two strings have different characters in the same positions.
     *
     * @param s1 the first Card string
     * @param s2 the second Card string
     * @return true if the characters are different, false otherwise
     */
    private boolean numberCharNotEqualInSamePosition(String s1, String s2) {
        return s1.regionMatches(0, s2, 0, 6) && s1.regionMatches(7, s2, 7, 7);
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }
    public int getTurn() {
        return turn;
    }
    public boolean getFinalTurn() {
        return finalTurn;
    }
    public Deck getGoldenDeck() {
        return goldenDeck;
    }
    public Deck getNormalDeck() {
        return normalDeck;
    }
    public boolean hasGameEnded() {
        return gameEnded;
    }
    public void setGameEnded(Boolean gameEnded) {
        this.gameEnded = gameEnded;
    }

    public void setTurn(int turn) {
        this.turn = turn;
    }

    public ArrayList<GoalCard> getPublicGoals() {
        return publicGoals;
    }
}