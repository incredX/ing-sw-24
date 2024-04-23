package IS24_LB11.cli.controller;

import IS24_LB11.cli.GameStage;
import IS24_LB11.cli.event.ServerEvent;
import IS24_LB11.cli.ViewHub;
import IS24_LB11.cli.utils.Side;
import IS24_LB11.game.Player;
import IS24_LB11.game.PlayerSetup;
import IS24_LB11.game.components.PlayableCard;
import IS24_LB11.game.utils.Position;
import com.googlecode.lanterna.input.KeyStroke;

import java.io.IOException;
import java.util.ArrayList;

public class ClientInGame extends ClientState {
    private final Player player;
    private GameStage gameStage;
    private Position pointer;
    private int selectedCard;

    public ClientInGame(ViewHub viewHub, PlayerSetup setup) throws IOException {
        super(viewHub);
        this.player = new Player(username, setup);
        this.selectedCard = 0;
    }

    @Override
    public ClientState execute() {
        player.getBoard().start(player.setup().getStarterCard());
        gameStage = viewHub.setGameStage(player);
        return super.execute();
    }

    @Override
    protected void processServerEvent(ServerEvent event) {
        if (processServerEventIfCommon(event)) return;
    }

    @Override
    protected void processCommand(String command) {
        if (processCommandIfCommon(command)) return;
        System.out.println(command);
        String[] tokens = command.split(" ", 2);
        switch (tokens[0].toUpperCase()) {
            case "SHOW" -> gameStage.showHand();
            case "HIDE" -> gameStage.hideHand();
            case "HOME" -> {
                gameStage.setPointer(new Position(0, 0));
                gameStage.centerGridBase();
                gameStage.rebuild();
            }
            default -> notificationStack.addUrgentPopUp("ERROR", tokens[0]+" is not a valid command");
        }
    }

    @Override
    protected void processKeyStroke(KeyStroke keyStroke) {
        if (notificationStack.consumeKeyStroke(keyStroke)) return;

        if (keyStroke.isShiftDown()) {
            switch (keyStroke.getKeyType()) {
                case ArrowUp -> { gameStage.shift(Side.NORD); return; }
                case ArrowDown -> { gameStage.shift(Side.SUD); return; }
                case ArrowLeft -> { gameStage.shift(Side.WEST); return; }
                case ArrowRight -> { gameStage.shift(Side.EAST); return; }
            }
        }
        if (keyStroke.isCtrlDown()) {
            switch (keyStroke.getKeyType()) {
                case ArrowUp -> {
                    selectPreviousCard();
                    gameStage.setSelectedCardInHand(selectedCard);
                    return;
                }
                case ArrowDown -> {
                    selectNextCard();
                    gameStage.setSelectedCardInHand(selectedCard);
                    return;
                }
                case Enter -> {
                    //TODO: export here stage.pointer
                    ArrayList<PlayableCard> hand = player.getHand();
                    if (hand.size() == 3 && player.placeCard(hand.get(selectedCard), gameStage.getPointer())) {
                        gameStage.placeSelectedCard();
                        gameStage.rebuild();
                    }
                    return;
                }
                case Character -> {
                    if (keyStroke.getCharacter() == 'f') {
                        player.getHand().get(selectedCard).flip();
                        gameStage.buildHandCard();
                        return;
                    }
                }
            }
        }
        super.processCommonKeyStrokes(keyStroke);
    }

    public void selectNextCard() {
        selectedCard = selectedCard == player.getHand().size() - 1 ? 0 : selectedCard + 1;
    }

    public void selectPreviousCard() {
        selectedCard = selectedCard == 0 ? player.getHand().size() - 1 : selectedCard - 1;
    }
}
