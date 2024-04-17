package IS24_LB11.network.events;

import IS24_LB11.game.Result;
import com.google.gson.JsonObject;

public class EventFactory {
    private static final String EXTRACT_ERROR = "extract error";

    public static Result<ClientEvent> createEvent(JsonObject object) {
        Result<String> resultType = extractString(object, "type");
        return resultType.andThan(type -> switch(type.toUpperCase()) {
            case "QUIT" ->  Result.Ok(new QuitEvent());
            case "LOGIN" -> extractString(object, "username").andThan(username -> Result.Ok(new LoginEvent(username)));
            case "GET" -> extractJsonObject(object, "data").andThan(data -> Result.Ok(new GetEvent(data)));
            default -> Result.Error("Unknown event type");
        });
    }

    public static JsonObject toJson(ClientEvent event) {
        JsonObject object = new JsonObject();
        switch (event) {
            case QuitEvent quitEvent -> object.addProperty("type", "QUIT");
            case LoginEvent loginEvent -> {
                object.addProperty("type", "LOGIN");
                object.addProperty("username", loginEvent.username());
            }
            case GetEvent getEvent -> {
                object.addProperty("type", "GET");
                object.add("data", getEvent.data());
            }
            default -> {}
        }
        return object;
    }

    private static Result<String> extractString(JsonObject object, String key) {
        if (!object.has(key)) return Result.Error(EXTRACT_ERROR, "missing field <" + key + ">");
        try {
            return Result.Ok(object.get(key).getAsString());
        } catch (ClassCastException e) {
            return Result.Error(EXTRACT_ERROR, "can't cast <" + key + "> to String");
        }
    }

    private static Result<JsonObject> extractJsonObject(JsonObject object, String key) {
        if (!object.has(key)) return Result.Error(EXTRACT_ERROR, "missing field <" + key + ">");
        try {
            return Result.Ok(object.get(key).getAsJsonObject());
        } catch (ClassCastException e) {
            return Result.Error(EXTRACT_ERROR, "can't cast <" + key + "> to JsonObject");
        }
    }
}
