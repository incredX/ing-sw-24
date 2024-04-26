package IS24_LB11.network.phases;

import IS24_LB11.game.Game;
import IS24_LB11.game.Player;
import IS24_LB11.game.components.GoalCard;
import IS24_LB11.game.tools.JsonConverter;
import IS24_LB11.game.tools.JsonException;
import IS24_LB11.network.ClientHandler;
import com.google.gson.*;


import java.util.ArrayList;

public class SetupPhase {

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
                obj.add("setup", new JsonParser().parse(playerSetup));

                JsonArray publicGoals = new JsonArray();
                for(GoalCard goalCard: clientHandler.getGame().getPublicGoals()) {
                    publicGoals.add(new JsonPrimitive(goalCard.asString()));
                }
                obj.add("publicGoals", publicGoals);

                JsonArray playerNames = new JsonArray();
                for(Player player : clientHandler.getGame().getPlayers()) {
                    playerNames.add(new JsonPrimitive(player.name()));
                }
                obj.add("playerNames", playerNames);

                JsonArray colors = new JsonArray();
                for(Player player : clientHandler.getGame().getPlayers()) {
                    colors.add(new JsonPrimitive(player.getColor().name()));
                }
                obj.add("colors", colors);

                clHandler.sendMessage(obj.toString());

            } catch (JsonException e) {
                throw new RuntimeException(e);
            }


        }



    }

}
