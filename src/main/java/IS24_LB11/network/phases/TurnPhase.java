package IS24_LB11.network.phases;

import IS24_LB11.game.DeckException;
import IS24_LB11.game.PlacedCard;
import IS24_LB11.game.tools.JsonConverter;
import IS24_LB11.game.tools.JsonException;
import IS24_LB11.game.utils.SyntaxException;
import IS24_LB11.network.ClientHandler;
import com.google.gson.Gson;
import com.google.gson.JsonObject;


public class TurnPhase {
    static Gson gson = new Gson();

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


        NotifyTurnPhase.startPhase(clientHandler);
    }
}
