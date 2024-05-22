package IS24_LB11.network.phases;

import IS24_LB11.game.Deck;
import IS24_LB11.game.DeckException;
import IS24_LB11.game.Player;
import IS24_LB11.network.ClientHandler;
import com.google.gson.*;

import java.util.Map;

/**
 * The NotifyTurnPhase class handles the notification phase of a player's turn in the game.
 */
public class NotifyTurnPhase {

    static Gson gson = new Gson();

    /**
     * Starts the notification phase for the player's turn.
     * This method sends notifications about the current player's turn and broadcasts the necessary game state information to all players.
     *
     * @param clientHandler the client handler managing the clients
     */
    public static void startPhase(ClientHandler clientHandler) {
        JsonObject response = new JsonObject();

        if (!clientHandler.getGame().hasGameEnded()) {
            // Send notification to the next player about their turn
            response.addProperty("type", "notification");
            if (clientHandler.getGame().getFinalTurn()) {
                response.addProperty("message", "It is your FINAL turn");
            } else {
                response.addProperty("message", "It is your turn");
            }
            clientHandler.sendMessage(response.toString());
            response.remove("message");
        }

        response.addProperty("type", "turn");
        if (clientHandler.getGame().hasGameEnded()) {
            response.addProperty("player", "");
            if(clientHandler.getGame().getPlayers().size() == 1) {
                response.addProperty("gameFinished", "");
            }
        } else {
            response.addProperty("player", clientHandler.getGame().currentPlayer().name());
        }

        // Add player respective scores
        JsonArray scores = new JsonArray();
        for (Player player : clientHandler.getGame().getPlayers()) {
            scores.add(new JsonPrimitive(player.getScore()));
        }
        response.add("scores", scores);

        // Add the first three cards of each deck to the response
        for (Map.Entry<String, JsonElement> entry : get3CardsFromEachDeck(clientHandler).entrySet()) {
            response.add(entry.getKey(), entry.getValue());
        }

        // Send current player's turn information to every player
        clientHandler.broadcast(response.toString());
        clientHandler.sendMessage(response.toString());
    }

    /**
     * Retrieves the first three cards from each deck and returns them as a JSON object.
     *
     * @param clientHandler the client handler managing the clients
     * @return a JSON object containing the first three cards from the normal and golden decks
     */
    public static JsonObject get3CardsFromEachDeck(ClientHandler clientHandler) {
        JsonObject obj = new JsonObject();

        // Add first 3 normal cards
        JsonArray normalCards = new JsonArray();
        JsonArray goldenCards = new JsonArray();
        Deck normalDeck = clientHandler.getGame().getNormalDeck();
        Deck goldenDeck = clientHandler.getGame().getGoldenDeck();

        for (int i = 1; i <= Math.min(3, normalDeck.size()); i++) {
            try {
                normalCards.add(new JsonPrimitive(normalDeck.showCard(i).asString()));
            } catch (DeckException e) {
                // Handle deck exception if necessary
            }
        }
        for (int i = 1; i <= Math.min(3, goldenDeck.size()); i++) {
            try {
                goldenCards.add(new JsonPrimitive(goldenDeck.showCard(i).asString()));
            } catch (DeckException e) {
                // Handle deck exception if necessary
            }
        }
        obj.add("normalDeck", normalCards);
        obj.add("goldenDeck", goldenCards);
        return obj;
    }
}
