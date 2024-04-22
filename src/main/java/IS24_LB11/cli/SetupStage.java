package IS24_LB11.cli;

import IS24_LB11.cli.style.DashedBorderStyle;
import IS24_LB11.cli.utils.CliBox;
import IS24_LB11.cli.view.*;
import IS24_LB11.game.PlayerSetup;
import IS24_LB11.game.components.*;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

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
        resize(terminalSize);
    }

    @Override
    public void build() {
        super.build();
        drawStarteCard();
        drawGoalPointer();
        drawGoals();
        drawHand();
    }

    @Override
    public void resize(TerminalSize terminalSize) {
        super.resize(terminalSize);
        starterCardView.setPosition(1,3);
        goalViews.getFirst().setPosition(starterCardView.getXAndWidth()+4, 2);
        goalViews.getLast().setPosition(starterCardView.getXAndWidth()+4, goalViews.getFirst().getYAndHeight()+1);
        TerminalPosition base = new TerminalPosition(1, starterCardView.getYAndHeight()+2);
        for (int i = 0; i < handView.size(); i++) {
            handView.get(i).setPosition(base);
            base = base.withColumn(handView.get(i).getXAndWidth());
        }
    }

    public void setChosenGoal(int index) {
        clearGoalPointer();
        this.chosenGoalIndex = index&1;
        drawGoalPointer();
    }

    public void buildStarterCard(StarterCard starterCard) {
        starterCardView = new StarterCardView(starterCard);
        starterCardView.setPosition(1,3);
        drawStarteCard();
    }

    private void drawStarteCard() {
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
