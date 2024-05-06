package IS24_LB11.cli.popup;

import IS24_LB11.cli.ViewHub;
import IS24_LB11.cli.controller.PlayerStateInterface;
import IS24_LB11.cli.view.TableView;
import IS24_LB11.cli.Scoreboard;
import com.googlecode.lanterna.input.KeyStroke;

import java.util.function.Consumer;

public class TablePopup extends Popup {
    private PlayerStateInterface playerState;

    public TablePopup(ViewHub viewHub, PlayerStateInterface playerState) {
        super(viewHub, new TableView(viewHub.getScreenSize()));
        this.playerState = playerState;
        this.overlap = true;
    }

    @Override
    public String label() { return "table"; }

    @Override
    public void update() {
        Scoreboard scoreboard = playerState.getScoreboard();
        castView(tableView -> {
            tableView.loadColors(scoreboard.getColors());
            tableView.loadPlayers(scoreboard.getPlayers());
            tableView.loadScores(scoreboard.getScores());
            tableView.loadCurrentPlayer(scoreboard.getCurrentPlayerIndex());
            tableView.loadGoals(playerState.getGoals());
            tableView.drawAll();
        });
    }

    @Override
    public void redrawView() {
        Scoreboard scoreboard = playerState.getScoreboard();
        castView(tableView -> {
            tableView.clear();
            tableView.loadColors(scoreboard.getColors());
            tableView.loadPlayers(scoreboard.getPlayers());
            tableView.loadScores(scoreboard.getScores());
            tableView.loadCurrentPlayer(scoreboard.getCurrentPlayerIndex());
            tableView.drawAll();
        });
    }

    @Override
    public void consumeKeyStroke(KeyStroke keyStroke) {
        if (!enabled) return; // focus is not here
        // cosume keyStroke here
    }

    public void setPlayerState(PlayerStateInterface playerState) {
        this.playerState = playerState;
    }

    private void castView(Consumer<TableView> consumer) {
        consumer.accept((TableView) popView);
    }
}
