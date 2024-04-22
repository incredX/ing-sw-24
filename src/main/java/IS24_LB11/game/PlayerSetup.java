package IS24_LB11.game;

import IS24_LB11.game.Result;
import IS24_LB11.game.components.GoalCard;
import IS24_LB11.game.components.PlayableCard;
import IS24_LB11.game.components.StarterCard;

import java.util.ArrayList;
import java.util.Arrays;

public class PlayerSetup {
    private final StarterCard starterCard;
    private final GoalCard[] goals;
    private final ArrayList<PlayableCard> hand;
    private int chosenGoalIndex;

    public PlayerSetup(StarterCard starterCard, GoalCard[] goals, ArrayList<PlayableCard> hand) {
        this.starterCard = starterCard;
        this.goals = goals;
        this.hand = hand;
        this.chosenGoalIndex = 0;
    }

    public boolean selectGoal(GoalCard goalCard) {
        if (goalCard.asString().compareTo(goals[0].asString())==0) {
            chosenGoalIndex = 0;
            return true;
        }
        if (goalCard.asString().compareTo(goals[1].asString())==0) {
            chosenGoalIndex = 1;
            return true;
        }
        return false;
    }

    public void choseGoal(int index) {
        chosenGoalIndex = index&1;
    }

    public void flipCard() {
        starterCard.flip();
    }

    public StarterCard starterCard() {
        return starterCard;
    }

    public GoalCard chosenGoal() {
        return goals[chosenGoalIndex];
    }

    public ArrayList<PlayableCard> hand() {
        return hand;
    }

    //ONLY FOR TESTS
    public GoalCard[] getGoals() {
        return goals;
    }

    @Override
    public String toString() {
        return "PlayerSetup{" +
                "starterCard=" + starterCard.asString() +
                ", goals=" + goals[0].asString() + " " + goals[1].asString() +
                ", hand=" + hand.stream().map(x -> x.asString()).reduce("", (x, y) -> x + " " + y) +
                ", chosenGoalIndex=" + chosenGoalIndex +
                '}';
    }
}
