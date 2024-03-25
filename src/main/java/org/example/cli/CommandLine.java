package org.example.cli;

public class CommandLine {
    private final StringBuilder line;
    private int cursor;
    private int offset;
    private int width;

    public CommandLine(int width) {
        this.line = new StringBuilder();
        this.width = width;
        this.cursor = 0;
        this.offset = 0;
    }

    public void insertChar(char c) {
        if (line.length() == cursor) line.append(c);
        else line.insert(cursor, c);
        moveCursor(1);
    }

    public void deleteChar() {
        if (cursor == 0) return;
        line.deleteCharAt(cursor-1);
        if (offset > 0) offset--;
        moveCursor(-1);
    }

    public void setLine(String string) {
        line.replace(0, line.length(), string);
        cursor = line.length();
        offset = Integer.max(0,cursor - innerWidth() +1);
    }

    public void clearLine() {
        line.delete(0, line.length());
        cursor = 0;
        offset = 0;
    }

    public void moveCursor(int delta) {
        int newCursor = cursor+delta;
        //System.out.printf("(% 3d) cur:% 3d, os:% 2d -> ", delta, cursor, offset);
        cursor = Integer.min(newCursor, line.length());
        cursor = Integer.max(cursor, 0);
        if (offset > cursor) offset = cursor;
        if (delta > 0 && cursor - offset >= innerWidth()) {
            offset += delta;
        } /*else if (delta < 0 && cursor >= maxLength() -1) {
            offset += delta;
        }*/
        //System.out.printf("cur:% 3d, os:% 2d\n", cursor, offset);
    }

    public void setWidth(int width) {
        this.width = width;
        if (cursor >= width) cursor = width-1;
    }

    public int innerWidth() { return width-3; }

    public String getFullLine() { return line.toString(); }
    public String getVisibleLine() { return line.substring(offset, Integer.min(line.length(), width)); }
    public int getCursor() { return cursor; }
    public int getOffset() { return offset; }
}
