package IS24_LB11.cli.view.stage;

import IS24_LB11.cli.ViewHub;
import IS24_LB11.cli.view.game.*;
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

    public SetupStage(ViewHub viewHub, PlayerSetup setup) {
        super(viewHub);
        this.chosenGoalIndex = 0;
        this.starterCardView = new StarterCardView(setup.getStarterCard());
        this.handView = new ArrayList<>(3);
        this.goalViews = new ArrayList<>(2);
        for(PlayableCard card: setup.hand()) switch(card) {
            case GoldenCard goldenCard -> handView.add(new GoldenCardView(goldenCard));
            case NormalCard normalCard -> handView.add(new NormalCardView(normalCard));
            default -> throw new IllegalArgumentException("Invalid card: " + card.asString());
        }
        for(GoalCard goal: setup.getGoals()) switch(goal) {
            case GoalPattern pattern -> goalViews.add(new GoalPatternView(pattern));
            case GoalSymbol symbol -> goalViews.add(new GoalSymbolView(symbol));
            default -> throw new IllegalStateException("Invalid goal: " + goal);
        }
        for (PlayableCardView cardView: handView) cardView.setMargins(0);
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
        placeStarterCard(getScreenSize());
        placeGoals(getScreenSize());
        placeHandHorizontal(getScreenSize());
        updateViewHub();
    }

    public void setChosenGoal(int index) {
        clearGoalPointer();
        this.chosenGoalIndex = index&1;
        drawGoalPointer();
    }

    public void buildStarterCard(StarterCard starterCard) {
        starterCardView = new StarterCardView(starterCard);
        placeStarterCard(rectangle.getSize().withRelative(0, 4));
        drawStarterCard();
    }

    private void drawStarterCard() {
        draw(starterCardView);
        buildRelativeArea(starterCardView.getRectangle());
    }

    private void drawGoals() {
        String[] legends = new String[] {"Gaol (a)", "Gaol (b)"};
        for (int i=0; i<goalViews.size(); i++) {
            draw(goalViews.get(i));
            fillRow(goalViews.get(i).getY(), goalViews.get(i).getX()+5, legends[i]);
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

    private void placeStarterCard(TerminalSize terminalSize) {
        if (!isMininimalSize(terminalSize)) {
            int w = starterCardView.getWidth(), h = starterCardView.getHeight();
            starterCardView.setPosition((terminalSize.getColumns()-w)/2, (terminalSize.getRows()-h)/2-2);
        } else
            starterCardView.setPosition(0,0);
    }

    private void placeGoals(TerminalSize terminalSize) {
        int goalWidth = goalViews.getFirst().getWidth(), goalHeight = goalViews.getFirst().getHeight();
        if (!isMininimalSize(terminalSize)) {
            int x = (terminalSize.getColumns()-2*goalWidth-goalHeight)/2;
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

    private void placeHandHorizontal(TerminalSize terminalSize) {
        int width = handView.size()*(handView.getFirst().getWidth()-1);
        int height = handView.getFirst().getHeight();
        int x = (terminalSize.getColumns()-width)/2 -1;
        int y = isMininimalSize(terminalSize) ?
                terminalSize.getRows()-height-6 : starterCardView.getYAndHeight()+1;
        for (PlayableCardView cardView: handView) {
            cardView.setPosition(x, y);
            x += cardView.getWidth()-1;
        }
    }

    private boolean isMininimalSize(TerminalSize terminalSize) {
        int goalHeight = goalViews.getFirst().getHeight();
        return terminalSize.getRows() < 2*starterCardView.getHeight()+goalHeight+2;
    }
}
