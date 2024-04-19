package IS24_LB11.network;
import java.io.*;
import java.net.*;
import java.util.ArrayList;

import com.google.gson.*;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;
    private String userName;
    private Server server;

    //useful to convert json to string and viceversa
    private Gson gson = new Gson();

    public ClientHandler(Server server, Socket socket) {
        this.clientSocket = socket;
        this.server = server;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);

            // Start thread for receiving messages
            Thread receivingThread = new Thread(new ServerReceivingThread(this, clientSocket, in, out));
            receivingThread.start();

            // Wait for thread to finish
            receivingThread.join();

            in.close();
            out.close();
            clientSocket.close();
        } catch (IOException | InterruptedException e) {
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

    public ArrayList<String> getAllUsernames() {
        return server.getAllUsernames();
    }


}
