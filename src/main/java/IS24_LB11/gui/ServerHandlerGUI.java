package IS24_LB11.gui;

import IS24_LB11.gui.phases.ClientGUIState;
import com.google.gson.JsonObject;
import com.google.gson.JsonStreamParser;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerHandlerGUI implements Runnable{
    private Socket socket;
    private JsonStreamParser parser;
    private PrintWriter writer;
    private ClientGUIState actualState;

    public ServerHandlerGUI(ClientGUIState clientGUIState, String serverIP, int serverPORT) throws IOException {
        socket = new Socket(serverIP, serverPORT);
        this.actualState = clientGUIState;
        parser = new JsonStreamParser(new InputStreamReader(socket.getInputStream()));
        writer = new PrintWriter(socket.getOutputStream());
    }

    public void run() {
        while (true) {
            if (socket.isClosed()) break;
            try {
                synchronized (parser) {
                    parser.wait(10);
                }
                if (parser.hasNext()) {
                    processEvent(parser.next().getAsJsonObject());
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        if (!socket.isClosed()) {
            try { socket.close(); }
            catch (IOException e) { }
        }
    }

    private void processEvent(JsonObject serverEvent) {
        if (serverEvent.has("type")) {
            switch (serverEvent.get("type").getAsString().toLowerCase()){
                case "setusername":
                    handleLoginEvent(serverEvent);
                    return;
                case "notification":
                    handleNotificationEvent(serverEvent);
                    return;
                case "heartbeat":
                    heartBeatEvent(serverEvent);
                    return;
                case "setup":
                    handleSetupEvent(serverEvent);
                    return;
                case "disconnected":
                    return;
                case "turn":
                    return;

                default: throw new IllegalStateException("Unexpected value: " + serverEvent.get("type"));
            }
        }

    }

    private void handleLoginEvent(JsonObject serverEvent){
        if (serverEvent.has("username")){
            actualState.setUsername(serverEvent.get("username").getAsString());
        }
    }
    private void handleNotificationEvent(JsonObject serverEvent){
        if (serverEvent.has("message")){
            // display message on GUI
        }
    }
    private void heartBeatEvent(JsonObject serverEvent){
        JsonObject message= new JsonObject();
        message.addProperty("type","heartbeat");
        write(message);
        //TODO: handle heartbeat client-side
    }

    private void handleSetupEvent(JsonObject serverEvent) {

    }

    public void write(JsonObject object) {
        writer.println(object.toString());
        writer.flush();
    }

    public void shutdown() {
        try {
            if (!socket.isClosed()) socket.close();
        }
        catch (IOException e) {/*TO check */}
    }

    public PrintWriter getWriter() {
        return writer;
    }
}