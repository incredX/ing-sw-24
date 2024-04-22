package IS24_LB11.cli.controller;

import IS24_LB11.cli.BoardKeyConsumer;
import IS24_LB11.cli.BoardStage;
import IS24_LB11.cli.event.ServerEvent;
import IS24_LB11.cli.ViewHub;
import IS24_LB11.game.Player;
import IS24_LB11.game.PlayerSetup;
import IS24_LB11.game.utils.Position;

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
        keyConsumers.add(new BoardKeyConsumer(boardStage, 2));
        viewHub.update();
        return super.execute();
    }

    @Override
    protected void processServerEvent(ServerEvent event) {
        if (processServerEventIfCommon(event)) return;
    }

    @Override
    protected void processCommand(String command) {
        if (processCommandIfCommon(command)) return;
        String[] tokens = command.split(" ", 2);
        switch (tokens[0].toLowerCase()) {
            case "HAND" -> {}
            case "CENTER" -> boardStage.setPointer(new Position(0,0));
        }
    }
}
