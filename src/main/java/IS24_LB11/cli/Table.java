package IS24_LB11.cli;

import IS24_LB11.cli.event.server.ServerNewTurnEvent;
import IS24_LB11.cli.event.server.ServerPlayerSetupEvent;
import IS24_LB11.game.components.GoalCard;
import IS24_LB11.game.components.GoldenCard;
import IS24_LB11.game.components.NormalCard;

import java.util.ArrayList;

/**
 * Table class represents the game table, which includes the scoreboard, public goals, normal deck, and golden deck.
 * It provides methods to update the table state based on new turn events and to access the table's components.
 */
public class Table {
    private Scoreboard scoreboard;
    private ArrayList<GoalCard> publicGoals;
    private ArrayList<NormalCard> normalDeck;
    private ArrayList<GoldenCard> goldenDeck;
    private boolean finalRanking;

    /**
     * Constructs a Table with the given scoreboard and public goals.
     *
     * @param scoreboard the scoreboard of the game
     * @param publicGoals the public goals of the game
     */
    public Table(Scoreboard scoreboard, ArrayList<GoalCard> publicGoals) {
        this.scoreboard = scoreboard;
        this.publicGoals = publicGoals;
        this.normalDeck = new ArrayList<>();
        this.goldenDeck = new ArrayList<>();
        this.finalRanking = false;
    }

    /**
     * Constructs a Table based on the server player setup event.
     *
     * @param setupEvent the server player setup event
     */
    public Table(ServerPlayerSetupEvent setupEvent) {
        this.scoreboard = new Scoreboard(setupEvent.playersList(), setupEvent.colorList());
        this.publicGoals = setupEvent.publicGoals();
        this.normalDeck = setupEvent.normalDeck();
        this.goldenDeck = setupEvent.goldenDeck();
        this.finalRanking = false;
    }

    /**
     * Updates the table state based on the server new turn event.
     *
     * @param newTurnEvent the server new turn event
     */
    public void update(ServerNewTurnEvent newTurnEvent) {
        finalRanking = newTurnEvent.player().isEmpty();
        scoreboard.setScores(newTurnEvent.scores());
        scoreboard.setNextPlayer(newTurnEvent.player());
        normalDeck = newTurnEvent.normalDeck();
        goldenDeck = newTurnEvent.goldenDeck();
        if (normalDeck.size() == 3 && !normalDeck.getLast().isFaceDown()) {
            normalDeck.getLast().flip();
        }
        if (goldenDeck.size() == 3 && !goldenDeck.getLast().isFaceDown()) {
            goldenDeck.getLast().flip();
        }
    }

    /**
     * Returns whether the final ranking has been reached.
     *
     * @return true if the final ranking has been reached, false otherwise
     */
    public boolean isFinalRanking() {
        return finalRanking;
    }

    /**
     * Returns the index of the current top player based on the scores.
     *
     * @return the index of the current top player
     */
    public int getCurrentTopPlayerIndex() {
        int indexMaxScore = 0, i = 1;
        for (int score : scoreboard.getScores().stream().skip(1).toList()) {
            if (score > scoreboard.getScores().get(indexMaxScore)) indexMaxScore = i;
            i++;
        }
        return indexMaxScore;
    }

    /**
     * Returns the scoreboard of the table.
     *
     * @return the scoreboard
     */
    public Scoreboard getScoreboard() {
        return scoreboard;
    }

    /**
     * Returns the public goals of the table.
     *
     * @return the public goals
     */
    public ArrayList<GoalCard> getPublicGoals() {
        return publicGoals;
    }

    /**
     * Returns the normal deck of the table.
     *
     * @return the normal deck
     */
    public ArrayList<NormalCard> getNormalDeck() {
        return normalDeck;
    }

    /**
     * Returns the golden deck of the table.
     *
     * @return the golden deck
     */
    public ArrayList<GoldenCard> getGoldenDeck() {
        return goldenDeck;
    }
}
