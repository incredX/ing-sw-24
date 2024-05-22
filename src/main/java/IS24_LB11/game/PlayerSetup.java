package IS24_LB11.game;

import IS24_LB11.game.components.GoalCard;
import IS24_LB11.game.components.JsonConvertable;
import IS24_LB11.game.components.PlayableCard;
import IS24_LB11.game.components.StarterCard;
import IS24_LB11.game.utils.Color;

import java.util.ArrayList;

/**
 * Represents the setup configuration for a player, including their starter card, goals, hand of cards, and chosen color.
 */
public class PlayerSetup implements JsonConvertable {
    private final StarterCard starterCard;
    private final GoalCard[] goals;
    private final ArrayList<PlayableCard> hand;
    private int chosenGoalIndex;
    private Color color;

    /**
     * Constructs a new PlayerSetup with the specified starter card, goals, hand, and color.
     *
     * @param starterCard the starter card for the player
     * @param goals an array of goals for the player
     * @param hand the player's hand of playable cards
     * @param color the color representing the player
     */
    public PlayerSetup(StarterCard starterCard, GoalCard[] goals, ArrayList<PlayableCard> hand, Color color) {
        this.starterCard = starterCard;
        this.goals = goals;
        this.hand = hand;
        this.chosenGoalIndex = 0;
        this.color = color;
    }

    /**
     * Selects a goal card for the player.
     *
     * @param goalCard the goal card to be selected
     * @return true if the goal card is successfully selected, false otherwise
     */
    public boolean selectGoal(GoalCard goalCard) {
        if (goalCard.asString().compareTo(goals[0].asString()) == 0) {
            chosenGoalIndex = 0;
            return true;
        }
        if (goalCard.asString().compareTo(goals[1].asString()) == 0) {
            chosenGoalIndex = 1;
            return true;
        }
        return false;
    }

    /**
     * Chooses a goal for the player by its index.
     *
     * @param index the index of the goal to be chosen
     */
    public void chooseGoal(int index) {
        chosenGoalIndex = index & 1;
    }

    /**
     * Flips the starter card.
     */
    public void flipStarterCard() {
        starterCard.flip();
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
    public StarterCard getStarterCard() {
        return starterCard;
    }
    public GoalCard chosenGoal() {
        return goals[chosenGoalIndex];
    }
    public ArrayList<PlayableCard> hand() {
        return hand;
    }
    public GoalCard[] getGoals() {
        return goals;
    }
    public int getChosenGoalIndex() {
        return chosenGoalIndex;
    }
    public Color getColor() {
        return color;
    }
}