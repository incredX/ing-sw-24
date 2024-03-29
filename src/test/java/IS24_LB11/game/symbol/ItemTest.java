package IS24_LB11.game.symbol;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import IS24_LB11.game.symbol.Item;
import IS24_LB11.game.utils.SyntaxException;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ItemTest {
    @Test
    @DisplayName("fromCharacter return correct Item value")
    public void testFromCharacter() throws SyntaxException {
        assert(Item.QUILL == Item.fromCharacter('Q'));
        assert(Item.INKWELL == Item.fromCharacter('K'));
        assert(Item.MANUSCRIPT == Item.fromCharacter('M'));
    }

    @Test
    @DisplayName("fromCharacter throw exception for char != Q,K,M")
    public void testSuitException() {
        assertThrows(SyntaxException.class, () -> Item.fromCharacter('A'));
    }

    @Test
    @DisplayName("validChar identify correct chars")
    public void testIsValidChar() {
        assert(Item.isValidChar('Q'));
        assert(Item.isValidChar('K'));
        assert(Item.isValidChar('M'));
        assert(!Item.isValidChar('q'));
        assert(!Item.isValidChar('k'));
        assert(!Item.isValidChar('m'));
        assert(!Item.isValidChar('_'));
        assert(!Item.isValidChar('F'));
        assert(!Item.isValidChar('A'));
        assert(!Item.isValidChar('P'));
        assert(!Item.isValidChar('I'));
    }
}
