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

public class ServerHandlerGUI implements Runnable{
    private long lastHeartbeatTime=0;
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

    public ServerHandlerGUI(ClientGUIState clientGUIState, String serverIP, int serverPORT) throws IOException {
        socket = new Socket(serverIP, serverPORT);
        this.actualState = clientGUIState;
        parser = new JsonStreamParser(new InputStreamReader(socket.getInputStream()));
        writer = new PrintWriter(socket.getOutputStream());
        loginSceneController=null;
    }

    public void run() {
        while (running) {
            if (socket.isClosed()) break;
            try {
                while (!parser.hasNext()) {
                    if (System.currentTimeMillis()-lastHeartbeatTime > 3000 && lastHeartbeatTime != 0) {
                        Platform.runLater(() -> activeController.showPopUpRestartGame());
                        running=false;
                        break;
                    }
                }
                if (parser.hasNext())
                    processEvent(parser.next().getAsJsonObject());
            } catch (JsonIOException e) {
            }
        }
        Thread.currentThread().interrupt();
    }

    private void processEvent(JsonObject serverEvent) {
        if (serverEvent.has("type") && !(serverEvent.get("type").getAsString().equals("heartbeat")))
            System.out.println(serverEvent);
        if (serverEvent.has("error"))
            handleErrorEvent(serverEvent);
        if (serverEvent.has("type")) {
            switch (serverEvent.get("type").getAsString().toLowerCase()){
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
                default: throw new IllegalStateException("Unexpected value: " + serverEvent.get("type"));
            }
        }

    }

    private void handleErrorEvent(JsonObject serverEvent) {
        if (serverEvent.get("error").getAsString().equals("Server full, try again later.")){
                Platform.runLater(()-> {
                    loginSceneController.resetServerHandler();
                    try {
                        loginSceneController.restart();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
            shutdown();
        }
        else {
            String msg = serverEvent.get("error").getAsString();
            addMessage(msg);
        }
    }

    private void handleMessageEvent(JsonObject serverEvent) {
        if (serverEvent.has("to") && serverEvent.has("from") && serverEvent.has("message")){
            String msg="<" + serverEvent.get("from").getAsString() + "> " + serverEvent.get("message").getAsString();
            addMessage(msg);

        }
    }

    private void handleDisconnectedEvent(JsonObject serverEvent) {
        String playerDisconnected = serverEvent.get("player").getAsString();
        if (gameSceneController == (null))
            Platform.runLater(()->setupSceneController.removePlayer(playerDisconnected));
        else {
            Platform.runLater(() -> gameSceneController.removePlayer(playerDisconnected));
        }
    }

    private void handleTurnEvent(JsonObject serverEvent) {
        String currentPlayerTurn = serverEvent.get("player").getAsString();
        JsonArray playersScores = serverEvent.get("scores").getAsJsonArray();
        ArrayList<Integer> playerScores = extractIntegerArray(playersScores);

        if (currentPlayerTurn.equals("")) {
            if (serverEvent.has("gameFinished")) {
                Platform.runLater(() -> gameSceneController.updateGame(playerScores));
                Platform.runLater(() -> gameSceneController.showPopUpNotification("The game is finished, check scoreboard for the winner"));
                Platform.runLater(() -> gameSceneController.disableAllCardInputs(true));
            } else {
                Platform.runLater(() -> activeController.showExitNotification("You are the only player connected to the server"));
//                if (gameSceneController == null) {
//                    Platform.runLater(() -> setupSceneController.showExitNotification("You are the only player connected to the server"));
//                    return;
//                }
//                Platform.runLater(() -> gameSceneController.showExitNotification("You are the only player connected to the server"));
            }
        }
        else{

            JsonArray normalDeckString = serverEvent.get("normalDeck").getAsJsonArray();
            JsonArray goldenDeckString = serverEvent.get("goldenDeck").getAsJsonArray();
            ArrayList<PlayableCard> normalDeck = (ArrayList<PlayableCard>) extractCardArray(normalDeckString);
            ArrayList<PlayableCard> goldenDeck = (ArrayList<PlayableCard>) extractCardArray(goldenDeckString);

            if (!gameTurnStateStarted){
                gameTurnStateStarted=true;
                Platform.runLater(()->setupSceneController.changeToGameState());
            }
            Platform.runLater(()->gameSceneController.updateGame(currentPlayerTurn,playerScores,normalDeck,goldenDeck));
        }


    }

    private void handleLoginEvent(JsonObject serverEvent){
        if (serverEvent.has("username")){
            actualState.setUsername(serverEvent.get("username").getAsString());
        }
    }
    private void handleNotificationEvent(JsonObject serverEvent){
        if (serverEvent.has("message")){
            if(serverEvent.get("message").getAsString().equals("Welcome " + actualState.getUsername() + "!")){
                addMessage("<Server> " + serverEvent.get("message").getAsString());
                Platform.runLater(() -> loginSceneController.chatDisplay());
                Platform.runLater(()-> loginSceneController.disableLogin());
            }
            else if (serverEvent.get("message").getAsString().equals("Welcome, please log in"))
                return;
            else if (serverEvent.get("message").getAsString().equals("Please set max number of players")){
                Platform.runLater(() -> loginSceneController.setPlayers());
            }
            else{
                addMessage("<Server> " + serverEvent.get("message").getAsString());
                if (gameSceneController == (null)) {
                    if (!(setupSceneController == null))
                        Platform.runLater(() -> setupSceneController.chatDisplay());
                }
                else {
                    Platform.runLater(() -> gameSceneController.chatDisplay());
                    if(serverEvent.get("message").getAsString().equals("It is your FINAL turn")) {
                        Platform.runLater(()->gameSceneController.showPopUpNotification("It is your FINAL turn"));
                        Platform.runLater(() -> gameSceneController.setFinalTurn());
                    }
                }
            }

        }
    }
    private void addMessage(String msg){
        Platform.runLater(() -> activeController.addMessage(msg));
    }
    private void heartBeatEvent(JsonObject serverEvent){
        JsonObject message= new JsonObject();
        message.addProperty("type","heartbeat");
        write(message);
        lastHeartbeatTime=System.currentTimeMillis();
    }

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

            Platform.runLater(()-> loginSceneController.changeToSetupState(playerSetup,publicGoals,normalDeck,goldenDeck,playerNames));
        } catch (JsonException e) {
            throw new RuntimeException(e);
        } catch (SyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public void write(JsonObject object) {
        writer.println(object.toString());
        writer.flush();
    }

    public void shutdown() {
        this.running = false;
        this.writer.close();
        try {
            if (!socket.isClosed())
                socket.close();
        }
        catch (IOException e) {
            System.out.println(e);
        }
        Thread.currentThread().interrupt();
    }

    public ArrayList<PlayableCard> extractCardArray(JsonArray jsonArray){
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
    public ArrayList<GoalCard> extractGoalArray(JsonArray jsonArray){
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

    public ArrayList<String> extractStringArray(JsonArray jsonArray){
        ArrayList<String> strings = new ArrayList<>();
        for (int i = 0; i < jsonArray.size(); i++)
            strings.add(jsonArray.get(i).getAsString());
        return strings;
    }
    public ArrayList<Integer> extractIntegerArray(JsonArray jsonArray){
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