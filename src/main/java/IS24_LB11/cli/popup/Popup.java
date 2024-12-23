package IS24_LB11.cli.popup;

import IS24_LB11.cli.ViewHub;
import IS24_LB11.cli.controller.PlayerStateInterface;
import IS24_LB11.cli.utils.TerminalBox;
import IS24_LB11.cli.view.popup.PopupView;
import com.googlecode.lanterna.input.KeyStroke;

public abstract class Popup<T extends PopupView> {
    protected final ViewHub viewHub;
    protected final T popView;
    protected boolean visible;
    protected boolean enabled;
    protected boolean overlap;
    protected boolean readOnly;

    public Popup(ViewHub viewHub, T popView) {
        this.viewHub = viewHub;
        this.popView = popView;
        this.visible = false;
        this.enabled = false;
        this.overlap = false;
    }

    public abstract String label();

    public abstract void update();

    public abstract void consumeKeyStroke(KeyStroke keyStroke);

    public abstract void setPlayerState(PlayerStateInterface playerState);

    public void redrawView() {
        popView.redraw();
    }

    public void resize() {
        popView.resize(viewHub.getScreenSize().withRelative(0,-2));
        if (visible) {
            if (!viewInsideStage()) hide();
            //else viewHub.getStage().setCover(popView, true);
        }
    }

    public void show() {
        resize();
        if (viewInsideStage()) {
            if (!visible) {
                viewHub.addPopup(popView);
            }
            visible = true;
        }
    }

    public void hide() {
        if (visible) {
            viewHub.removePopup(popView.getId());
            visible = false;
        }
    }

    public void enable() {
        enabled = true;
    }

    public void disable() {
        enabled = false;
    }

    public boolean isVisible() {
        return visible;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public boolean canOverlap() { return overlap; }

    private boolean viewInsideStage() {
        return viewHub.getStage().getWidth() > popView.getWidth() &&
                viewHub.getStage().getHeight() > popView.getHeight() &&
                popView.getX() >= 0 && popView.getY() >= 0;
    }

    public boolean overlapping(TerminalBox box) {
        int popX1 = popView.getX(), popY1 = popView.getY();
        int popX2 = popView.getXAndWidth(), popY2 = popView.getYAndHeight();
        int boxX1 = box.getX(), boxY1 = box.getY();
        int boxX2 = box.getXAndWidth(), boxY2 = box.getYAndHeight();
        boolean x1Overlap = (popX1 <= boxX1 && boxX1 <= popX2) || (boxX1 <= popX1 && popX1 <= boxX2);
        boolean x2Overlap = (popX1 <= boxX2 && boxX2 <= popX2) || (boxX1 <= popX2 && popX2 <= boxX2);
        boolean y1Overlap = (popY1 <= boxY1 && boxY1 <= popY2) || (boxY1 <= popY1 && popY1 <= boxY2);
        boolean y2Overlap = (popY1 <= boxY2 && boxY2 <= popY2) || (boxY2 <= popY1 && popY2 <= boxY2);
        return (x1Overlap || x2Overlap) && (y1Overlap || y2Overlap);
    }

    public boolean overlapping(Popup popup) {
        return overlapping(popup.popView);
    }
}
