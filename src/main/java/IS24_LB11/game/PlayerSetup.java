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
        this.chosenGoalIndex = -1;
    }

    public void selectGoal(int index) {
        chosenGoalIndex = index%2; // <- module 2 to prevent possible errors
    }

    public void flipCard() {
        starterCard.flip();
    }

    public StarterCard starterCard() {
        return starterCard;
    }

    public Result<GoalCard> chosenGoal() {
        if (chosenGoalIndex < 0) return Result.Error("no goal was chosen");
        return Result.Ok(goals[chosenGoalIndex]);
    }

    public ArrayList<PlayableCard> hand() {
        return hand;
    }
}
