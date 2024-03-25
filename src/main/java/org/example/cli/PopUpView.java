package org.example.cli;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import org.example.cli.utils.Side;
import org.example.cli.utils.TerminalRectangle;
import org.example.cli.utils.TextBox;

import java.util.Optional;

public class PopUpView extends TextBox {
    private static final String ESCAPE_SEQUENCE = "[ESC]";
    private Optional<String> title;

    public PopUpView(TerminalSize size, TerminalPosition position, String message, String title) {
        super(size, position, message);
        this.title = Optional.of(title);
    }

    @Override
    public void build() {
        super.build();
        title.ifPresent(s -> fillRow(borderArea.getY(), 1, s));
    }

    @Override
    public void loadText() {
        int row = firstRow()+1;
        for (String line: lines) {
            int offset = (innerWidth()-line.length())/2;
            fillRow(row, offset, line);
            if (row == lastRow()-1) break;
            row++;
        }
        int offset = (innerWidth()-ESCAPE_SEQUENCE.length())/2;
        fillRow(borderArea.side(Side.SUD), offset, ESCAPE_SEQUENCE);
    }

    public void setTitle(String title) { this.title = Optional.of(title); }

    public TerminalRectangle getRectangle() {
        return new TerminalRectangle(rectangle.getSize(), rectangle.getPosition());
    }
}
