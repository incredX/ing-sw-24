package IS24_LB11.game;

import IS24_LB11.game.components.JsonConvertable;
import IS24_LB11.game.components.PlayableCard;
import IS24_LB11.game.utils.Position;

/**
 * Represents a card that has been placed on the board at a specific position.
 */
public class PlacedCard implements JsonConvertable {
    private final PlayableCard card;
    private final Position position;
    private boolean visited;

    /**
     * Constructs a PlacedCard with the specified playable card and position.
     *
     * @param card the playable card to be placed
     * @param position the position where the card is placed
     */
    public PlacedCard(PlayableCard card, Position position) {
        this.card = card;
        this.position = position;
        this.visited = false;
    }

    public void setVisited(boolean value) {
        visited = value;
    }
    public boolean isVisited() {
        return visited;
    }
    public PlayableCard card() {
        return card;
    }
    public Position position() {
        return position;
    }
}