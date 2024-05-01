package IS24_LB11.cli.view;

import IS24_LB11.cli.CommandLine;
import IS24_LB11.cli.style.SingleBorderStyle;
import IS24_LB11.cli.utils.LayerInterface;
import IS24_LB11.cli.utils.TerminalBox;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.screen.Screen;

import static IS24_LB11.cli.utils.Side.EAST;
import static IS24_LB11.cli.utils.Side.WEST;
import static IS24_LB11.game.utils.Direction.UP_LEFT;
import static IS24_LB11.game.utils.Direction.UP_RIGHT;

public class CommandLineView extends TerminalBox implements LayerInterface {
    public static final String COMMAND_INTRO = " > ";
    public static final String DISABLED_CMD_LINE_MESSAGE = " == inactive == ";

    private TerminalPosition cursorPosition;
    private TextColor color;
    private String commandString;

    public CommandLineView(TerminalSize terminalSize) {
        super(terminalSize.withRows(3),
                new TerminalPosition(0, terminalSize.getRows()-3),
                new SingleBorderStyle());
        setMargins(0);
        setCursorPosition(COMMAND_INTRO.length());
    }

    @Override
    public int zIndex() { return 10; }

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

    public void loadCommandLine(CommandLine commandLine) {
        if (commandLine.isEnabled()) {
            setCursorPosition(COMMAND_INTRO.length()+commandLine.getRelativeCursor());
            commandString = commandLine.getVisibleLine();
            setEnableColor();
        } else {
            setCursorPosition(1);
            commandString = DISABLED_CMD_LINE_MESSAGE;
            setDisableColor();
        }
    }

    public void drawCommandLine() {
        fillRow(lastRow(), ' ');
        fillRow(lastRow(), COMMAND_INTRO);
        fillRow(lastRow(), COMMAND_INTRO.length(), commandString, color);
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
        cursorPosition = rectangle.getPosition().withRelative(firstColumn() + cursor, lastRow());
    }

    public void setEnableColor() {
        color = TextColor.ANSI.DEFAULT;
    }

    public void setDisableColor() {
        color = TextColor.ANSI.BLACK_BRIGHT;
    }
}
