package IS24_LB11.gui;

import IS24_LB11.game.PlacedCard;
import IS24_LB11.game.components.GoalCard;
import IS24_LB11.game.components.PlayableCard;
import IS24_LB11.game.components.StarterCard;
import com.google.gson.JsonObject;

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
    public void sendTurn(PlacedCard placedCard, PlayableCard cardChooseToDraw) {
        JsonObject message = new JsonObject();
        message.addProperty("type","turnActions");
        message.addProperty("placedCard","turnActions");
        message.addProperty("deckType","turnActions");
        message.addProperty("indexVisibleCards","turnActions");
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
