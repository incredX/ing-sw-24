package IS24_LB11.network.phases;

import IS24_LB11.game.Deck;
import IS24_LB11.game.DeckException;
import IS24_LB11.game.Player;
import IS24_LB11.network.ClientHandler;
import com.google.gson.*;

import java.util.Map;

public class NotifyTurnPhase {

    static Gson gson = new Gson();

    public static void startPhase(ClientHandler clientHandler) {
        JsonObject response = new JsonObject();

        // send notification to next player turn
        response.addProperty("type", "notification");
        if (clientHandler.getGame().getFinalTurn()) {
            response.addProperty("message", "It is your FINAL turn");
        } else {
            response.addProperty("message", "It is your turn");
        }
        clientHandler.sendMessage(response.toString());
        response.remove("message");


        response.addProperty("type", "turn");
        if(clientHandler.getGame().hasGameEnded()){
            response.addProperty("player", "");
            if(clientHandler.getGame().getPlayers().size()>1){
                response.addProperty("gameFinished", "");
            }
        }
            else
            response.addProperty("player", clientHandler.getGame().currentPlayer().name());

        //add player respective scores
        JsonArray scores = new JsonArray();
        for(Player player : clientHandler.getGame().getPlayers()) {
            scores.add(new JsonPrimitive(player.getScore()));
        }
        response.add("scores", scores);


        // add first three cards of each deck to response
        for (Map.Entry<String, JsonElement> entry : get3CardsFromEachDeck(clientHandler).entrySet()) {
            response.add(entry.getKey(), entry.getValue());
        }

        //send current player turn to every player
        clientHandler.broadcast(response.toString());
        clientHandler.sendMessage(response.toString());
    }

    public static JsonObject get3CardsFromEachDeck(ClientHandler clientHandler) {
        JsonObject obj = new JsonObject();

        //add first 3 normal cards
        JsonArray normalCards = new JsonArray();
        JsonArray goldenCards = new JsonArray();
        Deck normalDeck = clientHandler.getGame().getNormalDeck();
        Deck goldenDeck = clientHandler.getGame().getGoldenDeck();

        for (int i=1; i<=Integer.min(3, normalDeck.size()); i++) {
            try { normalCards.add(new JsonPrimitive(normalDeck.showCard(i).asString())); }
            catch (DeckException e) { }

        }
        for (int i=1; i<=Integer.min(3, goldenDeck.size()); i++) {
            try { goldenCards.add(new JsonPrimitive(goldenDeck.showCard(i).asString())); }
            catch (DeckException e) { }
        }
        obj.add("normalDeck", normalCards);
        obj.add("goldenDeck", goldenCards);
        return obj;
    }
}
