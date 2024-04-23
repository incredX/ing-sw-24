package IS24_LB11.cli.view;

import IS24_LB11.cli.style.SingleBorderStyle;
import IS24_LB11.cli.utils.CliBox;
import IS24_LB11.cli.utils.Side;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;

import static IS24_LB11.cli.view.PlayableCardView.HEIGHT;
import static IS24_LB11.cli.view.PlayableCardView.WIDTH;

public class CardsBoxView extends CliBox {
    protected TerminalPosition pointerPosition;
    protected final String title;

    public CardsBoxView(String title, int width, int height, int x, int y) {
        super(width, height, x, y, new SingleBorderStyle());
        this.pointerPosition = null;
        this.title = title;
    }

    @Override
    public void build() {
        drawBorders();
        drawTitle();
        drawPointer();
    }

    @Override
    public void resize(TerminalSize terminalSize) {
        int x = (terminalSize.getColumns()-getWidth())/2;
        int y = (terminalSize.getRows()-getHeight())/2;
        setPosition(new TerminalPosition(x, y));
    }


    protected void drawTitle() {
        fillRow(borderArea.side(Side.NORD), firstColumn(), String.format("[%s]", title));
    }

    protected void drawPointer() {
        if (pointerPosition == null) return;
        TextColor color = TextColor.ANSI.WHITE_BRIGHT;
        int baseX = pointerPosition.getColumn();
        int baseY = pointerPosition.getRow();
        fillColumn(firstColumn()+baseX+2, baseY-2, "# #", color);
        fillRow(baseY, baseX, "# +", color);
        fillColumn(firstColumn()+baseX+10, baseY-2, "# #", color);
        fillRow(baseY, baseX+10, "+ #", color);
    }

    public void hidePointer() {
        pointerPosition = null;
    }
}
