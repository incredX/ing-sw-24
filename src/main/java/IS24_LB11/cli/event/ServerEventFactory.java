package IS24_LB11.cli.event;

import IS24_LB11.game.PlayerSetup;
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
                return createServerEvent(type, jsonValue);
            });
        });
    }

    private static Result<ServerEvent> createServerEvent(String type, JsonObject data) {
        JsonConverter converter = new JsonConverter();
        return switch (type.toUpperCase()) {
            case "HEARTBEAT" -> Ok(new ServerHeartBeatEvent());
            case "SETUSERNAME" -> extractString(data, "username")
                    .map(username -> new ServerLoginEvent(username));
            case "NOTIFICATION" -> extractString(data, "message")
                    .map(message -> new ServerNotificationEvent(message));
            case "UPDATE" -> extractString(data, "player").andThen(name ->
                    extractJsonObject(data, "board").andThen(board -> {
                        try {
                            return Ok(new ServerUpdateEvent(name, converter.JSONToBoard(board.toString())));
                        } catch (JsonException | SyntaxException e) {
                            return Error("error parsing board");
                        }
                    }));
            case "MESSAGE" -> extractString(data, "message")
                    .andThen(message -> extractString(data, "from")
                            .andThen(from -> extractStringOrElse(data, "to", "")
                                    .map(to -> new ServerMessageEvent(message, from, to))));
            case "SETUP" -> extractJsonObject(data, "setup")
                    .andThen(jsonSetup -> {
                        try {
                            PlayerSetup setup = converter.JSONToSetupPlayer(jsonSetup.toString());
                            return Ok(new ServerPlayerSetupEvent(setup));
                        }
                        catch (JsonException | SyntaxException e) {
                            return Error("error parsing setup");
                        }
                    });
            default -> Error("unknown server event", "received <"+type+">");
        };
    }

    private static Result<JsonObject> toResultJson(JsonObject object) {
        if (object.has("error")) {
            if (object.has("cause"))
                return Error(object.get("error").getAsString(), object.get("cause").getAsString());
            else
                return Error(object.get("error").getAsString());
        } else return Ok(object);
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
