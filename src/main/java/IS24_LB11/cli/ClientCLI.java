package IS24_LB11.cli;

import IS24_LB11.cli.controller.LobbyState;
import IS24_LB11.cli.controller.ClientState;
import IS24_LB11.cli.listeners.InputListener;
import IS24_LB11.cli.listeners.ResizeListener;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

public class ClientCLI {
    public static void main(String[] args) {
        ClientState state;
        ViewHub viewHub;
        InputListener inputListener;
        ResizeListener resizeListener;
        HashMap<String, Thread> threadMap = new HashMap<>();

        Debugger.startDebugger();
        //Debugger.closeDebugger();



        try {
            viewHub = new ViewHub();
            state = new LobbyState(viewHub);
            resizeListener = new ResizeListener(state);
            inputListener = new InputListener(state);
        } catch (IOException e) {
            Debugger.print(e);
            return;
        }

        Debugger.print("booting client.");

        threadMap.put("views", new Thread(viewHub));
        threadMap.put("input", new Thread(inputListener));
        threadMap.put("resize", new Thread(resizeListener));

        for (Thread t: threadMap.values()) t.start();

        JsonParser jsonParser = new JsonParser();
        for (String arg: Arrays.stream(args).skip(1).toArray(String[]::new)) {
            try {
                state.getServerHandler().write(jsonParser.parse(arg).getAsJsonObject());
            } catch (JsonSyntaxException | ClassCastException e) {
                Debugger.print(e);
                System.exit(1);
            }
        }

        while (true) {
            ClientState nextState = state.execute();
            if (nextState == null) break;
            else {
                state = nextState;
                inputListener.setState(state);
                resizeListener.setState(state);
                System.gc();
            }
        }

        Debugger.print("closing client.");

        inputListener.shutdown();
        threadMap.get("views").interrupt();
        threadMap.get("resize").interrupt();

        try { Thread.sleep(200); }
        catch (InterruptedException e) { Debugger.print(e); }

        Debugger.closeDebugger();
        System.exit(0);
    }
}
