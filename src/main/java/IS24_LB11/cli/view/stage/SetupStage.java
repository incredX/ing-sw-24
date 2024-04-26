package IS24_LB11.cli.view.stage;

import IS24_LB11.cli.ViewHub;
import IS24_LB11.cli.controller.SetupState;
import IS24_LB11.cli.view.game.*;
import IS24_LB11.game.components.*;
import com.googlecode.lanterna.TerminalPosition;

import java.util.ArrayList;

public class SetupStage extends Stage {
    private static final String[] GOAL_LABELS = new String[]{"goal (a)", "goal (b)"};
    private final ArrayList<PlayableCardView> handView;
    private final ArrayList<GoalView> goalViews;
    private final SetupState state;
    private StarterCardView starterCardView;
    private int chosenGoalIndex;

    public SetupStage(ViewHub viewHub, SetupState state) {
        super(viewHub);
        this.state = state;
        this.chosenGoalIndex = 0;
        this.starterCardView = new StarterCardView(state.getStarterCard());
        this.handView = new ArrayList<>(3);
        this.goalViews = new ArrayList<>(2);
        loadStarterCard();
        loadGoals();
        loadHand();
        resize();
    }

    @Override
    public void build() {
        drawBorders();
        drawStarterCard();
        drawGoalPointer();
        drawGoals();
        drawHand();
        updateViewHub();
    }

    @Override
    public void resize() {
        super.resize();
        placeStarterCard();
        placeGoals();
        placeHandHorizontal();
        rebuild();
    }

    public void setChosenGoal(int index) {
        clearGoalPointer();
        this.chosenGoalIndex = index&1;
        drawGoalPointer();
    }

    public void loadStarterCard() {
        starterCardView = new StarterCardView(state.getStarterCard());
    }

    public void loadGoals() {
        goalViews.clear();
        for(GoalCard goal: state.getGoals()) switch(goal) {
            case GoalPattern pattern -> goalViews.add(new GoalPatternView(pattern));
            case GoalSymbol symbol -> goalViews.add(new GoalSymbolView(symbol));
            default -> throw new IllegalStateException("Invalid goal: " + goal);
        }
    }

    public void loadHand() {
        handView.clear();
        for(PlayableCard card: state.getHand()) {
            switch (card) {
                case GoldenCard goldenCard -> handView.add(new GoldenCardView(goldenCard));
                case NormalCard normalCard -> handView.add(new NormalCardView(normalCard));
                default -> throw new IllegalArgumentException("Invalid card: " + card.asString());
            }
            handView.getLast().setMargins(0);
        }
    }

    private void drawStarterCard() {
        draw(starterCardView);
        buildRelativeArea(starterCardView.getRectangle());
    }

    private void drawGoals() {
        for (int i=0; i<goalViews.size(); i++) {
            draw(goalViews.get(i));
            fillRow(goalViews.get(i).getY(), goalViews.get(i).getX()+5, GOAL_LABELS[i]);
            buildRelativeArea(goalViews.get(i).getRectangle()
                    .withRelativePosition(0,-1)
                    .withRelativeSize(0,2));
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

    public void placeStarterCard() {
        if (!isMininimalSize()) {
            int w = starterCardView.getWidth(), h = starterCardView.getHeight();
            starterCardView.setPosition((getWidth()-w)/2, (getHeight()-h)/2-2);
        } else
            starterCardView.setPosition(0,0);
    }

    private void placeGoals() {
        int goalWidth = goalViews.getFirst().getWidth(), goalHeight = goalViews.getFirst().getHeight();
        if (!isMininimalSize()) {
            int x = (getWidth()-2*goalWidth-goalHeight)/2;
            int y = starterCardView.getY()-goalHeight-1;
            for (GoalView goal : goalViews) {
                goal.setPosition(x, y);
                x += goalWidth+4;
            }
        } else {
            goalViews.getFirst().setPosition(starterCardView.getXAndWidth()+4, 2);
            goalViews.getLast().setPosition(goalViews.getFirst().getXAndWidth()+4, 2);
        }
    }

    private void placeHandHorizontal() {
        int width = handView.size()*(handView.getFirst().getWidth()-1);
        int height = handView.getFirst().getHeight();
        int x = (getWidth()-width)/2 -1;
        int y = starterCardView.getYAndHeight()+1;
        for (PlayableCardView cardView: handView) {
            cardView.setPosition(x, y);
            x += cardView.getWidth()-1;
        }
    }

    private boolean isMininimalSize() {
        int goalHeight = goalViews.getFirst().getHeight();
        return getHeight() < 2*starterCardView.getHeight()+goalHeight+2;
    }
}
