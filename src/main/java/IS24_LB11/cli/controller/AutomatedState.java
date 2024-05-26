package IS24_LB11.cli.controller;

import IS24_LB11.cli.Debugger;
import IS24_LB11.cli.Table;
import IS24_LB11.cli.ViewHub;
import IS24_LB11.cli.automation.PlacementFunction;
import IS24_LB11.cli.event.server.*;
import IS24_LB11.cli.listeners.ServerHandler;
import IS24_LB11.cli.notification.Priority;
import IS24_LB11.cli.popup.DecksPopup;
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
    private int turn = 0;
    private float goldenRate;
    private boolean finalTurn = false;

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

        try { Thread.sleep(250); }
        catch (InterruptedException e) { Debugger.print(e); }

        if (numPlayers >= 2) {
            sendToServer("login", "username", username);
            try { Thread.sleep(250); }
            catch (InterruptedException e) { Debugger.print(e); }
            sendToServer("numOfPlayers", "numOfPlayers", numPlayers);
        } else {
            try {
                Thread.sleep(1000);
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

                finalTurn = (table.getNormalDeck().isEmpty() && table.getGoldenDeck().isEmpty())
                        || table.getScoreboard().getScores().get(table.getCurrentTopPlayerIndex()) >= 20;

                JsonConverter converter = new JsonConverter();
                Position spot = placementFunction.getSpot(player.getBoard());
                PlayableCard handCard = player.getHand().get(rand.nextInt(player.getHand().size()));
                PlacedCard placedCard = new PlacedCard(handCard, spot);
                boolean fromGoldenDeck = rand.nextFloat() < goldenRate;
                int selectedCardIndex;

                if (finalTurn) {
                    fromGoldenDeck = false;
                    selectedCardIndex = 0;
                } else if (fromGoldenDeck) {
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

                System.out.println("\nTURN " + turn);

                if (handCard.isFaceDown()) handCard.flip();
                player.tryPlaceCard(handCard, spot).ifError(result -> {
                    System.out.println("PLACEMENT ERROR : " + result.toString());
                    handCard.flip();
                    player.tryPlaceCard(handCard, spot).ifError(result2 -> {
                        System.out.println("PLACEMENT ERROR : " + result2.toString());
                    });
                });

                if (!finalTurn) {
                    player.addCardToHand(fromGoldenDeck ?
                            table.getGoldenDeck().get(selectedCardIndex) :
                            table.getNormalDeck().get(selectedCardIndex));
                }

                try {
                    JsonObject jsonPlacedCard = (JsonObject) new JsonParser().parse(converter.objectToJSON(placedCard));
                    JsonElement jsonDeckType = new JsonPrimitive(fromGoldenDeck);
                    JsonElement jsonCardIndex = new JsonPrimitive(selectedCardIndex+1);
                    sendToServer("turnActions", new String[]{"placedCard", "deckType", "indexVisibleCards"},
                            new JsonElement[]{jsonPlacedCard, jsonDeckType, jsonCardIndex});
                } catch (JsonException e) {
                    Debugger.print(e);
                }

                //processCommandSendtoall("turn "+turn+" done");
                turn++;

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

    private void sendTurnActions(PlacedCard placedCard) {
        JsonConverter converter = new JsonConverter();
        DecksPopup decksPopup = (DecksPopup) popManager.getPopup("decks");

        boolean deckType = finalTurn ? false : !decksPopup.selectedNormalDeck();
        int deckIndex = 1 + (finalTurn ? 0 : decksPopup.getCardIndex());


        try {
            JsonObject jsonPlacedCard = (JsonObject) new JsonParser().parse(converter.objectToJSON(placedCard));
            JsonElement jsonDeckType = new JsonPrimitive(deckType);
            JsonElement jsonCardIndex = new JsonPrimitive(deckIndex);
            sendToServer("turnActions", new String[]{"placedCard", "deckType", "indexVisibleCards"},
                    new JsonElement[]{jsonPlacedCard, jsonDeckType, jsonCardIndex});
        } catch (JsonException e) {
            Debugger.print(e);
        }
        notificationStack.removeNotifications(Priority.LOW);
    }

    public Player getPlayer() {
        return player;
    }

    public Table getTable() {
        return table;
    }
}
