package IS24_LB11.cli.view.stage;

import IS24_LB11.cli.ViewHub;
import IS24_LB11.cli.controller.GameState;

public class TableStage extends Stage {
    private final GameState state;

    public TableStage(ViewHub viewHub, GameState state) {
        super(viewHub);
        this.state = state;
    }
}
