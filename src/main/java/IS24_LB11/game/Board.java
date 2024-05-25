package IS24_LB11.game;

import java.util.*;
import IS24_LB11.game.components.*;
import IS24_LB11.game.symbol.Item;
import IS24_LB11.game.symbol.Suit;
import IS24_LB11.game.symbol.Symbol;
import IS24_LB11.game.utils.Direction;
import IS24_LB11.game.utils.Position;

/**
 * Represents the game board that holds placed cards and manages available and closed spots,
 * as well as symbol counters.
 */
public class Board implements JsonConvertable {
    private final ArrayList<PlacedCard> placedCards;
    private final ArrayList<Position> availableSpots;
    private final ArrayList<Position> closedSpots;
    private final HashMap<Symbol, Integer> symbolCounter;

    /**
     * Constructs an empty game board.
     */
    public Board() {
        placedCards = new ArrayList<>();
        availableSpots = new ArrayList<>();
        closedSpots = new ArrayList<>();
        symbolCounter = new HashMap<>();
    }

    /**
     * Initializes the board with a {@link StarterCard}.
     *
     * @param starterCard the first card to be placed on the board
     */
    public void start(StarterCard starterCard) {
        Position start = new Position(0, 0);
        placedCards.add(new PlacedCard(starterCard, start));
        for (Suit suit : Suit.values()) {
            symbolCounter.put(suit, 0);
        }
        for (Item item : Item.values()) {
            symbolCounter.put(item, 0);
        }
        updateCounters(start);
        updateSpots(starterCard, start);
    }

    /**
     * Places the specified card at the given position if it is available.
     *
     * @param card the {@link PlayableCard} to be placed
     * @param position the position where the card is to be placed
     * @return true if the card was successfully placed, false otherwise
     */
    public boolean placeCard(PlayableCard card, Position position) {
        if (!spotAvailable(position)) return false;
        if (card.asString().charAt(0) == 'G' && !placeGoldCardCheck((GoldenCard) card) && !card.isFaceDown()) return false;
        placedCards.add(new PlacedCard(card, position));
        updateCounters(position);
        updateSpots(card, position);
        return true;
    }

    public Result<Position> tryPlaceCard(PlayableCard card, Position position) {
        if (!spotAvailable(position))
            return Result.Error("placement denied", String.format("[%s] is not an available spot", position));
        if (card.asString().charAt(0) == 'G' && !placeGoldCardCheck((GoldenCard) card) && !card.isFaceDown())
            return Result.Error("placement denied", String.format("not enough suits to place %s", card.asString()));
        placedCards.add(new PlacedCard(card, position));
        updateCounters(position);
        updateSpots(card, position);
        return Result.Ok(position);
    }

    /**
     * Checks if the required suits for placing a golden card are met.
     *
     * @param card the {@link GoldenCard} to be placed
     * @return true if the required suits are met, false otherwise
     */
    public boolean placeGoldCardCheck(GoldenCard card) {
        ArrayList<Suit> suitNeeded = card.getSuitsNeeded();
        for (Symbol symbol : symbolCounter.keySet()) {
            if (symbol!=null){
                if (symbolCounter.get(symbol) < suitNeeded.stream().filter(x -> symbol.equals(x)).count()) {
                    return false;
                }
            }
        }
        return true;
    }

    private void updateSpots(PlayableCard card, Position position) {
        availableSpots.removeIf(spot -> spot.equals(position));
        Direction.forEachDirection(dir -> {
            Position diagonal = position.withRelative(dir.relativePosition());
            if (!card.hasCorner(dir)) {
                availableSpots.removeIf(spot -> spot.equals(diagonal));
                closedSpots.add(diagonal);
            } else if (!(spotAvailable(diagonal) || spotClosed(diagonal) || spotTaken(diagonal))) {
                availableSpots.add(diagonal);
            }
        });
    }

    private void updateCounters(Position position) {
        getPlayableCard(position).ifPresent(card -> card.updateCounters(symbolCounter));
        Direction.forEachDirection(corner -> {
            Position cornerPosition = position.withRelative(corner.relativePosition());
            getPlayableCard(cornerPosition).ifPresent(card -> {
                Symbol symbol = card.getCorner(corner.opposite());
                symbolCounter.computeIfPresent(symbol, (s, count) -> count - 1);
            });
        });
    }

    /**
     * Counts the number of patterns matching the specified goal pattern on the board.
     *
     * @param goal the {@link GoalPattern} to count matches for
     * @return the number of matching patterns multiplied by the goal points
     */
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
                    if (!card.get().isVisited() && card.get().card().getSuit() == symbols.get(counter)) {
                        matchedSteps++;
                    }
                }
                counter++;
            }
            if (matchedSteps == counter) {
                patternsFound++;
                placedCard.setVisited(true);
                for (Position step : steps) {
                    getPlacedCard(position.withRelative(step)).ifPresent(card -> card.setVisited(true));
                }
            }
        }
        for (PlacedCard placedCard : placedCards.stream().skip(1).toList()) {
            getPlacedCard(placedCard.position()).ifPresent(card -> card.setVisited(false));
        }
        return patternsFound * goal.getPoints();
    }

    /**
     * Counts the number of goal symbols present on the board for the specified goal.
     *
     * @param goal the {@link GoalSymbol} to count symbols for
     * @return the number of goal symbols multiplied by the goal points
     */
    public int countGoalSymbols(GoalSymbol goal) {
        HashMap<Symbol, Integer> symbols = new HashMap<>();
        goal.getSymbols().forEach(symbol -> {
            symbols.put(symbol, symbols.getOrDefault(symbol, 0) + 1);
        });
        return goal.getPoints() * symbols.entrySet().stream()
                .map(entry -> symbolCounter.get(entry.getKey()) / entry.getValue())
                .min(Integer::compareTo).orElse(0);
    }

    /**
     * Calculates the score based on the last placed card on the board.
     *
     * @return the score of the last placed card
     */
    public int calculateScoreOnLastPlacedCard() {
        PlayableCard playableCard = placedCards.getLast().card();
        int score = Character.getNumericValue(playableCard.asString().charAt(7));
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
                        return (int) (score * placedCards.stream().filter(card ->
                                Math.abs(card.position().getY() - positionLastCard.getY()) == 1 &&
                                        Math.abs(card.position().getX() - positionLastCard.getX()) == 1).count());
                    default:
                        return 0;
                }
            default:
                return 0;
        }
    }

    /**
     * Checks if a given position is taken by a card.
     *
     * @param position the position to check
     * @return true if the position is taken, false otherwise
     */
    public boolean spotTaken(Position position) {
        return placedCards.stream().anyMatch(card -> card.position().equals(position));
    }

    /**
     * Checks if a given position is available for placing a card.
     *
     * @param position the position to check
     * @return true if the position is available, false otherwise
     */
    public boolean spotAvailable(Position position) {
        return availableSpots.stream().anyMatch(spot -> spot.equals(position));
    }

    /**
     * Checks if a given position is closed and not available for placing a card.
     *
     * @param position the position to check
     * @return true if the position is closed, false otherwise
     */
    public boolean spotClosed(Position position) {
        return closedSpots.stream().anyMatch(spot -> spot.equals(position));
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
