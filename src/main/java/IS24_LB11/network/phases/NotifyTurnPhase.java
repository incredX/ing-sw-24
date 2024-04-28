package IS24_LB11.network.phases;

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
        response.addProperty("message", "It is your turn");
        clientHandler.getClientHandlerWithUsername(clientHandler.getGame().currentPlayer().name())
                .sendMessage(response.toString());
        response.remove("message");


        response.addProperty("type", "turn");
        if(clientHandler.getGame().hasGameEnded())
            response.addProperty("player", "");
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
        System.out.println(response);
        //send current player turn to every player
        clientHandler.broadcast(response.toString());
        clientHandler.sendMessage(response.toString());
    }

    private static JsonObject get3CardsFromEachDeck(ClientHandler clientHandler) {
        JsonObject obj = new JsonObject();

        //add first 3 normal cards
        JsonArray normalCards = new JsonArray();
        for (int i=0; i<3; i++)
            normalCards.add(new JsonPrimitive(clientHandler.getGame().getNormalDeck().getCards().get(i).asString()));
        obj.add("normalDeck", normalCards);

        // add first 3 gold cards
        JsonArray goldenCards = new JsonArray();
        for (int i=0; i<3; i++)
            goldenCards.add(new JsonPrimitive(clientHandler.getGame().getGoldenDeck().getCards().get(i).asString()));
        obj.add("goldenDeck", goldenCards);

        return obj;
    }
}
