package IS24_LB11.game;

import IS24_LB11.game.components.*;
import IS24_LB11.game.symbol.Item;
import IS24_LB11.game.symbol.Suit;
import IS24_LB11.game.symbol.Symbol;
import IS24_LB11.game.tools.JsonConverter;
import IS24_LB11.game.tools.JsonException;
import IS24_LB11.game.utils.Position;
import IS24_LB11.game.utils.SyntaxException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

public class BoardTest {

    @Test
    public void testInvalidPlaceCard() throws SyntaxException, JsonException {
        Board board = new Board();
        JsonConverter jsonConverter = new JsonConverter();
        board.start((StarterCard)CardFactory.newPlayableCard("SEPIE_F0I__FPIA"));
        board.placeCard((GoldenCard)CardFactory.newPlayableCard("G_EEQFF1QFFA__"),new Position(1,1));
        board.placeCard((GoldenCard)CardFactory.newPlayableCard("GE_EEAF2EAAAF_"),new Position(-1,1));
        board.placeCard((GoldenCard)CardFactory.newPlayableCard("G_EEEAF2EAAAP_"),new Position(-1,-1));
        board.placeCard((GoldenCard)CardFactory.newPlayableCard("GEM__IF3_III__"),new Position(1,-1));

        System.out.println(jsonConverter.objectToJSON(board));
    }
    @Test
    void testPlacement() throws SyntaxException {
        Placement[] placements = new Placement[] {
                new Placement(
                    true,
                    new NormalCard("IPK_IF0"),
                    new Position(1, -1), // Ok placement
                    PositionArray(new Integer[][] { {-1,-1}, {-1,1}, {1,1}, {0,-2}, {2,-2} })
                ),
                new Placement(
                    false,
                    new NormalCard("EAE_AF1"),
                    new Position(2, -1), // Wrong placement (a card can't cover 2 corners of the same card)
                    PositionArray(new Integer[][] { {-1,-1}, {-1,1}, {1,1}, {0,-2}, {2,-2} }) // Available spots unchanged
                ),
                new Placement(
                    false,
                    new NormalCard("EAE_AB1"),
                    new Position(1, -1), // Wrong placement (can't place one card on top of the other)
                    PositionArray(new Integer[][] { {-1,-1}, {-1,1}, {1,1}, {0,-2}, {2,-2} }) // Available spots unchanged
                ),
                new Placement(
                    false,
                    new NormalCard("EAE_AB1"),
                    new Position(2, 0), // Wrong placement (can't place a card over a missing corner)
                    PositionArray(new Integer[][] { {-1,-1}, {-1,1}, {1,1}, {0,-2}, {2,-2} }) // Available spots unchanged
                ),
                new Placement(
                    false,
                    new NormalCard("EAE_AB1"),
                    new Position(2, 2), // Wrong placement (can't place a card "disconnected" from the others)
                    PositionArray(new Integer[][] { {-1,-1}, {-1,1}, {1,1}, {0,-2}, {2,-2} }) // Available spots unchanged
                ),
                new Placement(
                    true,
                    new NormalCard("EAE_AB1"),
                    new Position(0, -2), // Ok placement
                    PositionArray(new Integer[][] { {-1,-1}, {-1,1}, {1,1}, {2,-2}, {2,-2}, {-1,-3}, {1,-3} })
                ),
        };

        Board board = new Board();
        board.start(new StarterCard("EEEE_F0AI_PIAF"));

        for (Position spot: PositionArray(new Integer[][] { {-1,-1}, {1,-1}, {-1,1}, {1,1} }))
            assert (board.spotAvailable(spot));

        for (Placement placement : placements) {
            boolean result = board.placeCard(placement.card(), placement.position());
            Assertions.assertEquals(placement.isGood(), result, String.format("placement of %s", placement.card().asString()));
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


    @Test
    void testCountD0Patter() throws SyntaxException{
        GoalPattern goalD0 = (GoalPattern) CardFactory.newSerialCard("O2IIID0");

        Board board = new Board();
        board.start((StarterCard)CardFactory.newPlayableCard("SEEEE_F0PF_IAFP"));

        assert board.placeCard(CardFactory.newPlayableCard("NIIE_IF0"), new Position(-1,-1));
        assert board.placeCard(CardFactory.newPlayableCard("NI_IEIF0"), new Position(-2,-2));
        assert board.placeCard(CardFactory.newPlayableCard("N_EIIIF0"), new Position(-3,-3));
        assert board.placeCard(CardFactory.newPlayableCard("NEI_IIF0"), new Position(1,1));
        assert board.placeCard(CardFactory.newPlayableCard("N_QAIIF0"), new Position(2,2));
        assert board.placeCard(CardFactory.newPlayableCard("NMI_FIF0"), new Position(3,3));
        assert board.placeCard(CardFactory.newPlayableCard("NEE_IIF1"), new Position(4,4));
        assert board.placeCard(CardFactory.newPlayableCard("NI_EEIF1"), new Position(5,5));
        assert board.placeCard(CardFactory.newPlayableCard("NIPK_IF0"), new Position(6,6));

        assert ((board.countPatterns(goalD0)) == 6);
    }
    @Test
    void testCountD1Pattern() throws SyntaxException{
        GoalPattern goalD1 = (GoalPattern) CardFactory.newSerialCard("O2IIID1");

        Board board = new Board();
        board.start((StarterCard)CardFactory.newPlayableCard("SEEEE_F0AI_PIAF"));


        assert board.placeCard(CardFactory.newPlayableCard("GEEE_IB2EIIIP_"), new Position(-1,1));
        assert board.placeCard(CardFactory.newPlayableCard("N_QAIIB0"), new Position(-2,2));
        assert board.placeCard(CardFactory.newPlayableCard("GE_EEIB2EIIIF_"), new Position(-3,3));
        assert board.placeCard(CardFactory.newPlayableCard("GK_E_IB3_III__"), new Position(1,-1));
        assert board.placeCard(CardFactory.newPlayableCard("GEE__IB5_IIIII"), new Position(2,-2));
        assert board.placeCard(CardFactory.newPlayableCard("NMI_FIB0"), new Position(3,-3));
        assert board.placeCard(CardFactory.newPlayableCard("NIPK_IB0"), new Position(4,-4));
        assert board.placeCard(CardFactory.newPlayableCard("NI_EEIB1"), new Position(5,-5));
        assert board.placeCard(CardFactory.newPlayableCard("NEE_IIB1"), new Position(6,-6));

        assert ((board.countPatterns(goalD1)) == 6);
    }



 @Test
    void testCountL0Pattern() throws SyntaxException {
        GoalPattern goalL0 = (GoalPattern) CardFactory.newSerialCard("O3AIIL0");

        Board board = new Board();
        board.start((StarterCard)CardFactory.newPlayableCard("SEPIE_F0I__FPIA"));


   }

}

record Placement(
        boolean isGood,
        PlayableCard card,
        Position position,
        Position[] availableSpot
) { }
