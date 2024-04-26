package IS24_LB11.cli.popup;

import IS24_LB11.cli.ViewHub;
import IS24_LB11.cli.controller.ClientState;
import IS24_LB11.cli.controller.GameState;
import IS24_LB11.cli.controller.SetupState;
import IS24_LB11.cli.view.TableView;
import IS24_LB11.cli.Scoreboard;
import IS24_LB11.game.components.GoalCard;
import com.googlecode.lanterna.input.KeyStroke;

import java.util.ArrayList;
import java.util.function.Consumer;

public class TablePopup extends Popup {
    private GameState gameState;
    private SetupState setupState;

    public TablePopup(ViewHub viewHub, GameState gameState) {
        super(viewHub, new TableView(viewHub.getScreenSize()));
        this.gameState = gameState;
        this.setupState = null;
    }

    public TablePopup(ViewHub viewHub, SetupState setupState) {
        super(viewHub, new TableView(viewHub.getScreenSize()));
        this.setupState = setupState;
        this.gameState = null;
    }

    @Override
    public String label() { return "table"; }

    @Override
    public void update() {
        Scoreboard scoreboard = getScoreboard();
        castView(tableView -> {
            tableView.loadColors(scoreboard.getColors());
            tableView.loadPlayers(scoreboard.getPlayers());
            tableView.loadScores(scoreboard.getScores());
            tableView.loadGoals(getGoals());
            tableView.build();
        });
    }

    @Override
    public void consumeKeyStroke(ClientState state, KeyStroke keyStroke) {
        if (!enabled) return; // focus is not here
        // cosume keyStroke here
    }

    private ArrayList<GoalCard> getGoals() {
        if (setupState != null && gameState == null) return new ArrayList<>();
        if (gameState != null && setupState == null) return gameState.getGoals();
        return new ArrayList<>();
    }

    private Scoreboard getScoreboard() {
        if (setupState != null && gameState == null) return null;
        if (gameState != null && setupState == null) return gameState.getScoreboard();
        return null;
    }

    private void castView(Consumer<TableView> consumer) {
        consumer.accept((TableView) popView);
    }
}
