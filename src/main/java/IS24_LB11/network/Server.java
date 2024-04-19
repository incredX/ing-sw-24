package IS24_LB11.network;

import java.net.*;
import java.io.*;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * This class represents a server that listens for client connections and handles them accordingly.
 */
public class Server
{
    //initialize socket and input stream
    private Socket socket = null;
    private ServerSocket server = null;
    private DataInputStream in	 = null;
    private ArrayList<ClientHandler> clientHandlers = new ArrayList<ClientHandler>();

    //useful to convert json to string and viceversa
    private Gson gson = new Gson();

    /**
     * Constructs a Server instance with the specified port.
     *
     * @param PORT the port number the server will listen on (should be in range 49152-65535).
     */
    public Server(int PORT)
    {
        try {
            if(!isValidPort(PORT))
                throw new WrongPortException();

            server = new ServerSocket(PORT);
            System.out.println("Server waiting to start on port " + PORT);
        } catch(IOException i) {
            System.out.println(i);
        } catch (WrongPortException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Starts the server, listens for client connections, and handles them.
     */
    public void start() {
        System.out.println("Server started");
        System.out.println("Waiting for clients to connect");
        while (true) {
            try {
                Socket clientSocket = server.accept();

                if(clientHandlers.size() < 4) {
                    System.out.println("New client connected: " + clientSocket.getInetAddress().getHostName());

                    // Create client handler and start thread
                    ClientHandler clientHandler = new ClientHandler(this, clientSocket);
                    this.clientHandlers.add(clientHandler);
                    new Thread(clientHandler).start();
                }
                else{
                    System.out.println("Client " + clientSocket.getInetAddress().getHostName() +
                            " tried to join but server is full");

                    // Send message to client why they can't join
                    OutputStream outputStream = clientSocket.getOutputStream();
                    JsonObject jsonResponse = new JsonObject();
                    jsonResponse.addProperty("error", "Server full, try again later.");

                    // Writing JSON response
                    outputStream.write(jsonResponse.toString().getBytes());
                    outputStream.close();
                    clientSocket.close();

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Cleans up and shuts down the server.
     *
     * @throws IOException if an I/O error occurs.
     */
    public void exit() throws IOException {
        //TODO: cleanup and server shutdown
        socket.close();
    }

    /**
     * Checks if the provided port number is within the valid range.
     *
     * @param PORT the port number to check.
     * @return true if the port number is within the valid range, false otherwise.
     */
    private boolean isValidPort(int PORT){
        if(PORT >= 49152 && PORT <= 65535)
            return true;
        return false;
    }

    public ArrayList<String> getAllUsernames(){
        ArrayList<String> list = new ArrayList<>();
        for(ClientHandler clientHandler : clientHandlers) {
            list.add(clientHandler.getUserName());
        }
        return list;
    }

    /**
     * Retrieves the list of active client handlers.
     *
     * @return the list of active client handlers.
     */
    public ArrayList<ClientHandler> getClientHandlers(){
        return clientHandlers;
    }

    public static void main(String args[])
    {
        Server server = new Server(54321);
        server.start();
    }
}
