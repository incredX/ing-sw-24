package IS24_LB11.cli.view.game;

import IS24_LB11.cli.utils.SymbolAdapter;
import IS24_LB11.game.components.GoalPattern;
import IS24_LB11.game.symbol.Symbol;
import com.googlecode.lanterna.TerminalPosition;

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
            int col = ((goal.getDir()&1) == 1) ? lastColumn()-4 : start;
            int dx = ((goal.getDir()&1) == 1) ? -2 : 2;

            for (Symbol symbol: goal.getSymbols()) {
                fillRow(row, col, "[ ]");
                drawChar(new TerminalPosition(col+2, row), SymbolAdapter.fromSymbol(symbol));
                row++;
                col += dx;
            }
        } else {
            int c0 = firstColumn() + 6;
            int col = ((goal.getDir()&1) == 0) ? c0 : lastColumn()-4;
            int row = (goal.getDir() <= 1) ? firstRow() : lastRow();
            int dy = (goal.getDir() <= 1) ? 1 : -1;
            int i = 0;
            for (Symbol symbol: goal.getSymbols()) {
                int curCol = (i == 0) ? col : c0+2;
                fillRow(row, curCol, "[ ]");
                drawChar(new TerminalPosition(curCol+2, row), SymbolAdapter.fromSymbol(symbol));
                row += dy;
                i++;
            }
        }
    }
}
