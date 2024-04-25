package IS24_LB11.cli.view.game;


import IS24_LB11.cli.utils.Cell;
import IS24_LB11.cli.utils.CellFactory;
import IS24_LB11.cli.utils.CliBox;
import IS24_LB11.game.components.GoalCard;
import IS24_LB11.game.symbol.Symbol;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;

import static IS24_LB11.cli.utils.Side.NORD;
import static IS24_LB11.cli.utils.Side.SUD;

public class GoalView extends CliBox {
    protected static final int WIDTH = 17;
    protected static final int HEIGHT = 5;
    protected final GoalCard goal;

    public GoalView(GoalCard goal) {
        super(new TerminalSize(WIDTH, HEIGHT));
        this.goal = goal;
        setMargins(0);
        build();
    }

    @Override
    public void build() {
        drawBorders();
        drawSeparator();
        drawPoints();
        drawSymbols();
    }

    protected void drawPoints() {
        String points = String.valueOf(goal.getPoints());
        TerminalPosition start = new TerminalPosition(firstColumn()+1, HEIGHT/2);
        drawCell(start, new Cell('['));
        drawCell(start.withRelative(1, 0), new Cell(points, TextColor.ANSI.YELLOW_BRIGHT));
        drawCell(start.withRelative(2, 0), new Cell(']'));
    }

    protected void drawSymbols() {
        int column = 0;
        int extraSpace = 3-goal.getSymbols().size();
        TerminalPosition start = new TerminalPosition(firstColumn() + 8, HEIGHT/2);
        for (Symbol symbol: goal.getSymbols()) {
            drawCell(start.withRelative(column, 0), CellFactory.fromSymbol(symbol));
            column += 2 * (1 + extraSpace);
        }
    }

    protected void drawSeparator() {
        int column = firstColumn()+5;
        fillColumn(column, '¦');
        drawCell(new TerminalPosition(column, firstRow()-1), borderStyle.getSeparator(NORD));
        drawCell(new TerminalPosition(column, lastRow()+1), borderStyle.getSeparator(SUD));
    }
}
