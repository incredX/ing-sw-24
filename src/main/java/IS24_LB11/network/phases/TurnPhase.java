package IS24_LB11.network.phases;

import IS24_LB11.game.DeckException;
import IS24_LB11.game.PlacedCard;
import IS24_LB11.game.tools.JsonConverter;
import IS24_LB11.game.tools.JsonException;
import IS24_LB11.game.utils.SyntaxException;
import IS24_LB11.network.ClientHandler;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * The TurnPhase class handles the turn phase of the game, executing a player's turn and notifying the next player.
 */
public class TurnPhase {
    static Gson gson = new Gson();

    /**
     * Starts the turn phase for the game.
     * This method processes the turn event, executes the turn for the current player, and sends a notification
     * with the status of the turn. After the turn is executed, it starts the notification phase for the next player's turn.
     *
     * @param clientHandler the client handler managing the clients
     * @param event the JSON object representing the turn event
     */
    public static void startPhase(ClientHandler clientHandler, JsonObject event) {
        JsonObject response = new JsonObject();
        Gson gson = new Gson();

        String status = null;
        try {
            PlacedCard placedCard = (PlacedCard) new JsonConverter().JSONToObject(event.get("placedCard").toString());

            status = clientHandler.getGame().executeTurn(
                    clientHandler.getUserName(),
                    placedCard.position(),
                    placedCard.card(),
                    event.get("deckType").getAsBoolean(),
                    event.get("indexVisibleCards").getAsInt()
            );
            System.out.println("Status: " + status + "    " + clientHandler.getUserName() + "\n\n");
        } catch (JsonException e) {
            throw new RuntimeException(e);
        } catch (DeckException e) {
            throw new RuntimeException(e);
        } catch (SyntaxException e) {
            throw new RuntimeException(e);
        }
        response.addProperty("type", "notification");
        response.addProperty("message", status);
        clientHandler.sendMessage(response.toString());

        NotifyTurnPhase.startPhase(clientHandler.getClientHandlerWithUsername(clientHandler.getGame().currentPlayer().name()));
    }
}
