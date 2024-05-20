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
        publicGoals.add((GoalCard)goalDeck.drawCard());
        this.players = new ArrayList<>(numPlayers);
        this.finalTurn = false;
    }


    public Player currentPlayer() {
        if (turn==-1)
            return players.getFirst();
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
        PlayerSetup playerSetup = new PlayerSetup((StarterCard) starterDeck.drawCard(), goalCards, playerHand, Color.fromInt(players.size()));
        players.add(new Player(name, playerSetup));
    }

    public String setupGame(ArrayList<String> playerNames) throws DeckException {
        if (playerNames.size() != numPlayers) return NAMES_OUT_OF_BOUND;
        goalDeck.shuffle();
        goldenDeck.shuffle();
        normalDeck.shuffle();
        starterDeck.shuffle();
        //TODO: Make game start from a certain point
        //        for(int i=0; i<33; i++){
//            goldenDeck.drawCard();
//            normalDeck.drawCard();
//        }
        for (String name : playerNames)
            setupPlayer(name);
        return GameMessages.SETUP_COMPLETE;
    }

    public String chooseGoalPhase(ArrayList<GoalCard> playersGoalCardChoosen, ArrayList<StarterCard> starterCardFacePosition) {
        //not cheking if goal card not present in player hand
        for (Player player : players) {
            for (GoalCard goalCard : playersGoalCardChoosen) {
                if (player.getSetup().selectGoal(goalCard))
                    break;
            }
            for (StarterCard starterCard:starterCardFacePosition)
                if(numberCharNotEqualInSamePosition(player.getSetup().getStarterCard().asString(),starterCard.asString()))
                    if (player.getSetup().getStarterCard().asString().charAt(6)!=starterCard.asString().charAt(6))
                        player.getSetup().flipStarterCard();
            player.applySetup();
        }
        turn = 0;
        return GOAL_PHASE_COMPLETED;
    }

    //Check if is not player turn
    public String executeTurn(String playerName, Position position, PlayableCard playableCard, boolean deckType, int indexDeck) throws JsonException, DeckException, SyntaxException {
        if (!playerName.equals(currentPlayer().name())) return NOT_PLAYER_TURN;
        if (hasGameEnded()) return GAME_ENDED;
        return finalTurn ? executeFinalTurn(position,playableCard) : executeNormalTurn(position, playableCard, deckType, indexDeck);
    }

    private String executeNormalTurn(Position position, PlayableCard playableCard, boolean deckType, int indexDeck) throws DeckException, JsonException, SyntaxException {
        System.out.printf("executing turn of %s (turn %d)\n", currentPlayer().name(), turn);
        Player player = currentPlayer();
        if (normalDeck.isEmpty() && deckType == false)
            return CANT_DRAW_FROM_NORMAL_DECK_IS_EMPTY;
        if (goldenDeck.isEmpty() && deckType == true)
            return CANT_DRAW_FROM_GOLDEN_DECK_IS_EMPTY;
        if ((!deckType && normalDeck.getCards().size() - indexDeck < 0 )|| (deckType && goldenDeck.getCards().size() - indexDeck < 0) || indexDeck < 1 || indexDeck > 3)
            return INDEX_DECK_WRONG;
        if (player.placeCard(playableCard, position) == false)
            return INVALID_POSITION_CARD_OR_NOT_IN_HAND;
        else {
            player.incrementScoreLastCardPlaced();
        }
        //0 for standard deck, 1 for gold deck
        //deck empty o provo a pescare una carta non esistente
        if (!deckType) {
            player.addCardToHand((PlayableCard) normalDeck.drawCard(indexDeck));
        } else if (deckType) {
            player.addCardToHand((PlayableCard) goldenDeck.drawCard(indexDeck));
        }

        turn++;

        //controllo se turno finale lo faccio solo sull'ultima persona controllando tutti i punteggi
        if (!finalTurn)
            isFinalTurn();
        return VALID_TURN;
    }

    private String executeFinalTurn(Position position, PlayableCard playableCard) throws JsonException, SyntaxException {
        if ((turn+1) > lastTurn) {
            return GAME_ENDED;
        }
        Player player = players.get(turn % players.size());
        if (player.placeCard(playableCard, position) == false)
            return INVALID_POSITION_CARD_OR_NOT_IN_HAND;
        else {
            player.incrementScoreLastCardPlaced();
        }

        turn++;

        if(turn == lastTurn) {
            gameEnded=true;
            finalRanking = finalGamePhase();
        }

        return VALID_LAST_TURN;
    }
    //remind to check if front or back
    private void isFinalTurn() {
        if (turn % players.size() == 0 && finalTurn == false) {
            if (normalDeck.isEmpty() && goldenDeck.isEmpty()){
                finalTurn = true;
            lastTurn = turn + players.size();
            }
            for (Player player : players)
                if (player.getScore() >= 20) {
                    finalTurn = true;
                    lastTurn = turn + players.size();
                }
        }
    }
    private ArrayList<Player> finalGamePhase() throws SyntaxException {
        ArrayList<Player> ranking = players;
        for (Player player: ranking) {
            player.personalGoalScore();
            player.publicGoalScore(publicGoals);
        }
        ranking.sort((a,b)->Integer.compare(b.getScore(), a.getScore()));
        return ranking;
    }
    private boolean numberCharNotEqualInSamePosition(String s1, String s2){
        return s1.regionMatches(0,s2,0,6) && s1.regionMatches(7,s2,7,7);
    }
    //ONLY FOR TESTS
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

    public ArrayList<Player> getFinalRanking(){
        if (hasGameEnded())
            return finalRanking;
        return null;
    }

    public ArrayList<GoalCard> getPublicGoals() {
        return publicGoals;
    }
}