package IS24_LB11.cli.view.stage;

import IS24_LB11.cli.ViewHub;
import IS24_LB11.cli.controller.GameState;
import IS24_LB11.cli.utils.Side;
import IS24_LB11.cli.view.game.CardViewFactory;
import IS24_LB11.cli.view.game.PlayableCardView;
import IS24_LB11.game.utils.Position;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;

import java.util.ArrayList;

import static IS24_LB11.cli.view.game.PlayableCardView.WIDTH;

public class GameStage extends Stage {
    private static final int OFFSET_X = 4;
    private static final int OFFSET_Y = 1;
    private static final int UNIT_X = WIDTH-7;
    private static final int UNIT_Y = PlayableCardView.HEIGHT-3;

    private final GameState state;
    private final ArrayList<PlayableCardView> cardviews;
    private TextColor pointerColor;
    private TerminalPosition gridBase;

    public GameStage(GameState state, ViewHub viewHub) {
        super(viewHub);
        this.state = state;
        this.cardviews = new ArrayList<>();
        this.pointerColor = TextColor.ANSI.RED_BRIGHT;
        centerBoard();
        resize();
    }

    @Override
    public void drawAll() {
        drawBorders();
        drawGrid();
        drawPlacedCards();
        drawPointer();
        updateViewHub();
    }

    @Override
    public void resize() {
        super.resize(getScreenSize());
        centerBoard();
        updateBoard();
    }

    public void updatePointer() {
        Side gridShiftSide = null;

        TerminalPosition shiftedPosition = convertPosition(state.getBoardPointer());
        if (shiftedPosition.getColumn()+UNIT_X-OFFSET_X > innerWidth()) gridShiftSide = Side.WEST;
        else if (shiftedPosition.getRow()+UNIT_Y-OFFSET_Y > innerHeight()) gridShiftSide = Side.NORD;
        else if (shiftedPosition.getColumn() < OFFSET_X) gridShiftSide = Side.EAST;
        else if (shiftedPosition.getRow() < OFFSET_Y) gridShiftSide = Side.SUD;

        if (gridShiftSide != null) {
            shiftGridBase(gridShiftSide);
            redraw();
        }else drawPointer();
    }

    public void updateBoard() {
        loadCardViews();
        redraw();
    }

    public void centerBoard() {
        int x = (innerArea.getWidth()/2-OFFSET_X) / UNIT_X;
        int y = (innerArea.getHeight()/2-OFFSET_Y) / UNIT_Y;
        setGridBase(x, y);
    }

    private void shiftGridBase(Side side) {
        Position delta = side.asRelativePosition();
        gridBase = gridBase.withRelative(delta.getX()*UNIT_X, delta.getY()*UNIT_Y);
    }

    private void drawPlacedCards() {
        for (PlayableCardView cardView : cardviews) {
            TerminalSize size = cardView.getSize();
            TerminalPosition terminalPosition = convertPosition(cardView.getBoardPosition());
            terminalPosition = terminalPosition.withRelative(-3, -1);
            cardView.setPosition(terminalPosition);
            drawBox(cardView);
            buildRelativeArea(size.withRelativeRows(1), terminalPosition.max(new TerminalPosition(0,0)));
        }
    }

    public void clearPointer() {
        drawPointer(' ', ' ', TextColor.ANSI.DEFAULT);
    }

    private void drawPointer() {
        drawPointer('+', '#', pointerColor);
    }

    private void drawPointer(char base, char axis, TextColor pointerColor) {
        Position pointer = state.getBoardPointer();
        String verticalAxis = axis+" "+axis;
        String horizontalAxis = axis+" "+base, reversedAxis = base+" "+axis;
        int offsetX = 4;
        int baseX1 = pointer.getX()*UNIT_X+gridBase.getColumn()+offsetX;
        int baseX2 = baseX1+UNIT_X-2*offsetX-2;
        int baseY = pointer.getY()*UNIT_Y+gridBase.getRow()+UNIT_Y/2+1;
        fillColumn(baseX1+3, baseY-2, verticalAxis, pointerColor);
        fillRow(baseY, baseX1, horizontalAxis, pointerColor);
        fillColumn(baseX2+1, baseY-2, verticalAxis, pointerColor);
        fillRow(baseY, baseX2, reversedAxis, pointerColor);
        buildRelativeArea(UNIT_X-1, UNIT_Y, baseX1, baseY-UNIT_Y/2);
    }

    private void drawGrid() {
        String hline = "-".repeat(lastColumn());
        String vline = "¦".repeat(OFFSET_Y);
        for (int y=OFFSET_Y; y<=innerArea.getHeight(); y+=UNIT_Y) {
            fillRow(firstRow()+y, hline);
            vline += "+" + "¦".repeat(UNIT_Y-1);
            buildRelativeArea(borderArea.getWidth(), 1, firstColumn(), firstRow()+y);
        }
        for (int x=OFFSET_X; x<innerArea.getWidth(); x+=UNIT_X) {
            fillColumn(firstColumn()+x, vline);
            buildRelativeArea(1, innerHeight(), firstColumn()+x, firstRow());
        }
    }

    public void loadCardViews() {
        state.getPlacedCardsInBoard().stream().skip(cardviews.size()).forEach(placedCard -> {
            cardviews.add(CardViewFactory.newPlayableCardView(placedCard.card()));
            cardviews.getLast().setBoardPosition(placedCard.position());
        });
    }

    public void setPointerColor(TextColor color) {
        pointerColor = color;
    }

    private void setGridBase(int x, int y) {
        gridBase = new TerminalPosition(OFFSET_X + x * UNIT_X, OFFSET_Y + y * UNIT_Y);
    }

    private TerminalPosition convertPosition(Position position) {
        int x = position.getX()*UNIT_X+gridBase.getColumn();
        int y = position.getY()*UNIT_Y+gridBase.getRow();
        return new TerminalPosition(x, y);
    }
}
