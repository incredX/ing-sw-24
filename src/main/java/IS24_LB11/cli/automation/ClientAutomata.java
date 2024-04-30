package IS24_LB11.cli.automation;

import IS24_LB11.cli.Debugger;
import IS24_LB11.cli.ViewHub;
import IS24_LB11.cli.controller.AutomatedState;
import IS24_LB11.cli.controller.ClientState;
import IS24_LB11.cli.listeners.InputListener;
import IS24_LB11.cli.listeners.ResizeListener;
import IS24_LB11.game.Result;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import static IS24_LB11.game.Result.Error;
import static IS24_LB11.game.Result.Ok;

public class ClientAutomata {
    public static void main(String[] args) {
        ViewHub viewHub;
        InputListener inputListener;
        ResizeListener resizeListener;
        JsonObject automataSetup;
        Result<AutomatedState> automatedClientResult;
        ClientState state;

        // chack for command line argument (filepath of automata settings)
        if (args.length == 0) {
            Debugger.print("missing command line argument");
            return;
        }

        // boot the debbuger
        try {
            Debugger.startDebugger(Debugger.DIR_NAME);
            automataSetup = new JsonParser().parse(new FileReader(args[0])).getAsJsonObject();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }

        // build AutomatedClient from the automata setup
        try {
            viewHub = new ViewHub();
            automatedClientResult = buildFromJson(viewHub, automataSetup);
        } catch (IOException e) {
            Debugger.print(e);
            return;
        }
        Debugger.print("Starting automa");

        // execute automataClient and save the given next ClientState
        if (automatedClientResult.isError()) {
            Debugger.print(automatedClientResult.getError());
            return;
        }
        state = automatedClientResult.get();

        // instance the listeners
        try {
            resizeListener = new ResizeListener(state);
            inputListener = new InputListener(state);
        } catch (IOException e) {
            Debugger.print(e);
            return;
        }

        // boot the listeners
        Thread resizeThread = new Thread(resizeListener);
        Thread inputThread = new Thread(inputListener);
        Thread viewThread = new Thread(viewHub);

        resizeThread.start();
        inputThread.start();
        viewThread.start();

        // boot the state cycle
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

        // shutdown everything
        inputListener.shutdown();
        resizeThread.interrupt();
        viewThread.interrupt();

        try { Thread.sleep(200); }
        catch (InterruptedException e) { Debugger.print(e); }

        Debugger.closeDebugger();
        System.exit(0);
    }

    private static Result<AutomatedState> buildFromJson(ViewHub viewHub, JsonObject object) {
        return checkProperties(object, "username", "serverAddress", "serverPort", "numPlayers", "cardPlacement", "goldenRate")
                .andThen(ClientAutomata::placementFunctionFactory)
                .map(function -> new AutomatedState (
                        viewHub,
                        object.get("username").getAsString(),
                        object.get("serverAddress").getAsString(),
                        object.get("serverPort").getAsInt(),
                        object.get("numPlayers").getAsInt(),
                        object.get("goldenRate").getAsFloat(),
                        function)
                );
    }

    private static Result<JsonObject> checkProperties(JsonObject object, String... keys) {
        for (String key : keys) {
            if (!object.has(key)) return Result.Error("missing property " + key);
        }
        return Ok(object);
    }

    private static Result<PlacementFunction> placementFunctionFactory(JsonObject automataSetup) {
        return switch (automataSetup.get("cardPlacement").getAsString().toLowerCase()) {
            case "random" -> Ok(new RandomPlacement(automataSetup));
            default -> Error("unexpected cardPlacement criterion: " + automataSetup.get("cardPlacement").getAsString());
        };
    }
}
