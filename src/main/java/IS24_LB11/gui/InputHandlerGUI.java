package IS24_LB11.gui;

import IS24_LB11.game.PlacedCard;
import IS24_LB11.game.components.GoalCard;
import IS24_LB11.game.components.StarterCard;
import IS24_LB11.game.tools.JsonConverter;
import IS24_LB11.game.tools.JsonException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Handles sending different types of messages from the GUI to the server.
 */
public class InputHandlerGUI {
    private Socket socket;
    private PrintWriter writer;

    /**
     * Constructs an InputHandlerGUI with the given writer.
     *
     * @param writer the writer used to send messages to the server
     * @throws IOException if an I/O error occurs
     */
    public InputHandlerGUI(PrintWriter writer) throws IOException {
        this.writer = writer;
    }

    /**
     * Sends a login message with the specified username.
     *
     * @param username the username to be sent
     */
    public void sendLogin(String username) {
        JsonObject message = new JsonObject();
        message.addProperty("type", "login");
        message.addProperty("username", username);
        send(message.toString());
    }

    /**
     * Sends the maximum number of players allowed in the game.
     *
     * @param numOfPlayers the maximum number of players
     */
    public void sendMaxPlayers(int numOfPlayers) {
        JsonObject message = new JsonObject();
        message.addProperty("type", "numOfPlayers");
        message.addProperty("numOfPlayers", numOfPlayers);
        send(message.toString());
    }

    /**
     * Sends a ready message with the selected goal card and starter card.
     *
     * @param personalGoal the selected goal card
     * @param starterCard  the selected starter card
     */
    public void sendReady(GoalCard personalGoal, StarterCard starterCard) {
        JsonObject message = new JsonObject();
        message.addProperty("type", "setup");
        message.addProperty("starterCard", starterCard.asString());
        message.addProperty("goalCard", personalGoal.asString());
        send(message.toString());
    }

    /**
     * Sends a turn action message with the placed card and the informations of the card to draw.
     *
     * @param placedCard     the card placed during the turn
     * @param deckType       the type of the deck (true for main deck, false for secondary deck)
     * @param indexCardDeck  the index of the card in the deck
     */
    public void sendTurn(PlacedCard placedCard, boolean deckType, int indexCardDeck) {
        JsonObject message = new JsonObject();
        message.addProperty("type", "turnActions");
        try {
            JsonObject placedCardJson = (JsonObject) new JsonParser().parse(new JsonConverter().objectToJSON(placedCard));
            message.add("placedCard", placedCardJson);
        } catch (JsonException e) {
            throw new RuntimeException(e);
        }
        message.addProperty("deckType", deckType);
        message.addProperty("indexVisibleCards", indexCardDeck + 1);
        send(message.toString());
    }

    /**
     * Sends a final turn action message with the placed card.
     *
     * @param placedCard the card placed during the turn
     */
    public void sendTurn(PlacedCard placedCard) {
        JsonObject message = new JsonObject();
        message.addProperty("type", "turnActions");
        try {
            JsonObject placedCardJson = (JsonObject) new JsonParser().parse(new JsonConverter().objectToJSON(placedCard));
            message.add("placedCard", placedCardJson);
        } catch (JsonException e) {
            throw new RuntimeException(e);
        }
        message.addProperty("deckType", false);
        message.addProperty("indexVisibleCards", 1);
        send(message.toString());
    }

    /**
     * Sends a private message from one user to another.
     *
     * @param to    the recipient of the message
     * @param from  the sender of the message
     * @param mex   the message content
     */
    public void sendMessage(String to, String from, String mex) {
        JsonObject message = new JsonObject();
        message.addProperty("type", "message");
        message.addProperty("from", from);
        message.addProperty("to", to);
        message.addProperty("message", mex);
        send(message.toString());
    }

    /**
     * Sends a public message from one user to all users.
     *
     * @param from  the sender of the message
     * @param mex   the message content
     */
    public void sendToAllMessage(String from, String mex) {
        JsonObject message = new JsonObject();
        message.addProperty("type", "message");
        message.addProperty("from", from);
        message.addProperty("to", "");
        message.addProperty("message", mex);
        send(message.toString());
    }

    /**
     * Sends string to the server.
     *
     * @param message the message to be sent
     */
    private void send(String message) {
        writer.println(message);
        writer.flush();
        System.out.println(message);
    }
}
