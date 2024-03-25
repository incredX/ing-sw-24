package org.example.cli.utils;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;

public class TerminalRectangle {
    private TerminalSize size;
    private TerminalPosition position;
    private TerminalPosition endPoint;

    public TerminalRectangle(TerminalSize size, TerminalPosition position) {
        this.size = size;
        this.position = position;
        this.endPoint = position.withRelative(size.getColumns(), size.getRows());
    }

    public TerminalRectangle withPosition(TerminalPosition position) {
        return new TerminalRectangle(this.size, position);
    }

    public TerminalRectangle withRelative(TerminalPosition delta) {
        return new TerminalRectangle(size, position.withRelative(delta));
    }

    public boolean contains(TerminalPosition tPos) {
        return tPos.getColumn() >= position.getColumn() &&
                tPos.getColumn() < endPoint.getColumn() &&
                tPos.getRow() >= position.getRow() &&
                tPos.getRow() < endPoint.getRow();
    }

    private void updateEndPoint() {
        endPoint = position.withRelative(size.getColumns(), size.getRows());
    }

    public void reSize(int deltaWidth, int deltaHeight) {
        size = size.withRelative(deltaWidth, deltaHeight);
        updateEndPoint();
    }

    public void setSize(TerminalSize newSize) {
        size = new TerminalSize(newSize.getColumns(), newSize.getRows());
        updateEndPoint();
    }

    public void setPosition(TerminalPosition newPosition) {
        position = new TerminalPosition(newPosition.getColumn(), newPosition.getRow());
        updateEndPoint();
    }

    public void setPosition(int x, int y) {
        position = new TerminalPosition(x, y);
        updateEndPoint();
    }

    public TerminalSize getSize() {
        return new TerminalSize(size.getColumns(), size.getRows());
    }

    public TerminalPosition getPosition() {
        return new TerminalPosition(position.getColumn(), position.getRow());
    }

    public int side(Side side) {
        return switch (side) {
            case NORD -> position.getRow();
            case SUD -> endPoint.getRow();
            case WEST -> position.getColumn();
            case EAST -> endPoint.getColumn();
        };
    }

    public int side(int side) {
        return side(Side.fromInt(side));
    }

    public int getWidth() { return size.getColumns(); }
    public int getHeight() { return size.getRows(); }
    public int getX() { return position.getColumn(); }
    public int getY() { return position.getRow(); }
    public int getXAndWidth() { return endPoint.getColumn(); }
    public int getYAndHeight() { return endPoint.getRow(); }
}
