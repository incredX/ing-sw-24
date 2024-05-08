package IS24_LB11.gui.phases;

import IS24_LB11.game.PlayerSetup;
import IS24_LB11.game.components.*;
import IS24_LB11.game.utils.Color;
import IS24_LB11.gui.scenesControllers.SetupSceneController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class SetupGUIState extends ClientGUIState {

    private PlayerSetup personalSetup;
    private ArrayList<GoalCard> publicGoals;
    private ArrayList<PlayableCard> normalDeck;
    private ArrayList<PlayableCard> goldenDeck;
    private HashMap<Color, String> playersColors;
    SetupSceneController setupSceneController;

    public SetupGUIState(LoginGUIState prevState) {
        this.serverHandler = prevState.serverHandler;
        this.username = prevState.username;
        this.inputHandlerGUI = prevState.inputHandlerGUI;
        this.setupSceneController = null;
    }

    public void initialize(PlayerSetup playerSetup,
                           ArrayList<GoalCard> publicGoals,
                           ArrayList<PlayableCard> normalDeck,
                           ArrayList<PlayableCard> goldenDeck,
                           ArrayList<String> players) {
        this.personalSetup = playerSetup;
        this.publicGoals = publicGoals;
        this.normalDeck = normalDeck;
        this.goldenDeck = goldenDeck;
        this.playersColors = new HashMap<>();
        for (int i = 0; i < players.size(); i++) {
            playersColors.put(Color.fromInt(i),players.get(i));
        }
    }

    public void setChoosenGoalIndex(int choosenGoalIndex) {
        this.personalSetup.chooseGoal(choosenGoalIndex);
    }

    public int getChoosenGoalIndex() {
        return personalSetup.getChosenGoalIndex();
    }

    public ArrayList<GoalCard> getPrivateGoals() {
        return new ArrayList<GoalCard>(Arrays.asList(personalSetup.getGoals()));
    }

    public StarterCard getStarterCard() {
        return personalSetup.getStarterCard();
    }

    public void flipStarterCard() {
        personalSetup.getStarterCard().flip();
    }

    public void execute() {
        System.out.println(personalSetup.chosenGoal().asString());
        System.out.println(personalSetup.getStarterCard());
        inputHandlerGUI.sendReady(personalSetup.chosenGoal(),personalSetup.getStarterCard());
    }
}
