package IS24_LB11.network;
import java.io.*;
import java.net.*;
import java.util.*;
import com.google.gson.*;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;
    private String userName;
    private Server server;
    public ClientHandler(Server server, Socket socket) {
        this.clientSocket = socket;
        this.server = server;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);

            // Start separate threads for sending and receiving messages
            Thread receivingThread = new Thread(new ReceivingThread());
//            Thread sendingThread = new Thread(new SendingThread());
            receivingThread.start();
//            sendingThread.start();

            // Wait for both threads to finish
            receivingThread.join();
//            sendingThread.join();

            in.close();
            out.close();
            clientSocket.close();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Inner class for receiving messages from the client
    class ReceivingThread implements Runnable {
        @Override
        public void run() {
            try {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    System.out.println("Received from client " + clientSocket.getInetAddress().getHostName() + ": " + inputLine);

                    // Create temporary thread to send message to every client
//                    String finalInputLine = inputLine;
//                    new Thread(() -> broadcast(finalInputLine) );
                    broadcast(inputLine);
                    // Parse JSON
                    JsonObject jsonObject = JsonParser.parseString(inputLine).getAsJsonObject();

                    // Do something with the received JSON data

                    if (inputLine.equals("exit")) {
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Inner class for sending messages to the client
//    class SendingThread implements Runnable {
//        @Override
//        public void run() {
//            try {
//                Scanner scanner = new Scanner(System.in);
//                while (true) {
//                    System.out.print("Enter message to send to client: ");
//                    String message = scanner.nextLine();
//
//                    // Convert user input to JSON
//                    JsonObject jsonObject = new JsonObject();
//                    jsonObject.addProperty("message", message);
//
//                    // Send JSON to client
//                    out.println(jsonObject.toString());
//
//                    if (message.equals("exit")) {
//                        break;
//                    }
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }

    // Method to send a message to the client from external components
    public void sendMessage(String message) {
//        JsonObject jsonObject = new JsonObject();
//        jsonObject.addProperty("message", message);
        out.println(message);
    }

    public void broadcast(String message) {
        for (ClientHandler clientHandler : server.getActiveClients()){
            if(!this.equals(clientHandler))
                clientHandler.sendMessage(message);
        }
    }
}
