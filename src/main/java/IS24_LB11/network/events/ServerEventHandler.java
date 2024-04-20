package IS24_LB11.network.events;

import IS24_LB11.network.ClientHandler;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.PrintWriter;


public class ServerEventHandler {

    private static final Gson gson = new Gson();
    private static ClientHandler clientHandler;

    // Method to handle incoming events
    public static void handleEvent(ClientHandler ch, String eventJson) {
        clientHandler = ch;

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
            case "heartbeat":
                handleHeartBeatEvent(event);
                break;
            default:
                JsonObject error = new JsonObject();
                error.addProperty("error", "Unknown event");
                clientHandler.sendMessage(error.getAsString());
                break;

        }
    }

    // Method to handle login event
    private static void handleLoginEvent(JsonObject event) {
        System.out.println("Login request received");

        String username = null;
        JsonObject response = new JsonObject();

        // checks Syntax of json event and returns message
        String messageEventSyntax = hasPropertiesInData(event, "username");

        if(messageEventSyntax.equals("OK"))
            username = event.getAsJsonObject("data").get("username").getAsString();
        else {
            response.addProperty("error", messageEventSyntax);
            clientHandler.sendMessage(response.getAsString());
            return;
        }

        if(clientHandler.getAllUsernames().contains(username)) {
            response.addProperty("error", "Username is already in use");
            clientHandler.sendMessage(response.getAsString());
            return;
        }

        clientHandler.setUserName(username);

        JsonObject data = new JsonObject();
        data.addProperty("message", "Welcome " + username);
        data.addProperty("username", clientHandler.getUserName());

        response.addProperty("type", "OK");
        response.add("data", data);

        clientHandler.sendMessage(response.getAsString());
        return;
    }

    private static void handleHeartBeatEvent(JsonObject event) {
        clientHandler.setLastHeartbeatTime(System.currentTimeMillis());
    }

    // Method to handle message event
    private static void handleMessageEvent(JsonObject event) {

        // checks Syntax of json event and returns message
        String messageEventSyntax = hasPropertiesInData(event, "message", "to", "from");

        System.out.println(event.getAsString());

        if(messageEventSyntax.equals("OK")) {
            JsonObject data = event.getAsJsonObject("data");
            data.addProperty("from", clientHandler.getUserName());
            System.out.println(data.get("to").getAsString());
            if(data.get("to").getAsString().equals("")){
                clientHandler.broadcast(event.getAsString());
            }
            else {
                ClientHandler destinationClientHandler = clientHandler.getClientHandlerWithUsername(data.get("to").getAsString());
                if(destinationClientHandler != null) {
                    destinationClientHandler.sendMessage(event.getAsString());
                }
                else {
                    JsonObject response = new JsonObject();
                    response.addProperty("error", "Unknown username");
                    clientHandler.sendMessage(response.getAsString());
                }
            }
        }
    }


    // Method to handle quit event
    private static void handleQuitEvent(JsonObject event) {
        JsonObject response = new JsonObject();
        response.addProperty("type", "notification");
        JsonObject data = new JsonObject();
        data.addProperty("message", "Player " + clientHandler.getUserName() + " left the game");
        response.addProperty("data", data.getAsString());

        clientHandler.broadcast(response.getAsString());

        clientHandler.setConnectionClosed(true);

    }

    private static String hasPropertiesInData(JsonObject event, String... properties) {

        // Check if data object is null
        if (event == null || !event.has("data")) {
            return "Wrong request, property 'data' missing";
        }
        JsonObject data = event.getAsJsonObject("data");

        // Check if data object has all the specified properties
        for (String property : properties) {
            if (!data.has(property)) {
                return "Wrong request, properties missing";
            }
        }

        return "OK";
    }

}
