package IS24_LB11.cli.controller;

import IS24_LB11.cli.BoardKeyConsumer;
import IS24_LB11.cli.GameStage;
import IS24_LB11.cli.event.ServerEvent;
import IS24_LB11.cli.ViewHub;
import IS24_LB11.game.Player;
import IS24_LB11.game.PlayerSetup;
import IS24_LB11.game.utils.Position;

import java.io.IOException;

public class ClientInGame extends ClientState {
    private final Player player;
    private GameStage gameStage;

    public ClientInGame(ViewHub viewHub, PlayerSetup setup) throws IOException {
        super(viewHub);
        this.player = new Player(username, setup);
    }

    @Override
    public ClientState execute() {
        player.getBoard().start(player.setup().getStarterCard());
        gameStage = viewHub.setGameStage(player);
        keyConsumers.add(new BoardKeyConsumer(gameStage, 2));
        return super.execute();
    }

    @Override
    protected void processServerEvent(ServerEvent event) {
        if (processServerEventIfCommon(event)) return;
    }

    @Override
    protected void processCommand(String command) {
        if (processCommandIfCommon(command)) return;
        System.out.println(command);
        String[] tokens = command.split(" ", 2);
        switch (tokens[0].toUpperCase()) {
            case "HAND" -> {}
            case "HOME" -> {
                gameStage.setPointer(new Position(0, 0));
                gameStage.centerGridBase();
                gameStage.rebuild();
                viewHub.update();
            }
            default -> popUpStack.addUrgentPopUp("ERROR", tokens[0]+" is not a valid command");
        }
    }
}
