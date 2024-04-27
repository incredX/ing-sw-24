package IS24_LB11.cli.utils;

import IS24_LB11.cli.style.BorderStyle;
import IS24_LB11.cli.style.SingleBorderStyle;
import IS24_LB11.game.utils.Direction;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.graphics.TextImage;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.ScreenBuffer;

import java.util.EnumMap;

import static IS24_LB11.cli.utils.Side.*;

public class CliBox {
    protected TerminalRectangle rectangle;
    protected TerminalRectangle innerArea;
    protected TerminalRectangle borderArea;
    protected BorderStyle borderStyle;
    protected TextImage image;
    private EnumMap<Side, Integer> margins;

    public CliBox(TerminalSize size, TerminalPosition position, BorderStyle borderStyle) {
        this.rectangle = new TerminalRectangle(size, position);
        this.borderArea = new TerminalRectangle(size, position);
        this.innerArea = new TerminalRectangle(size, position);
        this.borderStyle = borderStyle;
        this.margins = new EnumMap<>(Side.class);
        this.image = new ScreenBuffer(size, textChar(' '));
        for(Side side: Side.values()) margins.put(side, side.isVertical() ? 0 : 1);
        updateBorderArea();
        updateInnerArea();
    }

    public CliBox(int width, int height, int x, int y, BorderStyle borderStyle) {
        this(new TerminalSize(width, height), new TerminalPosition(x, y), borderStyle);
    }

    public CliBox(TerminalSize terminalSize, BorderStyle borderStyle) {
        this(terminalSize, new TerminalPosition(0,0), borderStyle);
    }

    public CliBox(TerminalSize tSize) {
        this(tSize, new SingleBorderStyle());
    }

    public void clear() {
        image.setAll(textChar(' '));
    }

    public void drawAll() {
        drawBorders();
    }

    public void redraw() {
        clear();
        drawAll();
    }

    public void print(Screen screen) {
        TextGraphics graphics = screen.newTextGraphics();
        graphics.drawImage(rectangle.getPosition(), image);
    }

    public void resize(TerminalSize newSize) {
        rectangle.setSize(newSize);
        updateBorderArea();
        updateInnerArea();
        resetImage();
    }

    protected void resetImage() {
        image = new ScreenBuffer(getSize(), textChar(' '));
    }

    protected void drawBox(CliBox box) {
        int thisOffsetX = firstColumn() + Integer.max(0, box.rectangle.getX());
        int thisOffsetY = firstRow() + Integer.max(0, box.rectangle.getY());
        int boxOffsetX = Integer.max(0, -box.rectangle.getX());
        int boxOffsetY = Integer.max(0, -box.rectangle.getY());
        int columns = Integer.min(box.getWidth(), lastColumn()-box.rectangle.getX());
        int rows = Integer.min(box.getHeight(), lastRow()-box.rectangle.getY());
        if (columns <= 0 || rows <= 0 || boxOffsetX >= box.getWidth() || boxOffsetY >= box.getHeight()) return;
        box.image.copyTo(image, boxOffsetY, rows, boxOffsetX, columns, thisOffsetY, thisOffsetX);
    }

    protected void drawBorders() {
        fillRow(borderArea.side(NORD), borderStyle.getHLine());
        fillRow(borderArea.side(Side.SUD), borderStyle.getHLine());
        fillColumn(borderArea.side(WEST), borderStyle.getVLine());
        fillColumn(borderArea.side(EAST), borderStyle.getVLine());

        for (int i=0; i<4; i++) {
            TerminalPosition corner = getCornerPosition(i);
            drawChar(corner, borderStyle.getCorner(i));
        }
    }

    protected void drawChar(TerminalPosition position, TextCharacter character) {
        image.setCharacterAt(position, character);
    }

    protected void drawChar(TerminalPosition position, char c) {
        image.setCharacterAt(position, textChar(c));
    }

    protected void drawChar(TerminalPosition position, char c, TextColor color) {
        image.setCharacterAt(position, textChar(c, color));
    }

    protected void drawChar(int col, int row, char c, TextColor color) {
        image.setCharacterAt(col, row, textChar(c, color));
    }

    protected void fillRow(int row, int offset, char c, TextColor color) {
        TextGraphics graphics = image.newTextGraphics();
        graphics.drawLine(firstColumn()+offset, row, lastColumn(), row, textChar(c, color));
    }

    protected void fillRow(int row, char c) {
        fillRow(row, 0, c, TextColor.ANSI.DEFAULT);
    }

    protected void fillRow(int row, char c, TextColor color) {
        fillRow(row, 0, c, color);
    }

    protected void fillRow(int row, int offset, char c) {
        fillRow(row, offset, c, TextColor.ANSI.DEFAULT);
    }

    protected void fillRow(int row, int offset, String line, TextColor color) {
        TextGraphics graphics = image.newTextGraphics();
        graphics.setForegroundColor(color);
        graphics.putString(firstColumn()+offset, row, line.substring(0, Integer.min(lastColumn()-offset, line.length())));
    }

    protected void fillRow(int row, int offset, String line) {
        fillRow(row, offset, line, TextColor.ANSI.DEFAULT);
    }

    protected void fillRow(int row, String line) {
        fillRow(row, 0, line);
    }

    protected void fillColumn(int col, char c) {
        for (int i=0; i<=getHeight(); i++) {
            drawChar(new TerminalPosition(col, i), c);
        }
    }

    protected void fillColumn(int col, int offset, String line, TextColor color) {
        for (int i=firstRow()+offset,j=0; i<=lastRow(); i++) {
            if (j >= line.length()) break;
            drawChar(new TerminalPosition(col, i), line.charAt(j), color);
            j++;
        }
    }

    protected void fillColumn(int col, int offset, String line) {
        fillColumn(col, offset, line, TextColor.ANSI.DEFAULT);
    }

    protected void fillColumn(int col, String line) {
        fillColumn(col, 0, line);
    }

    public void setBorderStyle(BorderStyle bs) {
        borderStyle = bs;
    }

    public void setMargins(int margin) {
        for(Side side: Side.values()) margins.put(side, margin);
        updateBorderArea();
        updateInnerArea();
    }

    public void setMargin(Side side, int margin) {
        margins.put(side, margin);
        updateBorderArea();
        updateInnerArea();
    }

    protected void updateBorderArea() {
        borderArea.setPosition(new TerminalPosition(margins.get(EAST), margins.get(NORD)));
        borderArea.setSize(rectangle.getSize().withRelative(-getHorizontalMargins()-1, -getVerticallMargins()-1));
    }

    protected void updateInnerArea() {
        innerArea.setPosition(borderArea.getPosition().withRelative(1, 1));
        innerArea.setSize(borderArea.getSize().withRelative(-2,-2));
    }

    protected int innerWidth() { return innerArea.getWidth(); }
    protected int innerHeight() { return innerArea.getHeight(); }
    protected int firstRow() { return innerArea.getY(); }
    protected int lastRow() { return innerArea.getYAndHeight(); }
    protected int firstColumn() { return innerArea.getX(); }
    protected int lastColumn() { return innerArea.getXAndWidth(); }

    public void setPosition(TerminalPosition newPosition) {
        rectangle.setPosition(newPosition);
    }

    public void setPosition(int x, int y) {
        rectangle.setPosition(x, y);
    }

    protected TerminalPosition getCornerPosition(int dir) {
        int r = dir>>1, c = 2+(dir&1);
        return new TerminalPosition(borderArea.side(c), borderArea.side(r));
    }

    protected TerminalPosition getCornerPosition(Direction dir) {
        return getCornerPosition(dir.ordinal());
    }

    public TerminalPosition getPosition() {
        return rectangle.getPosition();
    }
    public TerminalSize getSize() { return rectangle.getSize(); }
    public TerminalRectangle getRectangle() { return rectangle; }
    public int getHorizontalMargins() { return margins.get(EAST) + margins.get(WEST); }
    public int getVerticallMargins() { return margins.get(NORD) + margins.get(SUD); }
    public int getHeight() { return rectangle.getHeight(); }
    public int getWidth() { return rectangle.getWidth(); }
    public int getY() { return rectangle.getY(); }
    public int getX() { return rectangle.getX(); }
    public int getYAndHeight() { return rectangle.getYAndHeight(); }
    public int getXAndWidth() { return rectangle.getXAndWidth(); }

    public static TextCharacter textChar(char c) {
        return TextCharacter.fromCharacter(c)[0];
    }

    public static TextCharacter textChar(char c, TextColor color) {
        return TextCharacter.fromCharacter(c, color, TextColor.ANSI.DEFAULT)[0];
    }
}
