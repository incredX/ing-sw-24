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
            case "numofplayers":
                handleNumOfPlayers(event);
                break;
            default:
                JsonObject error = new JsonObject();
                error.addProperty("error", "Unknown event");
                clientHandler.sendMessage(error.toString());
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

        if(!messageEventSyntax.equals("OK")) {
            response.addProperty("error", messageEventSyntax);
            clientHandler.sendMessage(response.getAsString());
            return;
        }
        if(clientHandler.getAllUsernames().contains(username)) {
            response.addProperty("error", "Username is already in use");
            clientHandler.sendMessage(response.getAsString());
            return;
        }

        username = event.get("username").getAsString();

        clientHandler.setUserName(username);

        // tell client what name they chose
        response.addProperty("type", "setUsername");
        response.addProperty("username", username);
        clientHandler.sendMessage(response.toString());

        // send welcome message to client
        response = new JsonObject();
        response.addProperty("type", "notification");
        response.addProperty("message", "Welcome " + username + "!");
        clientHandler.sendMessage(response.toString());

        // notify first client to set max number of players
        if(clientHandler.getAllUsernames().size() == 1) {
            response = new JsonObject();
            response.addProperty("type", "notification");
            response.addProperty("message", "Please set max number of players");
            clientHandler.sendMessage(response.toString());
        }
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

    private static void handleNumOfPlayers(JsonObject event) {
        JsonObject response = new JsonObject();
        String hasProps = hasProperties(event, "numOfPlayers");

        if(hasProps.equals("OK")) {
            if(event.get("numOfPlayers").getAsInt() >= 2 && event.get("numOfPlayers").getAsInt() <= 4) {
                clientHandler.setMaxPlayers(event.get("numOfPlayers").getAsInt());
                response.addProperty("type", "notification");
                response.addProperty("message", "Max number of players haas been set to " + event.get("numOfPlayers").getAsInt());
            }
            else {
                response.addProperty("error", "Number of players has to be greater than 2 and lower than 4");
            }
        }
        else{
            response.addProperty("error", hasProps);
        }
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
