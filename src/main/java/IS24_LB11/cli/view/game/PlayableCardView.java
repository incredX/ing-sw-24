package IS24_LB11.cli.view.game;


import IS24_LB11.cli.style.BorderStyle;
import IS24_LB11.cli.style.SingleBorderStyle;
import IS24_LB11.cli.utils.SymbolAdapter;
import IS24_LB11.cli.utils.TerminalBox;
import IS24_LB11.cli.utils.Side;
import IS24_LB11.game.components.PlayableCard;
import IS24_LB11.game.symbol.Symbol;
import IS24_LB11.game.utils.Direction;

import IS24_LB11.game.utils.Position;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TextColor;

import static IS24_LB11.cli.utils.Side.*;
import static IS24_LB11.game.utils.Direction.*;

public class PlayableCardView extends TerminalBox {
    public static final int WIDTH = 27;
    public static final int HEIGHT = 9;

    protected final PlayableCard card;
    protected Position position;

    public PlayableCardView(PlayableCard card, Position position, BorderStyle borderStyle) {
        super(new TerminalSize(WIDTH, HEIGHT), borderStyle);
        this.card = card;
        this.position = position;
        drawAll();
    }

    public PlayableCardView(PlayableCard card, BorderStyle borderStyle) {
        super(new TerminalSize(WIDTH, HEIGHT), borderStyle);
        this.card = card;
        this.position = null;
        drawAll();
    }

    public PlayableCardView(PlayableCard card, Position position) {
        this(card, position, new SingleBorderStyle());
    }

    public PlayableCardView(PlayableCard card) {
        this(card, (Position)null);
    }

    public void move(Side side) {
        if (position == null) return;
        position = position.withRelative(side.asRelativePosition());
    }

    public void drawAll() {
        drawBorders();
        Direction.forEachDirection(dir -> drawCorner(dir));
        drawSuit();
        drawPoints();
    }

    protected void drawSuit() {
        if (card.getSuit() == null) return;
        TerminalPosition center = new TerminalPosition(WIDTH/2, HEIGHT/2);
        Symbol symbol = card.getSuit();
        if (card.isFaceDown()) {
            drawChar(center, SymbolAdapter.fromSymbol(symbol));
            drawClosedSquare(center);
        } else {
            drawChar(center, Symbol.toChar(symbol));
            //drawOpenSquare(center);
        }
    }

    protected void drawPoints() {
        if (card.isFaceDown() || card.getPoints() == 0) return;
        TerminalPosition center = new TerminalPosition(WIDTH/2, firstRow());
        drawChar(center, (char)(card.getPoints()+'0'));
        drawChar(center.withRelative(-2, 0), '[');
        drawChar(center.withRelative(2, 0), ']');
    }

    protected void drawCorner(Direction dir) {
        final char hSep, vSep, corner;
        final int dx=4, dy=2, signX, signY;
        final TerminalPosition cornerPos = getCornerPosition(dir);
        final TextColor color = borderStyle.getColor();

        if (!card.hasCorner(dir)) return;

        final Symbol symbol = card.getCorner(dir);

        if (dir.isUp()) {
            hSep = borderStyle.getSeparator(NORD);
            signY = 1;
        } else {
            hSep = borderStyle.getSeparator(SUD);
            signY = -1;
        }
        if (dir.isRight()) {
            vSep = borderStyle.getSeparator(EAST);
            signX = -1;
        } else {
            vSep = borderStyle.getSeparator(WEST);
            signX = 1;
        }
        corner = borderStyle.getCorner(dir.opposite());

        drawChar(cornerPos.withRelative(dx*signX, 0), hSep, color);
        drawChar(cornerPos.withRelative(0, dy*signY), vSep, color);
        drawChar(cornerPos.withRelative(dx*signX, dy*signY), corner, color);
        drawChar(cornerPos.withRelative(2*signX, signY), SymbolAdapter.fromSymbol(symbol));
        drawChar(cornerPos.withRelative(dx*signX, signY), borderStyle.getVLine(), color);
        for (int x=1; x<dx; x++) {
            drawChar(cornerPos.withRelative((dx-x)*signX, dy*signY), borderStyle.getHLine(), color);
        }
    }

    protected void drawOpenSquare(TerminalPosition pos) {
        final TextColor color = borderStyle.getColor();
        drawChar(pos.withRelative(2, -1), borderStyle.getCorner(UP_RIGHT), color);
        drawChar(pos.withRelative(-2, -1), borderStyle.getCorner(UP_LEFT), color);
        drawChar(pos.withRelative(-2, 1), borderStyle.getCorner(DOWN_LEFT), color);
        drawChar(pos.withRelative(2, 1), borderStyle.getCorner(DOWN_RIGHT), color);
    }

    protected void drawClosedSquare(TerminalPosition pos) {
        final TextColor color = borderStyle.getColor();
        drawOpenSquare(pos);
        for (int i=-1; i<2; i++) {
            drawChar(pos.withRelative(i, -1), borderStyle.getHLine(), color);
            drawChar(pos.withRelative(i, 1), borderStyle.getHLine(), color);
        }
        drawChar(pos.withRelative(-2, 0), borderStyle.getVLine(), color);
        drawChar(pos.withRelative(2, 0), borderStyle.getVLine(), color);
    }

    public void setBoardPosition(Position position) {
        this.position = position;
    }

    public Position getBoardPosition() {
        return position;
    }
}
