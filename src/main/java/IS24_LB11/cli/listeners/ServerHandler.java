package IS24_LB11.cli.listeners;

import IS24_LB11.cli.Debugger;
import IS24_LB11.cli.controller.ClientState;
import IS24_LB11.cli.event.NotificationEvent;
import IS24_LB11.cli.event.ResultServerEvent;
import IS24_LB11.cli.event.ServerDownEvent;
import IS24_LB11.cli.event.server.ServerEventFactory;
import com.google.gson.*;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import static IS24_LB11.game.Result.Error;

/**
 * ServerHandler is a class that handles communication with a server.
 * It listens for server events, dispatching them to the current {@code ClientState}, and sends responses back to the server.
 * It extends the Listener class and implements the Runnable interface to run in a separate thread.
 */
public class ServerHandler extends Listener implements Runnable {
    private static final int MAX_TIMEOUT = 3_000;
    private static final int MIN_TIMEOUT = 1_000;
    //private static final int TIMEOUT = 1_000;
    private final Object timerLock = new Object();
    private final String serverIp;
    private final int serverPort;
    private Socket socket;
    private JsonStreamParser parser;
    private PrintWriter writer;
    private int timeout = MIN_TIMEOUT;

    /**
     * Constructs a ServerHandler with the given client state, server IP, and server port.
     *
     * @param state the client state which holds the event queue
     * @param serverIp the IP address of the server
     * @param serverPort the port number of the server
     */
    public ServerHandler(ClientState state, String serverIp, int serverPort) {
        super(state);
        this.serverIp = serverIp;
        this.serverPort = serverPort;
    }

    /**
     * The run method that continuously listens for server events.
     * It processes events received from the server and sends appropriate responses.
     */
    @Override
    public void run() {
        Thread.currentThread().setName("server-handler");
        Debugger.print("thread started.");

        try {
            state.queueEvent(new NotificationEvent("starting connection with server..."));
            socket = new Socket(serverIp, serverPort);
            socket.setTrafficClass(0x04); // IPTOS_RELIABILITY (0x04)
            //socket.setKeepAlive(true);
            socket.setTcpNoDelay(true);
            parser = new JsonStreamParser(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream());
        } catch (IOException | InterruptedException e) {
            Debugger.print(e);
            Debugger.print("thread closed.");
            state.tryQueueEvent(NotificationEvent.error("connection failed"));
            return;
        } catch (IllegalStateException e) {
            Debugger.print(e);
            Debugger.print("thread closed.");
            state.tryQueueEvent(NotificationEvent.error("illegal port number"));
            return;
        }

        startEventTimer();

        while (true) {
            if (socket.isClosed()) break;
            try {
                if (parser.hasNext()) {
                    JsonObject event;
                    wakeupTimer();
                    try {
                        event = parser.next().getAsJsonObject();
                    } catch (JsonSyntaxException | IllegalStateException e) {
                        state.queueEvent(new ResultServerEvent(Error("Bad request", "json syntax error")));
                        continue;
                    }
                    if (event.has("error") || (event.has("type") && !event.get("type").getAsString().equalsIgnoreCase("heartbeat")))
                        Debugger.print("from server: " + event);
                    state.queueEvent(new ResultServerEvent(ServerEventFactory.createServerEvent(event)));
                }
            } catch (JsonSyntaxException | IllegalStateException e) {
                state.tryQueueEvent(new ResultServerEvent(Error("Bad request", "json syntax error")));
            } catch (JsonIOException e) {
                Debugger.print(e);
                break;
            } catch (InterruptedException e) {
                break;
            }
        }

        shutdownEventTimer();
        if (state != null) {
            try {
                state.queueEvent(new ServerDownEvent());
            } catch (InterruptedException e) {
                Debugger.print(e);
            }
        }

        if (!socket.isClosed()) {
            try {
                socket.close();
            } catch (IOException e) {
                Debugger.print(e);
            }
        }
        Debugger.print("thread terminated.");
    }

    /**
     * Sends a JSON object to the server.
     *
     * @param object the JSON object to send
     */
    public void write(JsonObject object) {
        if (writer == null) return;
        if (object.has("type") && !object.get("type").getAsString().equalsIgnoreCase("heartbeat"))
            Debugger.print("to server: " + object);
        writer.println(object.toString());
        writer.flush();
//        synchronized (timerLock) {
//        }
    }

    /**
     * Shuts down the server handler by closing the socket connection.
     */
    public void shutdown() {
        try {
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {
            Debugger.print(e);
        }
    }

    /**
     * Wakes up the event timer to reset the timeout.
     */
    private void wakeupTimer() {
        synchronized (timerLock) {
            timerLock.notifyAll();
        }
    }

    /**
     * Shuts down the event timer.
     */
    private void shutdownEventTimer() {
        synchronized (timerLock) {
            timeout = 0;
            timerLock.notifyAll();
        }
    }

    /**
     * Starts a new event timer thread that monitors the connection to the server.
     */
    private void startEventTimer() {
        new Thread(() -> {
            Thread.currentThread().setName("event-timer");
            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
            long timeStamp = System.currentTimeMillis();
            while (true) {
                synchronized (timerLock) {
                    try {
                        timerLock.wait(timeout);
                        long diff = System.currentTimeMillis() - timeStamp;
                        if (timeout == 0) // => needs to be shutdown
                            break;
                        if (diff >= timeout) {
                            if (timeout >= MAX_TIMEOUT) {
                                state.queueEvent(new ServerDownEvent());
                                Debugger.print("Server down (time = " + diff + ")");
                                break;
                            } else {
                                timeout *= 2;
                                state.sendToServer("heartbeat");
                                Debugger.print("Server lost pace (" + diff + ")");
                            }
                        } else {
                            //Debugger.print("Server up  " + diff);
                            timeStamp += diff;
                            timeout = MIN_TIMEOUT;
                        }
                    } catch (InterruptedException e) {
                        Debugger.print(e);
                        break;
                    }
                }
            }
        }).start();
    }
}
