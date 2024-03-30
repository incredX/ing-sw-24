package IS24_LB11.network;

import java.net.*;
import java.io.*;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.JsonObject;


public class Server
{
    //initialize socket and input stream
    private Socket socket = null;
    private ServerSocket server = null;
    private DataInputStream in	 = null;
    private ArrayList<ClientHandler> activeClients = new ArrayList<ClientHandler>();

    // this variable is useful so we can prevent that when a client disconnects another one joins in his place
    // before the session is over
    private int numOfConnectedClientsThisSession = 0;
    /**
     *
     * @param PORT should be in range 49152-65535
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

    public void start() {
        System.out.println("Server started");
        System.out.println("Waiting for clients to connect");
        while (true) {
            try {
                Socket clientSocket = server.accept();

                if(numOfConnectedClientsThisSession < 4) {
                    System.out.println("New client connected: " + clientSocket.getInetAddress().getHostName());
                    numOfConnectedClientsThisSession = numOfConnectedClientsThisSession + 1;

                    // Create client handler and start thread
                    ClientHandler clientHandler = new ClientHandler(this, clientSocket);
                    this.activeClients.add(clientHandler);
                    new Thread(clientHandler).start();
                }
                else{
                    System.out.println("Client " + clientSocket.getInetAddress().getHostName() +
                            " tried to join but server is full");

                    // Send message to client why they can't join
                    OutputStream outputStream = clientSocket.getOutputStream();
                    Gson gson = new Gson();
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

    public void exit() throws IOException {
        //TODO: cleanup and server shutdown
        socket.close();
    }

    private boolean isValidPort(int PORT){
        if(PORT >= 49152 && PORT <= 65535)
            return true;
        return false;
    }

    public ArrayList<ClientHandler> getActiveClients(){
        return activeClients;
    }

    public static void main(String args[])
    {
        Server server = new Server(54321);
        server.start();
    }
}
