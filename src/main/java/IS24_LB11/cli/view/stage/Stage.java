package IS24_LB11.cli.view.stage;

import IS24_LB11.cli.ViewHub;
import IS24_LB11.cli.style.SingleBorderStyle;
import IS24_LB11.cli.utils.LayerInterface;
import IS24_LB11.cli.utils.Side;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.screen.Screen;
import IS24_LB11.cli.utils.TerminalBox;
import IS24_LB11.cli.utils.TerminalRectangle;

import java.util.ArrayDeque;


public class Stage extends TerminalBox implements LayerInterface {
    private final ViewHub viewHub;
    private final ArrayDeque<TerminalRectangle> builtAreas;

    public Stage(ViewHub viewHub) {
        super(viewHub.getScreenSize().withRelative(0, -2),
                new TerminalPosition(0, 0),
                new SingleBorderStyle());
        this.viewHub = viewHub;
        this.builtAreas = new ArrayDeque<>();
        setMargins(0);
        updateInnerArea();
    }

    @Override
    public int zIndex() { return 0; }

    @Override
    public void clear() {
        super.clear();
        buildRelativeArea(borderArea.getSize(), firstColumn(), firstRow());
    }

    @Override
    public void drawAll() {
        drawBorders();
    }

    public void buildStage() { buildArea(rectangle); }

    public void buildArea(TerminalRectangle area) {
        synchronized (builtAreas) {
            builtAreas.add(area);
        }
    }

    public void buildRelativeArea(TerminalRectangle area) {
        synchronized (builtAreas) {
            builtAreas.add(area.withRelative(getPosition()));
        }
    }

    public void buildRelativeArea(TerminalSize size, TerminalPosition position) {
        synchronized (builtAreas) {
            builtAreas.add(new TerminalRectangle(size, position.withRelative(getPosition())));
        }
    }

    public void buildRelativeArea(TerminalSize size, int col, int row) {
        synchronized (builtAreas) {
            builtAreas.add(new TerminalRectangle(size, new TerminalPosition(col, row).withRelative(getPosition())));
        }
    }

    public void buildRelativeArea(int width, int height, int col, int row) {
        synchronized (builtAreas) {
            builtAreas.add(new TerminalRectangle(new TerminalSize(width, height), new TerminalPosition(col, row).withRelative(getPosition())));
        }
    }

    @Override
    public void print(Screen screen) {
        TextGraphics graphics = screen.newTextGraphics();
        synchronized (builtAreas) {
            while (!builtAreas.isEmpty()) {
                TerminalRectangle area = builtAreas.pollLast();
                if (area == null) continue;
                graphics.drawImage(area.getPosition(), image, area.getPosition(), area.getSize());
            }
        }
    }

    @Override
    public void drawBox(TerminalBox terminalBox) {
        super.drawBox(terminalBox);
        buildRelativeArea(terminalBox.getRectangle());
    }

    @Override
    protected void drawBorders() {
        super.drawBorders();
        buildRelativeArea(rectangle.getWidth(), 1, 0, 0);
        buildRelativeArea(rectangle.getWidth(), 1, 0, borderArea.getYAndHeight());
        buildRelativeArea(1, rectangle.getHeight(), 0, 0);
        buildRelativeArea(1, rectangle.getHeight(), borderArea.getXAndWidth(), 0);
    }

    public void resize() {
        super.resize(viewHub.getScreenSize().withRelative(0, -2));
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

    public TerminalSize getScreenSize() {
        return viewHub.getScreenSize();
    }
}
