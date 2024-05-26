package IS24_LB11.game;

import IS24_LB11.cli.Debugger;
import IS24_LB11.game.components.*;
import IS24_LB11.game.tools.JsonConverter;
import IS24_LB11.game.tools.JsonException;
import IS24_LB11.game.utils.Color;
import IS24_LB11.game.utils.Position;

import java.util.ArrayList;

/**
 * Represents a player in the game, holding the player's state and behaviors.
 */
public class Player implements JsonConvertable {
    private final String name;
    private final Color color;
    private Board board;
    private final PlayerSetup setup;
    private final ArrayList<PlayableCard> hand;
    private GoalCard personalGoal;
    private int score;

    /**
     * Constructs a new player with the specified name and a setup with the initials cards.
     *
     * @param name the name of the player
     * @param setup the player's setup configuration
     */
    public Player(String name, PlayerSetup setup) {
        this.name = name;
        this.color = setup.getColor();
        this.board = new Board();
        this.setup = setup;
        this.hand = setup.hand();
        this.personalGoal = null;
        this.score = 0;
    }

    /**
     * Applies the player's setup, initializing their personal goal and place his starter card.
     */
    public void applySetup() {
        this.personalGoal = setup.chosenGoal();
        this.board.start(setup.getStarterCard());
    }

    /**
     * Places a card on the board at the specified position.
     *
     * @param card the card to place
     * @param position the position to place the card
     * @return true if the card was placed successfully, false otherwise
     */
    public boolean placeCard(PlayableCard card, Position position) {
        System.out.println("Player Hand: ");
        hand.stream().forEach(x-> System.out.printf(x.asString() + "  "));
        System.out.println();

        if (hand.stream().filter(x -> x.equals(card)).count()!=1){
            System.out.println("CARD NOT FOUND " + card.asString());
            return false;
        }


        int i=-1;
        if (board.placeCard(card, position)) {
            for (PlayableCard playableCard : hand) {
                if (playableCard.equals(card))
                    i=hand.indexOf(playableCard);
            }
            if (i!=-1)
                hand.remove(i);
            return true;
        } else {
            return false;
        }
    }

    public Result<Position> tryPlaceCard(PlayableCard card, Position position) {
        return board.tryPlaceCard(card, position)
                .andThen(pos -> {
                    if (hand.removeIf(carhand -> carhand.equals(card))) return Result.Ok(pos);
                    ArrayList<String> hand = new ArrayList<>(getHand().stream().map(c -> c.asString()).toList());
                    return Result.Error("card " + card.asString() + " not found in hand", "hand: " + hand);
                });
    }

    /**
     * Calculates and prints the score for the player's personal goal.
     */
    public void personalGoalScore() {
        int scoreGoal;
        if (personalGoal.asString().length() == 5) {
            scoreGoal = (board.countGoalSymbols((GoalSymbol) personalGoal));
        } else {
            scoreGoal = (board.countGoalPatterns((GoalPattern) personalGoal));
        }
        System.out.println(name + " Personal Goal Score: " + scoreGoal);
        incrementScore(scoreGoal);
    }

    /**
     * Calculates and prints the score for the player's public goals.
     *
     * @param publicGoals the list of public goals
     */
    public void publicGoalScore(ArrayList<GoalCard> publicGoals) {
        int scoreGoal;
        for (GoalCard goalCard : publicGoals) {
            if (goalCard.asString().length() == 5) {
                scoreGoal = (board.countGoalSymbols((GoalSymbol) goalCard));
            } else {
                scoreGoal = (board.countGoalPatterns((GoalPattern) goalCard));
            }
            System.out.println(name + " Private Goal Score: " + scoreGoal);
            incrementScore(scoreGoal);
        }
    }

    /**
     * Increments the player's score based on the last placed card.
     */
    public void incrementScoreLastCardPlaced() {
        score += board.calculateScoreOnLastPlacedCard();
    }

    /**
     * Increments the player's score by a specified amount.
     *
     * @param amount the amount to increment the score by
     */
    public void incrementScore(int amount) {
        score += amount;
    }

    /**
     * Adds a card to the player's hand.
     *
     * @param playableCard the card to add to the hand
     */
    public void addCardToHand(PlayableCard playableCard) {
        hand.add(playableCard);
    }
@Override
    public String toString() {
        JsonConverter jsonConverter = new JsonConverter();
        try {
            return "Player{" +
                    "playerName='" + name + '\'' +
                    ", color=" + color +
                    ", ACTUALhand=" + hand.stream().map(x -> x.asString()).reduce("", (x, y) -> x + " " + y) +
                    ", board=" + jsonConverter.objectToJSON(board) +
                    ", setup=" + setup +
                    ", personalGoal=" + personalGoal.asString() +
                    ", score=" + score +
                    ", symbols= " + board.getSymbolCounter() +
                    '}';
        } catch (JsonException e) {
            throw new RuntimeException(e);
        }
    }
    public Color getColor() {
        return color;
    }
    public void setBoard(Board board) {
        this.board = board;
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
    public GoalCard getPersonalGoal() {
        return personalGoal;
    }

    public PlayerSetup getSetup() {
        return setup;
    }
    public ArrayList<PlayableCard> getHand() {
        return hand;
    }
}