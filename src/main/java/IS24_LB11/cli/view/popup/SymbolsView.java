package IS24_LB11.cli.view.popup;

import IS24_LB11.cli.utils.SymbolAdapter;
import IS24_LB11.game.symbol.Item;
import IS24_LB11.game.symbol.Suit;
import IS24_LB11.game.symbol.Symbol;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static IS24_LB11.cli.utils.Side.WEST;
import static IS24_LB11.game.utils.Direction.*;

public class SymbolsView extends PopupView {
    private static final int DEFAULT_WIDTH = 13;
    private static final int DEFAULT_HEIGHT = 10;

    private ArrayList<Integer> symbols;

    public SymbolsView(TerminalSize parentSize) {
        super(DEFAULT_WIDTH, DEFAULT_HEIGHT, 0, (parentSize.getRows()-DEFAULT_HEIGHT)/2);
        symbols = new ArrayList<>(Arrays.stream(new Integer[]{0,0,0,0,0,0,0}).toList());
        setMargins(0);
    }

    @Override
    public void drawAll() {
        drawBorders();
        drawTitle();
        drawVerticalLine();
        drawSymbols();
    }

    @Override
    public void resize(TerminalSize terminalSize) {
        int y = (terminalSize.getRows()-getHeight())/2;
        setPosition(new TerminalPosition(0, y));
    }

    public void loadSymbols(HashMap<Symbol, Integer> symbolsCounter) {
        int i = 0;
        for (Suit suit : Suit.values()) {
            symbols.set(i, symbolsCounter.get(suit));
            i++;
        }
        for (Item item : Item.values()) {
            symbols.set(i, symbolsCounter.get(item));
            i++;
        }
    }

    private void drawSymbols() {
        int y = firstRow(), i=0;
        for(Suit suit: Suit.values()) {
            drawChar(firstColumn()+2, y, SymbolAdapter.fromSymbol(suit));
            fillRow(y, firstColumn()+6, String.format("%2d", symbols.get(i)));
            y++;
            i++;
        }
        fillRow(y, "-----|-----");
        y++;
        for(Item item: Item.values()) {
            drawChar(firstColumn()+2, y, SymbolAdapter.fromSymbol(item));
            fillRow(y, firstColumn()+6, String.format("%2d", symbols.get(i)));
            y++;
            i++;
        }
    }

    private void drawTitle() {
        fillRow(0, 1, "[symbols]");
    }

    private void drawVerticalLine() {
        fillColumn(firstColumn()+5, "|".repeat(lastRow()));
    }

    @Override
    public void drawBorders() {
        super.drawBorders();
        drawChar(getCornerPosition(UP_LEFT), borderStyle.getSeparator(WEST));
        drawChar(getCornerPosition(DOWN_LEFT), borderStyle.getSeparator(WEST));
    }
}
