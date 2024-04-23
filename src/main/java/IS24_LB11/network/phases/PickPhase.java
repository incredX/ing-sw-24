package IS24_LB11.network.phases;

import IS24_LB11.game.Game;
import IS24_LB11.game.Player;
import IS24_LB11.game.PlayerSetup;
import IS24_LB11.game.tools.JsonConverter;
import IS24_LB11.game.tools.JsonException;
import IS24_LB11.network.ClientHandler;
import IS24_LB11.network.Server;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class PickPhase {

    public static void startPhase(ClientHandler clientHandler, Game game) {

        Gson gson = new Gson();

        for(ClientHandler clHandler : clientHandler.getClientHandlers()){
            try {
                String playerSetup = new JsonConverter().objectToJSON(game.getPlayers()
                        .stream()
                        .filter(x -> x.name().equals(clHandler.getUserName()))
                        .findFirst()
                        .orElse(null)
                        .getSetup());

                JsonObject obj = new JsonObject();
                obj.addProperty("type", "setup");

                // check if this works
                obj.addProperty("setup", playerSetup);

                clHandler.sendMessage(obj.toString());

            } catch (JsonException e) {
                throw new RuntimeException(e);
            }


        }



    }

}
