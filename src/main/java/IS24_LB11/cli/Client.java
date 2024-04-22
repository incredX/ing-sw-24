package IS24_LB11.cli;

import IS24_LB11.cli.controller.ClientInLobby;
import IS24_LB11.cli.controller.ClientInSetup;
import IS24_LB11.cli.controller.ClientState;
import IS24_LB11.cli.listeners.InputListener;
import IS24_LB11.cli.listeners.ResizeListener;
import IS24_LB11.cli.listeners.ServerHandler;
import IS24_LB11.cli.view.ViewHub;
import IS24_LB11.game.Board;
import IS24_LB11.game.PlayerSetup;
import IS24_LB11.game.components.*;
import IS24_LB11.game.utils.Color;
import IS24_LB11.game.utils.SyntaxException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Client {
    public static void main(String[] args) throws SyntaxException {
        Debugger dbg = new Debugger();
        ClientState state;
        ViewHub viewHub;
        InputListener inputListener;
        ResizeListener resizeListener;
        ServerHandler serverHandler;
        HashMap<String, Thread> threadMap = new HashMap<>();

        try {
            viewHub = new ViewHub();
            if (args.length == 1 && args[0].equals("setup")) {
                PlayableCard[] hand = new PlayableCard[] {
                        new GoldenCard("_EEQFF1QFFA__"), new NormalCard("FEF_FF0"), new NormalCard("EA_AAF0")
                };
                PlayerSetup setup = new PlayerSetup(new StarterCard("EPIE_F0I__FPIA"),
                        new GoalCard[]{new GoalSymbol("2KK_"), new GoalPattern("3IPPL2")},
                        new ArrayList<>(List.of(hand)),
                         Color.RED);
                state = new ClientInSetup(viewHub, setup);
            } else {
                state = new ClientInLobby(viewHub);
            }
            resizeListener = new ResizeListener(state);
            inputListener = new InputListener(state);
            serverHandler = new ServerHandler(state, "127.0.0.1", 54321);
            state.setServerHandler(serverHandler);
        } catch (IOException e) {
            dbg.printException(e);
            return;
        }
        dbg.printIntro("init DONE.");

        threadMap.put("views", new Thread(viewHub));
        threadMap.put("input", new Thread(inputListener));
        threadMap.put("resize", new Thread(resizeListener));
        threadMap.put("server", new Thread(serverHandler));

        for (Thread t: threadMap.values()) t.start();

        while (true) {
            ClientState nextState = state.execute();
            if (nextState == null) break;
            else state = nextState;
        }

        dbg.printMessage("closing client.");

        inputListener.shutdown();
        serverHandler.shutdown();
        threadMap.get("views").interrupt();
        threadMap.get("resize").interrupt();
        threadMap.get("server").interrupt();
    }
}
