package IS24_LB11.cli.view.popup;

import IS24_LB11.cli.Scoreboard;
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
    private static final String DASHED_HORIZONTAL_SEPARATOR = "-".repeat(DEFAULT_WIDTH-4);
    private static final String FULL_SCORE_LINE = "=".repeat(30);

    private ArrayList<String> players;
    private ArrayList<TextColor> colors;
    private ArrayList<Integer> scores;
    private ArrayList<GoalView> goalViews;
    private int indexCurrentPlayer;
    private int indexWinner;

    public TableView(TerminalSize parentSize) {
        super(DEFAULT_WIDTH, DEFAULT_HEIGHT,
                (parentSize.getColumns()-DEFAULT_WIDTH)/2, (parentSize.getRows()-DEFAULT_HEIGHT)/2);
        players = new ArrayList<>();
        colors = defaultColors();
        scores = new ArrayList<>();
        goalViews = new ArrayList<>();
        indexCurrentPlayer = 0;
        indexWinner = -1;
    }

    @Override
    public void drawAll() {
        drawBorders();
        drawStructure();
        drawScoreBoard();
        drawGoals();
    }

    @Override
    public void resize(TerminalSize terminalSize) {
        int x = (terminalSize.getColumns()-getWidth())/2;
        int y = (terminalSize.getRows()-getHeight())/2;
        setPosition(new TerminalPosition(x, y));
    }

    public void loadScoreboard(Scoreboard scoreboard) {
        loadColors(scoreboard.getColors());
        loadPlayers(scoreboard.getPlayers());
        loadScores(scoreboard.getScores());
        loadCurrentPlayer(scoreboard.getCurrentPlayerIndex());
    }

    public void loadGoals(ArrayList<GoalCard> goals) {
        int[] cols = new int[] { 1, GoalView.WIDTH+4, 2*GoalView.WIDTH+4 };
        int i = 3-goals.size();
        for (GoalCard goal : goals) {
            switch (goal) {
                case GoalPattern pattern -> goalViews.add(new GoalPatternView(pattern));
                case GoalSymbol symbol -> goalViews.add(new GoalSymbolView(symbol));
                default -> throw new IllegalStateException("Invalid goal: " + goal);
            }
            goalViews.getLast().setPosition(cols[i], lastRow()-GoalView.HEIGHT);
            goalViews.getLast().drawAll();
            i++;
        }
    }

    private void loadColors(ArrayList<Color> colors) {
        this.colors = (ArrayList<TextColor>) colors.stream().map(color -> adaptColor(color)).collect(Collectors.toList());
    }

    private void loadPlayers(ArrayList<String> players) {
        this.players = players;
    }

    private void loadScores(ArrayList<Integer> scores) {
        this.scores = scores;
    }

    private void loadCurrentPlayer(int index) {
        indexCurrentPlayer = index;
    }

    private void drawScoreBoard() {
        // players
        for (int i = 0; i < players.size(); i++) {
            fillRow(firstRow()+2+i, 2, players.get(i), colors.get(i));
        }
        // scores
        for (int i = 0; i < scores.size(); i++) {
            int score = scores.get(i) < 30 ? scores.get(i) : scores.get(i)-30;
            String lineHead = scores.get(i) < 30 ? ">" : "> ";
            String scoreLine = score == 0 ? "" : "=".repeat(score-1) + lineHead;
            if (scores.get(i) >= 30) {
                fillRow(firstRow()+2+i, firstColumn()+22, FULL_SCORE_LINE, colors.get(i));
            }
            fillRow(firstRow()+2+i, firstColumn()+22, scoreLine, colors.get(i));
            fillRow(firstRow()+2+i, firstColumn()+19, String.format("%2d", scores.get(i)));
        }
        // arrow current player
        int indexPrevPlayer = indexCurrentPlayer == 0 ? players.size()-1 : indexCurrentPlayer - 1;
        drawChar(firstColumn(), firstRow()+2+indexPrevPlayer, ' ', TextColor.ANSI.DEFAULT);
        drawChar(firstColumn(), firstRow()+2+indexCurrentPlayer, '>', TextColor.ANSI.DEFAULT);

    }

    private void drawStructure() {
        if (indexWinner >= 0) {
            fillRow(firstRow(), 1, "SCOREBOARD | the winner of the game is ");
            fillRow(firstRow(), 40, players.get(indexWinner), colors.get(indexWinner));
        } else {
            fillRow(firstRow(), 1, "SCOREBOARD");
        }
        fillRow(firstRow()+1, DASHED_HORIZONTAL_SEPARATOR);
        fillRow(firstRow()+6, DASHED_HORIZONTAL_SEPARATOR);
        fillRow(firstRow()+7, 1, "PRIVATE GOAL");
        fillRow(firstRow()+7, 19, "| PUBLIC GOALS");
        fillRow(firstRow()+8, DASHED_HORIZONTAL_SEPARATOR);
        fillColumn(firstColumn()+19, 2, "||||-|-|||||");
        fillColumn(firstColumn()+23, 2, "[[[[");
        fillColumn(lastColumn()-12, 2, "||||");
        fillColumn(lastColumn()-1, 2, "]]]]");
    }

    private void drawGoals() {
        for (GoalView goal : goalViews) drawBox(goal);
    }

    private TextColor adaptColor(Color color) {
        return switch (color) {
            case RED -> TextColor.ANSI.RED_BRIGHT;
            case BLUE -> TextColor.ANSI.BLUE_BRIGHT;
            case GREEN -> TextColor.ANSI.GREEN_BRIGHT;
            case YELLOW -> TextColor.ANSI.YELLOW_BRIGHT;
        };
    }

    public void setWinnerIndex(int indexWinner) {
        this.indexWinner = indexWinner;
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
