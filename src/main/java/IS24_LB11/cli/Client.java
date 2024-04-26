package IS24_LB11.cli;

import IS24_LB11.cli.controller.GameState;
import IS24_LB11.cli.controller.LobbyState;
import IS24_LB11.cli.controller.SetupState;
import IS24_LB11.cli.controller.ClientState;
import IS24_LB11.cli.listeners.InputListener;
import IS24_LB11.cli.listeners.ResizeListener;
import IS24_LB11.cli.listeners.ServerHandler;
import IS24_LB11.cli.notification.NotificationStack;
import IS24_LB11.game.PlayerSetup;
import IS24_LB11.game.components.*;
import IS24_LB11.game.utils.Color;
import IS24_LB11.game.utils.SyntaxException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

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
                state = new SetupState(viewHub, getDefaultSetup(), defaultTable());
            } else if (args.length == 1 && args[0].equals("game")) {
                System.err.println(defaultTable());
                state = new GameState(viewHub, new NotificationStack(viewHub, 0), getDefaultSetup(), defaultTable());
            } else {
                state = new LobbyState(viewHub);
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
            else {
                System.out.println(state.toString()+" -> "+nextState.getClass());
                state = nextState;
                inputListener.setState(state);
                resizeListener.setState(state);
                serverHandler.setState(state);
                state.setServerHandler(serverHandler);
            }
        }

        dbg.printMessage("closing client.");

        inputListener.shutdown();
        serverHandler.shutdown();
        threadMap.get("views").interrupt();
        threadMap.get("resize").interrupt();
        threadMap.get("server").interrupt();
    }

    public static PlayerSetup getDefaultSetup() {
        try {
            PlayableCard[] hand = new PlayableCard[] {
                    new GoldenCard("_EEQFF1QFFA__"), new NormalCard("FEF_FF0"), new NormalCard("EA_AAF0")
            };
            return new PlayerSetup(new StarterCard("EPIE_F0I__FPIA"),
                    new GoalCard[]{new GoalSymbol("2KK_"), new GoalPattern("3IPPL2")},
                    new ArrayList<>(List.of(hand)),
                    Color.RED);
        } catch (SyntaxException e) { return null; }
    }

    public static Table defaultTable() {
        try {
            return new Table(
                    defaultScoreboard(),
                    (ArrayList<GoalCard>) Arrays.stream(new GoalCard[]{
                            new GoalSymbol("3QKM"), new GoalPattern("2IIID0")
                    }).collect(Collectors.toList())
            );
        } catch (SyntaxException e) { return null; }
    }

    public static Scoreboard defaultScoreboard() {
        return new Scoreboard(
                (ArrayList<String>) Arrays.stream(new String[] {"wasd", "Lorem ipsum", "player_3"}).collect(Collectors.toList()),
                (ArrayList<Integer>) Arrays.stream(new Integer[] {8, 17, 12}).collect(Collectors.toList()),
                defaultColors()
        );
    }

    public static ArrayList<Color> defaultColors() {
        ArrayList<Color> colors = new ArrayList<>();
        colors.add(Color.RED);
        colors.add(Color.GREEN);
        colors.add(Color.BLUE);
        colors.add(Color.YELLOW);
        return colors;
    }
}
