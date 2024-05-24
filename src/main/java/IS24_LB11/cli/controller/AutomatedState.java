package IS24_LB11.cli.controller;

import IS24_LB11.cli.Debugger;
import IS24_LB11.cli.Table;
import IS24_LB11.cli.ViewHub;
import IS24_LB11.cli.automation.PlacementFunction;
import IS24_LB11.cli.event.server.*;
import IS24_LB11.cli.listeners.ServerHandler;
import IS24_LB11.game.PlacedCard;
import IS24_LB11.game.Player;
import IS24_LB11.game.PlayerSetup;
import IS24_LB11.game.components.PlayableCard;
import IS24_LB11.game.tools.JsonConverter;
import IS24_LB11.game.tools.JsonException;
import IS24_LB11.game.utils.Position;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.input.KeyStroke;

import java.util.Random;

public class AutomatedState extends ClientState {
    private static Random rand = new Random();

    private PlacementFunction placementFunction;
    private PlayerSetup playerSetup;
    private Player player;
    private Table table;
    private int numPlayers;
    private float goldenRate;

    public AutomatedState(ViewHub viewHub, String username,
                          String serverAddress, int serverPort,
                          int numPlayers, float goldenRate, PlacementFunction placementFunction) {
        super(viewHub);
        this.username = username;
        this.placementFunction = placementFunction;
        this.numPlayers = numPlayers;
        this.goldenRate = goldenRate;
        this.serverHandler = new ServerHandler(this, serverAddress, serverPort);
    }

    @Override
    public ClientState execute() {
        System.out.println("Running automated state...");
        new Thread(serverHandler).start();

        try { Thread.sleep(200); }
        catch (InterruptedException e) { Debugger.print(e); }

        if (numPlayers >= 2) {
            sendToServer("login", "username", username);
            sendToServer("numOfPlayers", "numOfPlayers", numPlayers);
        } else {
            try {
                Thread.sleep(500);
                sendToServer("login", "username", username);
            } catch (InterruptedException e) {
                Debugger.print(e);
            }
        }
        return super.execute();
    }

    @Override
    protected void processServerEvent(ServerEvent event) {
        if (processServerEventIfCommon(event)) return;
        switch (event) {
            case ServerHeartBeatEvent heartBeatEvent -> {
                sendToServer("heartbeat");
            }
            case ServerLoginEvent loginEvent -> {
                username = loginEvent.username();
            }
            case ServerPlayerSetupEvent setupEvent -> {
                playerSetup = setupEvent.setup();
                table = new Table(setupEvent);
                player = new Player(username, playerSetup);
                player.applySetup();
                sendToServer("setup",
                        new String[]{"starterCard","goalCard"},
                        new String[]{playerSetup.getStarterCard().asString(), playerSetup.chosenGoal().asString()});
                if (placementFunction.placementTerminated())
                    setNextState(new GameState(this));
            }
            case ServerNewTurnEvent turnEvent -> {
                table.update(turnEvent);
                if (turnEvent.player().isEmpty()) {
                    quit();
                    break;
                }
                if (!turnEvent.player().equals(username)) break;

                JsonConverter converter = new JsonConverter();
                Position spot = placementFunction.getSpot(player.getBoard());
                PlayableCard handCard = player.getHand().get(rand.nextInt(3));
                PlacedCard placedCard = new PlacedCard(handCard, spot);
                boolean fromGoldenDeck = rand.nextFloat() < goldenRate;
                int selectedCardIndex;

                if (fromGoldenDeck) {
                    if (table.getGoldenDeck().isEmpty()) {
                        fromGoldenDeck = false;
                        selectedCardIndex = rand.nextInt(table.getNormalDeck().size());
                    } else
                        selectedCardIndex = rand.nextInt(table.getGoldenDeck().size());
                } else {
                    if (table.getNormalDeck().isEmpty()) {
                        fromGoldenDeck = true;
                        selectedCardIndex = rand.nextInt(table.getGoldenDeck().size());
                    } else
                        selectedCardIndex = rand.nextInt(table.getNormalDeck().size());
                }

                if (!player.placeCard(handCard, spot)) {
                    if (!handCard.isFaceDown()) handCard.flip();
                    player.placeCard(handCard, spot);
                }
                player.addCardToHand(fromGoldenDeck ? table.getGoldenDeck().get(selectedCardIndex) : table.getNormalDeck().get(selectedCardIndex));
                try {
                    JsonObject jsonPlacedCard = (JsonObject) new JsonParser().parse(converter.objectToJSON(placedCard));
                    JsonElement jsonDeckType = new JsonPrimitive(fromGoldenDeck);
                    JsonElement jsonCardIndex = new JsonPrimitive(selectedCardIndex+1);
                    sendToServer("turnActions", new String[]{"placedCard", "deckType", "indexVisibleCards"},
                            new JsonElement[]{jsonPlacedCard, jsonDeckType, jsonCardIndex});
                } catch (JsonException e) {
                    Debugger.print(e);
                }

                if (placementFunction.placementTerminated())
                    setNextState(new GameState(this));
            }
            case ServerPlayerDisconnectEvent disconnectEvent -> {
                if (table != null)
                    table.getScoreboard().removePlayer(disconnectEvent.player());
            }
            default -> {}
        }
    }

    @Override
    protected void processCommand(String command) {
        //
    }

    @Override
    protected void processKeyStroke(KeyStroke keyStroke) {
        //
    }

    @Override
    protected void processResize(TerminalSize screenSize) {
        //
    }

    public Player getPlayer() {
        return player;
    }

    public Table getTable() {
        return table;
    }
}
