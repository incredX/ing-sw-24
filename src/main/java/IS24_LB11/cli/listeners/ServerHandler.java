package IS24_LB11.cli.listeners;

import IS24_LB11.cli.Debugger;
import IS24_LB11.cli.controller.ClientState;
import IS24_LB11.cli.event.ResultServerEvent;

import IS24_LB11.cli.event.ServerDownEvent;
import IS24_LB11.cli.event.server.ServerEventFactory;
import com.google.gson.*;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import static IS24_LB11.game.Result.Error;

public class ServerHandler extends Listener implements Runnable {
    private Socket socket;
    private JsonStreamParser parser;
    private PrintWriter writer;
    private final Object timerLock = new Object();
    private int timeout = 3000;

    public ServerHandler(ClientState state, String serverIP, int serverPORT) throws IOException {
        super(state);
        socket = new Socket(serverIP, serverPORT);
        parser = new JsonStreamParser(new InputStreamReader(socket.getInputStream()));
        writer = new PrintWriter(socket.getOutputStream());
    }

    public void run() {
        Thread.currentThread().setName("server-handler");
        Debugger.print("thread started.");

        startEventTimer();

        while (true) {
            if (socket.isClosed()) break;
            try {
                //synchronized (parser) { parser.wait(10); }
                if (parser.hasNext()) {
                    JsonObject event;
                    resetEventTimer();
                    try {
                        event = parser.next().getAsJsonObject();
                    }
                    catch (JsonSyntaxException | IllegalStateException e) {
                        state.queueEvent(new ResultServerEvent(Error("Bad request", "json syntax error")));
                        continue;
                    }
                    if (event.has("error") || (event.has("type") && !event.get("type").getAsString().equalsIgnoreCase("heartbeat")))
                        Debugger.print("from server: "+event);
                    state.queueEvent(new ResultServerEvent(ServerEventFactory.createServerEvent(event)));
                }
            }
            catch (JsonSyntaxException | IllegalStateException e) {
                state.tryQueueEvent(new ResultServerEvent(Error("Bad request", "json syntax error")));
            }
            catch (JsonIOException e) {
                Debugger.print(e);
                break;
            }
            catch (InterruptedException e) { break; }
        }

        shutdownEventTimer();

        if (!socket.isClosed()) {
            try { socket.close(); }
            catch (IOException e) { Debugger.print(e); }
        }
        Debugger.print("thread terminated.");
    }
    
    public void write(JsonObject object) {
        if (object.has("type") && !object.get("type").getAsString().equalsIgnoreCase("heartbeat"))
            Debugger.print("to server: "+object);
        writer.println(object.toString());
        writer.flush();
    }

    public void shutdown() {
        try {
            if (!socket.isClosed()) socket.close();
        } catch (IOException e) { Debugger.print(e); }
    }

    private void resetEventTimer() {
        synchronized (timerLock) {
            timerLock.notify();
        }
    }

    private void shutdownEventTimer() {
        synchronized (timerLock) {
            timeout = 0;
            timerLock.notify();
        }
    }

    private void startEventTimer() {
        new Thread(() -> {
            long timeStamp = System.currentTimeMillis();
            System.out.println("starting event timer");
            while (true) {
                synchronized (timerLock) {
                    try {
                        timerLock.wait(timeout);
                        long diff = System.currentTimeMillis() - timeStamp;
                        if (diff >= timeout) {
                            if (timeout > 0) state.queueEvent(new ServerDownEvent());
                            break;
                        } else {
                            timeStamp += diff;
                        }
                    }
                    catch (InterruptedException e) { break; }
                }
            }
        }).start();
    }
}
