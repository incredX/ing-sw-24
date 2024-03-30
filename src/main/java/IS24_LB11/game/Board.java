package IS24_LB11.game;

import java.util.*;
import java.util.function.BiConsumer;

import IS24_LB11.game.components.PlayableCard;
import IS24_LB11.game.components.StarterCard;
import IS24_LB11.game.symbol.Item;
import IS24_LB11.game.symbol.Suit;
import IS24_LB11.game.symbol.Symbol;
import IS24_LB11.game.utils.Corners;
import IS24_LB11.game.utils.Position;

public class Board {
    private final ArrayList<PlacedCard> placedCards;
    private final ArrayList<Position> availableSpots;
    private final HashMap<Symbol, Integer> symbolCounter;

    public Board() {
        placedCards = new ArrayList<>();
        availableSpots = new ArrayList<>();
        symbolCounter = new HashMap<>();
    }

    /**
     * constrain the initialization of the board with a <code>StarterCard</code>
     *
     * @param starterCard first card of the board
     */
    public void start(StarterCard starterCard) {
        Position start = new Position(0, 0);
        placedCards.add(new PlacedCard(starterCard, start));
        for (Suit suit: Suit.values()) symbolCounter.put(suit, 0);
        for (Item item: Item.values()) symbolCounter.put(item, 0);
        updateCounters(start);
        updateSpots(start);
    }

    /**
     * place the specified card in the given position, if able.
     * To be placed, the card's position must belong to the available spots.
     *
     * @param card <code>PlayableCard</code> to be placed
     * @param position where the card is to be placed
     * @return true if the card has been placed
     */
    public boolean placeCard(PlayableCard card, Position position) {
        if (!spotAvailable(position)) return false;
        placedCards.add(new PlacedCard(card, position));
        updateCounters(position);
        updateSpots(position);
        return true;
    }

    private void updateSpots(Position position) {
        availableSpots.removeIf(spot -> spot.equals(position));

        getPlayableCard(position).ifPresent(card ->
            forEachDiagonal(position, (diagonal, direction) -> {
                if (card.hasCorner(direction) && !(spotAvailable(diagonal) || spotTaken(diagonal))){
                    availableSpots.add(diagonal);
                }
            })
        );
    }

    private void updateCounters(Position position) {
        getPlayableCard(position).ifPresent(card -> card.updateCounters(symbolCounter));

        forEachDiagonal(position, (diagonal, direction) ->
            getPlayableCard(diagonal).ifPresent(card -> {
                if (!card.isFaceDown() && card.hasCorner(Corners.opposite(direction))) {
                    Symbol symbol = card.getCorner(Corners.opposite(direction));
                    symbolCounter.computeIfPresent(symbol, (s, count) -> count-1);
                }
            })
        );
    }

    private void forEachDiagonal(Position position, BiConsumer<Position, Integer> consumer) {
        for (int dir=0; dir<4; dir++) {
            Position diagonalPosition = position.withRelative(2*(dir&1)-1, (dir&2)-1);
            consumer.accept(diagonalPosition, dir);
        }
    }

    public boolean spotTaken(Position position) {
        return placedCards.stream().anyMatch(card -> card.position().equals(position));
    }

    public boolean spotAvailable(Position position) {
        return availableSpots.stream().anyMatch(spot -> spot.equals(position));
    }

    public Optional<PlacedCard> getPlacedCard(Position position) {
        return placedCards.stream()
                .filter(card -> card.position().equals(position))
                .findFirst();
    }

    public Optional<PlayableCard> getPlayableCard(Position position) {
        return getPlacedCard(position).map(PlacedCard::card);
    }

    public ArrayList<PlacedCard> getPlacedCards() {
        return placedCards;
    }


    public HashMap<Symbol, Integer> getSymbolCounter() {
        return symbolCounter;
    }
    public ArrayList<Position> getAvailableSpots() {return availableSpots;}
}
