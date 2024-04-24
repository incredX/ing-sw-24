package IS24_LB11.cli.popup;

import IS24_LB11.cli.style.SingleBorderStyle;
import IS24_LB11.cli.utils.CliBox;
import com.googlecode.lanterna.terminal.Terminal;

public class PopupView extends CliBox {
    protected int id;
    public PopupView(int width, int height, int x, int y) {
        super(width, height, x, y, new SingleBorderStyle());
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
