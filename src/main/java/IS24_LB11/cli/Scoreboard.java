package IS24_LB11.cli;

import IS24_LB11.game.utils.Color;

import java.util.ArrayList;

public class Scoreboard {
    private final ArrayList<String> players;
    private final ArrayList<Integer> scores;
    private final ArrayList<Color> colors;
    private int indexPLayer;

    public Scoreboard(ArrayList<String> players, ArrayList<Integer> scores, ArrayList<Color> colors) {
        this.players = players;
        this.scores = scores;
        this.colors = colors;
        indexPLayer = 0;
    }

    public Scoreboard(ArrayList<String> players, ArrayList<Color> colors) {
        this.players = players;
        this.scores = new ArrayList<>(players.stream().map(p -> 0).toList());
        this.colors = colors;
        indexPLayer = 0;
    }

    public void setPlayers(ArrayList<String> players) {
        this.players.clear();
        this.players.addAll(players);
    }

    public void setScores(ArrayList<Integer> scores) {
        for (int i = 0; i < Integer.min(scores.size(), players.size()); i++) {
            if (this.scores.get(i) != scores.get(i)) this.scores.set(i, scores.get(i));
        }
    }

    public void removePlayer(String player) {
        int indexOfPlayerToRemove = this.players.indexOf(player);
        this.scores.remove(indexOfPlayerToRemove);
        this.players.remove(indexOfPlayerToRemove);
        this.colors.remove(indexOfPlayerToRemove);
        this.indexPLayer %= players.size();
    }

    public void setNextPlayer(String playerName) {
        for (int i = indexPLayer + 1; i < indexPLayer+players.size(); i++) {
            if (playerName.equals(players.get(i%players.size()))) {
                indexPLayer = i%players.size();
                return;
            }
        }
    }

    public ArrayList<String> getPlayers() {
        return players;
    }

    public ArrayList<Integer> getScores() {
        return scores;
    }

    public ArrayList<Color> getColors() {
        return colors;
    }

    public int getCurrentPlayerIndex() {
        return indexPLayer;
    }
}
