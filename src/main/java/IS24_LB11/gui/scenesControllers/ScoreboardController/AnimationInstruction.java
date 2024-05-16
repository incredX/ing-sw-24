package IS24_LB11.gui.scenesControllers.ScoreboardController;

public class AnimationInstruction {

    private final double pointRepresented;
    private final double X;
    private final double Y;


    public AnimationInstruction(double pointRepresented, double x, double y) {
        this.pointRepresented = pointRepresented;
        this.X = x;
        this.Y = y;
    }

    public double getPointRepresented() {
        return pointRepresented;
    }

    public double getX() {
        return X;
    }

    public double getY() {
        return Y;
    }

}
