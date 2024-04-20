package IS24_LB11.cli;

import IS24_LB11.cli.style.SingleBorderStyle;
import IS24_LB11.cli.utils.Side;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.terminal.Terminal;
import IS24_LB11.cli.utils.CliBox;
import IS24_LB11.cli.utils.TerminalRectangle;

import java.io.IOException;
import java.util.ArrayDeque;

import static IS24_LB11.cli.utils.Side.WEST;
import static IS24_LB11.cli.utils.Side.EAST;


public class Stage extends CliBox {
    protected static final String COMMAND_INTRO = " > ";

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
    public void clear() {
        super.clear();
        buildRelativeArea(borderArea.getSize(), firstColumn(), firstRow());
    }

    @Override
    public void build() {
        drawBorders();
        drawCommandLine();
    }

    public void buildCommandLine(CommandLine commandLine) {
        setCursorPosition(commandLine.getCursor());
        fillRow(lastRow(), COMMAND_INTRO.length(), ' ');
        fillRow(lastRow(), COMMAND_INTRO.length(), commandLine.getVisibleLine());
        buildRelativeArea(new TerminalSize(innerWidth(), 1), new TerminalPosition(0, lastRow()));
    }

    public void buildArea(TerminalRectangle area) {
        builtAreas.add(area);
    }

    public void buildRelativeArea(TerminalRectangle area) {
        builtAreas.add(area.withRelative(getTerminalPosition()));
    }

    public void buildRelativeArea(TerminalSize size, TerminalPosition position) {
        builtAreas.add(new TerminalRectangle(size, position.withRelative(getTerminalPosition())));
    }

    public void buildRelativeArea(TerminalSize size, int col, int row) {
        builtAreas.add(new TerminalRectangle(size, new TerminalPosition(col, row).withRelative(getTerminalPosition())));
    }

    public void buildRelativeArea(int width, int height, int col, int row) {
        builtAreas.add(new TerminalRectangle(new TerminalSize(width, height), new TerminalPosition(col, row).withRelative(getTerminalPosition())));
    }

    @Override
    public void print(Terminal terminal) throws IOException {
        while (!builtAreas.isEmpty()) {
            TerminalRectangle area = builtAreas.removeLast();
            TerminalPosition base = area.getPosition();
            TerminalPosition relative;
            for (int r=0; r<area.getHeight(); r++) {
                relative = base.withRelativeRow(r).minus(this.getTerminalPosition());
                terminal.setCursorPosition(base.withRelativeRow(r));
                for (int c=0; c<area.getWidth(); c++) {
                    if (relative.getRow() < rectangle.getHeight() && relative.getColumn()+c < rectangle.getWidth())
                        image[relative.getRow()][relative.getColumn()+c].print(terminal);
                }
            }
        }
        terminal.setCursorPosition(cursorPosition);
    }

    protected void drawCommandLine() {
        drawBottomDiv();
        fillRow(lastRow(), COMMAND_INTRO);
        buildRelativeArea(borderArea.getWidth(), 2, 0, lastRow()-1);
    }

    private void drawBottomDiv() {
        fillRow(lastRow()-1, borderStyle.getHLine(), TextColor.ANSI.DEFAULT);
        drawCell(new TerminalPosition(borderArea.side(WEST), lastRow()-1), borderStyle.getSeparator(WEST));
        drawCell(new TerminalPosition(borderArea.side(EAST), lastRow()-1), borderStyle.getSeparator(EAST));
    }

    @Override
    protected void drawBorders() {
        super.drawBorders();
        buildRelativeArea(rectangle.getWidth(), 1, 0, 0);
        buildRelativeArea(rectangle.getWidth(), 1, 0, borderArea.getYAndHeight());
        buildRelativeArea(1, rectangle.getHeight(), 0, 0);
        buildRelativeArea(1, rectangle.getHeight(), borderArea.getXAndWidth(), 0);
    }

    @Override
    public void resize(TerminalSize terminalSize) {
        super.resize(terminalSize.withRelative(0, -2));
        System.out.println("rectangle  : "+rectangle.getPosition()+"  "+rectangle.getHeight()+";"+rectangle.getWidth());
        System.out.println("border area: "+borderArea.getPosition()+"  "+borderArea.getHeight()+";"+borderArea.getWidth());
        System.out.println("inner area : "+innerArea.getPosition()+"  "+innerArea.getHeight()+";"+innerArea.getWidth());
    }

    public void shift(Side side) {
        return;
    }

    public void setCursorPosition(int cursor) {
        int column = firstColumn()+ COMMAND_INTRO.length() + cursor;
        cursorPosition = rectangle.getPosition().withRelative(column, lastRow());
    }

    public TerminalPosition getCenter() {
        return getTerminalPosition().withRelative(getWidth()/2, getHeight()/2);
    }
}
