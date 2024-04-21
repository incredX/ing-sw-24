package IS24_LB11.cli.utils;

import IS24_LB11.cli.style.BorderStyle;
import IS24_LB11.cli.style.SingleBorderStyle;
import IS24_LB11.game.utils.Direction;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;

public abstract class CliBox implements CliFrame {
    protected TerminalRectangle rectangle;
    protected TerminalRectangle innerArea;
    protected TerminalRectangle borderArea;
    protected BorderStyle borderStyle;
    protected Cell[][] image;

    public CliBox(TerminalSize size, TerminalPosition position, BorderStyle bs) {
        rectangle = new TerminalRectangle(size, position);
        borderArea = rectangle.withRelativeSize(-3, -1)
                .withPosition(new TerminalPosition(1, 0));
        innerArea = borderArea.withRelativeSize(-2, -2)
                .withRelativePosition(1, 1);
        borderStyle = bs;
        resetImage();
    }

    public CliBox(TerminalSize tSize, BorderStyle bs) {
        this(tSize, new TerminalPosition(0,0), bs);
    }

    public CliBox(TerminalSize tSize) {
        this(tSize, new SingleBorderStyle());
    }

    public void clear() {
        for (int r=0; r<getHeight(); r++) {
            for (int c=0; c<getWidth(); c++) image[r][c] = new Cell(' ', TextColor.ANSI.DEFAULT);
        }
    }

    public void rebuild() {
        clear();
        build();
    }

    public void print(Terminal terminal) throws IOException {
        TerminalPosition originalPosition = terminal.getCursorPosition();
        for (int r=0; r<getHeight(); r++) {
            terminal.setCursorPosition(rectangle.getX(), rectangle.getY()+r);
            for(int c=0; c<getWidth(); c++) {
                image[r][c].print(terminal);
            }
        }
        terminal.setCursorPosition(originalPosition);
    }

    public void resize(TerminalSize newSize) {
        int deltaWidth = newSize.getColumns() - rectangle.getWidth();
        int deltaHeight = newSize.getRows() - rectangle.getHeight();
        rectangle.setSize(newSize);
        borderArea.resize(deltaWidth, deltaHeight);
        updateInnerArea();
        resetImage();
    }

    protected void resetImage() {
        image = new Cell[rectangle.getHeight()][rectangle.getWidth()];
        for (int r=0; r<getHeight(); r++) {
            for (int c=0; c<getWidth(); c++) image[r][c] = new Cell(' ', TextColor.ANSI.DEFAULT);
        }
    }

    protected void draw(CliBox box) {
        for (int r=0; r<box.rectangle.getHeight(); r++) {
            for (int c=0; c<box.rectangle.getWidth(); c++) {
                int x = firstColumn() + box.rectangle.getX() + c;
                int y = firstRow() + box.rectangle.getY() + r;
                if (x > lastColumn() || y > lastRow() || x <= 0 || y <= 0) continue;
                image[y][x] = new Cell(box.image[r][c]);
            }
        }
    }

    protected void drawBorders() {
        fillRow(borderArea.side(Side.NORD), borderStyle.getHLine());
        fillRow(borderArea.side(Side.SUD), borderStyle.getHLine());
        fillColumn(borderArea.side(Side.WEST), borderStyle.getVLine());
        fillColumn(borderArea.side(Side.EAST), borderStyle.getVLine());

        for (int i=0; i<4; i++) {
            TerminalPosition corner = getCornerPosition(i);
            drawCell(corner, borderStyle.getCorner(i));
        }
    }

    protected void drawCell(TerminalPosition pos, char c) {
        image[pos.getRow()][pos.getColumn()].set(c);
    }

    protected void drawCell(TerminalPosition pos, char c, TextColor color) {
        image[pos.getRow()][pos.getColumn()] = new Cell(c, color);
    }

    protected void drawCell(TerminalPosition pos, Cell cell) {
        image[pos.getRow()][pos.getColumn()] = cell;
    }

    protected void fillRow(int row, char c) {
        for (int i=firstColumn(); i<=lastColumn(); i++) {
            drawCell(new TerminalPosition(i, row), c);
        }
    }

    protected void fillRow(int row, char c, TextColor color) {
        for (int i=firstColumn(); i<=lastColumn(); i++) {
            drawCell(new TerminalPosition(i, row), new Cell(c, color));
        }
    }

    protected void fillRow(int row, int offset, char c) {
        for (int i=firstColumn()+offset; i<=lastColumn(); i++) {
            drawCell(new TerminalPosition(i, row), c);
        }
    }

    protected void fillRow(int row, int offset, char c, TextColor color) {
        for (int i=firstColumn()+offset; i<=lastColumn(); i++) {
            drawCell(new TerminalPosition(i, row), new Cell(c, color));
        }
    }

    protected void fillRow(int row, String line) {
        for (int i=firstColumn(),j=0; i<=lastColumn(); i++) {
            if (j >= line.length()) break;
            drawCell(new TerminalPosition(i, row), line.charAt(j));
            j++;
        }
    }

    protected void fillRow(int row, int offset, String line) {
        for (int i=firstColumn()+offset,j=0; i<=lastColumn(); i++) {
            if (j >= line.length()) break;
            drawCell(new TerminalPosition(i, row), line.charAt(j));
            j++;
        }
    }

    protected void fillColumn(int col, char c) {
        for (int i=firstRow(); i<=lastRow(); i++) {
            drawCell(new TerminalPosition(col, i), c);
        }
    }

    protected void fillColumn(int col, String line) {
        for (int i=firstRow(),j=0; i<=lastRow(); i++) {
            if (j >= line.length()) break;
            drawCell(new TerminalPosition(col, i), line.charAt(j));
            j++;
        }
    }

    protected TerminalPosition getCornerPosition(int dir) {
        int r = dir>>1, c = 2+(dir&1);
        return new TerminalPosition(borderArea.side(c), borderArea.side(r));
    }

    protected TerminalPosition getCornerPosition(Direction dir) {
        return getCornerPosition(dir.ordinal());
    }

    protected void updateInnerArea() {
        innerArea.setPosition(borderArea.getPosition().withRelative(1,1));
        innerArea.setSize(borderArea.getSize().withRelative(-2,-2));
    }

    protected int innerWidth() { return innerArea.getWidth(); }
    protected int innerHeight() { return innerArea.getHeight(); }
    protected int firstRow() { return innerArea.getY(); }
    protected int lastRow() { return innerArea.getYAndHeight(); }
    protected int firstColumn() { return innerArea.getX(); }
    protected int lastColumn() { return innerArea.getXAndWidth(); }

    public void setBorderStyle(BorderStyle bs) {
        borderStyle = bs;
    }

    public void setMargins(int margin) {
        borderArea.setPosition(margin, margin);
        borderArea.setSize(rectangle.getSize().withRelative(-2*margin-1, -2*margin-1));
        updateInnerArea();
    }

    //TODO: upgrade method (borderArea is not setted correctly)
    public void setSize(TerminalSize size) {
        rectangle.setSize(size);
        borderArea.setSize(rectangle.getSize().withRelative(-3,-1));
        updateInnerArea();
        resetImage();
    }

    public void setTerminalPosition(TerminalPosition newPosition) {
        rectangle.setPosition(newPosition);
    }

    @Override
    public TerminalPosition getTerminalPosition() {
        return new TerminalPosition(rectangle.getX(), rectangle.getY());
    }
    public TerminalSize getSize() { return rectangle.getSize(); }
    public int getHeight() { return rectangle.getHeight(); }
    public int getWidth() { return rectangle.getWidth(); }
}
