package IS24_LB11.network;

import IS24_LB11.game.Game;
import IS24_LB11.game.Player;
import IS24_LB11.network.phases.NotifyTurnPhase;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonStreamParser;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Handles communication with a single client connected to the server.
 * This includes receiving messages from the client, sending messages to the client,
 * and managing the client's state within the game.
 */
public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private JsonStreamParser parser;
    private PrintWriter out;
    private String userName = null;
    private Server server;
    private boolean connectionClosed = false;
    private static final int HEARTBEAT_INTERVAL = 1000;
    private long lastHeartbeatTime;

    private ArrayList<Thread> allStartedThreads = new ArrayList<>();


    /**
     * Constructs a ClientHandler with the given server and client socket.
     * @param server the server managing this client
     * @param socket the socket representing the client's connection
     */
    public ClientHandler(Server server, Socket socket) {
        this.clientSocket = socket;
        this.server = server;
        this.lastHeartbeatTime = System.currentTimeMillis();
    }

    /**
     * The main run method of the ClientHandler, which handles client communication.
     */
    @Override
    public void run() {
        try {
            // Start a thread to send heartbeat messages to the client
            Thread heartbeatThread = new Thread(() -> {
                while (!connectionClosed) {
                    try {
                        JsonObject heartbeat = new JsonObject();
                        heartbeat.addProperty("type", "heartbeat");
                        sendMessage(heartbeat.toString());
                        Thread.sleep(HEARTBEAT_INTERVAL);
//                        System.out.println(userName + " -> " + (System.currentTimeMillis() - lastHeartbeatTime));
                        if (System.currentTimeMillis() - lastHeartbeatTime > HEARTBEAT_INTERVAL * 5.5) {
                            System.out.println("Heartbeat timed out for " + userName);

                            JsonObject response = new JsonObject();
                            response.addProperty("type", "notification");
                            response.addProperty("message", "Player " + userName + " crashed");

                            broadcast(response.toString());
                            exit();
                            break;
                        }
                    } catch (InterruptedException e) {
                        exit();
                    }
                }
            });

            // Wait for inputs from client
            parser = new JsonStreamParser(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream());

            heartbeatThread.start();
            addToStartedThreads(heartbeatThread);

            String inputLine;
            try {
                while (!connectionClosed && parser.hasNext()) {
                    // Handle the received JSON data
                    ServerEventHandler.handleEvent(this, parser.next().getAsJsonObject());
                }
            } catch (JsonIOException e){

            }


            exit();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends a message to the client.
     * @param message the message to send
     */
    public void sendMessage(String message) {
        synchronized (out) {
            out.println(message);
            out.flush();
        }
    }

    /**
     * Broadcasts a message to all other connected clients.
     * @param message the message to broadcast
     */
    public void broadcast(String message) {
        for (ClientHandler clientHandler : getClientHandlers()) {
            if (!this.equals(clientHandler) && clientHandler.getUserName() != null) {
                clientHandler.sendMessage(message);
            }
        }
    }

    /**
     * Gets the username of the client.
     * @return the client's username
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Sets the username of the client.
     * @param userName the username to set
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * Sets the connectionClosed flag to indicate if the connection is closed.
     * @param connectionClosed the flag value to set
     */
    public void setConnectionClosed(boolean connectionClosed) {
        this.connectionClosed = connectionClosed;
    }

    /**
     * Gets the list of all usernames connected to the server.
     * @return the list of usernames
     */
    public ArrayList<String> getAllUsernames() {
        return server.getAllUsernames();
    }

    /**
     * Gets the ClientHandler instance for a given username.
     * @param username the username to search for
     * @return the ClientHandler instance, or null if not found
     */
    public ClientHandler getClientHandlerWithUsername(String username) {
        for (ClientHandler clientHandler : getClientHandlers()) {
            if (clientHandler.getUserName().equals(username)) {
                return clientHandler;
            }
        }
        return null;
    }

    /**
     * Adds a thread to the list of started threads.
     * @param thread the thread to add
     */
    public void addToStartedThreads(Thread thread) {
        allStartedThreads.add(thread);
    }

    /**
     * Sets the last heartbeat time to the given value.
     * @param lastHeartbeatTime the last heartbeat time to set
     */
    public void setLastHeartbeatTime(long lastHeartbeatTime) {
        this.lastHeartbeatTime = lastHeartbeatTime;
    }

    /**
     * Exits the connection, performing necessary cleanup and notifications.
     */
    public void exit() {
        if (!connectionClosed) {
            try {
                System.out.println("Closing connection for " + userName);
                // Notify all that client disconnected
                JsonObject clientDisconnected = new JsonObject();
                clientDisconnected.addProperty("type", "disconnected");
                clientDisconnected.addProperty("player", this.getUserName());
                this.broadcast(clientDisconnected.toString());


                // Pass turn to another player
                if (this.getGame() != null && this.getGame().getPlayers().size() >= 1) {
                    Player currentPlayerReal;

                    Boolean currentPlayerDisconnected = false;

                    if (this.getUserName().equals(this.getGame().currentPlayer().name())) {
                        if (this.getGame().getTurn() >= 0){
                            currentPlayerDisconnected = true;
                            this.getGame().setTurn(this.getGame().getTurn() + 1);
                        }
                        currentPlayerReal = this.getGame().currentPlayer();
                    } else {
                        currentPlayerReal = this.getGame().currentPlayer();
                    }

                    this.getGame().getPlayers().removeIf(player -> player.name().equals(this.getUserName()));

                    if (this.getGame().getTurn() >= 0 && !this.getGame().hasGameEnded()) {
                        this.getGame().setTurn(this.getGame().getPlayers().indexOf(currentPlayerReal));

                        if (this.getGame().getPlayers().size() == 1) {
                            this.getGame().setGameEnded(true);
                            NotifyTurnPhase.startPhase(this.getClientHandlerWithUsername(this.getGame().currentPlayer().name()));
                        } else if (this.getGame().getPlayers().size() > 1) {
                            if(currentPlayerDisconnected)
                                NotifyTurnPhase.startPhase(this.getClientHandlerWithUsername(this.getGame().currentPlayer().name()));
                        }
                    }

                    JsonObject response = new JsonObject();
                    response.addProperty("type", "notification");
                    response.addProperty("message", "Player " + this.getUserName() + " disconnected");
                    this.broadcast(response.toString());
                }

                connectionClosed = true;
                out.close();
                clientSocket.close();
                for (Thread thread : allStartedThreads) {
                    thread.interrupt();
                }
                server.removeClientHandler(this);
            } catch (IOException e) {
                // Handle exception
            }
        }
    }

    /**
     * Sets the maximum number of players.
     * @param maxPlayers the maximum number of players
     */
    public void setMaxPlayers(int maxPlayers) {
        server.maxPlayers = maxPlayers;
        System.out.println("num max players set to " + server.maxPlayers);
    }

    /**
     * Gets the game instance managed by the server.
     * @return the game instance
     */
    public Game getGame() {
        return server.game;
    }

    /**
     * Sets the game instance to be managed by the server.
     * @param game the game instance to set
     */
    public void setGame(Game game) {
        server.game = game;
    }

    /**
     * Gets the maximum number of players allowed.
     * @return the maximum number of players
     */
    public int getMaxPlayers() {
        return server.maxPlayers;
    }

    /**
     * Gets the list of all client handlers connected to the server.
     * @return the list of client handlers
     */
    public ArrayList<ClientHandler> getClientHandlers() {
        return server.clientHandlers;
    }
}
