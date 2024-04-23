package IS24_LB11.cli.popup;

import IS24_LB11.cli.Stage;
import IS24_LB11.cli.utils.CliBox;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.terminal.Terminal;

public class Popup<S extends Stage, T extends CliBox> {
    protected final S stage;
    protected final T view;
    protected boolean visible;
    protected boolean enabled;

    public Popup(S stage, T view) {
        this.stage = stage;
        this.view = view;
        this.visible = false;
        this.enabled = false;
    }

    public void resize() {
        view.resize(stage.getSize());
    }

    public void buildView() {
        view.build();
    }

    public void drawViewInStage() {
        if (visible && viewInsideStage()) stage.draw(view);
    }

    public boolean consumeKeyStroke(KeyStroke keyStroke) {
        return false;
    }

    public void show() {
        visible = true;
        if (viewInsideStage()) {
            buildView();
            drawViewInStage();
        }
    }

    public void hide() {
        visible = false;
        stage.rebuild();
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
        return stage.getWidth() > view.getWidth() &&
                stage.getHeight() > view.getHeight() &&
                view.getX() >= 0 && view.getY() >= 0;
    }
}
