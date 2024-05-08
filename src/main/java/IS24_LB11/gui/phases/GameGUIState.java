package IS24_LB11.gui.phases;

import IS24_LB11.game.PlacedCard;
import IS24_LB11.game.Player;
import IS24_LB11.game.components.GoalCard;
import IS24_LB11.game.components.PlayableCard;
import IS24_LB11.game.utils.Color;
import IS24_LB11.game.utils.Position;
import IS24_LB11.gui.scenesControllers.GameSceneController;

import java.util.ArrayList;
import java.util.HashMap;

public class GameGUIState extends ClientGUIState {
    boolean isThisPlayerTurn = false;
    private Player player;
    private ArrayList<GoalCard> publicGoals;
    private ArrayList<PlayableCard> normalDeck;
    private ArrayList<PlayableCard> goldenDeck;
    private HashMap<Color, String> playersColors;
    private HashMap<String, Integer> playersScore;
    private ArrayList<String> players;
    GameSceneController gameSceneController;

    private PlayableCard cardChooseToPlay;
    private Position positionPlacedCard;
    private PlayableCard cardChooseToDraw;
    private boolean deckType;
    private int indexDeck;


    public GameGUIState(SetupGUIState prevState) {
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
        for (int i = 0; i < playersColors.size(); i++) {
            playersScore.put(players.get(i), 0);
        }
    }

    public void update(String currentPlayerTurn, ArrayList<Integer> playerScores, ArrayList<PlayableCard> normalDeck, ArrayList<PlayableCard> goldenDeck) {
        if (currentPlayerTurn == username)
            isThisPlayerTurn = true;
        this.normalDeck = normalDeck;
        this.goldenDeck = goldenDeck;
        for (int i = 0; i < playerScores.size(); i++) {
            playersScore.replace(players.get(i), playerScores.get(i));
        }
    }

    public void chooseCardToPlay(PlayableCard playableCard) {
        this.cardChooseToPlay = playableCard;
    }

    public void chooseCardToDraw(PlayableCard playableCard,int indexDeck,boolean deckType) {
        this.deckType=deckType;
        this.indexDeck=indexDeck;
        this.cardChooseToDraw = playableCard;
    }

    public ArrayList<GoalCard> getPublicGoals() {
        return publicGoals;
    }

    public ArrayList<PlayableCard> getNormalDeck() {
        return normalDeck;
    }

    public ArrayList<PlayableCard> getGoldenDeck() {
        return goldenDeck;
    }

    public Player getPlayer() {
        return player;
    }

    public void execute() {
        PlacedCard placedCard = new PlacedCard(cardChooseToPlay, positionPlacedCard);
        inputHandlerGUI.sendTurn(placedCard, cardChooseToDraw);
    }

    public void placeCard(PlacedCard placedCard){
        player.placeCard(placedCard.card(),placedCard.position());
    }
}
