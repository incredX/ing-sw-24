package IS24_LB11.game;

import IS24_LB11.game.components.JsonConvertable;
import IS24_LB11.game.components.PlayableCard;
import IS24_LB11.game.symbol.Symbol;
import IS24_LB11.game.tools.JsonConverter;
import IS24_LB11.game.utils.Position;
import IS24_LB11.game.symbol.*;
import IS24_LB11.game.utils.SyntaxException;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;

public class Game {
    private String NOT_PLAYER_TURN = "Not this player turn";
    private String INVALID_POSITION_CARD_OR_NOT_IN_HAND = "Invalid placement of card or card is not in player's hand";

    private String VALID_TURN = "Valid turn executed";
    private String VALID_TURN_AND_TRIGGERED_FINAL_TURN = "Valid turn executed and score of 20 exceeded";

    private int turn;
    private final int numPlayers;
    private final Deck goalDeck;
    private final Deck goldenDeck;
    private final Deck normalDeck;
    private final ArrayList<Player> players;

    public Game(int numPlayers) throws SyntaxException, FileNotFoundException {
        JsonConverter jsonConverter = new JsonConverter();
        this.turn = 0;
        this.numPlayers = numPlayers;
        this.goalDeck = jsonConverter.JSONToDeck('O'); // <- here we load the deck from json
        this.goldenDeck = jsonConverter.JSONToDeck('G'); // <- here we load deck from json
        this.normalDeck = jsonConverter.JSONToDeck('N'); // <- here we load deck from json
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

    public boolean placeCardPhase(PlayableCard card, Position position, Player player) {
        return player.placeCard(card, position);
    }

    private Player currentPlayer() {
        return players.get(turn%numPlayers);
    }


    //DEVO TESTARE ANCORA TUTTO!!
    private int scoreTurnPlayer(Player player,PlayableCard playableCard){
        int score = Integer.valueOf(playableCard.asString().charAt(7));
        HashMap<Symbol, Integer> symbolCounter = player.getBoard().getSymbolCounter();
        if (playableCard.asString().startsWith("N"))
            return score;
        if (playableCard.asString().startsWith("G")){
            switch (playableCard.asString().charAt(8)){
                case 'A':
                    return symbolCounter.get(Suit.ANIMAL);
                case 'I':
                    return symbolCounter.get(Suit.INSECT);
                case 'F':
                    return symbolCounter.get(Suit.MUSHROOM);
                case 'P':
                    return symbolCounter.get(Suit.PLANT);
                case 'Q':
                    return symbolCounter.get(Item.QUILL);
                case 'K':
                    return symbolCounter.get(Item.INKWELL);
                case 'M':
                    return symbolCounter.get(Item.MANUSCRIPT);
                case 'E':
                    return score;
                default:
                    return 0;
            }
        }
        return 0;
    }
    private boolean isFinalTurn(Player player){
        return player.getScore()>=20;
    }
    public String executeTurn(Player player, Position position, PlayableCard playableCard, boolean deckType, int indexDeck) throws DeckException {
        if (player.name().compareTo(currentPlayer().name())!=0)
            return NOT_PLAYER_TURN;
        if (!placeCardPhase(playableCard, position,player))
            return INVALID_POSITION_CARD_OR_NOT_IN_HAND;
        else {
            if (playableCard.asString().charAt(7)!=0)
                player.incrementScore(scoreTurnPlayer(player,playableCard));
        }
        //0 for standard deck, 1 for gold deck
        if (deckType)
            normalDeck.drawCard(indexDeck);
        else
            goldenDeck.drawCard(indexDeck);
        turn++;
        if (isFinalTurn(player))
            return VALID_TURN_AND_TRIGGERED_FINAL_TURN;
        return VALID_TURN;
    }

}