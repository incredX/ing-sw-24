package IS24_LB11.game.tools;
import IS24_LB11.game.components.Card;
import IS24_LB11.game.components.GoldenCard;
import IS24_LB11.game.components.NormalCard;
import IS24_LB11.game.utils.SyntaxException;
import com.google.gson.Gson;
public class jsonConverter {

    /*
    Per le carte non è necessario un vero e proprio to JSON basta fare override del metodo to String.
    Per board game e player sarà necessario l'utilizzo della libreria toJSON
    */
    public String objectToJSON(Object object){
        Gson gson = new Gson();
        return gson.toJson(object);
    }
    public String objectToJSON(Card card){
        return card.toString();
    }

    public Object JSONToObject(String jsonString){
        return null;
    }

}
