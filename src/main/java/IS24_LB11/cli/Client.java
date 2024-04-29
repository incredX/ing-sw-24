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
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class Client {
    public static void main(String[] args) {
        start(args);
    }

    public static void start(String[] args) {
        ClientState state;
        ViewHub viewHub;
        InputListener inputListener;
        ResizeListener resizeListener;
        ServerHandler serverHandler;
        HashMap<String, Thread> threadMap = new HashMap<>();

        //Debugger.startDebugger();
        //Debugger.closeDebugger();

        try {
            viewHub = new ViewHub();
            if (args.length == 1 && args[0].equals("setup")) {
                state = new SetupState(viewHub, getDefaultSetup(), defaultTable());
            } else if (args.length == 1 && args[0].equals("game")) {
                state = new GameState(viewHub, new NotificationStack(viewHub, 0), getDefaultSetup(), defaultTable());
            } else {
                state = new LobbyState(viewHub);
            }
            resizeListener = new ResizeListener(state);
            inputListener = new InputListener(state);
            serverHandler = new ServerHandler(state, "127.0.0.1", 54321);
            state.setServerHandler(serverHandler);
        } catch (IOException e) {
            Debugger.print(e);
            return;
        }

        threadMap.put("views", new Thread(viewHub));
        threadMap.put("input", new Thread(inputListener));
        threadMap.put("resize", new Thread(resizeListener));
        threadMap.put("server", new Thread(serverHandler));

        for (Thread t: threadMap.values()) t.start();

        JsonParser jsonParser = new JsonParser();
        for (String arg: Arrays.stream(args).skip(1).toArray(String[]::new)) {
            try {
                serverHandler.write(jsonParser.parse(arg).getAsJsonObject());
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
                serverHandler.setState(state);
                state.setServerHandler(serverHandler);
            }
        }

        Debugger.print("closing client.");

        inputListener.shutdown();
        serverHandler.shutdown();
        threadMap.get("views").interrupt();
        threadMap.get("resize").interrupt();
        threadMap.get("server").interrupt();

        try { Thread.sleep(200); }
        catch (InterruptedException e) { Debugger.print(e); }

        Debugger.closeDebugger();
        System.exit(0);
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
