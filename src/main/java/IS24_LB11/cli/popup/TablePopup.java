package IS24_LB11.cli.popup;

import IS24_LB11.cli.Debugger;
import IS24_LB11.cli.ViewHub;
import IS24_LB11.cli.controller.GameState;
import IS24_LB11.cli.controller.PlayerStateInterface;
import IS24_LB11.cli.view.TableView;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;

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
        castView(tableView -> {
            if (playerState.getTable().isFinalRanking()) {
                tableView.setWinnerIndex(playerState.getTable().getCurrentTopPlayerIndex());
            }
            tableView.loadScoreboard(playerState.getScoreboard());
            tableView.loadGoals(playerState.getGoals());
            tableView.drawAll();
        });
    }

    @Override
    public void redrawView() {
        castView(tableView -> {
            tableView.clear();
            tableView.loadScoreboard(playerState.getScoreboard());
            tableView.drawAll();
        });
    }

    @Override
    public void consumeKeyStroke(KeyStroke keyStroke) {
        Debugger.print("consuming key-stroke");
        if (!enabled) return; // focus is not here
        switch (playerState) {
            case GameState gameState -> consumeKeyStrokeInGame(gameState, keyStroke);
            default -> {
                return;
            }
        }
    }

    private void consumeKeyStrokeInGame(GameState gameState, KeyStroke keyStroke) {
        Debugger.print("consuming key-stroke in Game");
        if (gameState.getTable().isFinalRanking()) {
            Debugger.print("calling logout = " + (keyStroke.getKeyType() == KeyType.Enter));
            if (keyStroke.getKeyType() == KeyType.Enter) gameState.logout();
        }
    }

    public void setPlayerState(PlayerStateInterface playerState) {
        this.playerState = playerState;
    }

    private void castView(Consumer<TableView> consumer) {
        consumer.accept((TableView) popView);
    }
}
