package IS24_LB11.network.events;

import IS24_LB11.game.Result;
import IS24_LB11.network.ClientHandler;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.googlecode.lanterna.terminal.ansi.TelnetTerminalServer;

import java.io.PrintWriter;


public class ServerEventHandler {

    private static final Gson gson = new Gson();
    private static ClientHandler clientHandler;
    private static PrintWriter outputToClient;

    // Method to handle incoming events
    public static void handleEvent(ClientHandler ch,PrintWriter out, String eventJson) {
        clientHandler = ch;
        outputToClient = out;

        JsonObject event = gson.fromJson(eventJson, JsonObject.class);

        String eventType = event.get("type").getAsString().toLowerCase();
        switch (eventType) {
            case "login":
                handleLoginEvent(event);
                break;
            case "quit":
                handleQuitEvent(event);
                break;
            case "message":
                handleMessageEvent(event);
                break;
            default:
                JsonObject error = new JsonObject();
                error.addProperty("error", "Unknown event");
                outputToClient.println(error);
                break;

        }
    }

    // Method to handle login event
    private static void handleLoginEvent(JsonObject event) {
        System.out.println("Login request received");

        String username = null;
        JsonObject response = new JsonObject();
        if(event != null && event.has("username"))
            username = event.get("username").getAsString();
        else {
            response.addProperty("error", "Wrong login request, username property missing");
            outputToClient.println(response);
        }

        if(clientHandler.getAllUsernames().contains(username)) {
            response.addProperty("error", "Wrong login request, username already in use");
            outputToClient.println(response);
        }

        clientHandler.setUserName(username);
        response.addProperty("value", "Welcome " + username);
        outputToClient.println(response);

    }

    // Method to handle message event
    private static void handleMessageEvent(JsonObject event) {
        System.out.println("Login request received");
    }


    // Method to handle quit event
    private static void handleQuitEvent(JsonObject event) {
        // TODO: quit logic here
    }
}
