package IS24_LB11.cli.utils;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;

public final class Cell {
    private String value;
    private boolean covered;
    private TextColor color;

    public Cell(String value, boolean covered, TextColor color) {
        this.value = value;
        this.color = color;
        this.covered = covered;
    }

    public Cell(String value, TextColor color) {
        this(value, false, color);
    }


    public Cell(char value, boolean covered, TextColor color) {
        this.value = String.valueOf(value);
        this.color = color;
        this.covered = covered;
    }

    public Cell(char value, TextColor color) {
        this(String.valueOf(value), false, color);
    }

    public Cell(Cell cell) {
        this.value = cell.value;
        this.color = cell.color;
        this.covered = cell.covered;
    }

    public Cell(char value) {
        this(value, TextColor.ANSI.DEFAULT);
    }

    public void print(Terminal terminal) throws IOException {
        if (covered) return;
        if (color != TextColor.ANSI.DEFAULT) {
            terminal.setForegroundColor(color);
            terminal.putString(value);
            terminal.setForegroundColor(TextColor.ANSI.DEFAULT);
        } else {
            terminal.putString(value);
        }
    }

    public boolean isCovered() {
        return covered;
    }

    public void clear() {
        this.value = " ";
    }

    public void set(String value) {
        this.value = value;
    }

    public void set(char value) {
        this.value = String.valueOf(value);
    }

    public void set(TextColor color) { this.color = color; }

    public void set(char value, TextColor color) {
        this.value = String.valueOf(value);
        this.color = color;
    }

    public void set(Cell cell) {
        this.value = cell.value;
        this.color = cell.color;
    }

    public void setCovered(boolean covered) {
        this.covered = covered;
    }
}
