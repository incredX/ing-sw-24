package IS24_LB11.cli.view;

import IS24_LB11.cli.view.game.GoalPatternView;
import IS24_LB11.cli.view.game.GoalSymbolView;
import IS24_LB11.cli.view.game.GoalView;
import IS24_LB11.game.components.GoalCard;
import IS24_LB11.game.components.GoalPattern;
import IS24_LB11.game.components.GoalSymbol;
import IS24_LB11.game.utils.Color;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class TableView extends PopupView {
    private static final int DEFAULT_WIDTH = 60;
    private static final int DEFAULT_HEIGHT = 16;
    private static final String DASHED_HORIZONTAL_SEPARATOR = "-".repeat(DEFAULT_WIDTH-2);

    private ArrayList<String> players;
    private ArrayList<TextColor> colors;
    private ArrayList<Integer> scores;
    private ArrayList<GoalView> goalViews;
    private int indexCurrentPlayer;

    public TableView(TerminalSize parentSize) {
        super(DEFAULT_WIDTH, DEFAULT_HEIGHT,
                (parentSize.getColumns()-DEFAULT_WIDTH)/2, (parentSize.getRows()-DEFAULT_HEIGHT)/2);
        players = new ArrayList<>();
        colors = defaultColors();
        scores = new ArrayList<>();
        goalViews = new ArrayList<>();
        indexCurrentPlayer = 0;
    }

    @Override
    public void build() {
        drawBorders();
        drawStructure();
        drawGoals();
    }

    @Override
    public void resize(TerminalSize terminalSize) {
        int x = (terminalSize.getColumns()-getWidth())/2;
        int y = (terminalSize.getRows()-getHeight())/2;
        setPosition(new TerminalPosition(x, y));
    }

    public void loadColors(ArrayList<Color> colors) {
        this.colors = (ArrayList<TextColor>) colors.stream().map(color -> adaptColor(color)).collect(Collectors.toList());
    }

    public void loadPlayers(ArrayList<String> players) {
        this.players = players;
        for (int i = 0; i < players.size(); i++) {
            fillRow(firstRow()+2+i, 2, players.get(i), colors.get(i));
        }
    }

    public void loadScores(ArrayList<Integer> scores) {
        this.scores = scores;
        for (int i = 0; i < scores.size(); i++) {
            String scoreLine = scores.get(i) == 0 ? "" : "=".repeat(scores.get(i)-1)+">";
            fillRow(firstRow()+2+i, firstColumn()+19, String.format("%2d", scores.get(i)));
            fillRow(firstRow()+2+i, firstColumn()+22, scoreLine, colors.get(i));
        }
    }

    public void loadCurrentPlayer(int indexCurrentPlayer) {
        this.indexCurrentPlayer = indexCurrentPlayer;
        fillRow(firstRow()+2+indexCurrentPlayer, firstColumn(), '>');
    }

    public void loadGoals(ArrayList<GoalCard> goals) {
        int x = 1 + ((goals.size() == 2) ? DEFAULT_WIDTH+3 : 0);
        for (GoalCard goal : goals) {
            switch (goal) {
                case GoalPattern pattern -> goalViews.add(new GoalPatternView(pattern));
                case GoalSymbol symbol -> goalViews.add(new GoalSymbolView(symbol));
                default -> throw new IllegalStateException("Invalid goal: " + goal);
            }
            goalViews.getLast().setPosition(x, lastRow()-GoalView.HEIGHT);
            goalViews.getLast().build();
            if (goalViews.size() == 1) x += GoalView.WIDTH+3;
            else x += GoalView.WIDTH;
        }
    }

    private void drawStructure() {
        fillRow(firstRow(), 1, "SCOREBOARD");
        fillRow(firstRow()+1, DASHED_HORIZONTAL_SEPARATOR);
        fillRow(firstRow()+6, DASHED_HORIZONTAL_SEPARATOR);
        fillRow(firstRow()+7, 1, "PRIVATE GOAL");
        fillRow(firstRow()+7, 19, "| PUBLIC GOALS");
        fillRow(firstRow()+8, DASHED_HORIZONTAL_SEPARATOR);
        fillColumn(firstColumn()+19, 2, "||||-|-|||||");
        fillColumn(firstColumn()+23, 2, "[[[[");
        fillColumn(lastColumn()-1, 2, "]]]]");
    }

    private void drawGoals() {
        for (GoalView goal : goalViews) draw(goal);
    }

    private TextColor adaptColor(Color color) {
        return switch (color) {
            case RED -> TextColor.ANSI.RED_BRIGHT;
            case BLUE -> TextColor.ANSI.BLUE_BRIGHT;
            case GREEN -> TextColor.ANSI.GREEN_BRIGHT;
            case YELLOW -> TextColor.ANSI.YELLOW_BRIGHT;
        };
    }

    private static ArrayList<TextColor> defaultColors() {
        ArrayList<TextColor> colors = new ArrayList<>();
        colors.add(TextColor.ANSI.RED_BRIGHT);
        colors.add(TextColor.ANSI.GREEN_BRIGHT);
        colors.add(TextColor.ANSI.BLUE_BRIGHT);
        colors.add(TextColor.ANSI.YELLOW_BRIGHT);
        return colors;
    }
}
