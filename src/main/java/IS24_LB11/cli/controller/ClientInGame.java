package IS24_LB11.cli.controller;

import IS24_LB11.cli.BoardStage;
import IS24_LB11.cli.event.ServerEvent;
import IS24_LB11.cli.view.ViewHub;
import IS24_LB11.game.Player;
import IS24_LB11.game.PlayerSetup;
import IS24_LB11.game.utils.Color;

import java.io.IOException;

public class ClientInGame extends ClientState {
    private final Player player;
    private BoardStage boardStage;

    public ClientInGame(ViewHub viewHub, PlayerSetup setup) throws IOException {
        super(viewHub);
        this.player = new Player(username, setup);
    }

    @Override
    public ClientState execute() {
        boardStage = viewHub.setBoardStage(player.getBoard());
        return super.execute();
    }

    @Override
    protected void processServerEvent(ServerEvent event) {
        if (processCommonServerEvent(event)) return;
    }

    @Override
    protected void processCommand(String command) {
        if (processCommonCommand(command)) return;
        String[] tokens = command.split(" ", 2);
        //TODO: (inGame) command center set pointer in (0,0)
        switch (tokens[0].toLowerCase()) {
            case "HAND" -> {}
        }
    }
}
