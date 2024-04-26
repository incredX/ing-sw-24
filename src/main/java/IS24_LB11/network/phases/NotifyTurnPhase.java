package IS24_LB11.network.phases;

import IS24_LB11.game.components.GoalCard;
import IS24_LB11.game.components.GoldenCard;
import IS24_LB11.game.components.NormalCard;
import IS24_LB11.game.components.StarterCard;
import IS24_LB11.network.ClientHandler;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Map;

public class NotifyTurnPhase {

    static Gson gson = new Gson();

    public static void startPhase(ClientHandler clientHandler, ArrayList<GoalCard> pickedGoalCards, ArrayList<StarterCard> pickedStarterCards) {
        clientHandler.getGame().chooseGoalPhase(pickedGoalCards, pickedStarterCards);

        // notify first player turn
        JsonObject notification = new JsonObject();
        notification.addProperty("type", "notification");
        notification.addProperty("message", "It is your turn");
        clientHandler.getClientHandlerWithUsername(clientHandler.getGame().currentPlayer().name()).sendMessage(notification.toString());


        notification.addProperty("type", "turn");
        notification.addProperty("player", clientHandler.getGame().currentPlayer().name());

        // add first three cards of each deck to notification
        for (Map.Entry<String, JsonElement> entry : get3CardsFromEachDeck(clientHandler).entrySet()) {
            notification.add(entry.getKey(), entry.getValue());
        }

        clientHandler.getClientHandlerWithUsername(clientHandler.getGame().currentPlayer().name()).sendMessage(notification.toString());
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
