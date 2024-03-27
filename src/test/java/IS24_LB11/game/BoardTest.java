package IS24_LB11.game;

import IS24_LB11.game.components.NormalCard;
import IS24_LB11.game.components.PlayableCard;
import IS24_LB11.game.components.StarterCard;
import IS24_LB11.game.symbol.Item;
import IS24_LB11.game.symbol.Suit;
import IS24_LB11.game.symbol.Symbol;
import IS24_LB11.game.utils.Position;
import IS24_LB11.game.utils.SyntaxException;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

public class BoardTest {
    @Test
    void testPlacement() throws SyntaxException {
        Placement[] placements = new Placement[] {
                new Placement(
                    true,
                    new NormalCard("IPK_IF0"),
                    new Position(1, -1), // ok placement
                    PositionArray(new Integer[][] { {-1,-1}, {-1,1}, {1,1}, {0,-2}, {2,-2} })
                ),
                new Placement(
                    false,
                    new NormalCard("EAE_AB1"),
                    new Position(2, -1), // Wrong placement (2 cards can't be side by side)
                    PositionArray(new Integer[][] { {-1,-1}, {-1,1}, {1,1}, {0,-2}, {2,-2} }) // available spots unchanged
                ),
                new Placement(
                    false,
                    new NormalCard("EAE_AB1"),
                    new Position(1, -1), // Wrong placement (can't place one card on top of the other)
                    PositionArray(new Integer[][] { {-1,-1}, {-1,1}, {1,1}, {0,-2}, {2,-2} }) // available spots unchanged
                ),
                new Placement(
                    false,
                    new NormalCard("EAE_AB1"),
                    new Position(2, 0), // Wrong placement (can't place a card over a missing corner)
                    PositionArray(new Integer[][] { {-1,-1}, {-1,1}, {1,1}, {0,-2}, {2,-2} }) // available spots unchanged
                ),
                new Placement(
                    false,
                    new NormalCard("EAE_AB1"),
                    new Position(2, 2), // Wrong placement (can't place a card "disconnected" from the others)
                    PositionArray(new Integer[][] { {-1,-1}, {-1,1}, {1,1}, {0,-2}, {2,-2} }) // available spots unchanged
                ),
                new Placement(
                    true,
                    new NormalCard("EAE_AB1"),
                    new Position(0, -2), // ok placement
                    PositionArray(new Integer[][] { {-1,-1}, {-1,1}, {1,1}, {2,-2}, {2,-2}, {-1,-3}, {1,-3} })
                ),
        };

        Board board = new Board();
        board.start(new StarterCard("EEEE_F0AI_PIAF"));

        for (Position spot: PositionArray(new Integer[][] { {-1,-1}, {1,-1}, {-1,1}, {1,1} }))
            assert (board.spotAvailable(spot));

        for (Placement placement : placements) {
            boolean result = board.placeCard(placement.card(), placement.position());
            assert (result == placement.isGood());
            for (Position spot : placement.availableSpot()) {
                assert (board.spotAvailable(spot));
            }
        }
    }

    @Test
    void testCountersUpdate() throws SyntaxException {
        Board board = new Board();
        board.start(new StarterCard("EEEE_F0AI_PIAF"));
        assert board.placeCard(new NormalCard("IPK_IF0"), new Position(1,-1));
        assert board.placeCard(new NormalCard("EAE_AB1"), new Position(-1,-1));
        assert board.placeCard(new NormalCard("FEF_FF0"), new Position(0,-2));

        HashMap<Symbol, Integer> counters = new HashMap<>();
        counters.put(Suit.MUSHROOM, 2);
        counters.put(Suit.ANIMAL, 2);
        counters.put(Suit.INSECT, 1);
        counters.put(Suit.PLANT, 1);
        counters.put(Item.INKWELL, 1);

        counters.forEach((Symbol symbol, Integer count) -> {
            System.out.printf("%s : expected=%2d, actual=%2d\n", symbol, board.getSymbolCounter().get(symbol), count);
            assert (board.getSymbolCounter().get(symbol).compareTo(count) == 0);
        });
    }

    private Position[] PositionArray(Integer[][] array) {
        Position[] positions = new Position[array.length];
        for (int i=0; i< array.length; i++) {
            positions[i] = new Position(array[i][0], array[i][1]);
        }
        return positions;
    }
}

record Placement(
        boolean isGood,
        PlayableCard card,
        Position position,
        Position[] availableSpot
) { }