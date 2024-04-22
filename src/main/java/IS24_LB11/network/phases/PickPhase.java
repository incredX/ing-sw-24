package IS24_LB11.network.phases;

import IS24_LB11.game.Game;
import IS24_LB11.game.PlayerSetup;
import IS24_LB11.game.tools.JsonConverter;
import IS24_LB11.game.tools.JsonException;
import IS24_LB11.network.ClientHandler;
import IS24_LB11.network.Server;

public class PickPhase {

    public static void startPhase(ClientHandler clientHandler, Game game) {

//        for()

        JsonConverter converter = new JsonConverter();



        try {
            converter.objectToJSON(game.getPlayers().get(0).getSetup());
        } catch (JsonException e) {
            throw new RuntimeException(e);
        }
    }

}
