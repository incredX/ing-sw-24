package IS24_LB11.cli.event.server;

import IS24_LB11.game.PlayerSetup;

public class ServerPlayerSetupEvent implements ServerEvent {
    private final PlayerSetup playerSetup;

    public ServerPlayerSetupEvent(PlayerSetup playerSetup) {
        this.playerSetup = playerSetup;
    }

    public PlayerSetup getPlayerSetup() {
        return playerSetup;
    }
}
