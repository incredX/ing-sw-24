package IS24_LB11.game;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.function.Consumer;
import java.util.function.Function;

public class Result<T> {
    private final T value;
    private final String error;
    private final String cause;

    public static <T> Result<T> Ok(T value) {
        return new Result<>(value, null, null);
    }

    public static <T> Result<T> Error(String error, String cause) {
        return new Result<>(null, error, cause);
    }

    public static <T> Result<T> Error(String error) {
        return new Result<>(null, error, null);
    }

    private Result(T value, String error, String cause) {
        this.value = value;
        this.error = error;
        this.cause = cause;
    }

    public <U> Result<U> map(Function<T, U> function) {
        if (isOk()) return Ok(function.apply(value));
        else return Result.Error(error, cause);
    }

    public <U> Result<U> andThen(Function<T, Result<U>> function) {
        if (isOk()) return function.apply(value);
        return Result.Error(error, cause);
    }

    public <U> Result<U> mapError() {
        return Result.Error(error, cause);
    }

    public void ifOk(Consumer<T> consumer) {
        if (isOk()) consumer.accept(value);
    }

    public <U> Result<T> execIfOk(Runnable runnable) {
        if (isOk()) runnable.run();
        return this;
    }

    public T get() {
        return value;
    }

    public T getOrElse(T defaultValue) {
        return isOk() ? value : defaultValue;
    }

    public String getError() {
        return error;
    }

    public String getCause() {
        return cause;
    }

    public boolean isOk() {
        return error == null;
    }

    public boolean isError() {
        return error != null;
    }

    public static JsonObject toJson(Result<JsonElement> result) {
        JsonObject object = new JsonObject();
        if(result.isOk())
            object.add("value", result.get());
        else {
            object.addProperty("error", result.getError());

            if(result.getCause() != null)
                object.addProperty("cause", result.getCause());
        }
        return object;
    }
}
