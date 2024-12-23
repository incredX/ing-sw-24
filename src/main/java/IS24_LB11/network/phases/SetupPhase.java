package IS24_LB11.network.phases;

import IS24_LB11.game.Game;
import IS24_LB11.game.Player;
import IS24_LB11.game.components.GoalCard;
import IS24_LB11.game.tools.JsonConverter;
import IS24_LB11.game.tools.JsonException;
import IS24_LB11.network.ClientHandler;
import com.google.gson.*;

import java.util.Map;

import static IS24_LB11.network.phases.NotifyTurnPhase.get3CardsFromEachDeck;

/**
 * The SetupPhase class handles the setup phase of the game, initializing the game state and sending necessary information to each client.
 */
public class SetupPhase {

    /**
     * Starts the setup phase for the game.
     * This method initializes the game for each client, sending the initial setup information, including player setups,
     * public goals, player names, colors, and the first three cards of each deck.
     *
     * @param clientHandler the client handler managing the clients
     * @param game the game instance
     */
    public static void startPhase(ClientHandler clientHandler, Game game) {
        Gson gson = new Gson();

        for (ClientHandler clHandler : clientHandler.getClientHandlers()) {
            try {
                String playerSetup = new JsonConverter().objectToJSON(
                        game.getPlayers().stream()
                                .filter(x -> x.name().equals(clHandler.getUserName()))
                                .findFirst()
                                .orElse(null)
                                .getSetup()
                );

                JsonObject obj = new JsonObject();
                obj.addProperty("type", "setup");
                obj.add("setup", new JsonParser().parse(playerSetup));

                JsonArray publicGoals = new JsonArray();
                for (GoalCard goalCard : clientHandler.getGame().getPublicGoals()) {
                    publicGoals.add(new JsonPrimitive(goalCard.asString()));
                }
                obj.add("publicGoals", publicGoals);

                JsonArray playerNames = new JsonArray();
                for (Player player : clientHandler.getGame().getPlayers()) {
                    playerNames.add(new JsonPrimitive(player.name()));
                }
                obj.add("playerNames", playerNames);

                JsonArray colors = new JsonArray();
                for (Player player : clientHandler.getGame().getPlayers()) {
                    colors.add(new JsonPrimitive(player.getColor().name()));
                }
                obj.add("colors", colors);

                // Add the first three cards of each deck to the response
                for (Map.Entry<String, JsonElement> entry : get3CardsFromEachDeck(clientHandler).entrySet()) {
                    obj.add(entry.getKey(), entry.getValue());
                }

                clHandler.sendMessage(obj.toString());
                System.out.println(obj.toString());
            } catch (JsonException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
