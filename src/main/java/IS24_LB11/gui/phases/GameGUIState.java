package IS24_LB11.gui.phases;

import IS24_LB11.game.Player;
import IS24_LB11.game.PlayerSetup;
import IS24_LB11.game.components.PlayableCard;
import IS24_LB11.game.utils.Color;
import IS24_LB11.gui.scenesControllers.GameSceneController;
import IS24_LB11.gui.scenesControllers.SetupSceneController;

import java.util.ArrayList;
import java.util.HashMap;

public class GameGUIState extends ClientGUIState{
    boolean isThisPlayerTurn=false;
    private Player player;
    private ArrayList<PlayableCard> normalDeck;
    private ArrayList<PlayableCard> goldenDeck;
    private HashMap<Color, String> playersColors;
    private HashMap<String, Integer> playersScore;
    private ArrayList<String> players;
    GameSceneController gameSceneController;

    public GameGUIState(SetupGUIState prevState) {
        this.serverHandler = prevState.serverHandler;
        this.username = prevState.username;
        this.inputHandlerGUI = prevState.inputHandlerGUI;
        this.gameSceneController = null;

        this.player= new Player(username,prevState.getPersonalSetup());
        this.player.applySetup();
        this.normalDeck=prevState.getNormalDeck();
        this.goldenDeck=prevState.getGoldenDeck();
        this.playersColors=prevState.getPlayersColors();
        this.players=prevState.getPlayers();
        this.playersScore=new HashMap<>();
        for (int i = 0; i < playersColors.size(); i++) {
            playersScore.put(players.get(i),0);
        }
    }



    public void update(String currentPlayerTurn, ArrayList<Integer> playerScores, ArrayList<PlayableCard> normalDeck, ArrayList<PlayableCard> goldenDeck) {
        if (currentPlayerTurn==username)
            isThisPlayerTurn=true;
        this.normalDeck=normalDeck;
        this.goldenDeck=goldenDeck;
        for (int i = 0; i < playerScores.size(); i++) {
            playersScore.replace(players.get(i),playerScores.get(i));
        }
    }
}
