package org.example.cli.utils;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.terminal.Terminal;
import org.example.cli.utils.Cell;
import org.example.cli.utils.CliFrame;
import org.example.cli.utils.Side;
import org.example.cli.utils.TerminalRectangle;
import org.example.cli.style.BorderStyle;
import org.example.cli.style.SingleBorderStyle;

import java.io.IOException;

public abstract class CliBox implements CliFrame {
    protected TerminalRectangle rectangle;
    protected TerminalRectangle innerArea;
    protected TerminalRectangle borderArea;
    protected BorderStyle borderStyle;
    protected Cell[][] image;

    public CliBox(TerminalSize size, TerminalPosition position, BorderStyle bs) {
        image = new Cell[size.getRows()][size.getColumns()];
        rectangle = new TerminalRectangle(size, position);
        borderArea = new TerminalRectangle(
                rectangle.getSize().withRelative(-3, -1),
                new TerminalPosition(1, 0));
        innerArea = new TerminalRectangle(
                borderArea.getSize().withRelative(-2, -2),
                borderArea.getPosition().withRelative(1, 1));
        borderStyle = bs;

        for (int r=0; r<getHeight(); r++) {
            for (int c=0; c<getWidth(); c++) image[r][c] = new Cell(' ');
        }
    }

    public CliBox(TerminalSize tSize, BorderStyle bs) {
        this(tSize, new TerminalPosition(0,0), bs);
    }

    public CliBox(TerminalSize tSize) {
        this(tSize, new SingleBorderStyle());
    }

    public void clear() {
        for (int r=0; r<getHeight(); r++) {
            for (int c=0; c<getWidth(); c++) image[r][c].set(' ');
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
        borderArea.reSize(deltaWidth, deltaHeight);
        updateInnerArea();
        image = new Cell[rectangle.getHeight()][rectangle.getWidth()];
        for (int r=0; r<getHeight(); r++) {
            for (int c=0; c<getWidth(); c++) image[r][c] = new Cell(' ');
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

    protected void drawCell(TerminalPosition pos, Cell cell) {
        image[pos.getRow()][pos.getColumn()] = cell;
    }

    protected void fillRow(int row, char c) {
        for (int i=firstColumn(); i<=lastColumn(); i++) {
            drawCell(new TerminalPosition(i, row), c);
        }
    }

    protected void fillRow(int row, int offset, char c) {
        for (int i=firstColumn()+offset; i<=lastColumn(); i++) {
            drawCell(new TerminalPosition(i, row), c);
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

    protected TerminalPosition getCornerPosition(int dir) {
        int r = dir>>1, c = 2+(dir&1);
        return new TerminalPosition(borderArea.side(c), borderArea.side(r));
    }

    protected void updateInnerArea() {
        innerArea.setPosition(borderArea.getPosition().withRelative(1,1));
        innerArea.setSize(borderArea.getSize().withRelative(-2,-2));
    }

    protected int innerWidth() { return innerArea.getWidth(); }
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

    public void setPosition(TerminalPosition newPosition) {
        rectangle.setPosition(newPosition);
    }

    @Override
    public TerminalPosition getPosition() {
        return new TerminalPosition(rectangle.getX(), rectangle.getY());
    }
    public int getHeight() { return rectangle.getHeight(); }
    public int getWidth() { return rectangle.getWidth(); }
}
