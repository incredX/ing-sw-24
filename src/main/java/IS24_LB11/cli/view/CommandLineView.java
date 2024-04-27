package IS24_LB11.cli.view;

import IS24_LB11.cli.CommandLine;
import IS24_LB11.cli.style.SingleBorderStyle;
import IS24_LB11.cli.utils.CliBox;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.screen.Screen;

import static IS24_LB11.cli.utils.Side.EAST;
import static IS24_LB11.cli.utils.Side.WEST;
import static IS24_LB11.game.utils.Direction.UP_LEFT;
import static IS24_LB11.game.utils.Direction.UP_RIGHT;

public class CommandLineView extends CliBox {
    protected static final String COMMAND_INTRO = " > ";

    private TerminalPosition cursorPosition;
    private String commandString;

    public CommandLineView(TerminalSize terminalSize) {
        super(terminalSize.withRows(3),
                new TerminalPosition(0, terminalSize.getRows()-3),
                new SingleBorderStyle());
        setMargins(0);
        setCursorPosition(0);
    }

    @Override
    public void drawAll() {
        drawBorders();
        drawCommandLine();
    }

    @Override
    public void print(Screen screen) {
        screen.setCursorPosition(cursorPosition);
        super.print(screen);
    }

    public void buildCommandLine(CommandLine commandLine) {
        setCursorPosition(commandLine.getCursor());
        commandString = commandLine.getVisibleLine();
    }

    public void drawCommandLine() {
        fillRow(lastRow(), ' ');
        fillRow(lastRow(), COMMAND_INTRO+commandString);
    }

    @Override
    protected void drawBorders() {
        super.drawBorders();
        drawChar(getCornerPosition(UP_LEFT), borderStyle.getSeparator(WEST), TextColor.ANSI.DEFAULT);
        drawChar(getCornerPosition(UP_RIGHT), borderStyle.getSeparator(EAST), TextColor.ANSI.DEFAULT);
    }

    @Override
    public void resize(TerminalSize newSize) {
        rectangle.setPosition(0, newSize.getRows()-3);
        super.resize(newSize.withRows(3));
    }

    public void setCursorPosition(int cursor) {
        int column = firstColumn()+ COMMAND_INTRO.length() + cursor;
        cursorPosition = rectangle.getPosition().withRelative(column, lastRow());
    }
}
