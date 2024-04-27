package IS24_LB11.cli.view.game;


import IS24_LB11.cli.utils.SymbolAdapter;
import IS24_LB11.cli.utils.CliBox;
import IS24_LB11.game.components.GoalCard;
import IS24_LB11.game.symbol.Symbol;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;

import static IS24_LB11.cli.utils.Side.NORD;
import static IS24_LB11.cli.utils.Side.SUD;

public class GoalView extends CliBox {
    public static final int WIDTH = 17;
    public static final int HEIGHT = 5;
    protected final GoalCard goal;

    public GoalView(GoalCard goal) {
        super(new TerminalSize(WIDTH, HEIGHT));
        this.goal = goal;
        setMargins(0);
        drawAll();
    }

    @Override
    public void drawAll() {
        drawBorders();
        drawSeparator();
        drawPoints();
        drawSymbols();
    }

    protected void drawPoints() {
        char points = String.valueOf(goal.getPoints()).charAt(0);
        TerminalPosition start = new TerminalPosition(firstColumn()+1, HEIGHT/2);
        drawChar(start, '[');
        drawChar(start.withRelative(1, 0), points, TextColor.ANSI.YELLOW_BRIGHT);
        drawChar(start.withRelative(2, 0), ']');
    }

    protected void drawSymbols() {
        int column = 0;
        int extraSpace = 3-goal.getSymbols().size();
        TerminalPosition start = new TerminalPosition(firstColumn() + 8, HEIGHT/2);
        for (Symbol symbol: goal.getSymbols()) {
            drawChar(start.withRelative(column, 0), SymbolAdapter.fromSymbol(symbol));
            column += 2 * (1 + extraSpace);
        }
    }

    protected void drawSeparator() {
        int column = firstColumn()+5;
        fillColumn(column, '¦');
        drawChar(new TerminalPosition(column, firstRow()-1), borderStyle.getSeparator(NORD));
        drawChar(new TerminalPosition(column, lastRow()+1), borderStyle.getSeparator(SUD));
    }
}
