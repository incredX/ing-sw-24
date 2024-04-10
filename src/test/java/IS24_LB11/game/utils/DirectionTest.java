package IS24_LB11.game.utils;

import IS24_LB11.game.Result;
import org.junit.jupiter.api.Test;

import static IS24_LB11.game.utils.Direction.*;

public class DirectionTest {

    @Test
    void testOppositeDirection() {
        Direction.forEachDirection(direction -> {
            assert (direction == direction.opposite().opposite());
        });
    }

    @Test
    void testRelativePositions() {
        assert (UP_LEFT.relativePosition().equals(new Position(-1,-1)));
        assert (UP_RIGHT.relativePosition().equals(new Position(1,-1)));
        assert (DOWN_LEFT.relativePosition().equals(new Position(-1,1)));
        assert (DOWN_RIGHT.relativePosition().equals(new Position(1,1)));
    }

    @Test
    void testParsing() {
        assert (UP_LEFT == Direction.parse(0));
        assert (UP_RIGHT == Direction.parse(1));
        assert (DOWN_LEFT == Direction.parse(2));
        assert (DOWN_RIGHT == Direction.parse(3));
        assert (UP_LEFT == Direction.parse(4));
        assert (UP_RIGHT == Direction.parse(5));
        assert (DOWN_LEFT == Direction.parse(6));
        assert (DOWN_RIGHT == Direction.parse(7));
    }

    @Test
    void testTryParsing() {
        assert (UP_LEFT == Direction.tryParse("UP_LEFT").get());
        assert (UP_RIGHT == Direction.tryParse("UP_RIGHT").get());
        assert (DOWN_LEFT == Direction.tryParse("DOWN_LEFT").get());
        assert (DOWN_RIGHT == Direction.tryParse("DOWN_RIGHT").get());
        assert (UP_LEFT == Direction.tryParse(0).get());
        assert (UP_RIGHT == Direction.tryParse(1).get());
        assert (DOWN_LEFT == Direction.tryParse(2).get());
        assert (DOWN_RIGHT == Direction.tryParse(3).get());
        assert (Direction.tryParse("up_left").isError());
        assert (Direction.tryParse("UPLEFT").isError());
        assert (Direction.tryParse(-1).isError());
        assert (Direction.tryParse(4).isError());
    }
}
