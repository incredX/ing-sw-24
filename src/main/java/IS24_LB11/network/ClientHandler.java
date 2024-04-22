package IS24_LB11.network;
import java.io.*;
import java.net.*;
import java.util.ArrayList;

import IS24_LB11.network.events.ServerEventHandler;
import com.google.gson.*;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;
    private String userName;
    private Server server;
    private boolean connectionClosed = false;
    private int HEARTBEAT_INTERVAL = 2000;
    private long lastHeartbeatTime;

    private ArrayList<Thread> allStartedThreads = new ArrayList<>();

    //useful to convert json to string and viceversa
    private Gson gson = new Gson();

    public ClientHandler(Server server, Socket socket, String userName) {
        this.clientSocket = socket;
        this.server = server;
        this.userName = userName;
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
                            Thread.sleep(HEARTBEAT_INTERVAL);
                            long currentTime = System.currentTimeMillis();
                            if (currentTime - lastHeartbeatTime > HEARTBEAT_INTERVAL * 2) {
                                System.out.println("Heartbeat timed out for " + userName);
                                exit();
                                break;
                            }
                            JsonObject heartbeat = new JsonObject();
                            heartbeat.addProperty("type", "heartbeat");
                            out.println(heartbeat.toString());

                        } catch (InterruptedException e) {
                            exit();
                        }
                    }
                }
            });
            heartbeatThread.start();
            addToStartedThreads(heartbeatThread);

            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);

            String inputLine;
            while (!connectionClosed && (inputLine = in.readLine()) != null) {
//                System.out.println("Received from client " + this.userName + ": " + inputLine);

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
        for (ClientHandler clientHandler : server.getClientHandlers()){
            if(!this.equals(clientHandler))
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
        for (ClientHandler clientHandler : server.getClientHandlers()){
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

    private void exit() {
        try {
            connectionClosed = true;
            in.close();
            out.close();
            clientSocket.close();
            for (Thread thread : allStartedThreads){
                thread.interrupt();
            }
            server.removeClientHandler(this);
        } catch (IOException e) {

        }
    }

}
