package IS24_LB11.cli.event.server;

import IS24_LB11.game.PlayerSetup;
import IS24_LB11.game.Result;
import IS24_LB11.game.components.CardFactory;
import IS24_LB11.game.components.GoldenCard;
import IS24_LB11.game.components.NormalCard;
import IS24_LB11.game.components.PlayableCard;
import IS24_LB11.game.tools.JsonConverter;
import IS24_LB11.game.tools.JsonException;
import IS24_LB11.game.utils.SyntaxException;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.stream.Collectors;

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
            case "MESSAGE" -> extractString(data, "message")
                    .andThen(message -> extractString(data, "from")
                            .andThen(from -> extractStringOrElse(data, "to", "")
                                    .map(to -> new ServerMessageEvent(message, from, to))
                            )
                    );
            case "UPDATE" -> extractString(data, "player")
                    .andThen(name -> extractJsonObject(data, "board")
                            .andThen(board -> {
                                try {
                                    return Ok(new ServerUpdatePlayerBoardEvent(name, converter.JSONToBoard(board.toString())));
                                } catch (JsonException | SyntaxException e) {
                                    return Error("error parsing board");
                                }
                            })
                    );
            case "TURN" -> extractString(data, "player")
                    .andThen(player -> extractCardArray(data, "normalDeck", 3)
                            .andThen(normalDeck -> extractCardArray(data,"goldenDeck", 3)
                                    .map(goldenDeck -> new ServerNewTurnEvent( player,
                                            (ArrayList<NormalCard>) normalDeck.stream().map(c -> (NormalCard)c).collect(Collectors.toList()),
                                            (ArrayList<GoldenCard>) goldenDeck.stream().map(c -> (GoldenCard)c).collect(Collectors.toList())
                                    ))
                            )
                    );
            case "SETUP" -> extractJsonObject(data, "setup")
                    .andThen(jsonSetup -> {
                        try {
                            PlayerSetup setup = (PlayerSetup) converter.JSONToObject(jsonSetup.toString());
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

    private static Result<JsonArray> extractJsonArray(JsonObject object, String key) {
        if (!object.has(key)) return Error(EXTRACT_ERROR, "missing field <" + key + ">");
        try {
            return Ok(object.get(key).getAsJsonArray());
        } catch (ClassCastException e) {
            return Error(EXTRACT_ERROR, "can't cast <" + key + "> to JsonObject");
        }
    }

    private static Result<ArrayList<PlayableCard>> extractCardArray(JsonArray array, int expectedSize) {
        if (array.size() != expectedSize) return Error(EXTRACT_ERROR, "expected " + expectedSize + " cards");
        ArrayList<PlayableCard> cards = new ArrayList<>();
        for (int i = 0; i < array.size(); i++) {
            try {
                cards.add(CardFactory.newPlayableCard(array.get(i).getAsString()));
            } catch (SyntaxException | ClassCastException e) {
                return Error(EXTRACT_ERROR, "can't cast <" + array.get(i).getAsString() + "> to PlayableCard");
            }
        }
        return Ok(cards);
    }

    private static Result<ArrayList<PlayableCard>> extractCardArray(JsonObject object, String key, int expectedSize) {
        return extractJsonArray(object, key)
                .andThen(cards -> extractCardArray(cards, expectedSize));
    }
}
