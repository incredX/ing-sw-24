package IS24_LB11.network.events;

import IS24_LB11.game.Player;
import IS24_LB11.game.components.*;
import IS24_LB11.game.utils.SyntaxException;
import IS24_LB11.network.ClientHandler;
import IS24_LB11.network.phases.GameInitPhase;
import IS24_LB11.network.phases.NotifyTurnPhase;
import IS24_LB11.network.phases.TurnPhase;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;


public class ServerEventHandler {

    private static final Gson gson = new Gson();
    private static ClientHandler clientHandler;

    private static ArrayList<GoalCard> pickedGoalCards = new ArrayList<>();
    private static ArrayList<StarterCard> pickedStarterCards = new ArrayList<>();


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
                handleNumOfPlayersEvent(event);
                break;
            case "setup":
                handleSetupEvent(event);
                break;
            case "turnactions":
                handleTurnActionsEvent(event);
                break;
            case "scoreboard":
                handleScoreboardEvent(event);
                break;
            default:
                JsonObject error = new JsonObject();
                error.addProperty("error", "Unknown event ("+eventType+")");
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

        if(clientHandler.getUserName() != null){
            response.addProperty("error", "You already logged in");
            return;
        }

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

        // tell client the playerName they chose
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

        // check if this is the last player, so the game can start
        if(clientHandler.getAllUsernames().size() > 1 &&
                clientHandler.getAllUsernames().size() == clientHandler.getMaxPlayers()) {
            response = new JsonObject();
            response.addProperty("type", "notification");
            response.addProperty("message", "Last player logged in successfully! GAME IS STARTING NOW");

            clientHandler.broadcast(response.toString());
            clientHandler.sendMessage(response.toString());

            // start game setup
            new Thread(() -> {
                GameInitPhase.startPhase(clientHandler, clientHandler.getGame(), clientHandler.getMaxPlayers());
            }).start();
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
        System.out.println(response);
        clientHandler.broadcast(response.toString());

        clientHandler.exit();
    }


    private static void handleSetupEvent(JsonObject event) {
        JsonObject response = new JsonObject();

        String checkEvent = hasProperties(event, "starterCard", "goalCard");

        if(checkEvent.equals("OK")){

            try {
                pickedGoalCards.add((GoalCard) CardFactory.newSerialCard(event.get("goalCard").getAsString()));
                pickedStarterCards.add((StarterCard) CardFactory.newSerialCard(event.get("starterCard").getAsString()));

                // start notify turn phase
                if (pickedGoalCards.size() == clientHandler.getMaxPlayers()) {
                    // choose goals for each player
                    clientHandler.getGame().chooseGoalPhase(pickedGoalCards, pickedStarterCards);

                    new Thread(() ->{
                        NotifyTurnPhase.startPhase(clientHandler);
                    }).start();
                }
            } catch (SyntaxException e) {
                throw new RuntimeException(e);
            }

        }
        else {
            response.addProperty("error", checkEvent);
            clientHandler.sendMessage(response.toString());
        }
    }


    private static void handleScoreboardEvent(JsonObject event){
        JsonObject response = new JsonObject();
        response.addProperty("type", "scoreboard");

        ArrayList<Player> players = (ArrayList<Player>) clientHandler.getGame().getPlayers().clone();
        players.sort((a,b)->Integer.compare(b.getScore(), a.getScore()));;

        String actualRank = "";
        for(Player player : players){
            actualRank = actualRank + player.name() + " " + player.getScore() + "\n";
        }

        response.addProperty("scoreboard", actualRank);
    }


    private static void handleTurnActionsEvent(JsonObject event){

        String hasProps = hasProperties(event, "placedCard", "deckType", "indexVisibleCards");

        if(hasProps.equals("OK")){
            // execute turn
            TurnPhase.startPhase(clientHandler, event);

        }
        else {
            JsonObject response = new JsonObject();
            response.addProperty("error", hasProps);
            clientHandler.sendMessage(response.toString());
        }
    }


    private static void handleNumOfPlayersEvent(JsonObject event) {
        JsonObject response = new JsonObject();
        String hasProps = hasProperties(event, "numOfPlayers");

        if(hasProps.equals("OK")) {
            if(event.get("numOfPlayers").getAsInt() >= 2 && event.get("numOfPlayers").getAsInt() <= 4) {
                clientHandler.setMaxPlayers(event.get("numOfPlayers").getAsInt());
                response.addProperty("type", "notification");
                response.addProperty("message", "Max number of players has been set to " + event.get("numOfPlayers").getAsInt());
            }
            else {
                response.addProperty("error", "Number of players has to be greater than 2 and lower than 4");
            }
        }
        else{
            response.addProperty("error", hasProps);
        }
        clientHandler.sendMessage(response.toString());
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
