package IS24_LB11.network.phases;

import IS24_LB11.game.Game;
import IS24_LB11.game.Player;
import IS24_LB11.game.components.GoalCard;
import IS24_LB11.game.tools.JsonConverter;
import IS24_LB11.game.tools.JsonException;
import IS24_LB11.network.ClientHandler;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


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

                ArrayList<String> publicGoals = new ArrayList<>();
                for(GoalCard goalCard: clientHandler.getGame().getPublicGoals()) {
                    publicGoals.add(goalCard.asString());
                }
                obj.add("publicGoals", new Gson().toJsonTree(publicGoals).getAsJsonArray());

                ArrayList<String> playerNames = new ArrayList<>();
                for(Player player : clientHandler.getGame().getPlayers()) {
                    playerNames.add(player.name());
                }
                obj.add("playerNames", new Gson().toJsonTree(playerNames).getAsJsonArray());

                ArrayList<String> colors = new ArrayList<>();
                for(Player player : clientHandler.getGame().getPlayers()) {
                    colors.add(player.getColor().name());
                }
                obj.add("colors", new Gson().toJsonTree(colors).getAsJsonArray());

                clHandler.sendMessage(obj.toString());

            } catch (JsonException e) {
                throw new RuntimeException(e);
            }


        }



    }

}
