package IS24_LB11.cli;

import IS24_LB11.cli.controller.ClientInLobby;
import IS24_LB11.cli.controller.ClientState;
import IS24_LB11.cli.listeners.InputListener;
import IS24_LB11.cli.listeners.ResizeListener;
import IS24_LB11.cli.listeners.ServerHandler;
import IS24_LB11.game.Board;

import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;

public class Client {
    public static void main(String[] args) {
        Debugger dbg = new Debugger();
        ClientState state;
        ViewHub viewHub;
        InputListener inListener;
        ResizeListener reListener;
        ServerHandler serverHandler;
        HashMap<String, Thread> threadMap = new HashMap<>();

        try {
            viewHub = new ViewHub();
            state = new ClientInLobby(new Board(), viewHub);
            inListener = new InputListener(state);
            reListener = new ResizeListener(state);
            serverHandler = new ServerHandler(state, "127.0.0.1", 54321);
        } catch (IOException e) {
            dbg.printException(e);
            return;
        }
        dbg.printIntro("init DONE.");

        threadMap.put("input", new Thread(inListener));
        threadMap.put("resize", new Thread(reListener));
        threadMap.put("views", new Thread(viewHub));
        threadMap.put("server", new Thread(serverHandler));

        for (Thread t: threadMap.values()) t.start();

        while (true) {
            ClientState nextState = state.execute();
            if (nextState == null) break;
            else state = nextState;
        }

        dbg.printMessage("closing controller.");

        try { System.in.close(); }
        catch (IOException e) { dbg.printException(e); }
        threadMap.get("resize").interrupt();
        threadMap.get("views").interrupt();
        threadMap.get("server").interrupt();
    }
}
