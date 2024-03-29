package IS24_LB11.game;

import IS24_LB11.game.components.GoalCard;
import IS24_LB11.game.components.PlayableCard;

import java.awt.*;
/*
java.awt.* (Abstract Window Toolkit)  allows us to use some intefaces that help us to menage graphic intefaces
 */
import java.util.ArrayList;

public class Player {

    private boolean status;
    private String name;
    private GoalCard personalGoal;
    private ArrayList<PlayableCard> onHandCard = new ArrayList<PlayableCard>();
    private Color color;
    private int score;

}
