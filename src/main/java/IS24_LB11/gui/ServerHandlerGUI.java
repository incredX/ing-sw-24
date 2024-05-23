package IS24_LB11.gui;

import IS24_LB11.game.PlayerSetup;
import IS24_LB11.game.components.*;
import IS24_LB11.game.tools.JsonConverter;
import IS24_LB11.game.tools.JsonException;
import IS24_LB11.game.utils.SyntaxException;
import IS24_LB11.gui.phases.ClientGUIState;
import IS24_LB11.gui.scenesControllers.GameSceneController;
import IS24_LB11.gui.scenesControllers.GenericSceneController;
import IS24_LB11.gui.scenesControllers.LoginSceneController;
import IS24_LB11.gui.scenesControllers.SetupSceneController;
import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonStreamParser;
import javafx.application.Platform;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Handles communication with the server, including parsing incoming JSON messages
 * and sending appropriate responses. Manages different GUI states based on the
 * game's progression.
 */
public class ServerHandlerGUI implements Runnable {

    private long lastHeartbeatTime = 0;
    private Socket socket;
    private JsonStreamParser parser;
    private PrintWriter writer;
    private ClientGUIState actualState;
    private boolean running = true;
    private boolean gameTurnStateStarted = false;
    private LoginSceneController loginSceneController;
    private SetupSceneController setupSceneController = null;
    private GameSceneController gameSceneController = null;
    private GenericSceneController activeController = null;

    /**
     * Constructs a ServerHandlerGUI object to manage communication with the server.
     *
     * @param clientGUIState the current state of the client GUI
     * @param serverIP       the IP address of the server
     * @param serverPORT     the port number of the server
     * @throws IOException if an I/O error occurs when creating the socket
     */
    public ServerHandlerGUI(ClientGUIState clientGUIState, String serverIP, int serverPORT) throws IOException {
        socket = new Socket(serverIP, serverPORT);
        this.actualState = clientGUIState;
        parser = new JsonStreamParser(new InputStreamReader(socket.getInputStream()));
        writer = new PrintWriter(socket.getOutputStream());
        loginSceneController = null;
    }

    /**
     * Continuously listens for messages from the server and processes them accordingly.
     * Heartbeat check from the server.
     */
    public void run() {
        while (running) {
            if (socket.isClosed()) break;
            try {
                while (!parser.hasNext()) {
                    if (System.currentTimeMillis() - lastHeartbeatTime > 3000 && lastHeartbeatTime != 0) {
                        Platform.runLater(() -> activeController.showPopUpRestartGame());
                        running = false;
                        break;
                    }
                }
                if (parser.hasNext())
                    processEvent(parser.next().getAsJsonObject());
            } catch (JsonIOException e) {
                // Handle JsonIOException
            }
        }
        Thread.currentThread().interrupt();
    }

    /**
     * Processes a JSON event from the server.
     *
     * @param serverEvent the JSON object representing the server event
     */
    private void processEvent(JsonObject serverEvent) {
        if (serverEvent.has("type") && !(serverEvent.get("type").getAsString().equals("heartbeat")))
            System.out.println(serverEvent);
        if (serverEvent.has("error"))
            handleErrorEvent(serverEvent);
        if (serverEvent.has("type")) {
            switch (serverEvent.get("type").getAsString().toLowerCase()) {
                case "setusername":
                    handleLoginEvent(serverEvent);
                    return;
                case "notification":
                    handleNotificationEvent(serverEvent);
                    return;
                case "heartbeat":
                    heartBeatEvent(serverEvent);
                    return;
                case "setup":
                    handleSetupEvent(serverEvent);
                    return;
                case "disconnected":
                    handleDisconnectedEvent(serverEvent);
                    return;
                case "turn":
                    handleTurnEvent(serverEvent);
                    return;
                case "message":
                    handleMessageEvent(serverEvent);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + serverEvent.get("type"));
            }
        }
    }

    /**
     * Handles an error event from the server.
     *
     * @param serverEvent the JSON object representing the error event
     */
    private void handleErrorEvent(JsonObject serverEvent) {
        if (serverEvent.get("error").getAsString().equals("Server full, try again later.")) {
            Platform.runLater(() -> {
                loginSceneController.resetServerHandler();
                try {
                    loginSceneController.restart();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            shutdown();
        } else {
            String msg = serverEvent.get("error").getAsString();
            addMessage(msg);
        }
    }

    /**
     * Handles a message event from the server.
     *
     * @param serverEvent the JSON object representing the message event
     */
    private void handleMessageEvent(JsonObject serverEvent) {
        if (serverEvent.has("to") && serverEvent.has("from") && serverEvent.has("message")) {
            String msg = "<" + serverEvent.get("from").getAsString() + "> " + serverEvent.get("message").getAsString();
            addMessage(msg);
        }
    }

    /**
     * Handles a disconnected event from the server.
     *
     * @param serverEvent the JSON object representing the disconnected event
     */
    private void handleDisconnectedEvent(JsonObject serverEvent) {
        String playerDisconnected = serverEvent.get("player").getAsString();
        //TODO: check why in login message doesn't work
        if (gameSceneController == (null)) {
            if (setupSceneController != null)
                Platform.runLater(() -> setupSceneController.removePlayer(playerDisconnected));
        }
        else {
            Platform.runLater(() -> gameSceneController.removePlayer(playerDisconnected));
        }
    }

    /**
     * Handles a turn event from the server.
     *
     * @param serverEvent the JSON object representing the turn event
     */
    private void handleTurnEvent(JsonObject serverEvent) {
        String currentPlayerTurn = serverEvent.get("player").getAsString();
        JsonArray playersScores = serverEvent.get("scores").getAsJsonArray();
        ArrayList<Integer> playerScores = extractIntegerArray(playersScores);

        if (currentPlayerTurn.equals("")) {
            if (serverEvent.has("gameFinished")) {
                Platform.runLater(() -> {
                    try {

                        gameSceneController.updateGame(playerScores);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                });
                Platform.runLater(() -> gameSceneController.showPopUpNotification("The game is finished, check scoreboard for the winner"));
                Platform.runLater(() -> gameSceneController.disableAllCardInputs(true));
            } else {
                Platform.runLater(() -> activeController.showExitNotification("You are the only player connected to the server"));
            }
        } else {
            JsonArray normalDeckString = serverEvent.get("normalDeck").getAsJsonArray();
            JsonArray goldenDeckString = serverEvent.get("goldenDeck").getAsJsonArray();
            ArrayList<PlayableCard> normalDeck = (ArrayList<PlayableCard>) extractCardArray(normalDeckString);
            ArrayList<PlayableCard> goldenDeck = (ArrayList<PlayableCard>) extractCardArray(goldenDeckString);

            if (!gameTurnStateStarted) {
                gameTurnStateStarted = true;
                Platform.runLater(() -> setupSceneController.changeToGameState());
            }
            Platform.runLater(()-> {
                try {
                    gameSceneController.updateGame(currentPlayerTurn,playerScores,normalDeck,goldenDeck);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    /**
     * Handles a login event from the server.
     *
     * @param serverEvent the JSON object representing the login event
     */
    private void handleLoginEvent(JsonObject serverEvent) {
        if (serverEvent.has("username")) {
            actualState.setUsername(serverEvent.get("username").getAsString());
        }
    }

    /**
     * Handles a notification event from the server.
     *
     * @param serverEvent the JSON object representing the notification event
     */
    private void handleNotificationEvent(JsonObject serverEvent) {
        if (serverEvent.has("message")) {
            String message = serverEvent.get("message").getAsString();
            if (message.equals("Welcome " + actualState.getUsername() + "!")) {
                addMessage("<Server> " + message);
                Platform.runLater(() -> loginSceneController.chatDisplay());
                Platform.runLater(() -> loginSceneController.disableLogin());
            } else if (message.equals("Welcome, please log in")) {
                return;
            } else if (message.equals("Please set max number of players")) {
                Platform.runLater(() -> loginSceneController.setPlayers());
            } else {
                addMessage("<Server> " + message);
                if (gameSceneController == null) {
                    if (setupSceneController != null)
                        Platform.runLater(() -> setupSceneController.chatDisplay());
                } else {
                    Platform.runLater(() -> gameSceneController.chatDisplay());
                    if (message.equals("It is your FINAL turn")) {
                        Platform.runLater(() -> gameSceneController.showPopUpNotification("It is your FINAL turn"));
                        Platform.runLater(() -> gameSceneController.setFinalTurn());
                    }
                }
            }
        }
    }

    /**
     * Adds a message to the chat box.
     *
     * @param msg the message to add
     */
    private void addMessage(String msg) {
        Platform.runLater(() -> activeController.addMessage(msg));
    }

    /**
     * Handles a heartbeat event from the server to ensure the connection is alive.
     *
     * @param serverEvent the JSON object representing the heartbeat event
     */
    private void heartBeatEvent(JsonObject serverEvent) {
        JsonObject message = new JsonObject();
        message.addProperty("type", "heartbeat");
        write(message);
        lastHeartbeatTime = System.currentTimeMillis();
    }

    /**
     * Handles a setup event from the server.
     *
     * @param serverEvent the JSON object representing the setup event
     */
    private void handleSetupEvent(JsonObject serverEvent) {
        try {
            PlayerSetup playerSetup = (PlayerSetup) new JsonConverter().JSONToObject(serverEvent.get("setup").toString());

            JsonArray publicGoalsString = serverEvent.get("publicGoals").getAsJsonArray();
            JsonArray normalDeckString = serverEvent.get("normalDeck").getAsJsonArray();
            JsonArray goldenDeckString = serverEvent.get("goldenDeck").getAsJsonArray();
            JsonArray playerNamesString = serverEvent.get("playerNames").getAsJsonArray();

            ArrayList<GoalCard> publicGoals = (ArrayList<GoalCard>) extractGoalArray(publicGoalsString);
            ArrayList<PlayableCard> normalDeck = (ArrayList<PlayableCard>) extractCardArray(normalDeckString);
            ArrayList<PlayableCard> goldenDeck = (ArrayList<PlayableCard>) extractCardArray(goldenDeckString);
            ArrayList<String> playerNames = extractStringArray(playerNamesString);

            Platform.runLater(() -> loginSceneController.changeToSetupState(playerSetup, publicGoals, normalDeck, goldenDeck, playerNames));
        } catch (JsonException | SyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sends a JSON object to the server.
     *
     * @param object the JSON object to send
     */
    public void write(JsonObject object) {
        writer.println(object.toString());
        writer.flush();
    }

    /**
     * Shuts down the server handler, closing the socket and stopping the thread.
     */
    public void shutdown() {
        this.running = false;
        this.writer.close();
        try {
            if (!socket.isClosed())
                socket.close();
        } catch (IOException e) {
            System.out.println(e);
        }
        Thread.currentThread().interrupt();
    }

    /**
     * Extracts an array of PlayableCard objects from a JSON array.
     *
     * @param jsonArray the JSON array to extract from
     * @return an ArrayList of PlayableCard objects
     */
    public ArrayList<PlayableCard> extractCardArray(JsonArray jsonArray) {
        ArrayList<PlayableCard> cards = new ArrayList<>();
        for (int i = 0; i < jsonArray.size(); i++) {
            try {
                cards.add(CardFactory.newPlayableCard(jsonArray.get(i).getAsString()));
            } catch (SyntaxException e) {
                throw new RuntimeException(e);
            }
        }
        return cards;
    }

    /**
     * Extracts an array of GoalCard objects from a JSON array.
     *
     * @param jsonArray the JSON array to extract from
     * @return an ArrayList of GoalCard objects
     */
    public ArrayList<GoalCard> extractGoalArray(JsonArray jsonArray) {
        ArrayList<GoalCard> cards = new ArrayList<>();
        for (int i = 0; i < jsonArray.size(); i++) {
            try {
                cards.add((GoalCard) CardFactory.newSerialCard(jsonArray.get(i).getAsString()));
            } catch (SyntaxException e) {
                throw new RuntimeException(e);
            }
        }
        return cards;
    }

    /**
     * Extracts an array of strings from a JSON array.
     *
     * @param jsonArray the JSON array to extract from
     * @return an ArrayList of strings
     */
    public ArrayList<String> extractStringArray(JsonArray jsonArray) {
        ArrayList<String> strings = new ArrayList<>();
        for (int i = 0; i < jsonArray.size(); i++)
            strings.add(jsonArray.get(i).getAsString());
        return strings;
    }

    /**
     * Extracts an array of integers from a JSON array.
     *
     * @param jsonArray the JSON array to extract from
     * @return an ArrayList of integers
     */
    public ArrayList<Integer> extractIntegerArray(JsonArray jsonArray) {
        ArrayList<Integer> integers = new ArrayList<>();
        for (int i = 0; i < jsonArray.size(); i++)
            integers.add(jsonArray.get(i).getAsInt());
        return integers;
    }

    public PrintWriter getWriter() {
        return writer;
    }

    public void setLoginSceneController(LoginSceneController loginSceneController) {
        this.loginSceneController = loginSceneController;
        this.activeController = loginSceneController;
    }
    public void setSetupSceneController(SetupSceneController setupSceneController) {
        this.setupSceneController = setupSceneController;
        this.activeController = setupSceneController;
    }

    public void setGameSceneController(GameSceneController gameSceneController) {
        this.gameSceneController = gameSceneController;
        this.activeController = gameSceneController;
    }
}
