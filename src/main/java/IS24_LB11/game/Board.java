package IS24_LB11.game;

import java.util.*;
import java.util.function.Consumer;

import IS24_LB11.game.components.PlayableCard;
import IS24_LB11.game.components.StarterCard;
import IS24_LB11.game.symbol.Empty;
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

    public void start(StarterCard starterCard) {
        Position start = new Position(0, 0);
        placedCards.add(new PlacedCard(starterCard, start));
        for (Suit suit: Suit.values()) symbolCounter.put(suit, 0);
        for (Item item: Item.values()) symbolCounter.put(item, 0);
        updateCounters(start);
        updateSpots(start);
    }

    public boolean placeCard(PlayableCard card, Position position) {
        if (!spotAvailable(position) || spotTaken(position)) return false;
        placedCards.add(new PlacedCard(card, position));
        updateCounters(position);
        updateSpots(position);
        return true;
    }

    private void updateSpots(Position position) {
        availableSpots.remove(position);

        getPlayableCard(position).ifPresent(card ->
            card.forEachDirection(dir -> {
                int dx = 2*(dir&1)-1; // dx = -1 || +1
                int dy = 2*(dir>>1)-1; // dy = -1 || +1
                Position cornerPosition = position.withRelative(dx, dy);
                if (card.hasCorner(dir) && !(spotAvailable(cornerPosition) || spotTaken(cornerPosition)))
                    availableSpots.add(cornerPosition);
            })
        );
    }

    private void updateCounters(Position position) {
        getPlayableCard(position).ifPresent(card -> card.updateCounters(symbolCounter));

        forEachCorner(position, corner ->
            getPlayableCard(corner).ifPresent(card -> {
                int x = (corner.getX() - position.getX() +1) / 2;
                int y = (corner.getY() - position.getY() +1) / 2;
                int dir = x + (y<<1);
                if (!card.isFaceDown() && card.hasCorner(Corners.opposite(dir))) {
                    Symbol symbol = card.getCorner(Corners.opposite(dir));
                    if (symbolCounter.containsKey(symbol))
                        symbolCounter.replace(symbol, symbolCounter.get(symbol) -1);
                }
            }
        ));
    }

    private void forEachCorner(Position position, Consumer<Position> consumer) {
        for (int x=-1; x<=1; x+=2) {
            for (int y=-1; y<=1; y+=2) {
                Position cornerPos = position.withRelative(x, y);
                consumer.accept(cornerPos);
            }
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
}
