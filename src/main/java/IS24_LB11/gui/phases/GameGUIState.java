package IS24_LB11.gui.phases;

import IS24_LB11.game.PlacedCard;
import IS24_LB11.game.Player;
import IS24_LB11.game.components.GoalCard;
import IS24_LB11.game.components.PlayableCard;
import IS24_LB11.game.utils.Color;
import IS24_LB11.game.utils.Position;
import IS24_LB11.gui.Chat;
import IS24_LB11.gui.scenesControllers.GameSceneController;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * The GameGUIState class manages the game phase in the GUI, handling player actions,
 * game updates, and communication with the server.
 */
public class GameGUIState extends ClientGUIState {
    private boolean isThisPlayerTurn = false;
    private Player player;
    private ArrayList<GoalCard> publicGoals;
    private ArrayList<PlayableCard> normalDeck;
    private ArrayList<PlayableCard> goldenDeck;
    private HashMap<String, Color> playersColors;
    private HashMap<String, Integer> playersScore;
    private ArrayList<String> players;
    private GameSceneController gameSceneController;

    private PlayableCard cardChooseToPlay;
    private Position positionPlacedCard;
    private PlayableCard cardChooseToDraw;
    private boolean deckType;
    private int indexCardDeck;

    /**
     * Constructs a new GameGUIState, initializing it with the previous setup state.
     *
     * @param prevState the previous SetupGUIState.
     */
    public GameGUIState(SetupGUIState prevState) {
        this.clientGUI = prevState.getClientGUI();
        this.personalChat = new Chat();
        this.serverHandler = prevState.serverHandler;
        this.username = prevState.username;
        this.inputHandlerGUI = prevState.inputHandlerGUI;
        this.gameSceneController = null;
        this.publicGoals = prevState.getPublicGoals();

        this.player = new Player(username, prevState.getPersonalSetup());
        this.player.applySetup();

        this.normalDeck = prevState.getNormalDeck();
        this.goldenDeck = prevState.getGoldenDeck();
        this.playersColors = prevState.getPlayersColors();
        this.players = prevState.getPlayers();
        this.playersScore = new HashMap<>();
        for (String player : players) {
            playersScore.put(player, 0);
        }
    }

    /**
     * Updates the game state with the current player's turn, player scores, and decks.
     *
     * @param currentPlayerTurn the username of the current player's turn.
     * @param playerScores      the scores of the players.
     * @param normalDeck        the updated normal deck.
     * @param goldenDeck        the updated golden deck.
     */
    public void update(String currentPlayerTurn, ArrayList<Integer> playerScores, ArrayList<PlayableCard> normalDeck, ArrayList<PlayableCard> goldenDeck) {
        isThisPlayerTurn = currentPlayerTurn.equals(username);
        this.normalDeck = normalDeck;
        this.goldenDeck = goldenDeck;
        for (int i = 0; i < playerScores.size(); i++) {
            this.playersScore.replace(players.get(i), playerScores.get(i));
        }
    }

    /**
     * Updates the game state with the current player scores.
     *
     * @param playerScores the scores of the players.
     */
    public void update(ArrayList<Integer> playerScores) {
        for (int i = 0; i < playerScores.size(); i++) {
            playersScore.replace(players.get(i), playerScores.get(i));
        }
    }

    /**
     * Chooses a card to play.
     *
     * @param playableCard the card to play.
     */
    public void chooseCardToPlay(PlayableCard playableCard) {
        this.cardChooseToPlay = playableCard;
    }

    /**
     * Chooses a card to draw.
     *
     * @param playableCard the card to draw.
     * @param indexDeck    the index of the card in the deck.
     * @param deckType     the type of the deck (normal or golden).
     */
    public void chooseCardToDraw(PlayableCard playableCard, int indexDeck, boolean deckType) {
        this.deckType = deckType;
        this.indexCardDeck = indexDeck;
        this.cardChooseToDraw = playableCard;
    }

    /**
     * Gets the public goals.
     *
     * @return the public goals.
     */
    public ArrayList<GoalCard> getPublicGoals() {
        return publicGoals;
    }

    /**
     * Gets the normal deck.
     *
     * @return the normal deck.
     */
    public ArrayList<PlayableCard> getNormalDeck() {
        return normalDeck;
    }

    /**
     * Gets the golden deck.
     *
     * @return the golden deck.
     */
    public ArrayList<PlayableCard> getGoldenDeck() {
        return goldenDeck;
    }

    /**
     * Gets the player.
     *
     * @return the player.
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Gets the list of players.
     *
     * @return the list of players.
     */
    public ArrayList<String> getPlayers() {
        return players;
    }

    /**
     * Gets the chosen card to play.
     *
     * @return the chosen card to play.
     */
    public PlayableCard getCardChooseToPlay() {
        return cardChooseToPlay;
    }

    /**
     * Gets the chosen card to draw.
     *
     * @return the chosen card to draw.
     */
    public PlayableCard getCardChooseToDraw() {
        return cardChooseToDraw;
    }

    /**
     * Gets the players' scores.
     *
     * @return the players' scores.
     */
    public HashMap<String, Integer> getPlayersScore() {
        return playersScore;
    }

    /**
     * Gets the players' colors.
     *
     * @return the players' colors.
     */
    public HashMap<String, Color> getPlayersColors() {
        return playersColors;
    }

    /**
     * Executes the player's turn by placing the chosen card and drawing a new card if available.
     */
    public void execute() {
        PlacedCard placedCard = new PlacedCard(cardChooseToPlay, positionPlacedCard);
        if (!normalDeck.isEmpty() || !goldenDeck.isEmpty()) {
            if (!isFinalTurn){
                if (!deckType) {
                    player.addCardToHand(normalDeck.get(indexCardDeck));
                } else {
                    player.addCardToHand(goldenDeck.get(indexCardDeck));
                }
            }
            inputHandlerGUI.sendTurn(placedCard, deckType, indexCardDeck);
        } else {
            inputHandlerGUI.sendTurn(placedCard);
        }
    }

    /**
     * Places a card on the game board.
     *
     * @param placedCard the card to place.
     * @return true if the card was placed successfully, false otherwise.
     */
    public Boolean placeCard(PlacedCard placedCard) {
        return player.placeCard(placedCard.card(), placedCard.position());
    }

    /**
     * Gets the number of players.
     *
     * @return the number of players.
     */
    public int getNumberOfPlayer() {
        return players.size();
    }

    /**
     * Removes a disconnected player from the game.
     *
     * @param playerDisconnected the username of the disconnected player.
     */
    public void removePlayer(String playerDisconnected) {
        players.remove(playerDisconnected);
        playersScore.remove(playerDisconnected);
        playersColors.remove(playerDisconnected);
    }

    /**
     * Checks if it is the current player's turn.
     *
     * @return true if it is the current player's turn, false otherwise.
     */
    public boolean isThisPlayerTurn() {
        return isThisPlayerTurn;
    }

    /**
     * Sets the position of the placed card.
     *
     * @param positionPlacedCard the position of the placed card.
     */
    public void setPositionOfPlacedCard(Position positionPlacedCard) {
        this.positionPlacedCard = positionPlacedCard;
    }
}
