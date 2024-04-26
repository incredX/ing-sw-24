package IS24_LB11.network.phases;

import IS24_LB11.game.DeckException;
import IS24_LB11.game.PlacedCard;
import IS24_LB11.game.Player;
import IS24_LB11.game.tools.JsonConverter;
import IS24_LB11.game.tools.JsonException;
import IS24_LB11.game.utils.SyntaxException;
import IS24_LB11.network.ClientHandler;
import com.google.gson.JsonObject;


public class TurnPhase {

    public static void startPhase(ClientHandler clientHandler, JsonObject event) {
        JsonObject response = new JsonObject();

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

        if(clientHandler.getGame().hasGameEnded()){
            JsonObject finalResponse = new JsonObject();
            finalResponse.addProperty("type", "finalRank");
            String finalRank = "";
            for(Player player : clientHandler.getGame().getFinalRanking()){
                finalRank = finalRank + player.name() + " " + player.getScore() + "\n";
            }
            finalResponse.addProperty("finalrank", finalRank);

        }
        else{
            response.addProperty("type", "notification");
            response.addProperty("message", "It is your turn");

            clientHandler.getClientHandlerWithUsername(clientHandler.getGame().currentPlayer().name())
                    .sendMessage(response.toString());

        }
    }

}
