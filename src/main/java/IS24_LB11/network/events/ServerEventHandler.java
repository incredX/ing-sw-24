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



        if(event != null && event.has("data") &&
                event.getAsJsonObject("data").has("username"))

            username = event.getAsJsonObject("data").get("username").getAsString();
        else {
            response.addProperty("error", "Wrong login request, data or username property missing");
            outputToClient.println(response);
            return;
        }

        if(clientHandler.getAllUsernames().contains(username)) {
            response.addProperty("error", "Wrong login request, username already in use");
            outputToClient.println(response);
            return;
        }

        clientHandler.setUserName(username);

        response.addProperty("value", "Welcome " + username);
        outputToClient.println(response);
        return;
    }

    // Method to handle message event
    private static void handleMessageEvent(JsonObject event) {

        JsonObject response = new JsonObject();
        if(event != null && event.has("data") &&
                event.getAsJsonObject("data").has("message"))


        System.out.println("Login request received");
    }


    // Method to handle quit event
    private static void handleQuitEvent(JsonObject event) {
        // TODO: quit logic here
    }

    private static String hasPropertiesInData(JsonObject event, String... properties) {

        // Check if data object is null
        if (event == null || !event.has("data")) {
            return "Wrong request, data is not inside";
        }
        JsonObject data = event.getAsJsonObject("data");

        // Check if data object has all the specified properties
        for (String property : properties) {
            if (!data.has(property)) {
                return "Wrong request, properties missing missing";
            }
        }

        return "OK";
    }

}
