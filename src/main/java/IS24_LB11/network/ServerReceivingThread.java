package IS24_LB11.network;

import IS24_LB11.network.events.ServerEventHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerReceivingThread implements Runnable {
    private ClientHandler clientHandler;
    private Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;

    public ServerReceivingThread(ClientHandler clientHandler, Socket clientSocket, BufferedReader in, PrintWriter out) {
        this.clientHandler = clientHandler;
        this.clientSocket = clientSocket;
        this.in = in;
        this.out = out;
    }

    @Override
    public void run() {
        try {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                System.out.println("Received from client " + clientSocket.getInetAddress().getHostName() + ": " + inputLine);

                // Do something with the received JSON data
                out.println(ServerEventHandler.handleEvent(clientHandler, inputLine).toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
