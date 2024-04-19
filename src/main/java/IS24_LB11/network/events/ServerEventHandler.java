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

    // Method to handle incoming events
    public static JsonObject handleEvent(ClientHandler ch, String eventJson) {
        clientHandler = ch;

        JsonObject event = gson.fromJson(eventJson, JsonObject.class);

        String eventType = event.get("type").getAsString();
        switch (eventType) {
            case "login":
                return handleLoginEvent(event);
            case "quit":
                return handleQuitEvent(event);
            case "message":
                return handleMessageEvent(event);
            default:
                JsonObject error = new JsonObject();
                error.addProperty("error", "Unknown event");
                return error;

        }
    }

    // Method to handle login event
    private static JsonObject handleLoginEvent(JsonObject event) {
        System.out.println("Login request received");

        String username = null;
        JsonObject response = new JsonObject();
        if(event != null && event.has("username"))
            username = event.get("username").getAsString();
        else {
            response.addProperty("error", "Wrong login request, username property missing");
            return response;
        }

        if(clientHandler.getAllUsernames().contains(username)) {
            response.addProperty("error", "Wrong login request, username already in use");
            return response;
        }

        clientHandler.setUserName(username);
        response.addProperty("value", "Welcome " + username);
        return response;

    }

    // Method to handle message event
    private static JsonObject handleMessageEvent(JsonObject event) {

        // TODO: message logic here
        return null;
    }


    // Method to handle quit event
    private static JsonObject handleQuitEvent(JsonObject event) {
        // TODO: quit logic here

        return null;

    }
}
