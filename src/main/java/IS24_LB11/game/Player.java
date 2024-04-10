package IS24_LB11.game;

import IS24_LB11.game.components.GoalCard;
import IS24_LB11.game.components.PlayableCard;
import IS24_LB11.game.components.PlayerSetup;
import IS24_LB11.game.utils.Color;
import IS24_LB11.game.utils.Position;

/*
java.awt.* (Abstract Window Toolkit)  allows us to use some intefaces that help us to menage graphic intefaces
 */
import java.util.ArrayList;

public class Player {
    private String name;
    private GoalCard personalGoal;
    private final Board board;
    private ArrayList<PlayableCard> hand = new ArrayList<PlayableCard>();
    private Color color;
    private int score;

    public Player(String name, Color color, PlayerSetup setup) {
        this.name = name;
        this.color = color;
        this.board = new Board();
        this.score = 0;
        this.personalGoal = setup.chosenGoal().get();
        this.hand = setup.hand();

        this.board.start(setup.starterCard());
    }

    public boolean placeCard(PlayableCard card, Position position) {
        return board.placeCard(card, position);
    }

    public String name() {
        return name;
    }
}
