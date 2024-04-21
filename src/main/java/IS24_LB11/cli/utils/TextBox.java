package IS24_LB11.cli.utils;

import IS24_LB11.cli.style.SingleBorderStyle;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;

import java.util.ArrayList;
import java.util.Arrays;

public class TextBox extends CliBox {
    protected final ArrayList<String> lines;
    protected int maxLineLenght;

    public TextBox(TerminalSize size, TerminalPosition position, String text) {
        super(size, position, new SingleBorderStyle());
        lines = new ArrayList<>();
        addText(text);
    }

    public TextBox(TerminalPosition position, String text) {
        super(new TerminalSize(8, 8), position, new SingleBorderStyle());
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
        maxLineLenght = lines.stream().max((s1, s2) -> Integer.compare(s1.length(), s2.length())).get().length();
    }

    public void setText(String text) {
        lines.clear();
        addText(text);
    }
}
