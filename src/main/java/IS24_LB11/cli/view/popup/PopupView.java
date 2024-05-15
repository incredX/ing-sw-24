package IS24_LB11.cli.view.popup;

import IS24_LB11.cli.style.SingleBorderStyle;
import IS24_LB11.cli.utils.LayerInterface;
import IS24_LB11.cli.utils.TerminalBox;

public class PopupView extends TerminalBox implements LayerInterface {
    protected Integer id;
    public PopupView(int width, int height, int x, int y) {
        super(width, height, x, y, new SingleBorderStyle());
        id = null;
    }

    @Override
    public int zIndex() { return 1; }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
