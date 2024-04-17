package IS24_LB11.game.components;

public interface CardInterface extends JsonConvertable{
    String SHORT_ID_MSG = "string %s is not long enough";
    String NOT_A_DIGIT_MSG = "'%c' is not a digit";
    String INVALID_DIGIT_MSG = "'%c' is not a valid digit";
    String INVALID_SEQUENCE_MSG = "'%s' is not a valid sequence of chars";
    String INVALID_INPUT = "'%s' is not a valid input to convert to json";
    String asString();
    int getPoints();
}
