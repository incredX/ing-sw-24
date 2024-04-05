package IS24_LB11.game.tools;

import java.util.ArrayList;

public class JsonDivider {
    public ArrayList<String> toSingleStrings(String stringInput){
        ArrayList<String> stringObjects = new ArrayList<>();
        while (stringInput.contains("&")){
            int startIndex = stringInput.indexOf("&");
            stringInput=stringInput.substring(startIndex);
            int finalIndex = stringInput.indexOf("");
        }
        return stringObjects;
    }
}
