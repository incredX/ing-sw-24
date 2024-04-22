package IS24_LB11.game;

import java.util.*;

import IS24_LB11.game.components.*;
import IS24_LB11.game.symbol.Item;
import IS24_LB11.game.symbol.Suit;
import IS24_LB11.game.symbol.Symbol;
import IS24_LB11.game.utils.Direction;
import IS24_LB11.game.utils.Position;
import IS24_LB11.game.utils.SyntaxException;

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
        for (Suit suit : Suit.values()) symbolCounter.put(suit, 0);
        for (Item item : Item.values()) symbolCounter.put(item, 0);
        updateCounters(start);
        updateSpots(start);
    }

    /**
     * place the specified card in the given position, if able.
     * To be placed, the card's position must belong to the available spots.
     *
     * @param card     <code>PlayableCard</code> to be placed
     * @param position where the card is to be placed
     * @return true if the card has been placed
     */
    public boolean placeCard(PlayableCard card, Position position) throws SyntaxException {
        if (!spotAvailable(position)) return false;
        if (card.asString().charAt(0) == 'G' && !placeGoldCardCheck((GoldenCard) card) && !card.isFaceDown()) return false;
        placedCards.add(new PlacedCard(card, position));
        updateCounters(position);
        updateSpots(position);
        return true;
    }

    public boolean placeGoldCardCheck(GoldenCard card) throws SyntaxException {
        ArrayList<Suit> suitNeeded = card.getSuitsNeeded();
        for (Symbol symbol:symbolCounter.keySet())
            if (symbolCounter.get(symbol)<suitNeeded.stream().filter(x->x==symbol).count())
                return false;
        return true;
    }

    private void updateSpots(Position position) {
        availableSpots.removeIf(spot -> spot.equals(position));
        getPlayableCard(position).ifPresent(card ->
                Direction.forEachDirection(corner -> {
                    Position diagonal = position.withRelative(corner.relativePosition());
                    if (card.hasCorner(corner) && !(spotAvailable(diagonal) || spotTaken(diagonal))) {
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
                    symbolCounter.computeIfPresent(symbol, (s, count) -> count - 1);
                }
            });
        });
    }

    public int countGoalPatterns(GoalPattern goal) {
        ArrayList<Symbol> symbols = goal.getSymbols();
        Position[] steps = goal.getPatternSteps();
        Integer patternsFound = 0;
        for (PlacedCard placedCard : placedCards.stream().skip(1).toList()) {
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
            for (PlacedCard placedCard : placedCards.stream().skip(1).toList())
                getPlacedCard(placedCard.position()).ifPresent(card -> card.setVisited(false));
        }
        return patternsFound * (goal.getPoints());
    }

    public int countGoalSymbols(GoalSymbol goal) {
        HashMap<Symbol, Integer> symbols = new HashMap<>();
        goal.getSymbols().stream().forEach(symbol -> {
            if (!symbols.containsKey(symbol)) symbols.put(symbol, 1);
            else symbols.put(symbol, symbols.get(symbol)+1);
        });
        return goal.getPoints() * symbols.entrySet().stream()
                .map(entry -> symbolCounter.get(entry.getKey())/entry.getValue())
                .min(Integer::compareTo).get();
    }





    public int calculateScoreOnLastPlacedCard() {
        PlayableCard playableCard = placedCards.getLast().card();
        int score = playableCard.asString().charAt(7)-48;
        HashMap<Symbol, Integer> symbolCounter = getSymbolCounter();
        if (playableCard.isFaceDown()) return 0;
        switch (playableCard.asString().charAt(0)) {
            case 'N':
                return score;
            case 'G':
                switch (playableCard.asString().charAt(8)) {
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
                    case '_':
                        return score;
                    case 'E':
                        Position positionLastCard = placedCards.getLast().position();
                        return (int) (score * placedCards.stream().filter(card -> Math.abs(card.position().getY() - positionLastCard.getY())==1 && Math.abs(card.position().getX() - positionLastCard.getX())==1).count());
                    default:
                        return 0;
                }
            default:
                return 0;
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

    public ArrayList<Position> getAvailableSpots() {
        return availableSpots;
    }
}
