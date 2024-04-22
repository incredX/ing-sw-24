package IS24_LB11.game;

import IS24_LB11.game.components.*;
import IS24_LB11.game.tools.JsonConverter;
import IS24_LB11.game.tools.JsonException;
import IS24_LB11.game.utils.Color;
import IS24_LB11.game.utils.Position;
import IS24_LB11.game.utils.SyntaxException;

/*
java.awt.* (Abstract Window Toolkit)  allows us to use some intefaces that help us to menage graphic intefaces
 */
import java.util.ArrayList;

public class Player implements JsonConvertable {
    private final String name;
    private final Color color;
    private Board board;
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
        this.board.start(setup.getStarterCard());
    }

    public boolean placeCard(PlayableCard card, Position position) throws JsonException, SyntaxException {
        if (hand.stream().filter(x -> x.asString().compareTo(card.asString()) == 0).count() == 0) {
            return false;
        }
        if (board.placeCard(card, position)) {
            hand.removeIf(carhand -> carhand.asString().compareTo(card.asString()) == 0);
            return true;
        } else {
            return false;
        }
    }
    public void personalGoalScore(){
        if (personalGoal.asString().length()==5)
            incrementScore(board.countGoalSymbols((GoalSymbol) personalGoal));
        else
            incrementScore(board.countGoalPatterns((GoalPattern) personalGoal));
    }
    public void incrementScoreLastCardPlaced() {
        score += board.calculateScoreOnLastPlacedCard();
    }

    public void incrementScore(int amount) {
        score += amount;
    }

    public String name() {
        return name;
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

    public void addCardToHand(PlayableCard playableCard) {
        hand.add(playableCard);
    }

    @Override
    public String toString() {
        JsonConverter jsonConverter = new JsonConverter();
        try {
            return "Player{" +
                    "name='" + name + '\'' +
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

    public void setBoard(Board board){
        this.board=board;
    }
}