package IS24_LB11.cli.automation;

import IS24_LB11.game.Board;
import IS24_LB11.game.utils.Position;

public interface PlacementFunction {
    Position getSpot(Board board);
    boolean placementTerminated();
}
