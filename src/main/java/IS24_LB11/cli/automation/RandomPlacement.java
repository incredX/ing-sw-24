package IS24_LB11.cli.automation;

import IS24_LB11.game.Board;
import IS24_LB11.game.utils.Position;
import com.google.gson.JsonObject;

import java.util.Random;

public class RandomPlacement implements PlacementFunction {
    private static Random rand = new Random();
    private int numPlacements;
    private float oldestSpotsRate;

    public RandomPlacement(JsonObject object) {
        numPlacements = object.get("numPlacements").getAsInt();
        oldestSpotsRate = object.get("oldestSpotsRate").getAsFloat();
    }

    public Position getSpot(Board board) {
        if (numPlacements == 0) { return null; }
        int numSpots = board.getAvailableSpots().size();
        numPlacements--;
        if (rand.nextFloat() < oldestSpotsRate || numSpots <= 4) {
            return board.getAvailableSpots().get(rand.nextInt(Integer.min(numSpots, 4)));
        } else {
            return board.getAvailableSpots().get(rand.nextInt(4, numSpots));
        }
    }

    public boolean placementTerminated() {
        return numPlacements <= 0;
    }
}
