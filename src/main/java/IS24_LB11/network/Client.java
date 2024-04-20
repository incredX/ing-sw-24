package IS24_LB11.network;
import java.io.*;
import java.net.*;
import java.util.*;
import com.google.gson.*;

/**
 * This class represents a client that connects to a server and sends/receives messages.
 */
public class Client {
    private Socket socket;
    private BufferedReader userInput;
    private BufferedReader in;
    private PrintWriter out;

    /**
     * Constructs a Client instance and connects it to the specified server.
     *
     * @param serverIP   the IP address of the server.
     * @param serverPORT the port number of the server.
     */
    public Client(String serverIP, int serverPORT) {
        try {
            socket = new Socket(serverIP, serverPORT);
            userInput = new BufferedReader(new InputStreamReader(System.in));
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            System.out.println("Connected to server.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * Starts the client, allowing it to send and receive messages.
     */
    public void start() {
        // Thread for receiving messages from the server
        Thread receivingThread = new Thread(() -> {
            try {
                String serverResponse;
                while ((serverResponse = in.readLine()) != null) {
                    System.out.println("Server response: " + serverResponse);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        receivingThread.start();

        System.out.println("Type 'exit' to exit the program");
        try {
            String userInputLine;
            while ((userInputLine = userInput.readLine()) != null) {
                // Convert user input to JSON
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("message", userInputLine);

                // Send JSON to server
                out.println(jsonObject.toString());

                if (userInputLine.equals("exit")) {
                    break;
                }
            }

            userInput.close();
            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String serverIP = "127.0.0.1"; // Change this to your server's address
        int serverPORT = 54321; // Change this to your server's port
        Client client = new Client(serverIP, serverPORT);
        client.start();
    }
}
