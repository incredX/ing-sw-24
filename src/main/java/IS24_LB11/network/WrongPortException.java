package IS24_LB11.network;

public class WrongPortException extends Exception{
    private static final String message = "The PORT number must be in the range 49152-65535";

    public WrongPortException() {
        super(message);
    }

    public WrongPortException(String message) {
        super(message);
    }
}
