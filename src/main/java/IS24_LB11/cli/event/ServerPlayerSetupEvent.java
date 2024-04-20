package IS24_LB11.cli.event;

import IS24_LB11.game.PlayerSetup;

public class ServerPlayerSetupEvent {
    private final PlayerSetup playerSetup;

    public ServerPlayerSetupEvent(PlayerSetup playerSetup) {
        this.playerSetup = playerSetup;
    }

    public PlayerSetup getPlayerSetup() {
        return playerSetup;
    }
}
