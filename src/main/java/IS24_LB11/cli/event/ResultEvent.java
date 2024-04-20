package IS24_LB11.cli.event;

import IS24_LB11.game.Result;
import com.google.gson.JsonElement;

public record ResultEvent(Result<JsonElement> result) implements Event {
}
