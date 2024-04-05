package IS24_LB11.game;

import IS24_LB11.game.components.GoalCard;
import IS24_LB11.game.components.JsonConvertable;
import IS24_LB11.game.components.PlayableCard;

import java.awt.*;
/*
java.awt.* (Abstract Window Toolkit)  allows us to use some intefaces that help us to menage graphic intefaces
 */
import java.util.ArrayList;

public class Player implements JsonConvertable {

    private boolean status;
    private String name;
    private GoalCard personalGoal;
    private ArrayList<PlayableCard> onHandCard = new ArrayList<PlayableCard>();
    private Color color;
    private int score;

    public String getName() {
        return name;
    }

    public Color getColor() {
        return color;
    }

    public int getScore() {
        return score;
    }

    public GoalCard getPersonalGoal() {
        return personalGoal;
    }

    public ArrayList<PlayableCard> getOnHandCard() {
        return onHandCard;
    }

    public boolean isStatus() {
        return status;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOnHandCard(ArrayList<PlayableCard> onHandCard) {
        this.onHandCard = onHandCard;
    }

    public void setPersonalGoal(GoalCard personalGoal) {
        this.personalGoal = personalGoal;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
