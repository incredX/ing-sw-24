package IS24_LB11.cli.event.server;

import IS24_LB11.game.PlayerSetup;
import IS24_LB11.game.Result;
import IS24_LB11.game.components.*;
import IS24_LB11.game.tools.JsonConverter;
import IS24_LB11.game.tools.JsonException;
import IS24_LB11.game.utils.Color;
import IS24_LB11.game.utils.SyntaxException;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.stream.Collectors;

import static IS24_LB11.game.Result.Error;
import static IS24_LB11.game.Result.Ok;

public class ServerEventFactory {
    private static final String EXTRACT_ERROR = "extract error";

    private static final JsonConverter converter = new JsonConverter();

    public static Result<ServerEvent> createServerEvent(JsonObject object) {
        return toResultJson(object).andThen( jsonValue -> {
            JsonObject value = jsonValue.getAsJsonObject();
            return extractString(value, "type").andThen(type -> {
                return createServerEvent(type, jsonValue);
            });
        });
    }

    private static Result<ServerEvent> createServerEvent(String type, JsonObject data) {
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
                                    .andThen(goldenDeck -> extractIntegerArray(data, "scores")
                                            .map(scores -> new ServerNewTurnEvent( player,
                                                    (ArrayList<NormalCard>) normalDeck.stream().map(c -> (NormalCard)c).collect(Collectors.toList()),
                                                    (ArrayList<GoldenCard>) goldenDeck.stream().map(c -> (GoldenCard)c).collect(Collectors.toList()),
                                                    scores)
                                            )
                                    )
                            )
                    );
            case "SETUP" -> extractPlayerSetup(data, "setup")
                    .andThen(setup -> extractGoalArray(data, "publicGoals", 2)
                            .andThen(publicGoals -> extractStringArray(data, "playerNames")
                                    .andThen(players -> extractColorArray(data, "colors")
                                            .andThen(colors -> extractCardArray(data, "normalDeck", 3)
                                                    .andThen(normalDeck -> extractCardArray(data, "goldenDeck", 3)
                                                            .map(goldenDeck -> new ServerPlayerSetupEvent(
                                                                    setup, publicGoals, players, colors,
                                                                    (ArrayList<NormalCard>) normalDeck.stream().map(c -> (NormalCard)c).collect(Collectors.toList()),
                                                                    (ArrayList<GoldenCard>) goldenDeck.stream().map(c -> (GoldenCard)c).collect(Collectors.toList()))
                                                            )
                                                    )
                                            )
                                    )
                            )
                    );
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

    private static Result<PlayerSetup> extractPlayerSetup(JsonObject object, String key) {
        return extractJsonObject(object, "setup")
                .andThen(jsonSetup -> {
                    try {
                        PlayerSetup setup = (PlayerSetup) converter.JSONToObject(jsonSetup.toString());
                        return Ok(setup);
                    }
                    catch (JsonException | SyntaxException e) {
                        return Error("error parsing setup");
                    }
                });
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

    private static Result<ArrayList<String>> extractStringArray(JsonArray array) {
        ArrayList<String> strings = new ArrayList<>();
        for (int i = 0; i < array.size(); i++) {
            try { strings.add(array.get(i).getAsString()); }
            catch (ClassCastException e) {
                return Error(EXTRACT_ERROR, "can't cast <" + array.get(i).getAsString() + "> to PlayableCard");
            }
        }
        return Ok(strings);
    }

    private static Result<ArrayList<Integer>> extractIntegerArray(JsonArray array) {
        ArrayList<Integer> integers = new ArrayList<>();
        for (int i = 0; i < array.size(); i++) {
            try { integers.add(array.get(i).getAsInt()); }
            catch (ClassCastException e) {
                return Error(EXTRACT_ERROR, "can't cast <" + array.get(i).getAsString() + "> to PlayableCard");
            }
        }
        return Ok(integers);
    }

    private static Result<ArrayList<Color>> extractColorArray(JsonArray array) {
        ArrayList<Color> colors = new ArrayList<>();
        for (int i = 0; i < array.size(); i++) {
            try { colors.add(Color.valueOf(array.get(i).getAsString())); }
            catch (ClassCastException | IllegalArgumentException e) {
                return Error(EXTRACT_ERROR, "can't cast JsonArray to ArrayList of Colors");
            }
        }
        return Ok(colors);
    }

    private static Result<ArrayList<GoalCard>> extractGoalArray(JsonArray array, int expectedSize) {
        if (array.size() != expectedSize) return Error(EXTRACT_ERROR, "expected " + expectedSize + " cards");
        ArrayList<GoalCard> goals = new ArrayList<>();
        for (int i = 0; i < array.size(); i++) {
            try { goals.add((GoalCard) CardFactory.newSerialCard(array.get(i).getAsString())); }
            catch (SyntaxException | ClassCastException e) {
                return Error(EXTRACT_ERROR, "can't cast <" + array.get(i).getAsString() + "> to PlayableCard");
            }
        }
        return Ok(goals);
    }

    private static Result<ArrayList<PlayableCard>> extractCardArray(JsonArray array, int expectedSize) {
        if (array.size() != expectedSize) return Error(EXTRACT_ERROR, "expected " + expectedSize + " cards");
        ArrayList<PlayableCard> cards = new ArrayList<>();
        for (int i = 0; i < array.size(); i++) {
            try { cards.add(CardFactory.newPlayableCard(array.get(i).getAsString())); }
            catch (SyntaxException | ClassCastException e) {
                return Error(EXTRACT_ERROR, "can't cast <" + array.get(i).getAsString() + "> to PlayableCard");
            }
        }
        return Ok(cards);
    }

    private static Result<ArrayList<String>> extractStringArray(JsonObject object, String key) {
        return extractJsonArray(object, key).andThen(array -> extractStringArray(array));
    }

    private static Result<ArrayList<Integer>> extractIntegerArray(JsonObject object, String key) {
        return extractJsonArray(object, key).andThen(array -> extractIntegerArray(array));
    }

    private static Result<ArrayList<Color>> extractColorArray(JsonObject object, String key) {
        return extractJsonArray(object, key).andThen(array -> extractColorArray(array));
    }

    private static Result<ArrayList<GoalCard>> extractGoalArray(JsonObject object, String key, int expectedSize) {
        return extractJsonArray(object, key).andThen(array -> extractGoalArray(array, expectedSize));
    }

    private static Result<ArrayList<PlayableCard>> extractCardArray(JsonObject object, String key, int expectedSize) {
        return extractJsonArray(object, key).andThen(cards -> extractCardArray(cards, expectedSize));
    }
}
