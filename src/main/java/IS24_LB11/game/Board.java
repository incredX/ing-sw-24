package IS24_LB11.game;

import java.util.*;

import IS24_LB11.game.components.JsonConvertable;
import IS24_LB11.game.components.GoalPattern;
import IS24_LB11.game.components.PlayableCard;
import IS24_LB11.game.components.StarterCard;
import IS24_LB11.game.symbol.Item;
import IS24_LB11.game.symbol.Suit;
import IS24_LB11.game.symbol.Symbol;
import IS24_LB11.game.utils.Direction;
import IS24_LB11.game.utils.Position;

public class Board implements JsonConvertable {
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
                Direction.forEachDirection(corner -> {
                    Position diagonal = position.withRelative(corner.relativePosition());
                    if (card.hasCorner(corner) && !(spotAvailable(diagonal) || spotTaken(diagonal))){
                        availableSpots.add(diagonal);
                    }
                })
        );
    }

    private void updateCounters(Position position) {
        getPlayableCard(position).ifPresent(card -> card.updateCounters(symbolCounter));

        Direction.forEachDirection(corner -> {
            Position cornerPosition = position.withRelative(corner.relativePosition());
            getPlayableCard(cornerPosition).ifPresent(card -> {
                if (!card.isFaceDown() && card.hasCorner(corner.opposite())) {
                    Symbol symbol = card.getCorner(corner.opposite());
                    symbolCounter.computeIfPresent(symbol, (s, count) -> count-1);
                }
            });
        });
    }

    public int countPatterns(GoalPattern goal) {
        ArrayList<Symbol> symbols = goal.getSymbols();
        Position[] steps = goal.getPatternSteps();
        Integer patternsFound = 0;
        for(PlacedCard placedCard: placedCards.stream().skip(1).toList()) {
            Position position = placedCard.position();
            int matchedSteps = 1, counter = 1;
            if (placedCard.isVisited() || placedCard.card().getSuit() != symbols.getFirst()) continue;
            for (Position step : steps) {
                Optional<PlacedCard> card = getPlacedCard(position.withRelative(step));
                if (card.isPresent()) {
                    if (!card.get().isVisited() && card.get().card().getSuit() == symbols.get(counter))
                        matchedSteps++;
                }
                counter++;
            }
            if (matchedSteps == counter) {
                patternsFound++;
                placedCard.setVisited(true);
                for (Position step : steps)
                    getPlacedCard(position.withRelative(step)).ifPresent(card -> card.setVisited(true));
            }
        }
        if (patternsFound > 0) {
            for (PlacedCard placedCard: placedCards.stream().skip(1).toList())
                getPlacedCard(placedCard.position()).ifPresent(card -> card.setVisited(false));
        }
        return  patternsFound * (goal.getPoints());
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
