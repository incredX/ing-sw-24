package IS24_LB11.cli;

import IS24_LB11.cli.event.server.ServerNewTurnEvent;
import IS24_LB11.game.components.GoalCard;
import IS24_LB11.game.components.GoldenCard;
import IS24_LB11.game.components.NormalCard;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class Table {
    private Scoreboard scoreboard;
    private ArrayList<GoalCard> publicGoals;
    private ArrayList<NormalCard> normalDeck;
    private ArrayList<GoldenCard> goldenDeck;

    public Table(Scoreboard scoreboard, ArrayList<GoalCard> publicGoals) {
        this.scoreboard = scoreboard;
        this.publicGoals = publicGoals;
        this.normalDeck = new ArrayList<>();
        this.goldenDeck = new ArrayList<>();
    }

    public void update(ServerNewTurnEvent newTurnEvent) {
        scoreboard.setScores(newTurnEvent.scores());
        scoreboard.setNextPlayer(newTurnEvent.player());
        normalDeck = newTurnEvent.normalDeck();
        goldenDeck = newTurnEvent.goldenDeck();
        System.out.printf("%16s : (%s)  (%s)\n", newTurnEvent.player(),
                normalDeck.stream().map(NormalCard::asString).collect(Collectors.joining(", ")),
                goldenDeck.stream().map(GoldenCard::asString).collect(Collectors.joining(", ")));
        if (!normalDeck.getLast().isFaceDown()) normalDeck.getLast().flip();
        if (!goldenDeck.getLast().isFaceDown()) goldenDeck.getLast().flip();
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
