package IS24_LB11.network.events;

import IS24_LB11.game.DeckException;
import IS24_LB11.game.PlacedCard;
import IS24_LB11.game.Player;
import IS24_LB11.game.components.CardFactory;
import IS24_LB11.game.components.CardInterface;
import IS24_LB11.game.components.GoalCard;
import IS24_LB11.game.components.StarterCard;
import IS24_LB11.game.tools.JsonConverter;
import IS24_LB11.game.tools.JsonException;
import IS24_LB11.game.utils.SyntaxException;
import IS24_LB11.network.ClientHandler;
import IS24_LB11.network.phases.GameSetupPhase;
import IS24_LB11.network.phases.PickPhase;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.PrintWriter;
import java.util.ArrayList;


public class ServerEventHandler {

    private static final Gson gson = new Gson();
    private static ClientHandler clientHandler;

    private static ArrayList<StarterCard> pickedStarterCard = null;
    private static ArrayList<GoalCard> pickedGoalCard = null;


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
            case "placedCard":
                handlePlacedCardEvent(event);
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

        // tell client the name they chose
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
                GameSetupPhase.startPhase(clientHandler, clientHandler.getGame(), clientHandler.getMaxPlayers());
            });


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

        clientHandler.exit();
    }

    private static void handleSetupEvent(JsonObject event) {
        JsonObject response = new JsonObject();

        String checkEvent = hasProperties(event, "starterCard", "goalCard");

        if(checkEvent.equals("OK")){

            try {
                pickedStarterCard.add((StarterCard) CardFactory.newSerialCard(event.get("starterCard").getAsString()));
                pickedGoalCard.add((GoalCard) CardFactory.newSerialCard(event.get("goalCard").getAsString()));

                // start turn phase
                if (pickedGoalCard.size() == clientHandler.getMaxPlayers()) {
                    new Thread(() ->{
                        clientHandler.getGame().chooseGoalPhase(pickedGoalCard, pickedStarterCard);

                        // notify first player turn
                        JsonObject notification = new JsonObject();
                        notification.addProperty("type", "notification");
                        notification.addProperty("message", "It is your turn");
                        clientHandler.getClientHandlers().getFirst().sendMessage(notification.toString());
                    });
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

    private static void handlePlacedCardEvent(JsonObject event){
        JsonObject response = new JsonObject();

        String hasProps = hasProperties(event, "placedCard", "deckType", "indexVisibleCards");

        if(hasProps.equals("OK")){
            try {
                // execute turn
                PlacedCard placedCard = (PlacedCard) new JsonConverter().JSONToObject(event.get("placedCard").toString());
                String status = clientHandler.getGame().executeTurn(
                                clientHandler.getUserName(),
                                placedCard.position(),
                                placedCard.card(),
                                event.get("deckType").getAsBoolean(),
                                event.get("indexVisibleCards").getAsInt()
                                );
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
            } catch (JsonException e) {
                throw new RuntimeException(e);
            } catch (SyntaxException e) {
                throw new RuntimeException(e);
            } catch (DeckException e) {
                throw new RuntimeException(e);
            }

        }
        else {
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
                response.addProperty("message", "Max number of players haas been set to " + event.get("numOfPlayers").getAsInt());
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
