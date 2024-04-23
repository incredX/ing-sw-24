package IS24_LB11.cli;

import IS24_LB11.cli.utils.CliBox;

public class Popup<S extends Stage, T extends CliBox> {
    protected final S stage;
    protected final T view;
    protected boolean visible;

    public Popup(S stage, T view) {
        this.stage = stage;
        this.view = view;
        this.visible = true;
    }

    public void buildView() {
        view.build();
    }

    public void drawViewInStage() {
        stage.draw(view);
    }

    public void show() {
        visible = true;
        if (viewInsideStage()) drawViewInStage();
    }

    public void hide() {
        visible = false;
        stage.rebuild();
    }

    private boolean viewInsideStage() {
        return stage.getWidth() > view.getWidth() &&
                stage.getHeight() > view.getHeight() &&
                view.getX() >= 0 && view.getY() >= 0;
    }
}
