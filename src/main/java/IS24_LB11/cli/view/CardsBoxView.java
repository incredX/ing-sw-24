package IS24_LB11.cli.view;

import IS24_LB11.cli.utils.Side;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TextColor;

public class CardsBoxView extends PopupView {
    protected TerminalPosition pointerPosition;
    protected final String label;

    public CardsBoxView(String label, int width, int height, int x, int y) {
        super(width, height, x, y);
        this.pointerPosition = null;
        this.label = label;
    }

    @Override
    public void build() {
        drawBorders();
        drawTitle();
        drawPointer();
    }

    protected void drawTitle() {
        fillRow(borderArea.side(Side.NORD), firstColumn(), String.format("[%s]", label));
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
