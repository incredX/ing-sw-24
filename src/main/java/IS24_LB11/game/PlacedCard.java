package IS24_LB11.game;

import IS24_LB11.game.components.PlayableCard;
import IS24_LB11.game.utils.Position;

public class PlacedCard {
    private final Position position;
    private final PlayableCard card;

    public PlacedCard(PlayableCard card, Position position) {
        this.card = card;
        this.position = position;
    }

    public PlacedCard(PlayableCard card, int x, int y) {
        this(card, new Position(x, y));
    }

    public PlayableCard getCard() {
        return card;
    }

    public Position getPosition() {
        return position;
    }
}
