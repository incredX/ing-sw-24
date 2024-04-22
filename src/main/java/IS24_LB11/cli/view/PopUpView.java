package IS24_LB11.cli.view;

import IS24_LB11.cli.Stage;
import IS24_LB11.cli.style.SingleBorderStyle;
import IS24_LB11.cli.utils.*;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;

import static IS24_LB11.cli.utils.Side.EAST;
import static IS24_LB11.cli.utils.Side.WEST;
import static IS24_LB11.game.utils.Direction.UP_LEFT;
import static IS24_LB11.game.utils.Direction.UP_RIGHT;

public class PopUpView extends CliBox {
    private static final String ESCAPE_SEQUENCE = "[ESC]";

    private final String title;
    private final String message;

    public PopUpView(Stage stage, String title, String message) {
        super(stage.getSize().withRows(3),
                new TerminalPosition(0, stage.getYAndHeight()-3),
                new SingleBorderStyle());
        this.title = title;
        this.message = message;
        setMargins(0);
    }

    @Override
    public void build() {
        drawBorders();
        drawText();
    }

    @Override
    public void resize(TerminalSize stageSize) {
        setSize(stageSize.withRows(3));
    }

    @Override
    protected void drawBorders() {
        fillRow(borderArea.side(Side.NORD), borderStyle.getHLine());
        fillRow(borderArea.side(Side.NORD), innerWidth()-ESCAPE_SEQUENCE.length()-1, ESCAPE_SEQUENCE);
        drawCell(new TerminalPosition(borderArea.side(WEST),lastRow()), borderStyle.getVLine(), TextColor.ANSI.DEFAULT);
        drawCell(new TerminalPosition(borderArea.side(EAST),lastRow()), borderStyle.getVLine(), TextColor.ANSI.DEFAULT);
        drawCell(getCornerPosition(UP_LEFT), borderStyle.getSeparator(WEST), TextColor.ANSI.DEFAULT);
        drawCell(getCornerPosition(UP_RIGHT), borderStyle.getSeparator(EAST), TextColor.ANSI.DEFAULT);
    }

    private void drawText() {
        fillRow(lastRow(), title + " | " + message);
    }

    public TerminalRectangle getRectangle() {
        return new TerminalRectangle(rectangle.getSize(), rectangle.getPosition());
    }
}
