package IS24_LB11.network.events;

import com.google.gson.JsonObject;

public record GetEvent(JsonObject data) implements ClientEvent {
}
