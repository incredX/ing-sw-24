package IS24_LB11.cli;

import IS24_LB11.cli.style.SingleBorderStyle;
import IS24_LB11.cli.utils.Side;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.terminal.Terminal;
import IS24_LB11.cli.utils.CliBox;
import IS24_LB11.cli.utils.TerminalRectangle;

import java.io.IOException;
import java.util.ArrayDeque;


public class Stage extends CliBox {
    private final ArrayDeque<TerminalRectangle> builtAreas;

    public Stage(TerminalSize terminalSize) {
        super(terminalSize.withRelative(0, -4),
                new TerminalPosition(0, 2),
                new SingleBorderStyle());
        setMargins(0);
        updateInnerArea();
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
    }

    public void buildArea(TerminalRectangle area) {
        builtAreas.add(area);
    }

    public void buildRelativeArea(TerminalRectangle area) {
        builtAreas.add(area.withRelative(getPosition()));
    }

    public void buildRelativeArea(TerminalSize size, TerminalPosition position) {
        builtAreas.add(new TerminalRectangle(size, position.withRelative(getPosition())));
    }

    public void buildRelativeArea(TerminalSize size, int col, int row) {
        builtAreas.add(new TerminalRectangle(size, new TerminalPosition(col, row).withRelative(getPosition())));
    }

    public void buildRelativeArea(int width, int height, int col, int row) {
        builtAreas.add(new TerminalRectangle(new TerminalSize(width, height), new TerminalPosition(col, row).withRelative(getPosition())));
    }

    @Override
    public void print(Terminal terminal) throws IOException {
        TerminalPosition originalPosition = terminal.getCursorPosition();
        while (!builtAreas.isEmpty()) {
            TerminalRectangle area = builtAreas.removeLast();
            TerminalPosition base = area.getPosition();
            TerminalPosition relative;
            for (int r=0; r<area.getHeight(); r++) {
                relative = base.withRelativeRow(r).minus(this.getPosition());
                terminal.setCursorPosition(base.withRelativeRow(r));
                for (int c=0; c<area.getWidth(); c++) {
                    if (relative.getRow() < rectangle.getHeight() && relative.getColumn()+c < rectangle.getWidth())
                        image[relative.getRow()][relative.getColumn()+c].print(terminal);
                }
            }
        }
        terminal.setCursorPosition(originalPosition);
    }

//    private void drawBottomDiv() {
//        fillRow(lastRow()-1, borderStyle.getHLine(), TextColor.ANSI.DEFAULT);
//        drawCell(new TerminalPosition(borderArea.side(WEST), lastRow()-1), borderStyle.getSeparator(WEST));
//        drawCell(new TerminalPosition(borderArea.side(EAST), lastRow()-1), borderStyle.getSeparator(EAST));
//    }

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
        super.resize(terminalSize.withRelative(0, -4));
    }

    public void shift(Side side) {
        return;
    }

    public TerminalPosition getCenter() {
        return getPosition().withRelative(getWidth()/2, getHeight()/2);
    }
}
