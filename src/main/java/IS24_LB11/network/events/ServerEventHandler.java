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
        String messageEventSyntax = hasProperties(event, "username");

        if(messageEventSyntax.equals("OK"))
            username = event.get("username").getAsString();
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

        response.addProperty("message", "Welcome " + username);
        response.addProperty("username", clientHandler.getUserName());

        response.addProperty("type", "OK");

        clientHandler.sendMessage(response.toString());
        return;
    }

    private static void handleHeartBeatEvent(JsonObject event) {
        clientHandler.setLastHeartbeatTime(System.currentTimeMillis());
    }

    // Method to handle message event
    private static void handleMessageEvent(JsonObject event) {

        // checks Syntax of json event and returns message
        String messageEventSyntax = hasProperties(event, "message", "to", "from");

        System.out.println(event.toString());

        if(messageEventSyntax.equals("OK")) {
            event.addProperty("from", clientHandler.getUserName());

            System.out.println(event.get("to").getAsString());

            if(event.get("to").getAsString().equals("")){
                clientHandler.broadcast(event.toString());
            }
            else {
                ClientHandler destinationClientHandler = clientHandler.getClientHandlerWithUsername(event.get("to").getAsString());
                if(destinationClientHandler != null) {
                    if(destinationClientHandler.getUserName().equals(clientHandler.getUserName())){
                        JsonObject response = new JsonObject();
                        response.addProperty("error", "If you want to send a message to yourself try saying it out loudly :)");
                        clientHandler.sendMessage(response.toString());
                    }

                    else {
                        destinationClientHandler.sendMessage(event.toString());
                    }
                }
                else {
                    JsonObject response = new JsonObject();
                    response.addProperty("error", "Unknown username");
                    clientHandler.sendMessage(response.toString());
                }
            }
        }
        else {
            JsonObject response = new JsonObject();
            response.addProperty("error", messageEventSyntax);
            clientHandler.sendMessage(response.toString());
        }
    }


    // Method to handle quit event
    private static void handleQuitEvent(JsonObject event) {
        JsonObject response = new JsonObject();
        response.addProperty("type", "notification");
        response.addProperty("message", "Player " + clientHandler.getUserName() + " left the game");

        clientHandler.broadcast(response.toString());

        clientHandler.setConnectionClosed(true);

    }

    private static String hasProperties(JsonObject event, String... properties) {
        // Check if event object has all the specified properties
        for (String property : properties) {
            if (!event.has(property)) {
                return "Wrong request, properties missing";
            }
        }

        return "OK";
    }

}
