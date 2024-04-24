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
    private final ViewHub viewHub;
    private final ArrayDeque<TerminalRectangle> builtAreas;

    public Stage(ViewHub viewHub, TerminalSize terminalSize) {
        super(terminalSize.withRelative(0, -4),
                new TerminalPosition(0, 2),
                new SingleBorderStyle());
        this.viewHub = viewHub;
        this.builtAreas = new ArrayDeque<>();
        setMargins(0);
        updateInnerArea();
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
            for (int r=0; r<=area.getHeight(); r++) {
                relative = base.withRelativeRow(r).minus(this.getPosition());
                terminal.setCursorPosition(base.withRelativeRow(r));
                int relCol = relative.getColumn(), relRow = relative.getRow();
                for (int c=0; c<=area.getWidth(); c++) {
                    if (relRow < rectangle.getHeight() && relCol < rectangle.getWidth() && relRow >= 0 && relCol >= 0)
                        image[relRow][relCol].print(terminal);
                    relCol++;
                }
            }
        }
        terminal.setCursorPosition(originalPosition);
    }

    @Override
    public void draw(CliBox cliBox) {
        super.draw(cliBox);
        buildRelativeArea(cliBox.getRectangle());
    }

    @Override
    public void setCover(CliBox box, boolean covered) {
        super.setCover(box, covered);
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
        super.resize(terminalSize.withRelative(0, -4));
    }

    protected void updateViewHub() {
        viewHub.update();
    }

    public void shift(Side side) {
        return;
    }

    public TerminalPosition getInnerBase() {
        return new TerminalPosition(firstColumn(), firstRow());
    }

    public TerminalPosition getCenter() {
        return getPosition().withRelative(getWidth()/2, getHeight()/2);
    }
}
