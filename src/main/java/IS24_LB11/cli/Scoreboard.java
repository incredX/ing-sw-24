package IS24_LB11.cli;

import IS24_LB11.game.utils.Color;

import java.util.ArrayList;

/**
 * The Scoreboard class represents the scoreboard for the game,
 * managing players, their scores, and their associated colors.
 */
public class Scoreboard {
    private final ArrayList<String> players;
    private final ArrayList<Integer> scores;
    private final ArrayList<Color> colors;
    private int indexPLayer;

    /**
     * Constructs a Scoreboard object with the given players, scores, and colors.
     *
     * @param players the list of players
     * @param scores the list of scores corresponding to the players
     * @param colors the list of colors corresponding to the players
     */
    public Scoreboard(ArrayList<String> players, ArrayList<Integer> scores, ArrayList<Color> colors) {
        this.players = players;
        this.scores = scores;
        this.colors = colors;
        this.indexPLayer = 0;
    }

    /**
     * Constructs a Scoreboard object with the given players and colors,
     * initializing all scores to zero.
     *
     * @param players the list of players
     * @param colors the list of colors corresponding to the players
     */
    public Scoreboard(ArrayList<String> players, ArrayList<Color> colors) {
        this.players = players;
        this.scores = new ArrayList<>(players.stream().map(p -> 0).toList());
        this.colors = colors;
        this.indexPLayer = 0;
    }

    /**
     * Sets the list of players.
     *
     * @param players the new list of players
     */
    public void setPlayers(ArrayList<String> players) {
        this.players.clear();
        this.players.addAll(players);
    }

    /**
     * Sets the scores for the players.
     *
     * @param scores the new list of scores
     */
    public void setScores(ArrayList<Integer> scores) {
        for (int i = 0; i < Integer.min(scores.size(), players.size()); i++) {
            if (!this.scores.get(i).equals(scores.get(i))) {
                this.scores.set(i, scores.get(i));
            }
        }
    }

    /**
     * Removes a player from the scoreboard.
     *
     * @param player the player to remove
     */
    public void removePlayer(String player) {
        int indexOfPlayerToRemove = this.players.indexOf(player);
        this.scores.remove(indexOfPlayerToRemove);
        this.players.remove(indexOfPlayerToRemove);
        this.colors.remove(indexOfPlayerToRemove);
        this.indexPLayer %= players.size();
    }

    /**
     * Sets the next player to play based on their name.
     *
     * @param playerName the name of the next player
     */
    public void setNextPlayer(String playerName) {
        for (int i = indexPLayer + 1; i < indexPLayer + players.size(); i++) {
            if (playerName.equals(players.get(i % players.size()))) {
                indexPLayer = i % players.size();
                return;
            }
        }
    }

    /**
     * Gets the number of players.
     *
     * @return the number of players
     */
    public int getNumPlayers() {
        return players.size();
    }

    /**
     * Gets the list of players.
     *
     * @return the list of players
     */
    public ArrayList<String> getPlayers() {
        return players;
    }

    /**
     * Gets the list of scores.
     *
     * @return the list of scores
     */
    public ArrayList<Integer> getScores() {
        return scores;
    }

    /**
     * Gets the list of colors.
     *
     * @return the list of colors
     */
    public ArrayList<Color> getColors() {
        return colors;
    }

    /**
     * Gets the index of the current player.
     *
     * @return the index of the current player
     */
    public int getCurrentPlayerIndex() {
        return indexPLayer;
    }
}
