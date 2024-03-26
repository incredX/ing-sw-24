package IS24_LB11.game;

import IS24_LB11.game.components.NormalCard;
import IS24_LB11.game.components.PlayableCard;
import IS24_LB11.game.components.StarterCard;
import IS24_LB11.game.utils.Position;
import IS24_LB11.game.utils.SyntaxException;
import org.junit.jupiter.api.Test;

public class BoardTest {
    @Test
    void testAvailableSpots1() throws SyntaxException {
        PlayableCard[] cardSequence = new PlayableCard[] {
                new NormalCard("FEF_FF0"),
                new NormalCard("IPK_IF0"),
                new NormalCard("Q_AFAF0"),
        };
        Position[] placements = new Position[] {
                new Position(-1, -1), // Ok placement
                new Position(1, -1), // Ok placement
                new Position(1, 1), // Ok placement
        };
        Position[][] freeSpotsSequence = new Position[][] {
                PositionArray(new Integer[][] { {-1,-1}, {1,-1}, {-1,1}, {1,1} }),
                PositionArray(new Integer[][] { {1,-1}, {-1,1}, {1,1}, {-2,-2}, {0,-2}, {-2,0} }),
                PositionArray(new Integer[][] { {-1,1}, {1,1}, {-2,-2}, {0,-2}, {-2,0}, {2,-2} }),
                PositionArray(new Integer[][] { {-1,1}, {-2,-2}, {0,-2}, {-2,0}, {2,-2}, {0,2}, {2,2} }),
        };

        Board board = new Board();
        board.start(new StarterCard("EPIE_F0I__FPIA"));

        for (Position spot: freeSpotsSequence[0]) assert (board.spotAvailable(spot));

        for (int i=0; i< placements.length; i++) {
            assert (board.placeCard(cardSequence[i], placements[i]));
            for (Position spot: freeSpotsSequence[i+1]) {
                assert (board.spotAvailable(spot));
            }
        }
    }

    @Test
    void testAvailableSpots2() throws SyntaxException {
        PlayableCard[] cardSequence = new PlayableCard[] {
                new NormalCard("IPK_IF0"), // ok placement
                new NormalCard("EAE_AB1"), // wrong placement
                new NormalCard("EAE_AB1"), // another wrong placement
                new NormalCard("EAE_AB1"), // this one too
                new NormalCard("EAE_AB1"), // still wrong
        };
        boolean[] goodPlacements = new boolean[] {
                true, false, false, false, false
        };
        Position[] placements = new Position[] {
                new Position(1, -1), // Ok placement
                new Position(2, -1), // Wrong placement (2 cards can't be side by side)
                new Position(1, -1), // Wrong placement (can't place one card on top of the other)
                new Position(2, 0), // Wrong placement (can't place a card over a missing corner)
                new Position(2, 2), // Wrong placement (can't place a card "disconnected" from the others)
        };
        Position[][] freeSpotsSequence = new Position[][] {
                PositionArray(new Integer[][] { {-1,-1}, {1,-1}, {-1,1}, {1,1} }), // available spots after board init.
                PositionArray(new Integer[][] { {-1,-1}, {-1,1}, {1,1}, {0,-2}, {2,-2} }),
                PositionArray(new Integer[][] { {-1,-1}, {-1,1}, {1,1}, {0,-2}, {2,-2} }), // available spots unchanged
                PositionArray(new Integer[][] { {-1,-1}, {-1,1}, {1,1}, {0,-2}, {2,-2} }), // available spots unchanged
                PositionArray(new Integer[][] { {-1,-1}, {-1,1}, {1,1}, {0,-2}, {2,-2} }), // available spots unchanged
                PositionArray(new Integer[][] { {-1,-1}, {-1,1}, {1,1}, {0,-2}, {2,-2} }), // available spots unchanged
        };

        Board board = new Board();
        board.start(new StarterCard("EPIE_F0I__FPIA"));

        for (Position spot: freeSpotsSequence[0]) assert (board.spotAvailable(spot));

        for (int i=0; i< placements.length; i++) {
            boolean result = board.placeCard(cardSequence[i], placements[i]);
            assert (result == goodPlacements[i]);
            for (Position spot: freeSpotsSequence[i+1]) {
                assert (board.spotAvailable(spot));
            }
        }
    }

    private Position[] PositionArray(Integer[][] array) {
        Position[] positions = new Position[array.length];
        for (int i=0; i< array.length; i++) {
            positions[i] = new Position(array[i][0], array[i][1]);
        }
        return positions;
    }
}
