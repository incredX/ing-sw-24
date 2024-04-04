package IS24_LB11.game;

import IS24_LB11.game.components.PlayableCard;
import IS24_LB11.game.utils.Position;

public class PlacedCard {
    private final PlayableCard card;
    private final Position position;
    private boolean visited;

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
