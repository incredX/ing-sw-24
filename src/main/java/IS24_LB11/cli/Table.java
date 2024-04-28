package IS24_LB11.cli;

import IS24_LB11.game.components.GoalCard;

import java.util.ArrayList;

public class Table {
    private Scoreboard scoreboard;
    private ArrayList<GoalCard> publicGoals;

    public Table(Scoreboard scoreboard, ArrayList<GoalCard> publicGoals) {
        this.scoreboard = scoreboard;
        this.publicGoals = publicGoals;
    }

    public void setNextPlayer(String player) {
        scoreboard.setNextPlayer(player);
    }

    public void setScore(ArrayList<Integer> scores) {
        scoreboard.setScores(scores);
    }

    public Scoreboard getScoreboard() {
        return scoreboard;
    }

    public ArrayList<GoalCard> getPublicGoals() {
        return publicGoals;
    }
}
