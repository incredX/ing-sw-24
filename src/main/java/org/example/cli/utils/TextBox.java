package org.example.cli.utils;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import org.example.cli.style.SingleBorderStyle;

import java.util.ArrayList;
import java.util.Arrays;

public class TextBox extends CliBox {
    protected final ArrayList<String> lines;

    public TextBox(TerminalSize size, TerminalPosition position, String text) {
        super(size, position, new SingleBorderStyle());
        lines = new ArrayList<>();
        addText(text);
    }

    @Override
    public void build() {
        drawBorders();
        loadText();
    }

    public void rebuild() {
        clear();
        build();
    }

    public void loadText() {
        int row = firstRow();
        for (String line: lines) {
            fillRow(row, line);
            if (row == lastRow()) break;
            row++;
        }
    }

    public void addText(String text) {
        lines.addAll(Arrays.asList(text.split("\n")));
    }

    public void setText(String text) {
        lines.clear();
        lines.addAll(Arrays.asList(text.split("\n")));
    }
}
