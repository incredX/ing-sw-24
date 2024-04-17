package IS24_LB11.game;

import IS24_LB11.game.components.PlayableCard;
import IS24_LB11.game.utils.Position;

import java.util.ArrayList;

public class Game {
    private int turn;
    private final int numPlayers;
    private final Deck goalDeck;
    private final Deck goldenDeck;
    private final Deck normalDeck;
    private final ArrayList<Player> players;

    public Game(int numPlayers) {
        this.turn = 0;
        this.numPlayers = numPlayers;
        this.goalDeck = null; // <- here we load the deck from json
        this.goldenDeck = null; // <- here we load deck from json
        this.normalDeck = null; // <- here we load deck from json
        this.players = new ArrayList<>(numPlayers);
    }

    public Result<String> newPlayer(String name) {
        if (players.size() == numPlayers)
            return Result.Error("can't add new player", "room is full");
        if (players.stream().noneMatch(player -> player.name().equals(name))) {
            int colorIndex = players.size();
            //setups.add(new Player(name, Color.fromInt(colorIndex).get()));
            return Result.Ok("OK"); // <- still to be decided what return
        }
        return Result.Error("can't add player", "there is already a player with the same name");
    }

    public void placeCard(PlayableCard card, Position position) {
        Player currentPlayer = currentPlayer();
        currentPlayer.placeCard(card, position);
    }

    private Player currentPlayer() {
        return players.get(turn%numPlayers);
    }
}
