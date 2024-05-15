package IS24_LB11.gui;

import IS24_LB11.game.PlacedCard;
import IS24_LB11.game.components.GoalCard;
import IS24_LB11.game.components.PlayableCard;
import IS24_LB11.game.components.StarterCard;
import IS24_LB11.game.tools.JsonConverter;
import IS24_LB11.game.tools.JsonException;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class InputHandlerGUI {
    private Socket socket;
    private PrintWriter writer;

    public InputHandlerGUI(PrintWriter writer) throws IOException {
        this.writer = writer;
    }

    public void sendLogin(String username){
        JsonObject message = new JsonObject();

        message.addProperty("type", "login");
        message.addProperty("username", username);

        writer.println(message.toString());
        writer.flush();
    }

    public void sendMaxPlayers(int numOfPlayers) {
        JsonObject message = new JsonObject();
        message.addProperty("type","numOfPlayers");
        message.addProperty("numOfPlayers",numOfPlayers);

        writer.println(message.toString());
        writer.flush();
    }

    public void sendReady(GoalCard personalGoal, StarterCard starterCard) {
        JsonObject message = new JsonObject();
        message.addProperty("type","setup");
        message.addProperty("starterCard",starterCard.asString());
        message.addProperty("goalCard",personalGoal.asString());

        writer.println(message.toString());
        writer.flush();
    }

    public void sendTurn(PlacedCard placedCard, boolean deckType, int indexCardDeck) {
        JsonObject message = new JsonObject();
        message.addProperty("type","turnActions");
        try {
            JsonObject placedCardJson = (JsonObject) new JsonParser().parse(new JsonConverter().objectToJSON(placedCard));
            message.add("placedCard", placedCardJson);
        } catch (JsonException e) {
            throw new RuntimeException(e);
        }
        message.addProperty("deckType",deckType);
        message.addProperty("indexVisibleCards",indexCardDeck+1);

        writer.println(message.toString());
        writer.flush();
    }
    public void sendTurn(PlacedCard placedCard) {
        JsonObject message = new JsonObject();
        message.addProperty("type","turnActions");
        try {
            JsonObject placedCardJson = (JsonObject) new JsonParser().parse(new JsonConverter().objectToJSON(placedCard));
            message.add("placedCard", placedCardJson);
        } catch (JsonException e) {
            throw new RuntimeException(e);
        }
        message.addProperty("deckType",false);
        message.addProperty("indexVisibleCards",1);

        writer.println(message.toString());
        writer.flush();
    }

    public void sendMessage(String to, String from,String mex) {
        JsonObject message = new JsonObject();
        message.addProperty("type","message");
        message.addProperty("from",from);
        message.addProperty("to",to);
        message.addProperty("message",mex);
        writer.println(message.toString());
        writer.flush();
    }

    public void sendToAllMessage(String from,String mex){
        JsonObject message = new JsonObject();
        message.addProperty("type","message");
        message.addProperty("from",from);
        message.addProperty("to","");
        message.addProperty("message",mex);
        writer.println(message.toString());
        writer.flush();
    }
}
