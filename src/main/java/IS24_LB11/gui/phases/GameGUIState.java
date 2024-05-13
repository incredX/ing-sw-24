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

public class GameGUIState extends ClientGUIState {
    private boolean isThisPlayerTurn = false;
    private Player player;
    private ArrayList<GoalCard> publicGoals;
    private ArrayList<PlayableCard> normalDeck;
    private ArrayList<PlayableCard> goldenDeck;
    private HashMap<String, Color> playersColors;
    private HashMap<String, Integer> playersScore;
    private ArrayList<String> players;
    GameSceneController gameSceneController;

    private PlayableCard cardChooseToPlay;
    private Position positionPlacedCard;
    private PlayableCard cardChooseToDraw;
    private boolean deckType;
    private int indexCardDeck;


    public GameGUIState(SetupGUIState prevState) {
        this.personalChat= new Chat();
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
        if (currentPlayerTurn.equals(username))
            isThisPlayerTurn = true;
        else
            isThisPlayerTurn = false;
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
        this.indexCardDeck =indexDeck;
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

    public ArrayList<String> getPlayers() {
        return players;
    }

    public PlayableCard getCardChooseToPlay() {
        return cardChooseToPlay;
    }

    public PlayableCard getCardChooseToDraw() {
        return cardChooseToDraw;
    }

    public HashMap<String, Integer> getPlayersScore() {
        return playersScore;
    }
    public HashMap<String, Color> getPlayersColors() {
        return playersColors;
    }

    public void execute() {
        if(!this.deckType)
            player.addCardToHand(normalDeck.get(indexCardDeck));
        else
            player.addCardToHand(goldenDeck.get(indexCardDeck));

        System.out.printf(String.valueOf(player.getHand()));

        PlacedCard placedCard = new PlacedCard(cardChooseToPlay, positionPlacedCard);
        inputHandlerGUI.sendTurn(placedCard, deckType, indexCardDeck);
    }

    public Boolean placeCard(PlacedCard placedCard){
        return player.placeCard(placedCard.card(),placedCard.position());
    }

    public int getNumberOfPlayer(){
        return players.size();
    }

    public void removePlayer(String playerDisconnected) {
        players.remove(playerDisconnected);
        playersScore.remove(playerDisconnected);
        playersColors.remove(playerDisconnected);
    }

    public void sendMessage(String to, String from,String mex){
        inputHandlerGUI.sendMessage(to,from,mex);
    }

    public void sendToAll (String from, String mex){
        inputHandlerGUI.sendToAllMessage(from, mex);
    }

    public boolean isThisPlayerTurn() {
        return isThisPlayerTurn;
    }

    public void setPositionOfPlacedCard(Position positionPlacedCard){
        this.positionPlacedCard = positionPlacedCard;
    }
}
