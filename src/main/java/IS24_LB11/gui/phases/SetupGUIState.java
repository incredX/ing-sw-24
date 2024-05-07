package IS24_LB11.gui.phases;

import IS24_LB11.game.PlayerSetup;
import IS24_LB11.game.components.*;
import IS24_LB11.game.utils.Color;
import IS24_LB11.gui.scenesControllers.SetupSceneController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class SetupGUIState extends ClientGUIState {
    private int choosenGoalIndex;
    private StarterCard starterCard;
    private ArrayList<PlayableCard> hand;
    private ArrayList<GoalCard> publicGoals;
    private ArrayList<GoalCard> privateGoals;

    private ArrayList<PlayableCard> normalDeck;
    private ArrayList<PlayableCard> goldenDeck;
    private HashMap<Color, String> playersColors;
    SetupSceneController setupSceneController;

    public SetupGUIState(LoginGUIState prevState) {
        this.serverHandler = prevState.serverHandler;
        this.username = prevState.username;
        this.inputHandlerGUI = prevState.inputHandlerGUI;
        this.setupSceneController = null;
        choosenGoalIndex = 0;
    }

    public void initialize(PlayerSetup playerSetup,
                           ArrayList<GoalCard> publicGoals,
                           ArrayList<PlayableCard> normalDeck,
                           ArrayList<PlayableCard> goldenDeck,
                           ArrayList<String> players) {
        this.starterCard = playerSetup.getStarterCard();
        this.hand = playerSetup.hand();
        this.privateGoals = new ArrayList<>(Arrays.stream(playerSetup.getGoals()).toList());
        this.publicGoals = publicGoals;
        this.normalDeck = normalDeck;
        this.goldenDeck = goldenDeck;

        this.playersColors = new HashMap<>();
        for (int i = 0; i < players.size(); i++) {
            playersColors.put(Color.fromInt(i),players.get(i));
        }
    }

    public void setChoosenGoalIndex(int choosenGoalIndex) {
        this.choosenGoalIndex = choosenGoalIndex;
    }

    public int getChoosenGoalIndex() {
        return choosenGoalIndex;
    }

    public StarterCard getStarterCard() {
        return starterCard;
    }

    public void flipStarterCard() {
        starterCard.flip();
    }

    public void execute() {
        inputHandlerGUI.sendReady(privateGoals.get(choosenGoalIndex),starterCard);
    }
}
