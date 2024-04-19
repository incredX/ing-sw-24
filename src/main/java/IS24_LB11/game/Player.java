package IS24_LB11.game;

import IS24_LB11.game.components.GoalCard;
import IS24_LB11.game.components.PlayableCard;
import IS24_LB11.game.utils.Color;
import IS24_LB11.game.utils.Position;

/*
java.awt.* (Abstract Window Toolkit)  allows us to use some intefaces that help us to menage graphic intefaces
 */
import java.util.ArrayList;

public class Player {
    private final String name;
    private final Color color;
    private final Board board;
    private final PlayerSetup setup;
    private final ArrayList<PlayableCard> hand;
    private GoalCard personalGoal;
    private int score;

    public Player(String name, Color color, PlayerSetup setup) {
        this.name = name;
        this.color = color;
        this.board = new Board();
        this.setup = setup;
        this.hand = setup.hand();
        this.personalGoal = null;
        this.score = 0;
    }

    public void applySetup() {
        this.personalGoal = setup.chosenGoal().get();
        this.board.start(setup.starterCard());
    }

    public boolean placeCard(PlayableCard card, Position position) {
        if (hand.stream().mapToInt(x->x.asString().compareTo(card.asString())).findFirst()==null)
            return false;
        return board.placeCard(card, position);
    }

    public void incrementScore(int amount) {
        score += amount;
    }

    public String name() {
        return name;
    }

    public PlayerSetup setup() {
        return setup;
    }

    public int getScore() {
        return score;
    }

    public Board getBoard() {
        return board;
    }
}