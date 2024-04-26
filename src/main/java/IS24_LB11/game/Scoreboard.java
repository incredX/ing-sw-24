package IS24_LB11.game;

import com.googlecode.lanterna.TextColor;

import java.util.ArrayList;

public class Scoreboard {
    private final ArrayList<String> players;
    private final ArrayList<Integer> scores;
    private final ArrayList<TextColor> colors;
    private int indexCurrentPlayer;

    public Scoreboard(ArrayList<String> players, ArrayList<Integer> scores, ArrayList<TextColor> colors) {
        this.players = players;
        this.scores = scores;
        this.colors = colors;
        indexCurrentPlayer = 0;
    }

    public void setScore(String playerName, int score) {
        for (int i = 0; i < players.size(); i++) {
            if (playerName.equals(players.get(i))) scores.set(i, score);
        }
    }

    public void setNextPlayer() {
        indexCurrentPlayer += (indexCurrentPlayer+1) % players.size();
    }

    public ArrayList<String> getPlayers() {
        return players;
    }

    public ArrayList<Integer> getScores() {
        return scores;
    }

    public ArrayList<TextColor> getColors() {
        return colors;
    }

    public int getIndexCurrentPlayer() {
        return indexCurrentPlayer;
    }
}
