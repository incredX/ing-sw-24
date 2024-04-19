package IS24_LB11.cli.listeners;

import IS24_LB11.cli.controller.ClientState;
import IS24_LB11.cli.event.ResultEvent;

import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonStreamParser;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import static IS24_LB11.game.Result.Error;
import static IS24_LB11.game.Result.Ok;

public class ServerHandler extends Listener implements Runnable {
    private final Socket socket;
    private final JsonStreamParser parser;
    private final PrintWriter writer;

    public ServerHandler(ClientState state, String serverIP, int serverPORT) throws IOException {
        super(state);
        socket = new Socket(serverIP, serverPORT);
        parser = new JsonStreamParser(new InputStreamReader(socket.getInputStream()));
        writer = new PrintWriter(socket.getOutputStream());
    }

    public void run() {
        System.out.println("server-handler: online");

        Thread.currentThread().setName("server-handler");
        try { Thread.sleep(500); }
        catch (InterruptedException ignored) { }

        while (!Thread.interrupted()) {
            if (socket.isClosed()) break;
            synchronized (parser) {
                try {
                    if (parser.hasNext()) {
                        JsonObject event;
                        try { event = parser.next().getAsJsonObject(); }
                        catch (JsonSyntaxException | IllegalStateException e) {
                            state.queueEvent(new ResultEvent(Error("Bad request", "json syntax error")));
                            continue;
                        }
                        state.queueEvent(new ResultEvent(Ok(event)));
                    }
                    parser.wait(250);
                }
                catch (JsonSyntaxException | IllegalStateException e) {
                    state.tryQueueEvent(new ResultEvent(Error("Bad request", "json syntax error")));
                }
                catch (JsonIOException e) {
                    System.out.println("caught exception: " + e.toString()); }
                catch (InterruptedException e) { break; }
            }
        }
        if (!socket.isClosed()) {
            try { socket.close(); }
            catch (IOException e) { System.out.printf("%s\n", e); }
        }
        System.out.println("server-handler offline");
    }
    
    public void write(JsonObject object) {
        System.out.println(object.toString());
        writer.println(object.toString());
        writer.flush();
    }
}
