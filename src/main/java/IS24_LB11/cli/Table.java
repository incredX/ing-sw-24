package IS24_LB11.cli;

import IS24_LB11.cli.event.server.ServerNewTurnEvent;
import IS24_LB11.cli.event.server.ServerPlayerSetupEvent;
import IS24_LB11.game.components.GoalCard;
import IS24_LB11.game.components.GoldenCard;
import IS24_LB11.game.components.NormalCard;

import java.util.ArrayList;

public class Table {
    private Scoreboard scoreboard;
    private ArrayList<GoalCard> publicGoals;
    private ArrayList<NormalCard> normalDeck;
    private ArrayList<GoldenCard> goldenDeck;
    private boolean finalRanking;

    public Table(Scoreboard scoreboard, ArrayList<GoalCard> publicGoals) {
        this.scoreboard = scoreboard;
        this.publicGoals = publicGoals;
        this.normalDeck = new ArrayList<>();
        this.goldenDeck = new ArrayList<>();
        this.finalRanking = false;
    }

    public Table(ServerPlayerSetupEvent setupEvent) {
        this.scoreboard = new Scoreboard(setupEvent.playersList(), setupEvent.colorList());
        this.publicGoals = setupEvent.publicGoals();
        this.normalDeck = setupEvent.normalDeck();
        this.goldenDeck = setupEvent.goldenDeck();
        this.finalRanking = false;
    }

    public void update(ServerNewTurnEvent newTurnEvent) {
        finalRanking = newTurnEvent.player().isEmpty();
        scoreboard.setScores(newTurnEvent.scores());
        scoreboard.setNextPlayer(newTurnEvent.player());
        normalDeck = newTurnEvent.normalDeck();
        goldenDeck = newTurnEvent.goldenDeck();
        if (normalDeck.size() == 3 && !normalDeck.getLast().isFaceDown()) normalDeck.getLast().flip();
        if (goldenDeck.size() == 3 && !goldenDeck.getLast().isFaceDown()) goldenDeck.getLast().flip();
    }

    public boolean isFinalRanking() {
        return finalRanking;
    }

    public int getCurrentTopPlayerIndex() {
        int indexMaxScore = 0, i = 1;
        for (int score: scoreboard.getScores().stream().skip(1).toList()) {
            if (score > scoreboard.getScores().get(indexMaxScore)) indexMaxScore = i;
            i++;
        }
        return indexMaxScore;
    }

    public Scoreboard getScoreboard() {
        return scoreboard;
    }

    public ArrayList<GoalCard> getPublicGoals() {
        return publicGoals;
    }

    public ArrayList<NormalCard> getNormalDeck() {
        return normalDeck;
    }

    public ArrayList<GoldenCard> getGoldenDeck() {
        return goldenDeck;
    }
}
