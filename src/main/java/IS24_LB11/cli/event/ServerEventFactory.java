package IS24_LB11.cli.event;

import IS24_LB11.game.Result;
import IS24_LB11.game.tools.JsonConverter;
import IS24_LB11.game.tools.JsonException;
import IS24_LB11.game.utils.SyntaxException;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import static IS24_LB11.game.Result.Error;
import static IS24_LB11.game.Result.Ok;

public class ServerEventFactory {
    private static final String EXTRACT_ERROR = "extract error";

    public static Result<ServerEvent> createServerEvent(JsonObject object) {
        return toResultJson(object).andThen( jsonValue -> {
            JsonObject value = jsonValue.getAsJsonObject();
            return extractString(value, "type").andThen(type -> {
                return extractJsonObject(value, "data").andThen(data ->  createServerEvent(type, data));
            });
        });
    }

    private static Result<ServerEvent> createServerEvent(String type, JsonObject data) {
        JsonConverter converter = new JsonConverter();
        return switch (type.toUpperCase()) {
            case "HEARTBEAT" -> Ok(new ServerHeartBeatEvent());
            case "OK" -> extractStringOrElse(data, "message", "")
                    .map(message -> new ServerOkEvent(message));
            case "UPDATE" -> extractString(data, "player").andThen(name ->
                    extractJsonObject(data, "board").andThen(board -> {
                        try {
                            return Ok(new ServerUpdateEvent(name, converter.JSONToBoard(board.toString())));
                        } catch (JsonException | SyntaxException e) {
                            return Error("error parsing board");
                        }
                    }));
            case "MESSAGE" -> extractString(data, "message").andThen(message ->
                    extractString(data, "from").map(from -> new ServerMessageEvent(message, from)));
            default -> Error("unknown server event", "received <"+type+">");
        };
    }

    private static Result<JsonElement> toResultJson(JsonObject object) {
        if (object.has("error")) {
            if (object.has("cause"))
                return Error(object.get("error").getAsString(), object.get("cause").getAsString());
            else
                return Error(object.get("error").getAsString());
        } else if (object.has("value")) {
            return Ok(object.get("value"));
        } else {
            return Error("result syntax error", "missing expected fields (error | value)");
        }
    }

    private static Result<String> extractString(JsonObject object, String key) {
        if (!object.has(key)) return Error(EXTRACT_ERROR, "missing field <" + key + ">");
        try {
            return Ok(object.get(key).getAsString());
        } catch (ClassCastException e) {
            return Error(EXTRACT_ERROR, "can't cast <" + key + "> to String");
        }
    }

    private static Result<String> extractStringOrElse(JsonObject object, String key, String defaultValue) {
        if (!object.has(key)) return Ok(defaultValue);
        try {
            return Ok(object.get(key).getAsString());
        } catch (ClassCastException e) {
            return Error(EXTRACT_ERROR, "can't cast <" + key + "> to String");
        }
    }

    private static Result<JsonObject> extractJsonObject(JsonObject object, String key) {
        if (!object.has(key)) return Error(EXTRACT_ERROR, "missing field <" + key + ">");
        try {
            return Ok(object.get(key).getAsJsonObject());
        } catch (ClassCastException e) {
            return Error(EXTRACT_ERROR, "can't cast <" + key + "> to JsonObject");
        }
    }
}
