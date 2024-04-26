package IS24_LB11.network.phases;

import IS24_LB11.game.Player;
import IS24_LB11.game.components.GoldenCard;
import IS24_LB11.game.components.NormalCard;
import IS24_LB11.network.ClientHandler;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
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
        ArrayList<String> scores = new ArrayList<>();
        for(Player player : clientHandler.getGame().getPlayers()) {
            scores.add(String.valueOf(player.getScore()));
        }
        response.add("scores", gson.toJsonTree(scores));


        // add first three cards of each deck to response
        for (Map.Entry<String, JsonElement> entry : get3CardsFromEachDeck(clientHandler).entrySet()) {
            response.add(entry.getKey(), entry.getValue());
        }
        //send current player turn to every player
        clientHandler.broadcast(response.toString());
        clientHandler.sendMessage(response.toString());
    }

    private static JsonObject get3CardsFromEachDeck(ClientHandler clientHandler) {
        JsonObject obj = new JsonObject();

        //add first 3 normal cards
        ArrayList<String> cards = new ArrayList<>();
        cards.add(((NormalCard) clientHandler.getGame().getNormalDeck().getCards().get(0)).asString());
        cards.add(((NormalCard) clientHandler.getGame().getNormalDeck().getCards().get(1)).asString());
        cards.add(((NormalCard) clientHandler.getGame().getNormalDeck().getCards().get(2)).asString());
        obj.addProperty("normalDeck", gson.toJson(cards));

        // add first 3 gold cards
        cards.clear();
        cards.add(((GoldenCard) clientHandler.getGame().getGoldenDeck().getCards().get(0)).asString());
        cards.add(((GoldenCard) clientHandler.getGame().getGoldenDeck().getCards().get(1)).asString());
        cards.add(((GoldenCard) clientHandler.getGame().getGoldenDeck().getCards().get(2)).asString());
        obj.addProperty("goldDeck", gson.toJson(cards));

        return obj;
    }
}
