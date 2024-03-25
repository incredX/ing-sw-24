package IS24_LB11.game;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;

import IS24_LB11.game.symbol.Suit;
import IS24_LB11.game.utils.SyntaxException;

public class SuitTest {
    @Test
    @DisplayName("fromCharacter return correct Suit value")
    public void testFromCharacter() throws SyntaxException {
        assert(Suit.ANIMAL == Suit.fromCharacter('A'));
        assert(Suit.MUSHROOM == Suit.fromCharacter('F'));
        assert(Suit.INSECT == Suit.fromCharacter('I'));
        assert(Suit.PLANT == Suit.fromCharacter('P'));
    }

    @Test
    @DisplayName("fromCharacter throw exception for char != A,F,I,P")
    public void testSuitException() {
        assertThrows(SyntaxException.class, () -> Suit.fromCharacter('Q'));
    }

    @Test
    @DisplayName("validChar identify correct chars")
    public void testIsValidChar() {
        assert(Suit.isValidChar('A'));
        assert(Suit.isValidChar('F'));
        assert(Suit.isValidChar('I'));
        assert(Suit.isValidChar('P'));
        assert(!Suit.isValidChar('a'));
        assert(!Suit.isValidChar('f'));
        assert(!Suit.isValidChar('i'));
        assert(!Suit.isValidChar('p'));
    }
}