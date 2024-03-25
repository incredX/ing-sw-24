package org.example.cli;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.terminal.Terminal;
import org.example.cli.style.SingleBorderStyle;
import org.example.cli.utils.CliBox;
import org.example.cli.utils.TerminalRectangle;

import java.io.IOException;
import java.util.ArrayDeque;

import static org.example.cli.utils.Side.WEST;
import static org.example.cli.utils.Side.EAST;


public class Stage extends CliBox {
    private static final String COMMAND_INTRO = " > ";
    private final ArrayDeque<TerminalRectangle> builtAreas;
    private TerminalPosition cursorPosition;

    public Stage(TerminalSize terminalSize) {
        super(terminalSize.withRelative(0, -2),
                new TerminalPosition(0, 2),
                new SingleBorderStyle());
        setMargins(0);
        updateInnerArea();
        setCursorPosition(0);
        builtAreas = new ArrayDeque<>();
    }

    @Override
    public void build() {
        drawBorders();
        drawBottomDiv();
        fillRow(lastRow(), COMMAND_INTRO);
        buildRelativeArea(new TerminalRectangle(
                new TerminalSize(getWidth(), getHeight()),
                new TerminalPosition(0, 0)
        ));
    }

    public void buildCommandLine(CommandLine commandLine) {
        setCursorPosition(commandLine.getCursor());
        fillRow(lastRow(), COMMAND_INTRO.length(), ' ');
        fillRow(lastRow(), COMMAND_INTRO.length(), commandLine.getVisibleLine());
        buildRelativeArea(new TerminalRectangle(
                new TerminalSize(innerWidth(), 1),
                new TerminalPosition(0, lastRow())
        ));
    }

    public void buildArea(TerminalRectangle area) {
        builtAreas.add(area);
    }

    public void buildRelativeArea(TerminalRectangle area) {
        builtAreas.add(area.withRelative(getPosition()));
    }

    @Override
    public void print(Terminal terminal) throws IOException {
        //super.print(terminal);
        while (!builtAreas.isEmpty()) {
            TerminalRectangle area = builtAreas.removeLast();
            TerminalPosition base = area.getPosition();
            TerminalPosition relative;
            for (int r=0; r<area.getHeight(); r++) {
                relative = base.withRelativeRow(r).minus(this.getPosition());
                terminal.setCursorPosition(base.withRelativeRow(r));
                for (int c=0; c<area.getWidth(); c++) {
                    image[relative.getRow()][relative.getColumn()+c].print(terminal);
                }
            }
        }
        terminal.setCursorPosition(cursorPosition);
    }

    private void drawBottomDiv() {
        fillRow(lastRow()-1, borderStyle.getHLine());
        drawCell(new TerminalPosition(borderArea.side(WEST), lastRow()-1), borderStyle.getSeparator(WEST));
        drawCell(new TerminalPosition(borderArea.side(EAST), lastRow()-1), borderStyle.getSeparator(EAST));
    }

    @Override
    public void resize(TerminalSize terminalSize) {
        clear();
        super.resize(terminalSize.withRelative(0, -2));
        updateInnerArea();
        build();
    }

    public void setCursorPosition(int cursor) {
        int column = firstColumn()+ COMMAND_INTRO.length() + cursor;
        cursorPosition = rectangle.getPosition().withRelative(column, lastRow());
    }

    public TerminalPosition getCenter() {
        return getPosition().withRelative(getWidth()/2, getHeight()/2);
    }
}
