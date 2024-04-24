package IS24_LB11.cli.popup;

import IS24_LB11.cli.ViewHub;
import com.googlecode.lanterna.input.KeyStroke;

public class Popup<T extends PopupView> {
    protected final ViewHub viewHub;
    protected final T popView;
    protected boolean visible;
    protected boolean enabled;

    public Popup(ViewHub viewHub, T popView) {
        this.viewHub = viewHub;
        this.popView = popView;
        this.visible = false;
        this.enabled = false;
        //viewHub.addPopup(popView);
    }

    public void update() {
        popView.build();
        viewHub.update();
    }

    public boolean consumeKeyStroke(KeyStroke keyStroke) {
        return false;
    }

    public void resize() {
        popView.resize(viewHub.getScreenSize());
    }

    public void show() {
        if (viewInsideStage()) {
            System.out.println("showing popup");
            if (!visible) {
                viewHub.addPopup(popView);
                update();
            }
        }
        visible = true;
    }

    public void hide() {
        if (visible) {
            viewHub.removePopup(popView.getId());
            update();
        }
        visible = false;
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

    private boolean viewInsideStage() {
        System.out.printf("%s vs %s in %s\n", viewHub.getStage().getSize(), popView.getSize(), popView.getPosition());
        return viewHub.getStage().getWidth() > popView.getWidth() &&
                viewHub.getStage().getHeight() > popView.getHeight() &&
                popView.getX() >= 0 && popView.getY() >= 0;
    }
}
