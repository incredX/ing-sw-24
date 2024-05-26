package IS24_LB11.network;

import IS24_LB11.game.Player;
import IS24_LB11.game.components.*;
import IS24_LB11.game.utils.SyntaxException;
import IS24_LB11.network.phases.GameInitPhase;
import IS24_LB11.network.phases.NotifyTurnPhase;
import IS24_LB11.network.phases.TurnPhase;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;

/**
 * The ServerEventHandler class handles various events received from clients.
 * This includes login, quit, message, heartbeat, setup, and turn actions events.
 */
public class ServerEventHandler {

    private static final Gson gson = new Gson();
    private static ClientHandler clientHandler;

    private static ArrayList<GoalCard> pickedGoalCards = new ArrayList<>();
    private static ArrayList<StarterCard> pickedStarterCards = new ArrayList<>();

    /**
     * Resets the state of picked goal cards and starter cards.
     */
    public static void reset() {
        pickedGoalCards.clear();
        pickedStarterCards.clear();
    }

    /**
     * Handles incoming events from clients.
     * @param ch the client handler managing the client
     * @param eventJson the JSON string representing the event
     */
    public static void handleEvent(ClientHandler ch, JsonObject event) {
        clientHandler = ch;

        String eventType = event.get("type").getAsString().toLowerCase();
        switch (eventType) {
            case "login":
                handleLoginEvent(event);
                break;
            case "quit":
                handleQuitEvent();
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
                error.addProperty("error", "Unknown event (" + eventType + ")");
                clientHandler.sendMessage(error.toString());
                break;
        }
    }

    /**
     * Handles login events.
     * @param event the JSON object representing the login event
     */
    private static void handleLoginEvent(JsonObject event) {
        System.out.println("Login request received");

        String username;
        JsonObject response = new JsonObject();
        String messageEventSyntax = hasProperties(event, "username");

        if (clientHandler.getUserName() != null) {
            response.addProperty("type", "notification");
            response.addProperty("message", "You already logged in");
            clientHandler.sendMessage(response.toString());
            return;
        }

        if (!messageEventSyntax.equals("OK")) {
            response.addProperty("type", "notification");
            response.addProperty("message", messageEventSyntax);
            clientHandler.sendMessage(response.toString());
            return;
        }

        username = event.get("username").getAsString();

        if (clientHandler.getAllUsernames().contains(username)) {
            response.addProperty("type", "notification");
            response.addProperty("message", "Username is already in use");
            clientHandler.sendMessage(response.toString());
            return;
        }

        clientHandler.setUserName(username);

        response.addProperty("type", "setUsername");
        response.addProperty("username", username);
        clientHandler.sendMessage(response.toString());

        response = new JsonObject();
        response.addProperty("type", "notification");
        response.addProperty("message", "Welcome " + username + "!");
        clientHandler.sendMessage(response.toString());

        if (clientHandler.getAllUsernames().size() == 1) {
            response = new JsonObject();
            response.addProperty("type", "notification");
            response.addProperty("message", "Please set max number of players");
            clientHandler.sendMessage(response.toString());
        }

        if (clientHandler.getAllUsernames().size() > 1 && clientHandler.getAllUsernames().size() == clientHandler.getMaxPlayers()) {
            response = new JsonObject();
            response.addProperty("type", "notification");
            response.addProperty("message", "Last player logged in successfully! GAME IS STARTING NOW");
            clientHandler.broadcast(response.toString());
            clientHandler.sendMessage(response.toString());

            new Thread(() -> GameInitPhase.startPhase(clientHandler, clientHandler.getGame(), clientHandler.getMaxPlayers())).start();
        }

        JsonObject newPlayerNotification = new JsonObject();
        newPlayerNotification.addProperty("type", "notification");
        newPlayerNotification.addProperty("message", "Player " + clientHandler.getUserName() + " connected");
        clientHandler.broadcast(newPlayerNotification.toString());
    }

    /**
     * Handles heartbeat events.
     * @param event the JSON object representing the heartbeat event
     */
    private static void handleHeartBeatEvent(JsonObject event) {
        clientHandler.setLastHeartbeatTime(System.currentTimeMillis());
    }

    /**
     * Handles message events.
     * @param event the JSON object representing the message event
     */
    private static void handleMessageEvent(JsonObject event) {
        String messageEventSyntax = hasProperties(event, "message", "to", "from");

        System.out.println(event.toString());

        if (messageEventSyntax.equals("OK")) {
            if (event.get("to").getAsString().isEmpty()) {
                clientHandler.broadcast(event.toString());
            } else {
                ClientHandler destinationClientHandler = clientHandler.getClientHandlerWithUsername(event.get("to").getAsString());

                if (destinationClientHandler != null) {
                    if (destinationClientHandler.getUserName().equals(clientHandler.getUserName())) {
                        JsonObject response = new JsonObject();
                        response.addProperty("error", "If you want to send a message to yourself try saying it out loudly :)");
                        clientHandler.sendMessage(response.toString());
                    } else {
                        destinationClientHandler.sendMessage(event.toString());
                    }
                } else {
                    JsonObject response = new JsonObject();
                    response.addProperty("error", "Unknown username");
                    clientHandler.sendMessage(response.toString());
                }
            }
        } else {
            JsonObject response = new JsonObject();
            response.addProperty("error", messageEventSyntax);
            clientHandler.sendMessage(response.toString());
        }
    }

    /**
     * Handles quit events.
     */
    private static void handleQuitEvent() {
        clientHandler.exit();
    }

    /**
     * Handles setup events.
     * @param event the JSON object representing the setup event
     */
    private static void handleSetupEvent(JsonObject event) {
        JsonObject response = new JsonObject();
        String checkEvent = hasProperties(event, "starterCard", "goalCard");

        if (checkEvent.equals("OK")) {
            try {
                pickedGoalCards.add((GoalCard) CardFactory.newSerialCard(event.get("goalCard").getAsString()));
                pickedStarterCards.add((StarterCard) CardFactory.newSerialCard(event.get("starterCard").getAsString()));

                if (pickedGoalCards.size() == clientHandler.getGame().getPlayers().size()) {
                    clientHandler.getGame().chooseGoalPhase(pickedGoalCards, pickedStarterCards);

                    new Thread(() -> NotifyTurnPhase.startPhase(clientHandler.getClientHandlerWithUsername(clientHandler.getGame().currentPlayer().name()))).start();
                }
            } catch (SyntaxException e) {
                throw new RuntimeException(e);
            }
        } else {
            response.addProperty("error", checkEvent);
            clientHandler.sendMessage(response.toString());
        }
    }

    /**
     * Handles scoreboard events.
     * @param event the JSON object representing the scoreboard event
     */
    private static void handleScoreboardEvent(JsonObject event) {
        JsonObject response = new JsonObject();
        response.addProperty("type", "scoreboard");

        ArrayList<Player> players = new ArrayList<>(clientHandler.getGame().getPlayers());
        players.sort((a, b) -> Integer.compare(b.getScore(), a.getScore()));

        StringBuilder actualRank = new StringBuilder();
        for (Player player : players) {
            actualRank.append(player.name()).append(" ").append(player.getScore()).append("\n");
        }

        response.addProperty("scoreboard", actualRank.toString());
        clientHandler.sendMessage(response.toString());
    }

    /**
     * Handles turn actions events.
     * @param event the JSON object representing the turn actions event
     */
    private static void handleTurnActionsEvent(JsonObject event) {
        String hasProps = hasProperties(event, "placedCard", "deckType", "indexVisibleCards");

        if (hasProps.equals("OK")) {
            TurnPhase.startPhase(clientHandler, event);
        } else {
            JsonObject response = new JsonObject();
            response.addProperty("error", hasProps);
            clientHandler.sendMessage(response.toString());
        }
    }

    /**
     * Handles setting the number of players event.
     * @param event the JSON object representing the number of players event
     */
    private static void handleNumOfPlayersEvent(JsonObject event) {
        JsonObject response = new JsonObject();
        String hasProps = hasProperties(event, "numOfPlayers");

        if (hasProps.equals("OK")) {
            int numOfPlayers = event.get("numOfPlayers").getAsInt();
            if (numOfPlayers >= 2 && numOfPlayers <= 4) {
                clientHandler.setMaxPlayers(numOfPlayers);
                response.addProperty("type", "notification");
                response.addProperty("message", "Max number of players has been set to " + numOfPlayers);
            } else {
                response.addProperty("error", "Number of players has to be greater than 2 and lower than 4");
            }
        } else {
            response.addProperty("error", hasProps);
        }
        clientHandler.sendMessage(response.toString());
    }

    /**
     * Checks if the given JSON object contains the specified properties.
     * @param event the JSON object to check
     * @param properties the properties to look for
     * @return "OK" if all properties are present, otherwise an error message
     */
    private static String hasProperties(JsonObject event, String... properties) {
        for (String property : properties) {
            if (!event.has(property)) {
                return "Wrong request, properties missing";
            }
        }
        return "OK";
    }

}
