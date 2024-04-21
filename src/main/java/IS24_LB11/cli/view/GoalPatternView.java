package IS24_LB11.cli.view;

import IS24_LB11.cli.utils.CellFactory;
import IS24_LB11.game.components.GoalPattern;
import IS24_LB11.game.symbol.Symbol;
import com.googlecode.lanterna.TerminalPosition;

import static IS24_LB11.cli.utils.Side.NORD;

public class GoalPatternView extends GoalView {
    public GoalPatternView(GoalPattern goal) {
        super(goal);
    }

    @Override
    protected void drawSymbols() {
        GoalPattern goal = (GoalPattern) this.goal;

        if (goal.getVariant() == 'D') {
            int start = firstColumn() + 6;
            int row = firstRow();
            int col = (goal.getDir() == NORD.ordinal()) ? lastColumn()-4 : start;
            int dx = (goal.getDir() == NORD.ordinal()) ? -2 : 2;

            for (Symbol symbol: goal.getSymbols()) {
                fillRow(row, col, "[ ]");
                drawCell(new TerminalPosition(col+2, row), CellFactory.fromSymbol(symbol));
                row++;
                col += dx;
            }
        } else {
            int c0 = firstColumn() + 6;
            int col = (goal.getDir()%3 == 0) ? lastColumn()-4 : c0;
            int row = (goal.getDir() <= 1) ? firstRow() : lastRow();
            int dy = (goal.getDir() <= 1) ? 1 : -1;
            int i = 0;
            for (Symbol symbol: goal.getSymbols()) {
                int curCol = (i == 0) ? col : c0+2;
                fillRow(row, curCol, "[ ]");
                drawCell(new TerminalPosition(curCol+2, row), CellFactory.fromSymbol(symbol));
                row += dy;
                i++;
            }
        }
    }
}
