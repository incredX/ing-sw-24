package IS24_LB11.network;

import IS24_LB11.game.Game;
import IS24_LB11.game.Player;
import IS24_LB11.network.phases.NotifyTurnPhase;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;
    private String userName = null;
    private Server server;
    private boolean connectionClosed = false;
    //TODO:RESET THIS
    private int HEARTBEAT_INTERVAL = 500;
    private long lastHeartbeatTime;

    private ArrayList<Thread> allStartedThreads = new ArrayList<>();

    //useful to convert json to string and viceversa
    private Gson gson = new Gson();

    public ClientHandler(Server server, Socket socket) {
        this.clientSocket = socket;
        this.server = server;
        this.lastHeartbeatTime = System.currentTimeMillis();
    }

    @Override
    public void run() {
        try {
            // Start a thread to send heartbeat messages to the client
            Thread heartbeatThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (!connectionClosed) {
                        try {
                            JsonObject heartbeat = new JsonObject();
                            heartbeat.addProperty("type", "heartbeat");
                            out.println(heartbeat.toString());

                            Thread.sleep(HEARTBEAT_INTERVAL);

                            //System.out.println(userName + "   " + (System.currentTimeMillis() - lastHeartbeatTime));

                            if (System.currentTimeMillis() - lastHeartbeatTime > HEARTBEAT_INTERVAL*5.5) {
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
                }
            });


            // wait for inputs from client
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);

            heartbeatThread.start();
            addToStartedThreads(heartbeatThread);

            String inputLine;
            while (!connectionClosed && (inputLine = in.readLine()) != null) {
                // Handle the received JSON data
                ServerEventHandler.handleEvent(this, inputLine);
            }

            exit();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to send a message to the client from external components
    public void sendMessage(String message) {
        out.println(message);
    }

    public void broadcast(String message) {
        for (ClientHandler clientHandler : getClientHandlers()){
            if(!this.equals(clientHandler) && clientHandler.getUserName() != null)
                clientHandler.sendMessage(message);
        }
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setConnectionClosed(boolean connectionClosed) {
        this.connectionClosed = connectionClosed;
    }

    public ArrayList<String> getAllUsernames() {
        return server.getAllUsernames();
    }

    public ClientHandler getClientHandlerWithUsername(String username) {
        for (ClientHandler clientHandler : getClientHandlers()){
            if(clientHandler.getUserName().equals(username)){
                return clientHandler;
            }
        }
        return null;
    }

    public void addToStartedThreads(Thread thread) {
        allStartedThreads.add(thread);
    }

    public void setLastHeartbeatTime(long lastHeartbeatTime) {
        this.lastHeartbeatTime = lastHeartbeatTime;
    }

    public void exit() {
        if(!connectionClosed) {
            try {
                System.out.println("Closing connection for " + userName);
                // notify all that client disconnected
                JsonObject clientDisconnected = new JsonObject();
                clientDisconnected.addProperty("type", "disconnected");
                clientDisconnected.addProperty("player", this.getUserName());
                this.broadcast(clientDisconnected.toString());


                //pass turn to another player
                if (this.getGame() != null && this.getGame().getPlayers().size() >= 1) {
                    Player currentPlayerReal;

                    if (this.getUserName().equals(this.getGame().currentPlayer().name())){
                        this.getGame().setTurn(this.getGame().getTurn()+1);
                        currentPlayerReal = this.getGame().currentPlayer();
                    }
                    else{
                        currentPlayerReal = this.getGame().currentPlayer();
                    }

                    this.getGame().getPlayers().removeIf(player -> player.name().equals(this.getUserName()));
                    this.getGame().setTurn(this.getGame().getPlayers().indexOf(currentPlayerReal));

                    if(this.getGame().getPlayers().size() == 1){
                        this.getGame().setGameEnded(true);
                        NotifyTurnPhase.startPhase(this.getClientHandlerWithUsername(this.getGame().currentPlayer().name()));
                    }else if (this.getGame().getPlayers().size() > 1 && this.getGame().getTurn() >= 0) {
                            NotifyTurnPhase.startPhase(this.getClientHandlerWithUsername(this.getGame().currentPlayer().name()));
                    }

                    JsonObject response = new JsonObject();
                    response.addProperty("type", "notification");
                    response.addProperty("message", "Player " + this.getUserName() + " disconnected");
                    this.broadcast(response.toString());

                }

                connectionClosed = true;
                in.close();
                out.close();
                clientSocket.close();
                for (Thread thread : allStartedThreads) {
                    thread.interrupt();
                }
                server.removeClientHandler(this);
            } catch (IOException e) {

            }
        }
    }

    public void setMaxPlayers(int maxPlayers) {
        server.maxPlayers = maxPlayers;
        System.out.println("num max players set to " + server.maxPlayers);
    }

    public Game getGame() {
        return server.game;
    }

    public void setGame(Game game) {
        server.game = game;
    }
    public int getMaxPlayers() {
        return server.maxPlayers;
    }

    public ArrayList<ClientHandler> getClientHandlers(){
        return server.clientHandlers;
    }
}