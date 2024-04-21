package IS24_LB11.cli.view;

import IS24_LB11.cli.utils.Cell;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import IS24_LB11.cli.utils.Side;
import IS24_LB11.cli.utils.TerminalRectangle;
import IS24_LB11.cli.utils.TextBox;
import com.googlecode.lanterna.TextColor;

import java.util.Arrays;
import java.util.Optional;

public class PopUpView extends TextBox {
    private static final int MAX_WIDTH = 64;
    private static final int MAX_HEIGHT = 16;
    private static final String ESCAPE_SEQUENCE = "[ESC]";

    private final String title;

    public PopUpView(TerminalPosition base, String message, String title) {
        super(base, message);
        this.title = title;
        TerminalSize target = targetSize();
        setSize(target);
        this.rectangle.setPosition(base.withRelative(-target.getColumns()/2,-target.getRows()/2));
        //this.rectangle.setPosition(center.withRelative(-target.getColumns()/2, -target.getRows()/2));
    }

    public PopUpView(TerminalPosition center, String message) {
        this(center, message, "");
    }

    @Override
    public void build() {
        super.build();
        if (!title.isEmpty()) fillRow(borderArea.getY(), 1, title);
    }

    @Override
    public void resize(TerminalSize newSize) {
        TerminalSize target = targetSize();
        if (target.getColumns() > newSize.getColumns()-16) target = target.withColumns(newSize.getColumns()-16);
        if (target.getRows() > newSize.getRows()-8) target = target.withRows(newSize.getRows()-8);
        setSize(target);
        image = new Cell[rectangle.getHeight()][rectangle.getWidth()];
        resetImage();
    }

    @Override
    public void loadText() {
        int row = firstRow()+1;
        for (String line: lines) {
            fillRow(row, 4, line);
            if (row == lastRow()-1) break;
            if (line.length() > innerWidth()) {
                row ++;
                fillRow(row, 0, line.substring(innerWidth()));
                if (row == lastRow()-1) break;
            }
            row++;
        }
        int offset = innerWidth()-ESCAPE_SEQUENCE.length();
        fillRow(borderArea.side(Side.NORD), offset, ESCAPE_SEQUENCE);

    }

    public TerminalRectangle getRectangle() {
        return new TerminalRectangle(rectangle.getSize(), rectangle.getPosition());
    }

    private TerminalSize targetSize() {
        int width, height = Integer.min(MAX_HEIGHT, lines.size()+4);
        if (!title.isEmpty())
            width = Integer.min(MAX_WIDTH, Integer.max(maxLineLenght, title.length())+12);
        else
            width = Integer.min(MAX_WIDTH, maxLineLenght+8);
        return new TerminalSize(width, height);
    }
}
