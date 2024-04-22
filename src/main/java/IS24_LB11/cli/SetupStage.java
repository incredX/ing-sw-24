package IS24_LB11.cli;

import IS24_LB11.cli.view.*;
import IS24_LB11.game.PlayerSetup;
import IS24_LB11.game.components.*;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;

import java.util.ArrayList;

public class SetupStage extends Stage {
    private int chosenGoalIndex;
    private StarterCardView starterCardView;
    private final ArrayList<PlayableCardView> handView;
    private final ArrayList<GoalView> goalViews;

    public SetupStage(TerminalSize terminalSize, PlayerSetup setup) {
        super(terminalSize);
        this.chosenGoalIndex = 0;
        this.starterCardView = new StarterCardView(setup.starterCard());
        this.handView = new ArrayList<>(3);
        this.goalViews = new ArrayList<>(2);
        for(PlayableCard card: setup.hand()) switch(card) {
            case GoldenCard goldenCard -> handView.add(new GoldenCardView(goldenCard));
            case NormalCard normalCard -> handView.add(new NormalCardView(normalCard));
            default -> throw new IllegalArgumentException("Invalid card: " + card);
        }
        for(GoalCard goal: setup.getGoals()) switch(goal) {
            case GoalPattern pattern -> goalViews.add(new GoalPatternView(pattern));
            case GoalSymbol symbol -> goalViews.add(new GoalSymbolView(symbol));
            default -> throw new IllegalStateException("Invalid goal: " + goal);
        }
        for (PlayableCardView cardView: handView) cardView.setMargins(0);
        resize(terminalSize);
    }

    @Override
    public void build() {
        super.build();
        drawStarterCard();
        drawGoalPointer();
        drawGoals();
        drawHand();
    }

    @Override
    public void resize(TerminalSize terminalSize) {
        super.resize(terminalSize);
        starterCardView.setPosition(0,0);
        goalViews.getFirst().setPosition(starterCardView.getXAndWidth()+4, 2);
        goalViews.getLast().setPosition(goalViews.getFirst().getXAndWidth()+4, 2);
        placeHandHorizontal(terminalSize);
    }

    public void setChosenGoal(int index) {
        clearGoalPointer();
        this.chosenGoalIndex = index&1;
        drawGoalPointer();
    }

    public void buildStarterCard(StarterCard starterCard) {
        starterCardView = new StarterCardView(starterCard);
        starterCardView.setPosition(1,3);
        drawStarterCard();
    }

    private void drawStarterCard() {
        draw(starterCardView);
        buildRelativeArea(starterCardView.getRectangle());
    }

    private void drawGoals() {
        for (GoalView goal : goalViews) {
            draw(goal);
            buildRelativeArea(goal.getRectangle());
        }
    }

    private void drawHand() {
        int x = handView.getLast().getXAndWidth(), y = handView.getLast().getYAndHeight();
        if (!rectangle.contains(new TerminalPosition(x, y))) return;
        for (PlayableCardView hand : handView) {
            draw(hand);
            buildRelativeArea(hand.getRectangle());
        }
    }

    private void drawGoalPointer() {
        GoalView goalView = goalViews.get(chosenGoalIndex);
        int x = goalView.getPosition().getColumn(), y = goalView.getPosition().getRow()+1;
        int w = goalView.getWidth()+1, h = goalView.getHeight()+2;
        fillColumn(x, y, "###");
        fillColumn(x+w, y, "###");
        fillColumn(x-1, y, "│││");
        fillColumn(x+w+1, y, "│││");
        buildRelativeArea(2, 3, x-1, y);
        buildRelativeArea(2, 3, x+w, y);
    }

    private void clearGoalPointer() {
        GoalView goalView = goalViews.get(chosenGoalIndex);
        int x = goalView.getPosition().getColumn(), y = goalView.getPosition().getRow()+1;
        int w = goalView.getWidth()+1, h = goalView.getHeight()+2;
        fillColumn(x, y, "   ");
        fillColumn(x+w, y, "   ");
        fillColumn(x-1, y, "   ");
        fillColumn(x+w+1, y, "   ");
        buildRelativeArea(2, 3, x-1, y);
        buildRelativeArea(2, 3, x+w, y);
    }

    private void placeHandHorizontal(TerminalSize terminalSize) {
        int width = handView.size()*(handView.getFirst().getWidth()-1);
        int height = handView.getFirst().getHeight();
        int x = (terminalSize.getColumns()-width)/2 -1;
        int y = terminalSize.getRows()-height-6;
        for (PlayableCardView cardView: handView) {
            cardView.setPosition(x, y);
            x += cardView.getWidth()-1;
        }
        System.out.printf("rect %dx%d contains (%d,%d)?\n", rectangle.getWidth(), rectangle.getHeight(),
                handView.getLast().getXAndWidth(), handView.getLast().getYAndHeight());
    }

//    private void drawGoalPointer() {
//        GoalView goalView = goalViews.get(chosenGoalIndex&2);
//        int x = goalView.getPosition().getColumn()-3, y = goalView.getPosition().getRow()-1;
//        int w = goalView.getWidth()+6, h = goalView.getHeight()+2;
//        CliBox box = new CliBox(w, h, x, y, new DashedBorderStyle());
//        box.build();
//        draw(box);
//        buildRelativeArea(box.getRectangle());
//    }

}
