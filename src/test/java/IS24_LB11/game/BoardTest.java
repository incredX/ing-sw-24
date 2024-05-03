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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;

public class BoardTest {

    @Test
    @DisplayName("Testing the invlid placement of a GoldenCard caused by the lack of symbols the inizialization of an existing StarterCard and ")
    public void testInvalidPlaceCard() throws SyntaxException, JsonException {
        Board board = new Board();
        JsonConverter jsonConverter = new JsonConverter();
        board.start((StarterCard)CardFactory.newPlayableCard("SEPIE_F0I__FPIA"));

        assertEquals(false, board.placeCard((GoldenCard)CardFactory.newPlayableCard("G_EEQFF1QFFA__"),new Position(1,1)));
        assertEquals(false, board.placeCard((GoldenCard)CardFactory.newPlayableCard("GE_EEAF2EAAAF_"),new Position(1,1)));
        assertEquals(false, board.placeCard((GoldenCard)CardFactory.newPlayableCard("G_EEEAF2EAAAP_"),new Position(1,1)));
        assertEquals(false, board.placeCard((GoldenCard)CardFactory.newPlayableCard("GEM__IF3_III__"),new Position(1,1)));

    }

    @Test
    @DisplayName("Testing ")
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
    @DisplayName("Testing ")
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
    @DisplayName("Testing the proper functioning of the method countPattern applied on the D0 pattern")
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

        assert ((board.countGoalPatterns(goalD0)) == 6);
    }
    @Test
    @DisplayName("Testing the proper functioning of the method countPattern applied on the D1 pattern")
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

        assert ((board.countGoalPatterns(goalD1)) == 6);
    }



     @Test
     @DisplayName("Testing the proper functioning of the method countPattern applied on the L0 pattern")
     void testCountL0Pattern() throws SyntaxException {
        GoalPattern goalL0 = (GoalPattern) CardFactory.newSerialCard("O3AIIL0");

        Board board = new Board();
        board.start((StarterCard)CardFactory.newPlayableCard("SEPIE_F0I__FPIA"));

        assert board.placeCard(CardFactory.newPlayableCard("GKEE_AB1KAAI__"), new Position(1,1));
        assert board.placeCard(CardFactory.newPlayableCard("GEE_EAB2EAAAI_"), new Position(2,0));
        assert board.placeCard(CardFactory.newPlayableCard("N_QAIIB0"), new Position(2,2));
        assert board.placeCard(CardFactory.newPlayableCard("NA_AEAB0"), new Position(3,-1));
        assert board.placeCard(CardFactory.newPlayableCard("NI_EEIB1"), new Position(4,0));
        assert board.placeCard(CardFactory.newPlayableCard("GEE__IB5_IIIII"), new Position(3,1));
        assert board.placeCard(CardFactory.newPlayableCard("NEE_IIB1"), new Position(4,2));
        assert board.placeCard(CardFactory.newPlayableCard("NKF_AFB0"), new Position(5,3));
        assert board.placeCard(CardFactory.newPlayableCard("N_FEFFB0"), new Position(4,4));
        assert board.placeCard(CardFactory.newPlayableCard("NMI_FIB0"), new Position(3,3));
        assert board.placeCard(CardFactory.newPlayableCard("GE_EEIB2EIIIF_"), new Position(2,4));

        assertEquals(9, board.countGoalPatterns(goalL0));
    }

    @Test
    @DisplayName("Testing the proper functioning of the method countPattern applied on the L1 pattern")
    void testCountL1Pattern() throws SyntaxException {
        GoalPattern goalL1 = (GoalPattern) CardFactory.newSerialCard("O3FAAL1");

        Board board = new Board();
        board.start((StarterCard)CardFactory.newPlayableCard("SEPIE_F0I__FPIA"));

        assert board.placeCard(CardFactory.newPlayableCard("NAAE_AB0"), new Position(1,-1));
        assert board.placeCard(CardFactory.newPlayableCard("NFEF_FB0"), new Position(2,-2));
        assert board.placeCard(CardFactory.newPlayableCard("NFF_EFB0"), new Position(3,-1));
        assert board.placeCard(CardFactory.newPlayableCard("N_EAAAB0"), new Position(1,1));
        assert board.placeCard(CardFactory.newPlayableCard("NA_AEAB0"), new Position(2,2));
        assert board.placeCard(CardFactory.newPlayableCard("NEA_AAB0"), new Position(2,0));
        assert board.placeCard(CardFactory.newPlayableCard("NE_FFFB0"), new Position(0,2));
        assert board.placeCard(CardFactory.newPlayableCard("N_FEFFB0"), new Position(-1,1));
        assert board.placeCard(CardFactory.newPlayableCard("GKEE_AB1KAAI__"), new Position(-2,2));
        assert board.placeCard(CardFactory.newPlayableCard("G_EEMAB1MAAP__"), new Position(-1,3));
        assert board.placeCard(CardFactory.newPlayableCard("GE_QEAB1QAAF__"), new Position(-2,4));
        assert board.placeCard(CardFactory.newPlayableCard("GEE_EAB2EAAAI_"), new Position(-1,5));

        assertEquals(12, board.countGoalPatterns(goalL1));

    }

    @Test
    @DisplayName("Testing the proper functioning of the method countPattern applied on the L2 pattern")
    void testCountL2Pattern() throws SyntaxException {
        GoalPattern goalL2 = (GoalPattern) CardFactory.newSerialCard("O3IPPL2");

        Board board = new Board();
        board.start((StarterCard)CardFactory.newPlayableCard("SEPIE_F0I__FPIA"));

        assert board.placeCard(CardFactory.newPlayableCard("NAAE_AB0"), new Position(1,-1));
        assert board.placeCard(CardFactory.newPlayableCard("NM_PAPB0"), new Position(2,-2));
        assert board.placeCard(CardFactory.newPlayableCard("NEEP_PB1"), new Position(3,-1));
        assert board.placeCard(CardFactory.newPlayableCard("NEE_IIB1"), new Position(1,1));
        assert board.placeCard(CardFactory.newPlayableCard("N_IEEIF1"), new Position(2,2));
        assert board.placeCard(CardFactory.newPlayableCard("NEE_PPB1"), new Position(2,0));
        assert board.placeCard(CardFactory.newPlayableCard("N_PEEPB1"), new Position(3,1));
        assert board.placeCard(CardFactory.newPlayableCard("NE_PPPB0"), new Position(0,2));
        assert board.placeCard(CardFactory.newPlayableCard("N_PEPPB0"), new Position(-1,1));
        assert board.placeCard(CardFactory.newPlayableCard("N_IQPPB0"), new Position(-1,3));
        assert board.placeCard(CardFactory.newPlayableCard("NFP_KPB0"), new Position(0,4));
        assert board.placeCard(CardFactory.newPlayableCard("GK_E_IB3_III__"), new Position(-2,4));
        assert board.placeCard(CardFactory.newPlayableCard("G__QEIB3_III__"), new Position(-1,5));

        assertEquals(12, board.countGoalPatterns(goalL2));
    }

    @Test
    @DisplayName("Testing the proper functioning of the method countPattern applied on the L3 pattern")
    void testCountL3Pattern() throws SyntaxException {
        GoalPattern goalL2 = (GoalPattern) CardFactory.newSerialCard("O3PFFL3");

        Board board = new Board();
        board.start((StarterCard)CardFactory.newPlayableCard("SEPIE_F0I__FPIA"));

        assert board.placeCard(CardFactory.newPlayableCard("NFEF_FB0"), new Position(1,-1));
        assert board.placeCard(CardFactory.newPlayableCard("NFF_EFB0"), new Position(2,-2));
        assert board.placeCard(CardFactory.newPlayableCard("NE_FFFB0"), new Position(1,1));
        assert board.placeCard(CardFactory.newPlayableCard("N_FEFFB0"), new Position(2,0));
        assert board.placeCard(CardFactory.newPlayableCard("NPEP_PB0"), new Position(2,2));
        assert board.placeCard(CardFactory.newPlayableCard("NPP_EPB0"), new Position(3,1));
        assert board.placeCard(CardFactory.newPlayableCard("G_EEQFB1QFFA__"), new Position(-1,1));
        assert board.placeCard(CardFactory.newPlayableCard("GMEE_FB1MFFI__"), new Position(-2,2));
        assert board.placeCard(CardFactory.newPlayableCard("GEK_EFB1KFFP__"), new Position(-1,3));
        assert board.placeCard(CardFactory.newPlayableCard("GEE_EFB2EFFFA_"), new Position(-2,4));
        assert board.placeCard(CardFactory.newPlayableCard("GQEE_PB1QPPI__"), new Position(0,4));
        assert board.placeCard(CardFactory.newPlayableCard("GEM_EPB1MPPF__"), new Position(-1,5));

        assertEquals(12, board.countGoalPatterns(goalL2));
    }

    @Test
    @DisplayName("Testing the proper functioning of the method countGoalPattern and countGoalSymbols applied on some random goals")
    void testCountMixedPattern() throws SyntaxException {
        GoalPattern goalL0 = (GoalPattern) CardFactory.newSerialCard("O3AIIL0");
        GoalPattern goalD1 = (GoalPattern) CardFactory.newSerialCard("O2AAAD1");
        GoalPattern goalD0 = (GoalPattern) CardFactory.newSerialCard("O2IIID0");

        Board board = new Board();
        board.start((StarterCard)CardFactory.newPlayableCard("SEPIE_F0I__FPIA"));

        assert board.placeCard(CardFactory.newPlayableCard("GKEE_AB1KAAI__"), new Position(1,1));
        assert board.placeCard(CardFactory.newPlayableCard("GEE_EAB2EAAAI_"), new Position(2,0));
        assert board.placeCard(CardFactory.newPlayableCard("N_QAIIB0"), new Position(2,2));
        assert board.placeCard(CardFactory.newPlayableCard("NA_AEAB0"), new Position(3,-1));
        assert board.placeCard(CardFactory.newPlayableCard("NI_EEIB1"), new Position(4,0));
        assert board.placeCard(CardFactory.newPlayableCard("GEE__IB5_IIIII"), new Position(3,1));
        assert board.placeCard(CardFactory.newPlayableCard("NEE_IIB1"), new Position(4,2));
        assert board.placeCard(CardFactory.newPlayableCard("N_EIIIB0"), new Position(5,3));
        assert board.placeCard(CardFactory.newPlayableCard("GEE_EIB2EIIIA_"), new Position(4,4));
        assert board.placeCard(CardFactory.newPlayableCard("NMI_FIB0"), new Position(3,3));
        assert board.placeCard(CardFactory.newPlayableCard("GE_EEIB2EIIIF_"), new Position(2,4));

        assertEquals(9, board.countGoalPatterns(goalL0));
        assertEquals(4, board.countGoalPatterns(goalD0));
        assertEquals(2, board.countGoalPatterns(goalD1));

    }

    @Test
    @DisplayName("Testing the proper functioning of the method calculateScoreOnLastPlaceCard checking the proper placement and the related quick score")
    void testCalculateScoreOnLastPlacedCard() throws SyntaxException {
        Board board = new Board();
        HashMap<Symbol, Integer> symbolCounterCard = new HashMap<>();
        board.start((StarterCard)CardFactory.newPlayableCard("SEEEE_F0PF_IAFP"));

        assert board.placeCard(CardFactory.newPlayableCard("N_IKAAB0"), new Position(1,1));
        assert board.placeCard(CardFactory.newPlayableCard("NIPK_IB0"), new Position(-1,1));
        assert board.placeCard(CardFactory.newPlayableCard("NKF_AFB0"), new Position(-1,-1));
        assert board.placeCard(CardFactory.newPlayableCard("NFP_KPB0"), new Position(1,-1));
        assert board.placeCard(CardFactory.newPlayableCard("NIPK_IF0"), new Position(2,2));
        assert board.placeCard(CardFactory.newPlayableCard("NFP_KPF0"), new Position(3,1));
        assert board.placeCard(CardFactory.newPlayableCard("NFP_KPB0"), new Position(4,0));
        assert board.placeCard(CardFactory.newPlayableCard("NFP_KPB0"), new Position(3,-1));
        assert board.placeCard(CardFactory.newPlayableCard("GE_EEPF2EPPPF_"), new Position(2,0));

        assertEquals(8, board.calculateScoreOnLastPlacedCard());
    }


    @Test
    @DisplayName("Testing the proper functioning of the method countGoalSymbols applied on one random goal checking the proper placement and the proper final score")
    void testCountSymbols() throws SyntaxException{
        GoalSymbol goal = (GoalSymbol) CardFactory.newSerialCard("O2III");

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

        assert ((board.countGoalSymbols(goal)) == 4);
    }

    @Test
    void testValidPlacement() throws SyntaxException {
        Board board = new Board();
        JsonConverter jsonConverter = new JsonConverter();

        board.start((StarterCard)CardFactory.newPlayableCard("SEE___F0PAFFAPI"));

        assertEquals(true, board.placeCard((GoldenCard)CardFactory.newPlayableCard("G_EEQFB1QFFA__"),new Position(1,-1)));
        assertEquals(true, board.placeCard((GoldenCard)CardFactory.newPlayableCard("GE_EEAB2EAAAF_"),new Position(2,0)));
        assertEquals(false, board.placeCard((GoldenCard)CardFactory.newPlayableCard("G_EEEAB2EAAAP_"),new Position(1,1)));
    }
}

record Placement(
        boolean isGood,
        PlayableCard card,
        Position position,
        Position[] availableSpot
) { }
