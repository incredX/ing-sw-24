package IS24_LB11.gui.phases;

import IS24_LB11.game.PlayerSetup;
import IS24_LB11.game.components.GoalCard;
import IS24_LB11.game.components.PlayableCard;
import IS24_LB11.game.components.StarterCard;
import IS24_LB11.game.utils.Color;
import IS24_LB11.gui.scenesControllers.SetupSceneController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * The SetupGUIState class manages the setup phase of the game in the GUI.
 * It stores information about the player's setup, public goals, decks, and players' colors.
 */
public class SetupGUIState extends ClientGUIState {

    private PlayerSetup personalSetup;
    private ArrayList<GoalCard> publicGoals;
    private ArrayList<PlayableCard> normalDeck;
    private ArrayList<PlayableCard> goldenDeck;
    private HashMap<String, Color> playersColors;
    private ArrayList<String> players;
    private SetupSceneController setupSceneController;

    /**
     * Constructs a new SetupGUIState based on the previous LoginGUIState.
     *
     * @param prevState the previous LoginGUIState.
     */
    public SetupGUIState(LoginGUIState prevState) {
        this.serverHandler = prevState.getServerHandler();
        this.username = prevState.getUsername();
        this.inputHandlerGUI = prevState.inputHandlerGUI;
        this.setupSceneController = null;
        this.clientGUI = prevState.getClientGUI();
    }

    /**
     * Initializes the SetupGUIState with the given parameters.
     *
     * @param playerSetup the player's setup.
     * @param publicGoals the list of public goals.
     * @param normalDeck the normal deck of playable cards.
     * @param goldenDeck the golden deck of playable cards.
     * @param players the list of players.
     */
    public void initialize(PlayerSetup playerSetup,
                           ArrayList<GoalCard> publicGoals,
                           ArrayList<PlayableCard> normalDeck,
                           ArrayList<PlayableCard> goldenDeck,
                           ArrayList<String> players) {
        this.personalSetup = playerSetup;
        this.publicGoals = publicGoals;
        this.normalDeck = normalDeck;
        this.goldenDeck = goldenDeck;
        this.players = players;
        this.playersColors = new HashMap<>();
        for (int i = 0; i < players.size(); i++) {
            playersColors.put(players.get(i), Color.fromInt(i));
        }
        System.out.println(playersColors);
    }

    /**
     * Sets the chosen goal index for the player.
     *
     * @param choosenGoalIndex the index of the chosen goal.
     */
    public void setChoosenGoalIndex(int choosenGoalIndex) {
        this.personalSetup.chooseGoal(choosenGoalIndex);
    }

    /**
     * Gets the chosen goal index for the player.
     *
     * @return the chosen goal index.
     */
    public int getChoosenGoalIndex() {
        return personalSetup.getChosenGoalIndex();
    }

    /**
     * Gets the list of private goals for the player.
     *
     * @return an ArrayList of the player's private goals.
     */
    public ArrayList<GoalCard> getPrivateGoals() {
        return new ArrayList<>(Arrays.asList(personalSetup.getGoals()));
    }

    /**
     * Gets the starter card for the player.
     *
     * @return the player's starter card.
     */
    public StarterCard getStarterCard() {
        return personalSetup.getStarterCard();
    }

    /**
     * Gets the player's setup.
     *
     * @return the player's setup.
     */
    public PlayerSetup getPersonalSetup() {
        return personalSetup;
    }

    /**
     * Gets the golden deck of playable cards.
     *
     * @return an ArrayList of the golden deck.
     */
    public ArrayList<PlayableCard> getGoldenDeck() {
        return goldenDeck;
    }

    /**
     * Gets the normal deck of playable cards.
     *
     * @return an ArrayList of the normal deck.
     */
    public ArrayList<PlayableCard> getNormalDeck() {
        return normalDeck;
    }

    /**
     * Gets the players' colors.
     *
     * @return a HashMap of players' colors.
     */
    public HashMap<String, Color> getPlayersColors() {
        return playersColors;
    }

    /**
     * Gets the list of public goals.
     *
     * @return an ArrayList of public goals.
     */
    public ArrayList<GoalCard> getPublicGoals() {
        return publicGoals;
    }

    /**
     * Gets the arrayList of players.
     *
     * @return an ArrayList of players.
     */
    public ArrayList<String> getPlayers() {
        return players;
    }

    /**
     * Flips the starter card for the player.
     */
    public void flipStarterCard() {
        personalSetup.getStarterCard().flip();
    }

    /**
     * Executes the setup process and sends the ready message to the server.
     */
    public void execute() {
        System.out.println(personalSetup.chosenGoal().asString());
        System.out.println(personalSetup.getStarterCard().asString());
        inputHandlerGUI.sendReady(personalSetup.chosenGoal(), personalSetup.getStarterCard());
    }

    /**
     * Removes a player from the list of players.
     *
     * @param playerDisconnected the player to be removed.
     */
    public void removePlayer(String playerDisconnected) {
        players.remove(playerDisconnected);
    }
}
